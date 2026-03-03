# Role-Based Upload to Firebase Firestore - Implementation Guide

## Overview

This document describes the implementation of role-based data uploads to Firebase Firestore. The system ensures that:
- **Employees** can only upload their own data (expenses, incomes)
- **Admins** can upload all users' data in their local database

## Architecture

### Key Components

1. **SyncQueryStrategy** (`domain/sync/strategy/SyncQueryStrategy.kt`)
   - Interface that defines permission-based query building
   - Method: `shouldFilterByUserId()` - determines if data should be filtered by userId

2. **PermissionBasedSyncQueryStrategy** (`domain/sync/strategy/PermissionBasedSyncQueryStrategy.kt`)
   - Implementation of SyncQueryStrategy
   - Evaluates `SYNC_ALL_USERS_DATA` permission at runtime
   - No caching - fresh evaluation on every call

3. **ExpenseSyncManager** (`data/remote/sync/ExpenseSyncManager.kt`)
   - Handles expense uploads with role-based filtering
   - Uses `syncQueryStrategy.shouldFilterByUserId()` to determine query scope

4. **IncomeSyncManager** (`data/remote/sync/IncomeSyncManager.kt`)
   - Handles income uploads with role-based filtering
   - Uses `syncQueryStrategy.shouldFilterByUserId()` to determine query scope

### Permission System

```kotlin
// From Permission.kt
enum class Permission {
    SYNC_ALL_USERS_DATA,  // Admin permission - sync all users' data
    SYNC_OWN_DATA,        // Employee permission - sync only own data
    // ... other permissions
}

// From RolePermissions.kt
Role.ADMIN -> setOf(
    Permission.SYNC_ALL_USERS_DATA,
    Permission.SYNC_OWN_DATA,
    // ... other permissions
)

Role.EMPLOYEE -> setOf(
    Permission.SYNC_OWN_DATA,
    // ... other permissions
)
```

## Implementation Details

### Upload Flow for Expenses

```kotlin
suspend fun uploadLocalExpenses(userId: String) {
    // Step 1: Check user's role and permissions
    val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()
    
    // Step 2: Fetch unsynced expenses based on role
    val unsyncedExpenses = if (shouldFilterByUserId) {
        // Employee: Only upload own expenses
        expenseDao.getUnsyncedExpenses(userId)
    } else {
        // Admin: Upload all users' expenses
        expenseDao.getUnsyncedExpenses()
    }
    
    // Step 3: Upload to Firestore (same logic for both roles)
    // ... batch write operations
}
```

### Upload Flow for Incomes

```kotlin
suspend fun uploadLocalIncomes(userId: String) {
    // Step 1: Check user's role and permissions
    val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()
    
    // Step 2: Fetch unsynced incomes based on role
    val unsyncedIncomes = if (shouldFilterByUserId) {
        // Employee: Only upload own incomes
        incomeDao.getUnsyncedIncomes(userId)
    } else {
        // Admin: Upload all users' incomes
        incomeDao.getUnsyncedIncomes()
    }
    
    // Step 3: Upload to Firestore (same logic for both roles)
    // ... batch write operations
}
```

### Upload Flow for Deleted Items

Both managers handle deleted items with the same role-based filtering:

```kotlin
suspend fun uploadDeletedExpenses(userId: String) {
    val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()
    val deletedExpenses = if (shouldFilterByUserId) {
        // Employee: Only sync own deleted expenses
        expenseDao.getUnsyncedDeletedExpenses().filter { it.userId == userId }
    } else {
        // Admin: Sync all deleted expenses
        expenseDao.getUnsyncedDeletedExpenses()
    }
    // ... upload logic
}
```

## DAO Support

### Required DAO Methods

Both `ExpenseDao` and `IncomeDao` provide overloaded methods:

```kotlin
// For Admin - fetch all unsynced items
@Query("SELECT * FROM expenses WHERE needsSync = 1")
suspend fun getUnsyncedExpenses(): List<ExpenseEntity>

// For Employee - fetch only user's unsynced items
@Query("SELECT * FROM expenses WHERE userId = :userId AND needsSync = 1")
suspend fun getUnsyncedExpenses(userId: String): List<ExpenseEntity>

// For deleted items (all users)
@Query("SELECT * FROM expenses WHERE isDeleted = 1 AND isSynced = 0 AND needsSync = 1")
suspend fun getUnsyncedDeletedExpenses(): List<ExpenseEntity>
```

## Usage Examples

### Example 1: Employee Uploads Own Data

```kotlin
// Employee user (userId = "emp123")
// Has only SYNC_OWN_DATA permission

// When uploadLocalExpenses is called:
expenseSyncManager.uploadLocalExpenses("emp123")

// Internal flow:
// 1. shouldFilterByUserId() returns true (Employee has no SYNC_ALL_USERS_DATA)
// 2. Fetches: expenseDao.getUnsyncedExpenses("emp123")
// 3. Uploads only expenses where userId == "emp123"
```

### Example 2: Admin Uploads All Data

```kotlin
// Admin user (userId = "admin456")
// Has SYNC_ALL_USERS_DATA permission

// When uploadLocalExpenses is called:
expenseSyncManager.uploadLocalExpenses("admin456")

// Internal flow:
// 1. shouldFilterByUserId() returns false (Admin has SYNC_ALL_USERS_DATA)
// 2. Fetches: expenseDao.getUnsyncedExpenses() // No userId filter
// 3. Uploads expenses for all users (emp123, emp789, admin456, etc.)
```

### Example 3: Admin Uploads After Employee Login

```kotlin
// Scenario: Admin logs out, Employee logs in (same app session)

// Employee user (userId = "emp123")
expenseSyncManager.uploadLocalExpenses("emp123")

// Internal flow:
// 1. CheckPermissionUseCase fetches current user's role (EMPLOYEE)
// 2. shouldFilterByUserId() returns true (no SYNC_ALL_USERS_DATA)
// 3. Only uploads expenses for "emp123"
// 4. Other users' unsynced data remains in local DB (will not be uploaded)
```

## Firestore Upload Process

### Batch Writing

Both managers use Firebase batch writes for efficiency:

```kotlin
// Batch size limit: 500 operations
var batch = firestore.batch()
var batchCount = 0

unsyncedExpenses.forEach { expense ->
    val docRef = firestore.collection("expenses").document(expense.expenseId)
    batch.set(docRef, expense.toDto())
    batchCount++
    
    if (batchCount >= 500) {
        batch.commit().await()
        // Update local sync status
        batch = firestore.batch()
        batchCount = 0
    }
}
```

### Document ID Strategy

- Uses entity ID (expenseId, incomeId) as Firestore document ID
- Ensures consistency between local Room DB and remote Firestore
- Prevents duplicate documents

## Logging and Debugging

### Log Messages

```kotlin
// When fetching data for upload
Log.d(TAG, "Fetching unsynced expenses for user: $userId (EMPLOYEE)")
// or
Log.d(TAG, "Fetching all unsynced expenses (ADMIN)")

// When uploading
Log.d(TAG, "Uploading ${unsyncedExpenses.size} local expenses")

// Batch operations
Log.d(TAG, "Batch committed successfully, updating local sync status for $batchCount expenses")
```

### Debugging Tips

1. **Check user role**: Verify `authRepository.getUserRole()` returns correct role
2. **Check permissions**: Use `CheckPermissionUseCase(Permission.SYNC_ALL_USERS_DATA)`
3. **Inspect DAO results**: Log the count of items returned by DAO queries
4. **Monitor Firestore**: Use Firebase console to verify uploaded documents

## Security Considerations

### Firestore Security Rules

The current implementation uploads data based on client-side role checks. For production, add server-side security rules:

```javascript
// Firestore Security Rules (firestore.rules)
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper function to check if user is admin
    function isAdmin() {
      return get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
    
    // Expenses collection
    match /expenses/{expenseId} {
      // Users can read/write their own expenses
      allow read, write: if request.auth.uid == resource.data.userId;
      
      // Admins can read/write all expenses
      allow read, write: if isAdmin();
    }
    
    // Incomes collection
    match /incomes/{incomeId} {
      // Users can read/write their own incomes
      allow read, write: if request.auth.uid == resource.data.userId;
      
      // Admins can read/write all incomes
      allow read, write: if isAdmin();
    }
  }
}
```

### Multi-Tenancy Considerations

- Each expense/income document has a `userId` field
- Admins see aggregated data from all users
- Employees see only their own data
- Role changes (promotion/demotion) take effect immediately due to runtime permission checks

## Performance Considerations

### Batch Size

- Firestore batch limit: 500 operations
- Implementation uses automatic batch splitting
- Optimized for large datasets (1000+ records)

### Network Efficiency

- Only uploads items where `needsSync = 1`
- Batch commits reduce network round trips
- Local sync status updated only after successful upload

### Admin Upload Performance

When admin uploads all users' data:
```kotlin
// Potential scenario: 10 users × 100 expenses each = 1000 expenses
// Result: 2 batches (500 + 500)
// Time: ~2-3 seconds depending on network
```

## Testing Checklist

### Unit Tests

- [ ] Test `shouldFilterByUserId()` returns true for EMPLOYEE
- [ ] Test `shouldFilterByUserId()` returns false for ADMIN
- [ ] Test `getUnsyncedExpenses()` returns all users' data
- [ ] Test `getUnsyncedExpenses(userId)` returns only specified user's data

### Integration Tests

- [ ] Test employee can upload only own expenses
- [ ] Test admin can upload all users' expenses
- [ ] Test role switch (Admin → Employee) correctly filters uploads
- [ ] Test batch splitting for large datasets (500+ records)
- [ ] Test deleted items upload with role filtering

### Manual Testing

1. Login as Employee
2. Add 5 expenses
3. Sync - verify only your expenses uploaded
4. Logout
5. Login as Admin
6. Verify you can see all users' data
7. Add expenses for different users
8. Sync - verify all users' expenses uploaded

## Troubleshooting

### Problem: Admin uploads only own data

**Cause**: `shouldFilterByUserId()` returning true for admin

**Solution**:
1. Check `RolePermissions.kt` - ensure ADMIN has `SYNC_ALL_USERS_DATA`
2. Check `CheckPermissionUseCase` - ensure it fetches correct role
3. Verify `authRepository.getUserRole()` returns `Role.ADMIN`

### Problem: Employee can upload other users' data

**Cause**: `shouldFilterByUserId()` returning false for employee

**Solution**:
1. Check `RolePermissions.kt` - ensure EMPLOYEE does NOT have `SYNC_ALL_USERS_DATA`
2. Clear app data and re-login
3. Verify Firestore security rules are enforced

### Problem: No data uploaded despite needsSync = 1

**Cause**: DAO query not returning expected results

**Solution**:
1. Check database: `adb shell` → `run-as <package>` → inspect DB
2. Verify `needsSync` flag is set correctly
3. Check if `isDeleted = 1` (deleted items use separate upload method)

## Future Enhancements

### 1. Selective User Upload for Admins

Allow admins to upload specific users' data:

```kotlin
suspend fun uploadLocalExpenses(
    authenticatedUserId: String,
    targetUserIds: Set<String>? = null
) {
    val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()
    
    val unsyncedExpenses = when {
        !shouldFilterByUserId && targetUserIds != null -> {
            // Admin uploading specific users
            expenseDao.getUnsyncedExpenses().filter { it.userId in targetUserIds }
        }
        !shouldFilterByUserId -> {
            // Admin uploading all users
            expenseDao.getUnsyncedExpenses()
        }
        else -> {
            // Employee uploading own data
            expenseDao.getUnsyncedExpenses(authenticatedUserId)
        }
    }
    // ... upload logic
}
```

### 2. Upload Progress Tracking

Add progress callback for UI updates:

```kotlin
suspend fun uploadLocalExpenses(
    userId: String,
    onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
) {
    // ... fetch unsynced expenses
    
    unsyncedExpenses.forEachIndexed { index, expense ->
        // ... upload logic
        onProgress(index + 1, unsyncedExpenses.size)
    }
}
```

### 3. Conflict Resolution Strategy

Add configurable conflict resolution:

```kotlin
enum class ConflictResolution {
    LOCAL_WINS,    // Keep local changes
    REMOTE_WINS,   // Prefer remote changes
    NEWEST_WINS    // Current behavior - compare timestamps
}
```

## Related Files

- `ExpenseSyncManager.kt` - Expense upload implementation
- `IncomeSyncManager.kt` - Income upload implementation
- `SyncQueryStrategy.kt` - Strategy interface
- `PermissionBasedSyncQueryStrategy.kt` - Strategy implementation
- `Permission.kt` - Permission enumeration
- `RolePermissions.kt` - Role-permission mapping
- `CheckPermissionUseCase.kt` - Permission checking use case
- `ExpenseDao.kt` - Expense DAO with query methods
- `IncomeDao.kt` - Income DAO with query methods

## Summary

The role-based upload implementation ensures data access control at the application layer:

✅ **Employees** can only upload their own data
✅ **Admins** can upload all users' data
✅ **Runtime permission checks** (no caching)
✅ **Consistent with download behavior** (same SyncQueryStrategy)
✅ **Batch optimized** for performance
✅ **Audit-friendly** with detailed logging

The implementation is complete and ready for use. Consider adding Firestore security rules for server-side enforcement in production.

