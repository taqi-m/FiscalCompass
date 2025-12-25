/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {setGlobalOptions} = require("firebase-functions/v2");
const {onDocumentUpdated} = require("firebase-functions/v2/firestore");
const {onCall, HttpsError} = require("firebase-functions/v2/https");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore, FieldValue} = require("firebase-admin/firestore");
const logger = require("firebase-functions/logger");

// Initialize Firebase Admin
initializeApp();
const db = getFirestore();

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
setGlobalOptions({maxInstances: 10});

/**
 * Triggers when a category is updated in globalCategories
 * If isDeleted is true, moves category to deleted/categories
 * and marks all related transactions as deleted
 */
exports.onCategoryUpdate = onDocumentUpdated(
    "globalCategories/{categoryId}",
    async (event) => {
      const categoryId = event.params.categoryId;
      const beforeData = event.data.before.data();
      const afterData = event.data.after.data();

      logger.info(`Category ${categoryId} updated`, {
        wasDeleted: beforeData?.isDeleted,
        isDeleted: afterData?.isDeleted,
      });

      // Check if category was just marked as deleted
      if (!beforeData?.isDeleted && afterData?.isDeleted === true) {
        logger.info(`Category ${categoryId} marked as deleted, processing...`);
        try {
          await handleCategoryDeletion(categoryId, afterData);
        } catch (error) {
          logger.error(`Error handling category deletion: ${error.message}`, {
            categoryId,
            error: error.stack,
          });
          throw error;
        }
      }
    },
);

/**
 * Handles the deletion of a category and its related transactions
 * @param {string} categoryId - The ID of the deleted category
 * @param {object} categoryData - The category data
 */
async function handleCategoryDeletion(categoryId, categoryData) {
  const batch = db.batch();
  const deletedTransactionIds = [];

  try {
    // 1. Move category to deleted/categories
    const deletedCategoryRef = db
        .collection("deleted")
        .doc("categories")
        .collection("items")
        .doc(categoryId);

    batch.set(deletedCategoryRef, {
      ...categoryData,
      deletedAt: FieldValue.serverTimestamp(),
      originalPath: `globalCategories/${categoryId}`,
    });

    // 2. Delete from original location
    const originalCategoryRef = db
        .collection("globalCategories")
        .doc(categoryId);
    batch.delete(originalCategoryRef);

    // Commit category deletion first
    await batch.commit();
    logger.info(`Category ${categoryId} moved to deleted/categories`);

    // 3. Find and mark all related transactions as deleted
    await markRelatedTransactionsAsDeleted(
        categoryId,
        deletedTransactionIds,
    );

    logger.info(`Category deletion completed`, {
      categoryId,
      transactionsAffected: deletedTransactionIds.length,
    });

    return {
      success: true,
      categoryId,
      transactionsAffected: deletedTransactionIds.length,
    };
  } catch (error) {
    logger.error(`Error in handleCategoryDeletion: ${error.message}`, {
      categoryId,
      error: error.stack,
    });
    throw error;
  }
}

/**
 * Marks all transactions related to a category as deleted
 * and moves them to deleted/transactions
 * @param {string} categoryFirestoreId - The firestore ID of the deleted category
 * @param {Array} deletedTransactionIds - Array to track deleted transaction IDs
 */
async function markRelatedTransactionsAsDeleted(
    categoryFirestoreId,
    deletedTransactionIds,
) {
  try {
    // Get all users
    const usersSnapshot = await db.collection("users").get();

    for (const userDoc of usersSnapshot.docs) {
      const userId = userDoc.id;

      // Process expenses
      await processTransactions(
          userId,
          "expenses",
          categoryFirestoreId,
          deletedTransactionIds,
      );

      // Process incomes
      await processTransactions(
          userId,
          "incomes",
          categoryFirestoreId,
          deletedTransactionIds,
      );
    }

    logger.info(`Marked ${deletedTransactionIds.length} transactions as deleted`);
  } catch (error) {
    logger.error(`Error marking transactions as deleted: ${error.message}`, {
      categoryFirestoreId,
      error: error.stack,
    });
    throw error;
  }
}

/**
 * Process transactions (expenses or incomes) for a user
 * @param {string} userId - The user ID
 * @param {string} collectionName - Either 'expenses' or 'incomes'
 * @param {string} categoryFirestoreId - The firestore ID of the deleted category
 * @param {Array} deletedTransactionIds - Array to track deleted transaction IDs
 */
async function processTransactions(
    userId,
    collectionName,
    categoryFirestoreId,
    deletedTransactionIds,
) {
  const transactionsRef = db
      .collection("users")
      .doc(userId)
      .collection(collectionName);

  const transactionsSnapshot = await transactionsRef
      .where("categoryFirestoreId", "==", categoryFirestoreId)
      .where("isDeleted", "==", false)
      .get();

  if (transactionsSnapshot.empty) {
    logger.info(`No ${collectionName} found for user ${userId} with category ${categoryFirestoreId}`);
    return;
  }

  logger.info(`Found ${transactionsSnapshot.size} ${collectionName} to delete for user ${userId}`);

  // Process in batches (Firestore batch limit is 500 operations)
  const BATCH_SIZE = 500;
  let batch = db.batch();
  let operationCount = 0;

  for (const transactionDoc of transactionsSnapshot.docs) {
    const transactionId = transactionDoc.id;
    const transactionData = transactionDoc.data();

    logger.info(`Processing ${collectionName} transaction ${transactionId} for deletion`);

    // Move transaction to deleted/transactions
    const deletedTransactionRef = db
        .collection("deleted")
        .doc("transactions")
        .collection("items")
        .doc(transactionId);

    // Remove localId to prevent duplication issues
    const dataToStore = {...transactionData};
    delete dataToStore.localId;

    batch.set(deletedTransactionRef, {
      ...dataToStore,
      isDeleted: true,
      deletedAt: FieldValue.serverTimestamp(),
      originalPath: `users/${userId}/${collectionName}/${transactionId}`,
      transactionType: collectionName,
      userId: userId,
    });
    logger.info(`Added SET operation to batch for deleted/${collectionName}/${transactionId}`);

    // Delete from original location
    const originalTransactionRef = transactionsRef.doc(transactionId);
    batch.delete(originalTransactionRef);
    logger.info(`Added DELETE operation to batch for users/${userId}/${collectionName}/${transactionId}`);

    deletedTransactionIds.push(transactionId);
    operationCount += 2;

    // Commit batch if we've reached the limit
    if (operationCount >= BATCH_SIZE) {
      logger.info(`Committing batch with ${operationCount} operations...`);
      await batch.commit();
      logger.info(`Batch committed successfully`);
      batch = db.batch();
      operationCount = 0;
    }
  }

  // Commit remaining operations
  if (operationCount > 0) {
    logger.info(`Committing final batch with ${operationCount} operations...`);
    await batch.commit();
    logger.info(`Final batch committed successfully`);
  }
}

/**
 * Callable function to restore a deleted category and its transactions
 * @param {object} request - Contains categoryId in data
 * @return {object} Result of the restoration
 */
exports.restoreCategory = onCall(async (request) => {
  const {categoryId} = request.data;

  if (!categoryId) {
    throw new HttpsError("invalid-argument", "categoryId is required");
  }

  // Check authentication (optional in emulator for testing)
  const isEmulator = process.env.FUNCTIONS_EMULATOR === "true";
  if (!request.auth && !isEmulator) {
    throw new HttpsError(
        "unauthenticated",
        "Must be authenticated to restore categories",
    );
  }

  logger.info(`Restoring category ${categoryId}`, {
    userId: request.auth?.uid || "emulator-test",
    isEmulator: isEmulator,
  });

  try {
    const result = await restoreCategoryAndTransactions(categoryId);
    return result;
  } catch (error) {
    logger.error(`Error restoring category: ${error.message}`, {
      categoryId,
      error: error.stack,
    });
    throw new HttpsError("internal", `Failed to restore category: ${error.message}`);
  }
});

/**
 * Restores a deleted category and its related transactions
 * @param {string} categoryId - The ID of the category to restore
 * @return {object} Result of the restoration
 */
async function restoreCategoryAndTransactions(categoryId) {
  try {
    // 1. Get the deleted category
    const deletedCategoryRef = db
        .collection("deleted")
        .doc("categories")
        .collection("items")
        .doc(categoryId);

    const deletedCategoryDoc = await deletedCategoryRef.get();

    if (!deletedCategoryDoc.exists) {
      throw new Error(`Deleted category ${categoryId} not found`);
    }

    const categoryData = deletedCategoryDoc.data();

    // 2. Restore category to globalCategories
    const batch = db.batch();
    const restoredCategoryRef = db
        .collection("globalCategories")
        .doc(categoryId);

    // Remove deletion metadata
    const {deletedAt, originalPath, ...cleanCategoryData} = categoryData;
    cleanCategoryData.isDeleted = false;
    cleanCategoryData.updatedAt = FieldValue.serverTimestamp();

    batch.set(restoredCategoryRef, cleanCategoryData);

    // Delete from deleted location
    batch.delete(deletedCategoryRef);

    await batch.commit();
    logger.info(`Category ${categoryId} restored to globalCategories`);

    // 3. Restore related transactions
    const restoredTransactionIds = await restoreRelatedTransactions(categoryId);

    logger.info(`Category restoration completed`, {
      categoryId,
      transactionsRestored: restoredTransactionIds.length,
    });

    return {
      success: true,
      categoryId,
      transactionsRestored: restoredTransactionIds.length,
      restoredTransactionIds,
    };
  } catch (error) {
    logger.error(`Error in restoreCategoryAndTransactions: ${error.message}`, {
      categoryId,
      error: error.stack,
    });
    throw error;
  }
}

/**
 * Restores all transactions related to a category
 * @param {string} categoryFirestoreId - The firestore ID of the category
 * @return {Array} Array of restored transaction IDs
 */
async function restoreRelatedTransactions(categoryFirestoreId) {
  const restoredTransactionIds = [];

  try {
    // Get all deleted transactions for this category
    const deletedTransactionsRef = db
        .collection("deleted")
        .doc("transactions")
        .collection("items");

    const deletedTransactionsSnapshot = await deletedTransactionsRef
        .where("categoryFirestoreId", "==", categoryFirestoreId)
        .get();

    if (deletedTransactionsSnapshot.empty) {
      logger.info(`No deleted transactions found for category ${categoryFirestoreId}`);
      return restoredTransactionIds;
    }

    logger.info(`Found ${deletedTransactionsSnapshot.size} transactions to restore`);

    // Process in batches
    const BATCH_SIZE = 500;
    let batch = db.batch();
    let operationCount = 0;

    for (const transactionDoc of deletedTransactionsSnapshot.docs) {
      const transactionId = transactionDoc.id;
      const transactionData = transactionDoc.data();

      // Extract metadata (but keep userId in the data)
      const originalPath = transactionData.originalPath;
      const transactionType = transactionData.transactionType;
      const userId = transactionData.userId;

      if (!originalPath || !userId || !transactionType) {
        logger.warn(`Transaction ${transactionId} missing required metadata, skipping`);
        continue;
      }

      // Remove metadata fields and localId (keep userId as it's part of the transaction data)
      const cleanTransactionData = {...transactionData};
      delete cleanTransactionData.originalPath;
      delete cleanTransactionData.deletedAt;
      delete cleanTransactionData.transactionType;
      delete cleanTransactionData.localId; // Remove to prevent duplication

      // Restore transaction to original location
      const restoredTransactionRef = db
          .collection("users")
          .doc(userId)
          .collection(transactionType)
          .doc(transactionId);

      cleanTransactionData.isDeleted = false;
      cleanTransactionData.updatedAt = FieldValue.serverTimestamp();

      batch.set(restoredTransactionRef, cleanTransactionData);

      // Delete from deleted location
      batch.delete(transactionDoc.ref);

      restoredTransactionIds.push(transactionId);
      operationCount += 2;

      // Commit batch if we've reached the limit
      if (operationCount >= BATCH_SIZE) {
        await batch.commit();
        batch = db.batch();
        operationCount = 0;
        logger.info(`Committed batch of ${BATCH_SIZE} operations`);
      }
    }

    // Commit remaining operations
    if (operationCount > 0) {
      await batch.commit();
      logger.info(`Committed final batch of ${operationCount} operations`);
    }

    return restoredTransactionIds;
  } catch (error) {
    logger.error(`Error restoring transactions: ${error.message}`, {
      categoryFirestoreId,
      error: error.stack,
    });
    throw error;
  }
}
