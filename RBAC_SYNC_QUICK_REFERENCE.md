# RBAC Sync Manager - Quick Reference Guide

## Quick Start

### For Regular Users (EMPLOYEE)

```kotlin
// Get user info
val userId = auth.currentUser?.uid ?: return
val userRole = authRepository.getUserRole() ?: Role.EMPLOYEE

// Upload your own incomes
incomeSyncManager.uploadLocalIncomes(userId, userRole)

// Download your own incomes
incomeSyncManager.downloadRemoteIncomes(userId, userRole)

// Upload your own expenses
expenseSyncManager.uploadLocalExpenses(userId, userRole)

// Download your own expenses
expenseSyncManager.downloadRemoteExpenses(userId, userRole)
```

---

### For Admins (ADMIN) - Sync Specific Users

```kotlin
val adminUserId = auth.currentUser?.uid ?: return
val adminRole = Role.ADMIN

// Sync specific users
val targetUsers = setOf("user_alice_123", "user_bob_456")

// Upload incomes for specific users
incomeSyncManager.uploadLocalIncomes(
    authenticatedUserId = adminUserId,
    userRole = adminRole,
    targetUserIds = targetUsers
)

// Download incomes for specific users
incomeSyncManager.downloadRemoteIncomes(
    authenticatedUserId = adminUserId,
    userRole = adminRole,
    targetUserIds = targetUsers
)
```

---

### For Admins (ADMIN) - Sync All Users

```kotlin
val adminUserId = auth.currentUser?.uid ?: return
val adminRole = Role.ADMIN

// Upload ALL users' incomes (don't specify targetUserIds)
incomeSyncManager.uploadLocalIncomes(
    authenticatedUserId = adminUserId,
    userRole = adminRole,
    targetUserIds = null  // This will sync ALL users
)

// Note: Download requires explicit targetUserIds
```

---

## Permission Checks

### Check if User Can Sync

```kotlin
val permissionResult = syncPermissionManager.checkSyncPermission(
    authenticatedUserId = userId,
    userRole = userRole,
    requestedUserIds = setOf("other_user_123")
)

if (permissionResult.canSync) {
    // Proceed with sync
    Log.d(TAG, "Can sync: ${permissionResult.reason}")
} else {
    // Show error
    Log.w(TAG, "Cannot sync: ${permissionResult.reason}")
}
```

---

## Common Patterns

### Pattern 1: Sync All Data (Regular User)

```kotlin
suspend fun syncMyData() {
    val userId = auth.currentUser?.uid ?: return
    val userRole = authRepository.getUserRole() ?: Role.EMPLOYEE
    
    try {
        // Sync categories (if permitted)
        if (checkPermissionUseCase(Permission.ADD_CATEGORY)) {
            categorySyncManager.uploadLocalCategories()
        }
        categorySyncManager.downloadRemoteCategories()
        
        // Sync incomes
        incomeSyncManager.uploadLocalIncomes(userId, userRole)
        incomeSyncManager.downloadRemoteIncomes(userId, userRole)
        
        // Sync expenses
        expenseSyncManager.uploadLocalExpenses(userId, userRole)
        expenseSyncManager.downloadRemoteExpenses(userId, userRole)
        
        Log.d(TAG, "Full sync completed successfully")
    } catch (e: Exception) {
        Log.e(TAG, "Sync failed: ${e.message}", e)
        throw e
    }
}
```

---

### Pattern 2: Admin Bulk Sync

```kotlin
suspend fun adminSyncAllUsers() {
    val adminUserId = auth.currentUser?.uid ?: return
    val adminRole = authRepository.getUserRole() ?: return
    
    if (adminRole != Role.ADMIN) {
        Log.w(TAG, "Only admins can perform bulk sync")
        return
    }
    
    try {
        // Upload all users' incomes
        incomeSyncManager.uploadLocalIncomes(
            authenticatedUserId = adminUserId,
            userRole = adminRole,
            targetUserIds = null  // ALL users
        )
        
        // Upload all users' expenses
        expenseSyncManager.uploadLocalExpenses(
            authenticatedUserId = adminUserId,
            userRole = adminRole,
            targetUserIds = null  // ALL users
        )
        
        Log.d(TAG, "Admin bulk sync completed")
    } catch (e: Exception) {
        Log.e(TAG, "Admin sync failed: ${e.message}", e)
        throw e
    }
}
```

---

### Pattern 3: Selective Sync

```kotlin
suspend fun syncSpecificUsers(userIds: List<String>) {
    val adminUserId = auth.currentUser?.uid ?: return
    val adminRole = authRepository.getUserRole() ?: return
    
    // Convert to set
    val targetUsers = userIds.toSet()
    
    // Check permission first
    val permissionResult = syncPermissionManager.checkSyncPermission(
        authenticatedUserId = adminUserId,
        userRole = adminRole,
        requestedUserIds = targetUsers
    )
    
    if (!permissionResult.canSync) {
        Log.w(TAG, "Permission denied: ${permissionResult.reason}")
        return
    }
    
    try {
        // Sync incomes for specific users
        incomeSyncManager.uploadLocalIncomes(
            authenticatedUserId = adminUserId,
            userRole = adminRole,
            targetUserIds = targetUsers
        )
        
        incomeSyncManager.downloadRemoteIncomes(
            authenticatedUserId = adminUserId,
            userRole = adminRole,
            targetUserIds = targetUsers
        )
        
        Log.d(TAG, "Synced data for ${userIds.size} users")
    } catch (e: Exception) {
        Log.e(TAG, "Selective sync failed: ${e.message}", e)
        throw e
    }
}
```

---

## Error Handling

### Handle Permission Denied

```kotlin
try {
    incomeSyncManager.uploadLocalIncomes(userId, userRole, targetUserIds)
} catch (e: Exception) {
    when {
        e.message?.contains("Permission denied") == true -> {
            // Show permission error to user
            showError("You don't have permission to sync this data")
        }
        e.message?.contains("network") == true -> {
            // Network error
            showError("Network error. Please check your connection")
        }
        else -> {
            // Unknown error
            showError("Sync failed: ${e.message}")
        }
    }
}
```

---

## UI Integration

### ViewModel Example

```kotlin
class SyncViewModel @Inject constructor(
    private val incomeSyncManager: IncomeSyncManager,
    private val expenseSyncManager: ExpenseSyncManager,
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    fun syncAllData() {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("Not authenticated")
                val userRole = authRepository.getUserRole() ?: Role.EMPLOYEE
                
                // Upload
                incomeSyncManager.uploadLocalIncomes(userId, userRole)
                expenseSyncManager.uploadLocalExpenses(userId, userRole)
                
                // Download
                incomeSyncManager.downloadRemoteIncomes(userId, userRole)
                expenseSyncManager.downloadRemoteExpenses(userId, userRole)
                
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun syncSpecificUsers(userIds: List<String>) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            
            try {
                val adminUserId = auth.currentUser?.uid ?: throw Exception("Not authenticated")
                val adminRole = authRepository.getUserRole() ?: throw Exception("Role not found")
                
                if (adminRole != Role.ADMIN) {
                    throw Exception("Only admins can sync other users")
                }
                
                val targetUsers = userIds.toSet()
                
                incomeSyncManager.uploadLocalIncomes(adminUserId, adminRole, targetUsers)
                expenseSyncManager.uploadLocalExpenses(adminUserId, adminRole, targetUsers)
                
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}
```

---

### Compose UI Example

```kotlin
@Composable
fun SyncScreen(
    viewModel: SyncViewModel = hiltViewModel()
) {
    val syncState by viewModel.syncState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (syncState) {
            is SyncState.Idle -> {
                Button(onClick = { viewModel.syncAllData() }) {
                    Text("Sync My Data")
                }
                
                // Show admin options if user is admin
                if (isAdmin) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.syncAllUsers() }) {
                        Text("Sync All Users (Admin)")
                    }
                }
            }
            
            is SyncState.Syncing -> {
                CircularProgressIndicator()
                Text("Syncing data...")
            }
            
            is SyncState.Success -> {
                Icon(Icons.Default.Check, contentDescription = null)
                Text("Sync completed successfully!")
            }
            
            is SyncState.Error -> {
                Icon(Icons.Default.Error, contentDescription = null)
                Text("Error: ${(syncState as SyncState.Error).message}")
            }
        }
    }
}
```

---

## Debugging

### Enable Detailed Logs

All sync operations automatically log to Logcat with tag `SyncManager`:

```
// Search logs
adb logcat | grep "SyncManager"

// Key log patterns
"Upload permitted: ..."
"Admin mode: true/false"
"Syncing incomes for X user(s): ..."
"Validated X/Y incomes for user ..."
"SKIPPED: Income userId mismatch"
```

---

### Log Examples

#### Regular User Sync
```
D/SyncManager: Upload permitted: User with SYNC_OWN_DATA permission
D/SyncManager: Admin mode: false
D/SyncManager: Syncing incomes for 1 user(s): user_john_123
D/SyncManager: Uploading 5 local incomes for user: user_john_123 (authenticated: user_john_123, role: EMPLOYEE)
D/SyncManager: Validated 5/5 incomes for user user_john_123 (role: EMPLOYEE)
```

#### Admin Sync Multiple Users
```
D/SyncManager: Upload permitted: Admin with SYNC_ALL_USERS_DATA permission
D/SyncManager: Admin mode: true
D/SyncManager: Syncing incomes for 3 user(s): user_alice_456, user_bob_789, user_charlie_101
D/SyncManager: Uploading 10 local incomes for user: user_alice_456 (authenticated: admin_123, role: ADMIN)
D/SyncManager: Validated 10/10 incomes for user user_alice_456 (role: ADMIN)
```

#### Permission Denied
```
W/SyncManager: Upload denied: User can only sync their own data
```

---

## Troubleshooting

### Problem: "Permission denied" error

**Solution**: Check user role and permissions
```kotlin
val userRole = authRepository.getUserRole()
Log.d(TAG, "User role: $userRole")

val hasPermission = RolePermissions.hasPermission(userRole, Permission.SYNC_OWN_DATA)
Log.d(TAG, "Has sync permission: $hasPermission")
```

### Problem: "No users to sync"

**Solution**: Check if there are unsynced items
```kotlin
val unsyncedCount = incomeDao.getUnsyncedIncomes(userId).size
Log.d(TAG, "Unsynced incomes: $unsyncedCount")
```

### Problem: Admin can't sync all users

**Solution**: Make sure `targetUserIds` is null
```kotlin
// ❌ Wrong
incomeSyncManager.uploadLocalIncomes(adminUserId, adminRole, emptySet())

// ✅ Correct
incomeSyncManager.uploadLocalIncomes(adminUserId, adminRole, null)
```

---

## Best Practices

### 1. Always Check Permissions First

```kotlin
val permissionResult = syncPermissionManager.checkSyncPermission(
    authenticatedUserId, userRole, targetUserIds
)
if (!permissionResult.canSync) {
    showError(permissionResult.reason)
    return
}
```

### 2. Handle Errors Gracefully

```kotlin
try {
    incomeSyncManager.uploadLocalIncomes(userId, userRole)
} catch (e: Exception) {
    Log.e(TAG, "Sync failed", e)
    showErrorToUser(e.message)
}
```

### 3. Show Progress to Users

```kotlin
// Use StateFlow or LiveData
_syncProgress.value = "Syncing incomes..."
incomeSyncManager.uploadLocalIncomes(userId, userRole)
_syncProgress.value = "Syncing expenses..."
expenseSyncManager.uploadLocalExpenses(userId, userRole)
_syncProgress.value = "Complete!"
```

### 4. Sync in Background

```kotlin
// Use WorkManager for background sync
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val userId = inputData.getString("userId") ?: return Result.failure()
            val userRole = Role.valueOf(inputData.getString("userRole") ?: "EMPLOYEE")
            
            incomeSyncManager.uploadLocalIncomes(userId, userRole)
            expenseSyncManager.uploadLocalExpenses(userId, userRole)
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

---

## Migration Checklist

- [ ] Update all `uploadLocalIncomes` calls to include `userRole`
- [ ] Update all `downloadRemoteIncomes` calls to include `userRole`
- [ ] Update all `uploadLocalExpenses` calls to include `userRole`
- [ ] Update all `downloadRemoteExpenses` calls to include `userRole`
- [ ] Add `AuthRepository` dependency where needed
- [ ] Test with both ADMIN and EMPLOYEE roles
- [ ] Update UI to show admin-only sync options
- [ ] Add permission checks in ViewModels
- [ ] Update error handling for permission denied
- [ ] Test multi-user sync scenarios

---

## Support

For questions or issues:
1. Check logs with `adb logcat | grep "SyncManager"`
2. Review RBAC_SYNC_REFACTORING.md for detailed documentation
3. Check Permission and RolePermissions classes for current permissions

---

**Last Updated**: January 8, 2026  
**Version**: 1.0.0
