# Role-Based Upload - Quick Reference

## Quick Start

### For Regular Users (EMPLOYEE)

```kotlin
// Upload only your own expenses
val userId = auth.currentUser?.uid ?: return
expenseSyncManager.uploadLocalExpenses(userId)

// Upload only your own incomes
incomeSyncManager.uploadLocalIncomes(userId)
```

---

### For Admins (ADMIN)

```kotlin
// Upload ALL users' expenses and incomes
val adminUserId = auth.currentUser?.uid ?: return
expenseSyncManager.uploadLocalExpenses(adminUserId) // Uploads all users' data
incomeSyncManager.uploadLocalIncomes(adminUserId)   // Uploads all users' data
```

---

## How It Works

### Permission-Based Filtering

```kotlin
// Internal logic (automatic):
val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()

if (shouldFilterByUserId) {
    // EMPLOYEE: Upload only own data
    expenseDao.getUnsyncedExpenses(userId)
} else {
    // ADMIN: Upload all users' data
    expenseDao.getUnsyncedExpenses() // No filter
}
```

### Permissions by Role

| Role     | Permission              | Upload Scope           |
|----------|------------------------|------------------------|
| EMPLOYEE | `SYNC_OWN_DATA`        | Only own data          |
| ADMIN    | `SYNC_ALL_USERS_DATA`  | All users' data        |

---

## Common Scenarios

### Scenario 1: Employee Sync

```kotlin
// Employee logs in
val userId = "employee_001"

// Sync expenses
expenseSyncManager.syncExpenses(userId)

// Result: Only expenses where userId == "employee_001" are uploaded
```

---

### Scenario 2: Admin Sync

```kotlin
// Admin logs in
val userId = "admin_001"

// Sync expenses
expenseSyncManager.syncExpenses(userId)

// Result: ALL unsynced expenses are uploaded
// - employee_001's expenses
// - employee_002's expenses
// - admin_001's expenses
// - etc.
```

---

### Scenario 3: Role Switch in Same Session

```kotlin
// Admin logs out, employee logs in (same app session)

// Previous admin left 500 unsynced expenses from various users
// Employee syncs
expenseSyncManager.syncExpenses("employee_002")

// Result: Only employee_002's expenses are uploaded
// Other users' unsynced data remains local (will not be uploaded by employee)
```

---

## API Reference

### ExpenseSyncManager

```kotlin
// Upload unsynced expenses (role-based filtering)
suspend fun uploadLocalExpenses(userId: String)

// Upload deleted expenses (role-based filtering)
suspend fun uploadDeletedExpenses(userId: String)

// Complete sync (upload + download)
suspend fun syncExpenses(userId: String, isInitialization: Boolean = false)
```

### IncomeSyncManager

```kotlin
// Upload unsynced incomes (role-based filtering)
suspend fun uploadLocalIncomes(userId: String)

// Upload deleted incomes (role-based filtering)
suspend fun uploadDeletedIncomes(userId: String)

// Complete sync (upload + download)
suspend fun syncIncomes(userId: String, isInitialization: Boolean = false)
```

---

## Checking Permissions

### Check if User Can Sync All Users' Data

```kotlin
val canSyncAll = !syncQueryStrategy.shouldFilterByUserId()

if (canSyncAll) {
    Log.d(TAG, "User is ADMIN - can sync all users' data")
} else {
    Log.d(TAG, "User is EMPLOYEE - can only sync own data")
}
```

### Direct Permission Check

```kotlin
val hasPermission = checkPermissionUseCase(Permission.SYNC_ALL_USERS_DATA)

if (hasPermission) {
    // User has SYNC_ALL_USERS_DATA permission (ADMIN)
} else {
    // User has only SYNC_OWN_DATA permission (EMPLOYEE)
}
```

---

## Upload Behavior

### What Gets Uploaded?

#### Employee Upload
```sql
-- Expenses Query
SELECT * FROM expenses 
WHERE userId = :userId 
  AND needsSync = 1

-- Result: Only employee's own expenses
```

#### Admin Upload
```sql
-- Expenses Query
SELECT * FROM expenses 
WHERE needsSync = 1

-- Result: ALL users' expenses
-- Example:
-- - user_001: 50 expenses
-- - user_002: 30 expenses
-- - user_003: 20 expenses
-- Total: 100 expenses uploaded
```

---

## Logging

### Log Messages to Look For

```
// Employee upload
D/SyncManager: Fetching unsynced expenses for user: employee_001 (EMPLOYEE)
D/SyncManager: Uploading 15 local expenses

// Admin upload
D/SyncManager: Fetching all unsynced expenses (ADMIN)
D/SyncManager: Uploading 150 local expenses
```

---

## Batch Upload Performance

### Batch Size: 500 Operations

```kotlin
// Example: Admin uploading 1000 expenses from 10 users
// - Batch 1: 500 expenses (committed)
// - Batch 2: 500 expenses (committed)
// Total time: ~2-3 seconds
```

### Progress Tracking

```kotlin
Log.d(TAG, "Processing expense ${index + 1}/${unsyncedExpenses.size}")
Log.d(TAG, "Batch committed successfully, updating local sync status for $batchCount expenses")
```

---

## Troubleshooting

### Problem: Employee sees other users' data being uploaded

**Check:**
1. User role: `authRepository.getUserRole()` should return `Role.EMPLOYEE`
2. Permission check: `checkPermissionUseCase(Permission.SYNC_ALL_USERS_DATA)` should return `false`

**Solution:**
```kotlin
// Force re-check permissions
val role = authRepository.getUserRole()
Log.d(TAG, "Current role: $role")

val canSyncAll = checkPermissionUseCase(Permission.SYNC_ALL_USERS_DATA)
Log.d(TAG, "Can sync all users: $canSyncAll")
```

---

### Problem: Admin only uploads own data

**Check:**
1. User role: `authRepository.getUserRole()` should return `Role.ADMIN`
2. Permission check: `checkPermissionUseCase(Permission.SYNC_ALL_USERS_DATA)` should return `true`

**Solution:**
```kotlin
// Verify RolePermissions mapping
// In RolePermissions.kt:
Role.ADMIN to setOf(
    Permission.SYNC_ALL_USERS_DATA,  // <-- Must be present
    // ... other permissions
)
```

---

### Problem: No data uploaded

**Check:**
1. Items marked for sync: Query DB for `needsSync = 1`
2. Network connection
3. Firebase authentication

**Solution:**
```kotlin
// Check unsynced count
val count = expenseDao.getUnsyncedExpenseCount().first()
Log.d(TAG, "Unsynced expenses count: $count")

// Check if items need sync
val hasUnsynced = expenseDao.hasUnsyncedData()
Log.d(TAG, "Has unsynced data: $hasUnsynced")
```

---

## Database Queries

### DAO Methods Used

```kotlin
// Employee query (filtered by userId)
@Query("SELECT * FROM expenses WHERE userId = :userId AND needsSync = 1")
suspend fun getUnsyncedExpenses(userId: String): List<ExpenseEntity>

// Admin query (no filter)
@Query("SELECT * FROM expenses WHERE needsSync = 1")
suspend fun getUnsyncedExpenses(): List<ExpenseEntity>

// Deleted items (all users)
@Query("SELECT * FROM expenses WHERE isDeleted = 1 AND isSynced = 0 AND needsSync = 1")
suspend fun getUnsyncedDeletedExpenses(): List<ExpenseEntity>
```

---

## Code Examples

### Example 1: Manual Upload Control

```kotlin
class SyncViewModel @Inject constructor(
    private val expenseSyncManager: ExpenseSyncManager,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    fun uploadExpenses() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                expenseSyncManager.uploadLocalExpenses(userId)
                _syncStatus.value = "Upload complete"
            } catch (e: Exception) {
                _syncStatus.value = "Upload failed: ${e.message}"
            }
        }
    }
}
```

---

### Example 2: Sync with Progress

```kotlin
suspend fun syncWithProgress() {
    val userId = auth.currentUser?.uid ?: return
    
    // Upload expenses
    emit(SyncProgress(0, "Uploading expenses..."))
    expenseSyncManager.uploadLocalExpenses(userId)
    
    emit(SyncProgress(50, "Uploading incomes..."))
    incomeSyncManager.uploadLocalIncomes(userId)
    
    emit(SyncProgress(100, "Sync complete"))
}
```

---

### Example 3: Check Before Upload

```kotlin
suspend fun uploadIfNeeded(userId: String) {
    // Check if there's data to upload
    val hasUnsyncedExpenses = expenseDao.hasUnsyncedData()
    val hasUnsyncedIncomes = incomeDao.hasUnsyncedData()
    
    if (!hasUnsyncedExpenses && !hasUnsyncedIncomes) {
        Log.d(TAG, "No data to upload")
        return
    }
    
    // Upload
    if (hasUnsyncedExpenses) {
        expenseSyncManager.uploadLocalExpenses(userId)
    }
    
    if (hasUnsyncedIncomes) {
        incomeSyncManager.uploadLocalIncomes(userId)
    }
}
```

---

## Key Points

✅ **No manual role checks needed** - The sync managers handle it automatically

✅ **Same API for all roles** - Call `uploadLocalExpenses(userId)` regardless of role

✅ **Runtime permission evaluation** - No caching, always checks current role

✅ **Consistent with downloads** - Upload and download use same permission strategy

✅ **Batch optimized** - Handles large datasets efficiently (500 items per batch)

✅ **Atomic operations** - Local DB updated only after successful Firestore commit

---

## Related Documentation

- [ROLE_BASED_UPLOAD_IMPLEMENTATION.md](./ROLE_BASED_UPLOAD_IMPLEMENTATION.md) - Detailed implementation guide
- [RBAC_SYNC_QUICK_REFERENCE.md](./RBAC_SYNC_QUICK_REFERENCE.md) - RBAC sync system reference
- [PERMISSION_BASED_SYNC_IMPLEMENTATION.md](./PERMISSION_BASED_SYNC_IMPLEMENTATION.md) - Permission-based sync details

---

## Summary

The role-based upload system automatically filters data based on the current user's role:

| User Type | Uploads | Behavior |
|-----------|---------|----------|
| **Employee** | Own data only | `expenseDao.getUnsyncedExpenses(userId)` |
| **Admin** | All users' data | `expenseDao.getUnsyncedExpenses()` |

**No additional code required** - Just call the upload methods, and the system handles the rest!

