# Test Script for Category Deletion Functions

## Prerequisites
- Firebase Emulator should be running
- Test data should be populated in the emulator

## Test Scenarios

### Scenario 1: Delete a Category with Transactions

1. **Setup**: Create test data in emulator
   - Create a test category in `globalCategories`
   - Create test users
   - Create expenses and incomes linked to the category

2. **Execute**: Mark category as deleted
   ```javascript
   // In Firestore Emulator UI or via script
   db.collection('globalCategories').doc('TEST_CATEGORY_ID').update({
     isDeleted: true,
     updatedAt: firebase.firestore.FieldValue.serverTimestamp()
   });
   ```

3. **Verify**:
   - [ ] Category is moved to `deleted/categories/items/TEST_CATEGORY_ID`
   - [ ] Category has `deletedAt` timestamp
   - [ ] Category has `originalPath` field
   - [ ] Category is removed from `globalCategories`
   - [ ] All related expenses are moved to `deleted/transactions/items`
   - [ ] All related incomes are moved to `deleted/transactions/items`
   - [ ] Transactions have `deletedAt`, `originalPath`, `transactionType`, `userId` fields
   - [ ] Transactions are removed from original locations
   - [ ] Check Functions logs for success messages

### Scenario 2: Restore a Deleted Category

1. **Setup**: Ensure a category is in deleted state (use Scenario 1)

2. **Execute**: Call restore function
   ```javascript
   const functions = firebase.functions();
   const restoreCategory = functions.httpsCallable('restoreCategory');
   
   restoreCategory({ categoryId: 'TEST_CATEGORY_ID' })
     .then((result) => {
       console.log('Result:', result.data);
     })
     .catch((error) => {
       console.error('Error:', error);
     });
   ```

3. **Verify**:
   - [ ] Category is restored to `globalCategories/TEST_CATEGORY_ID`
   - [ ] Category has `isDeleted: false`
   - [ ] Category's `updatedAt` is updated
   - [ ] Category does NOT have `deletedAt` or `originalPath` fields
   - [ ] Category is removed from `deleted/categories/items`
   - [ ] All related transactions are restored to original locations
   - [ ] Transactions have `isDeleted: false`
   - [ ] Transactions' `updatedAt` is updated
   - [ ] Transactions do NOT have `deletedAt`, `originalPath`, `transactionType` fields
   - [ ] Transactions are removed from `deleted/transactions/items`
   - [ ] Function returns correct count of restored transactions

### Scenario 3: Delete Category with No Transactions

1. **Setup**: Create a category with no linked transactions

2. **Execute**: Mark category as deleted

3. **Verify**:
   - [ ] Category is moved to deleted collection
   - [ ] No transactions are affected
   - [ ] No errors in logs

### Scenario 4: Delete Category with Multiple Users' Transactions

1. **Setup**: 
   - Create a category
   - Create 3+ test users
   - Create expenses and incomes for each user linked to the category

2. **Execute**: Mark category as deleted

3. **Verify**:
   - [ ] Transactions from all users are moved to deleted collection
   - [ ] Each transaction retains its `userId` field
   - [ ] Function logs show processing for each user

### Scenario 5: Batch Operations (High Volume)

1. **Setup**: 
   - Create a category
   - Create 1000+ transactions linked to the category across multiple users

2. **Execute**: Mark category as deleted

3. **Verify**:
   - [ ] All transactions are moved successfully
   - [ ] Function logs show batch commits
   - [ ] No timeout errors
   - [ ] No partial failures

### Scenario 6: Error Handling - Restore Non-Existent Category

1. **Execute**: Call restore with invalid categoryId

2. **Verify**:
   - [ ] Function returns error
   - [ ] Error message indicates category not found
   - [ ] No data corruption

### Scenario 7: Authentication Check

1. **Execute**: Call restore function without authentication

2. **Verify**:
   - [ ] Function returns unauthenticated error
   - [ ] No data changes occur

## Automated Test Script (Node.js)

Save as `test-category-deletion.js`:

```javascript
const admin = require('firebase-admin');

// Initialize Firebase Admin with emulator settings
process.env.FIRESTORE_EMULATOR_HOST = 'localhost:8080';
process.env.FIREBASE_AUTH_EMULATOR_HOST = 'localhost:9099';

admin.initializeApp({
  projectId: 'fiscal-compass-emulator',
});

const db = admin.firestore();
const auth = admin.auth();

async function setupTestData() {
  console.log('Setting up test data...');
  
  // Create test category
  const categoryId = 'TEST_CAT_' + Date.now();
  await db.collection('globalCategories').doc(categoryId).set({
    firestoreId: categoryId,
    name: 'Test Category',
    description: 'Category for testing deletion',
    color: 0xFF0000,
    icon: 'test_icon',
    isExpenseCategory: true,
    isDeleted: false,
    isSynced: true,
    needsSync: false,
    parentCategoryFirestoreId: '',
    expectedPersonType: '',
    createdAt: admin.firestore.Timestamp.now(),
    updatedAt: admin.firestore.Timestamp.now(),
    lastSyncedAt: admin.firestore.Timestamp.now(),
  });
  
  // Create test users
  const user1Id = 'TEST_USER_1';
  const user2Id = 'TEST_USER_2';
  
  await db.collection('users').doc(user1Id).set({
    email: 'user1@test.com',
    userType: 'user',
  });
  
  await db.collection('users').doc(user2Id).set({
    email: 'user2@test.com',
    userType: 'user',
  });
  
  // Create test expenses
  for (let i = 0; i < 5; i++) {
    await db.collection('users').doc(user1Id).collection('expenses').add({
      firestoreId: '',
      localId: `LOCAL_${i}`,
      amount: 100 + i,
      amountPaid: 100 + i,
      description: `Test expense ${i}`,
      date: admin.firestore.Timestamp.now(),
      categoryId: 0,
      categoryFirestoreId: categoryId,
      userId: user1Id,
      isDeleted: false,
      isSynced: true,
      needsSync: false,
      createdAt: admin.firestore.Timestamp.now(),
      updatedAt: admin.firestore.Timestamp.now(),
    });
  }
  
  // Create test incomes
  for (let i = 0; i < 3; i++) {
    await db.collection('users').doc(user2Id).collection('incomes').add({
      firestoreId: '',
      localId: `LOCAL_INC_${i}`,
      amount: 500 + i,
      amountPaid: 500 + i,
      description: `Test income ${i}`,
      date: admin.firestore.Timestamp.now(),
      categoryId: 0,
      categoryFirestoreId: categoryId,
      userId: user2Id,
      isDeleted: false,
      isSynced: true,
      needsSync: false,
      createdAt: admin.firestore.Timestamp.now(),
      updatedAt: admin.firestore.Timestamp.now(),
    });
  }
  
  console.log(`Test data created. Category ID: ${categoryId}`);
  return categoryId;
}

async function testCategoryDeletion(categoryId) {
  console.log(`\nTesting category deletion for: ${categoryId}`);
  
  // Mark category as deleted
  await db.collection('globalCategories').doc(categoryId).update({
    isDeleted: true,
    updatedAt: admin.firestore.Timestamp.now(),
  });
  
  // Wait for Cloud Function to process
  console.log('Waiting for Cloud Function to process...');
  await new Promise(resolve => setTimeout(resolve, 5000));
  
  // Verify category moved
  const originalCat = await db.collection('globalCategories').doc(categoryId).get();
  const deletedCat = await db.collection('deleted').doc('categories')
    .collection('items').doc(categoryId).get();
  
  console.log('Category in original location:', originalCat.exists);
  console.log('Category in deleted location:', deletedCat.exists);
  
  if (deletedCat.exists) {
    console.log('Deleted category data:', deletedCat.data());
  }
  
  // Verify transactions moved
  const deletedTransactions = await db.collection('deleted').doc('transactions')
    .collection('items')
    .where('categoryFirestoreId', '==', categoryId)
    .get();
  
  console.log(`Deleted transactions count: ${deletedTransactions.size}`);
}

async function testCategoryRestoration(categoryId) {
  console.log(`\nTesting category restoration for: ${categoryId}`);
  
  // Note: This would call the Cloud Function
  // For emulator testing, you'd need to call it via the Functions SDK
  console.log('To test restoration, use the Firebase Functions SDK or REST API');
  console.log(`Call: restoreCategory({ categoryId: '${categoryId}' })`);
}

async function cleanup(categoryId) {
  console.log('\nCleaning up test data...');
  
  // Delete from all possible locations
  await db.collection('globalCategories').doc(categoryId).delete().catch(() => {});
  await db.collection('deleted').doc('categories').collection('items')
    .doc(categoryId).delete().catch(() => {});
  
  // Delete test users and their data
  const user1 = db.collection('users').doc('TEST_USER_1');
  const user2 = db.collection('users').doc('TEST_USER_2');
  
  const expenses = await user1.collection('expenses').get();
  for (const doc of expenses.docs) {
    await doc.ref.delete();
  }
  
  const incomes = await user2.collection('incomes').get();
  for (const doc of incomes.docs) {
    await doc.ref.delete();
  }
  
  await user1.delete();
  await user2.delete();
  
  // Delete any transactions from deleted collection
  const deletedTxns = await db.collection('deleted').doc('transactions')
    .collection('items')
    .where('categoryFirestoreId', '==', categoryId)
    .get();
  
  for (const doc of deletedTxns.docs) {
    await doc.ref.delete();
  }
  
  console.log('Cleanup complete');
}

async function runTests() {
  try {
    const categoryId = await setupTestData();
    await testCategoryDeletion(categoryId);
    await testCategoryRestoration(categoryId);
    
    // Uncomment to cleanup after tests
    // await cleanup(categoryId);
    
    console.log('\nTests complete!');
    process.exit(0);
  } catch (error) {
    console.error('Test failed:', error);
    process.exit(1);
  }
}

runTests();
```

## Running the Test

1. Start the Firebase Emulator:
   ```powershell
   firebase emulators:start
   ```

2. In another terminal, run the test script:
   ```powershell
   cd functions
   node test-category-deletion.js
   ```

3. Watch the Functions logs in the emulator terminal

4. Check the Emulator UI at http://localhost:4000 to verify data changes

## Manual Testing Checklist

- [ ] Functions deploy without errors
- [ ] Indexes are created
- [ ] Firestore rules are updated
- [ ] Category deletion trigger works
- [ ] Transactions are moved correctly
- [ ] Restore function is callable
- [ ] Restore function works correctly
- [ ] Error handling works as expected
- [ ] Logs provide useful information
- [ ] No data corruption occurs
- [ ] Batch operations handle large datasets
- [ ] Authentication is enforced
