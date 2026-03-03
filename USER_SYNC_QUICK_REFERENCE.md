# User Sync Quick Reference

## Quick Integration Guide

### 1. Inject UserSyncManager
```kotlin
@Inject
lateinit var userSyncManager: UserSyncManager
```

### 2. Initialize on App Startup (Admin Mode)
```kotlin
// In MainActivity or Application class
lifecycleScope.launch {
    if (userRole == Role.ADMIN) {
        userSyncManager.initializeAdminSync(userRole)
    }
}
```

### 3. Key Methods

| Method | Purpose | Returns |
|--------|---------|---------|
| `syncAllUsers(forceRefresh: Boolean)` | Fetch all users from Firebase | `SyncResult` |
| `syncUser(userId: String)` | Fetch single user | `SyncResult` |
| `getAllLocalUserIds()` | Get all user IDs from local DB | `List<String>` |
| `getAllLocalUsers()` | Get all users from local DB | `List<UserEntity>` |
| `hasLocalUsers()` | Check if users exist locally | `Boolean` |
| `initializeAdminSync(userRole: Role)` | Auto-sync for admin | `Boolean` |

### 4. Sync Result Handling
```kotlin
when (val result = userSyncManager.syncAllUsers()) {
    is UserSyncManager.SyncResult.Success -> {
        // result.users contains List<UserEntity>
    }
    is UserSyncManager.SyncResult.Error -> {
        // result.message contains error string
    }
}
```

## How It Fixes the Problem

### Before
```
App Launch (Admin) → Income Sync → getAllUnsyncedUserIds() → Empty List ❌
```

### After
```
App Launch (Admin) 
  → UserSyncManager.initializeAdminSync() 
  → Users stored in local DB ✓
  → Income Sync 
  → getAllUnsyncedUserIds() 
  → Returns actual user IDs ✓
```

## Files Modified

1. ✅ `UserRepository.kt` - Added 3 methods
2. ✅ `UserRepositoryImpl.kt` - Implemented Firebase integration
3. ✅ `UserDao.kt` - Added batch operations
4. ✅ `UserMapper.kt` - Created mapper functions
5. ✅ `UserSyncManager.kt` - NEW sync manager

## Database Impact

- **Table:** `users`
- **Operation:** Batch INSERT with REPLACE strategy
- **Timing:** Once on admin login (cached thereafter)
- **Size:** All users from Firebase "users" collection

## Performance Notes

- ⚡ Sync skipped if users already exist (unless `forceRefresh = true`)
- ⚡ Batch insert for efficiency
- ⚡ Happens asynchronously (suspend function)
- ⚡ Cached locally for offline access

## Common Usage Patterns

### Pattern 1: App Initialization
```kotlin
if (isAdmin) {
    userSyncManager.initializeAdminSync(Role.ADMIN)
}
```

### Pattern 2: Manual Refresh
```kotlin
button.setOnClickListener {
    lifecycleScope.launch {
        userSyncManager.syncAllUsers(forceRefresh = true)
    }
}
```

### Pattern 3: Get Users for Sync
```kotlin
val userIds = userSyncManager.getAllLocalUserIds()
incomeSyncManager.uploadLocalIncomes(
    authenticatedUserId = currentUserId,
    userRole = Role.ADMIN,
    targetUserIds = userIds.toSet()
)
```

## Testing Checklist

- [ ] Admin login triggers user sync
- [ ] Users visible in local database
- [ ] Income sync finds user IDs
- [ ] Expense sync finds user IDs
- [ ] Non-admin doesn't sync users
- [ ] Subsequent launches skip sync
- [ ] Force refresh works

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Users not syncing | Check Firebase connection and "users" collection |
| Empty user list | Verify initializeAdminSync called before income/expense sync |
| Duplicate users | Normal - using REPLACE strategy |
| Slow initialization | Check network speed and number of users |

---
**For detailed information, see:** `USER_SYNC_IMPLEMENTATION.md`
