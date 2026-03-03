# Category Deletion & Restoration Functions

## Overview
This document describes the Firebase Cloud Functions implemented for handling category deletions and restorations in the FiscalCompass application.

## Functions Implemented

### 1. `onCategoryUpdate` (Trigger Function)
**Type**: Firestore Trigger  
**Path**: `globalCategories/{categoryId}`  
**Trigger**: Document Update

**Functionality**:
- Automatically triggers when any category in `globalCategories` collection is updated
- Detects when a category's `isDeleted` field changes from `false` to `true`
- Initiates the category deletion process

**Process Flow**:
1. Monitors category updates in `globalCategories`
2. If `isDeleted` changes to `true`, calls `handleCategoryDeletion()`
3. Logs all operations for debugging

### 2. `handleCategoryDeletion` (Internal Function)
**Purpose**: Orchestrates the category deletion process

**Steps**:
1. Moves the category to `deleted/categories/items/{categoryId}`
2. Adds metadata: `deletedAt` timestamp and `originalPath`
3. Deletes the category from `globalCategories/{categoryId}`
4. Calls `markRelatedTransactionsAsDeleted()` to handle related transactions

### 3. `markRelatedTransactionsAsDeleted` (Internal Function)
**Purpose**: Finds and marks all related transactions as deleted

**Process**:
1. Iterates through all users in the `users` collection
2. For each user, processes both `expenses` and `incomes` subcollections
3. Calls `processTransactions()` for each collection type

### 4. `processTransactions` (Internal Function)
**Purpose**: Moves transactions from user collections to deleted collection

**Process**:
1. Queries transactions where `categoryFirestoreId` matches the deleted category
2. Only processes transactions where `isDeleted == false`
3. For each transaction:
   - Moves to `deleted/transactions/items/{transactionId}`
   - Adds metadata: `deletedAt`, `originalPath`, `transactionType`, `userId`
   - Sets `isDeleted = true`
   - Deletes from original location
4. Uses batched writes (500 operations per batch) for efficiency

### 5. `restoreCategory` (Callable Function)
**Type**: HTTPS Callable Function  
**Authentication**: Required

**Parameters**:
```javascript
{
  categoryId: string  // The ID of the category to restore
}
```

**Returns**:
```javascript
{
  success: boolean,
  categoryId: string,
  transactionsRestored: number,
  restoredTransactionIds: string[]
}
```

**Process**:
1. Validates authentication and parameters
2. Retrieves the category from `deleted/categories/items/{categoryId}`
3. Restores it to `globalCategories/{categoryId}`
4. Sets `isDeleted = false` and updates `updatedAt` timestamp
5. Removes deletion metadata (`deletedAt`, `originalPath`)
6. Calls `restoreRelatedTransactions()` to restore all related transactions
7. Returns summary of restoration

### 6. `restoreRelatedTransactions` (Internal Function)
**Purpose**: Restores all transactions related to a category

**Process**:
1. Queries `deleted/transactions/items` for transactions with matching `categoryFirestoreId`
2. For each transaction:
   - Extracts `userId` and `transactionType` from metadata
   - Restores to `users/{userId}/{transactionType}/{transactionId}`
   - Sets `isDeleted = false` and updates `updatedAt` timestamp
   - Removes deletion metadata
   - Deletes from deleted collection
3. Uses batched writes (500 operations per batch) for efficiency
4. Returns array of restored transaction IDs

## Database Structure

### Category in `globalCategories/{categoryId}`
```kotlin
CategoryDto {
  firestoreId: String
  color: Int
  createdAt: Timestamp
  description: String
  expectedPersonType: String
  icon: String
  isExpenseCategory: Boolean
  isSynced: Boolean
  lastSyncedAt: Timestamp?
  name: String
  isDeleted: Boolean  // Key field for deletion
  needsSync: Boolean
  parentCategoryFirestoreId: String
  updatedAt: Timestamp
}
```

### Deleted Category in `deleted/categories/items/{categoryId}`
All original fields plus:
```javascript
{
  ...CategoryDto,
  deletedAt: Timestamp,
  originalPath: "globalCategories/{categoryId}"
}
```

### Transaction in `users/{userId}/expenses/{transactionId}` or `users/{userId}/incomes/{transactionId}`
```kotlin
ExpenseDto / IncomeDto {
  firestoreId: String
  localId: String
  amount: Double
  amountPaid: Double
  description: String
  date: Timestamp
  categoryId: Long
  categoryFirestoreId: String  // Used to link to category
  userId: String
  personId: Long?
  personFirestoreId: String?
  // ... other fields
  isDeleted: Boolean
  isSynced: Boolean
  needsSync: Boolean
  lastSyncedAt: Timestamp?
}
```

### Deleted Transaction in `deleted/transactions/items/{transactionId}`
All original fields plus:
```javascript
{
  ...ExpenseDto/IncomeDto,
  isDeleted: true,
  deletedAt: Timestamp,
  originalPath: "users/{userId}/{expenses|incomes}/{transactionId}",
  transactionType: "expenses" | "incomes",
  userId: String
}
```

## Firestore Security Rules

The updated rules include:

```javascript
// Deleted items - admin only access
match /deleted/{type}/{document=**} {
  allow read, write: if isAdmin();
}
```

This ensures that only admin users can access the deleted collections directly.

## Usage Examples

### From Android/Kotlin Client

#### Delete a Category
```kotlin
// Simply update the category's isDeleted field
val categoryRef = firestore.collection("globalCategories").document(categoryId)
categoryRef.update("isDeleted", true)
    .addOnSuccessListener {
        // Category and related transactions will be automatically moved
        // by the Cloud Function
    }
```

#### Restore a Category
```kotlin
val functions = Firebase.functions
val restoreCategory = functions.getHttpsCallable("restoreCategory")

val data = hashMapOf(
    "categoryId" to categoryId
)

restoreCategory.call(data)
    .addOnSuccessListener { result ->
        val response = result.data as Map<String, Any>
        val success = response["success"] as Boolean
        val transactionsRestored = response["transactionsRestored"] as Int
        
        Log.d("Restore", "Success: $success, Transactions restored: $transactionsRestored")
    }
    .addOnFailureListener { exception ->
        Log.e("Restore", "Error restoring category", exception)
    }
```

### Testing with Firebase Emulator

1. Start the emulator:
```powershell
firebase emulators:start
```

2. Test category deletion:
   - Update a category's `isDeleted` field to `true` in the Firestore UI
   - Check the Functions logs to see the deletion process
   - Verify that the category is moved to `deleted/categories/items`
   - Verify that related transactions are moved to `deleted/transactions/items`

3. Test category restoration:
   - Use the Firebase Functions emulator to call `restoreCategory` with a categoryId
   - Verify that the category is restored to `globalCategories`
   - Verify that related transactions are restored to their original locations

## Error Handling

All functions include comprehensive error handling:
- Detailed logging at each step
- Error messages include context (categoryId, userId, etc.)
- Failed operations are logged with full stack traces
- Batched operations ensure partial failures don't corrupt data

## Performance Considerations

1. **Batched Writes**: All bulk operations use batches of 500 operations to comply with Firestore limits
2. **Pagination**: Large transaction sets are processed in chunks to avoid timeouts
3. **Indexing**: Ensure composite indexes exist for:
   - `users/{userId}/expenses` where `categoryFirestoreId == X AND isDeleted == false`
   - `users/{userId}/incomes` where `categoryFirestoreId == X AND isDeleted == false`
   - `deleted/transactions/items` where `categoryFirestoreId == X`

## Composite Indexes Required

Add these to `firestore.indexes.json`:

```json
{
  "indexes": [
    {
      "collectionGroup": "expenses",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "categoryFirestoreId", "order": "ASCENDING" },
        { "fieldPath": "isDeleted", "order": "ASCENDING" }
      ]
    },
    {
      "collectionGroup": "incomes",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "categoryFirestoreId", "order": "ASCENDING" },
        { "fieldPath": "isDeleted", "order": "ASCENDING" }
      ]
    }
  ]
}
```

## Deployment

### To Firebase Emulator (Development)
```powershell
firebase emulators:start
```

### To Production
```powershell
firebase deploy --only functions
```

### Deploy Specific Function
```powershell
firebase deploy --only functions:onCategoryUpdate
firebase deploy --only functions:restoreCategory
```

## Monitoring & Logs

View logs in production:
```powershell
firebase functions:log
```

View logs in emulator:
- Check the terminal where emulator is running
- Or visit the Emulator UI at http://localhost:4000

## Notes

1. **Transaction Consistency**: The functions use batched writes to ensure atomicity
2. **Soft Delete**: The original approach of updating `isDeleted` in place has been replaced with moving to a dedicated `deleted` collection
3. **Metadata**: All deleted items retain metadata for restoration purposes
4. **Admin Only**: Only admin users can access deleted collections directly (per Firestore rules)
5. **Emulator Testing**: Always test thoroughly in the emulator before deploying to production
