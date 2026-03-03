# Architecture Diagram - Permission-Based Sync System

## Dependency Graph (Post-Fix)

```
┌─────────────────────────────────────────────────────────────┐
│                     Application Layer                        │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │          EnhancedSyncManager                         │   │
│  │  - syncExpenses()                                    │   │
│  │  - syncIncomes()                                     │   │
│  │  - syncCategories()                                  │   │
│  └──────────┬─────────────────────┬────────────────────┘   │
│             │                     │                          │
└─────────────┼─────────────────────┼──────────────────────────┘
              │                     │
              ▼                     ▼
┌─────────────────────┐   ┌─────────────────────┐
│  ExpenseSyncManager │   │  IncomeSyncManager  │
│                     │   │                     │
│  - syncQueryStrategy├──►│  - syncQueryStrategy│
│                     │   │                     │
│  - download()       │   │  - download()       │
│  - upload()         │   │  - upload()         │
└─────────────────────┘   └─────────────────────┘
           │                        │
           └────────────┬───────────┘
                        │ injects
                        ▼
          ┌──────────────────────────────┐
          │    SyncQueryStrategy         │ ← Interface
          │  (PermissionBased Impl)      │
          │                              │
          │  - buildDownloadQuery()      │
          │  - shouldFilterByUserId()    │
          └───────────────┬──────────────┘
                          │ injects
                          ▼
          ┌──────────────────────────────┐
          │  CheckPermissionUseCase      │
          │                              │
          │  - invoke(Permission)        │
          └───────────────┬──────────────┘
                          │ uses
                          ▼
          ┌──────────────────────────────┐
          │     RolePermissions          │
          │  (Static Permission Map)     │
          │                              │
          │  ADMIN: SYNC_ALL_USERS_DATA  │
          │  EMPLOYEE: SYNC_OWN_DATA     │
          └──────────────────────────────┘
```

## Query Building Flow

```
┌───────────────────────────────────────────────────────────┐
│ 1. Sync Triggered                                         │
│    ExpenseSyncManager.downloadRemoteExpenses(userId)      │
└───────────────────┬───────────────────────────────────────┘
                    │
                    ▼
┌───────────────────────────────────────────────────────────┐
│ 2. Build Base Query                                       │
│    val baseQuery = firestore.collection("expenses")       │
└───────────────────┬───────────────────────────────────────┘
                    │
                    ▼
┌───────────────────────────────────────────────────────────┐
│ 3. Apply Permission Filter (Strategy Pattern)             │
│    val query = syncQueryStrategy.buildDownloadQuery(      │
│        baseQuery, userId                                  │
│    )                                                      │
└───────────────────┬───────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
        ▼                       ▼
┌──────────────────┐   ┌──────────────────┐
│ ADMIN Path       │   │ EMPLOYEE Path    │
│                  │   │                  │
│ Permission:      │   │ Permission:      │
│ SYNC_ALL_USERS   │   │ SYNC_OWN_DATA    │
│                  │   │                  │
│ Return:          │   │ Return:          │
│ baseQuery        │   │ baseQuery        │
│ (no filter)      │   │ .whereEqualTo(   │
│                  │   │   "userId",      │
│                  │   │   userId         │
│                  │   │ )                │
└──────────────────┘   └──────────────────┘
        │                       │
        └───────────┬───────────┘
                    │
                    ▼
┌───────────────────────────────────────────────────────────┐
│ 4. Execute Query                                          │
│    val snapshot = query.get().await()                     │
│                                                           │
│ ADMIN: Gets ALL users' expenses                          │
│ EMPLOYEE: Gets ONLY own expenses                         │
└───────────────────────────────────────────────────────────┘
```

## User Switching Flow

```
┌─────────────────────────────────────────────────────────┐
│ Session Start: Admin Logged In                          │
└───────────────────┬─────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────┐
│ Sync Operation                                          │
│ - syncQueryStrategy.buildDownloadQuery()                │
│ - CheckPermissionUseCase(SYNC_ALL_USERS_DATA)          │
│ - AuthRepository.getUserRole() → ADMIN                 │
│ - RolePermissions.hasPermission(ADMIN, ...) → true     │
│ → Returns unfiltered query                             │
└───────────────────┬─────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────┐
│ Admin Logs Out                                          │
│ - AuthRepository.logout()                               │
│ - appPreferences.removeUserType()                       │
│ - SyncQueryStrategy instance still exists (singleton)   │
└───────────────────┬─────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────┐
│ Employee Logs In                                        │
│ - AuthRepository.loginUser(employee)                    │
│ - appPreferences.setUserType("employee")                │
└───────────────────┬─────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────┐
│ Sync Operation (Same Strategy Instance!)                │
│ - syncQueryStrategy.buildDownloadQuery()                │
│ - CheckPermissionUseCase(SYNC_ALL_USERS_DATA)          │
│ - AuthRepository.getUserRole() → EMPLOYEE               │
│ - RolePermissions.hasPermission(EMPLOYEE, ...) → false │
│ → Returns query with userId filter                     │
└─────────────────────────────────────────────────────────┘

Result: Same strategy, different behavior based on current user!
```

## Dependency Cycle - Before vs After

### BEFORE (Circular Dependency ❌)

```
EnhancedSyncManager
    ↓ injects
ExpenseSyncManager
    ↓ injects
UserBehaviorFactory
    ↓ injects
AdminBehaviorStrategy
    ↓ injects
CategoriesInitStep
    ↓ injects
EnhancedSyncManager ← CYCLE DETECTED!
```

### AFTER (No Cycle ✅)

```
EnhancedSyncManager
    ↓ injects
ExpenseSyncManager
    ↓ injects
SyncQueryStrategy
    ↓ injects
CheckPermissionUseCase
    ↓ injects
AuthRepository
    ↓ reads
AppPreferences
    ↓
(no cycle back to EnhancedSyncManager)

AND separately:

UserBehaviorFactory
    ↓ injects
AdminBehaviorStrategy
    ↓ injects
CategoriesInitStep
    ↓ injects
EnhancedSyncManager

(UserBehaviorFactory used for initialization only,
 not for sync query building)
```

## Key Design Principles Applied

### 1. Dependency Inversion Principle (DIP)
```
ExpenseSyncManager depends on SyncQueryStrategy (abstraction)
Not on PermissionBasedSyncQueryStrategy (concrete)
→ Easy to swap implementations, test with mocks
```

### 2. Single Responsibility Principle (SRP)
```
SyncQueryStrategy: Query building with permissions
ExpenseSyncManager: Data synchronization logic
CheckPermissionUseCase: Permission evaluation
→ Each class has ONE reason to change
```

### 3. Open/Closed Principle (OCP)
```
Adding new roles? 
→ Update RolePermissions map only
→ No changes to sync managers or strategy
```

### 4. Strategy Pattern
```
Behavior (query filtering) varies by user role
→ Encapsulate in strategy
→ Select strategy at runtime based on permissions
```

## Summary

✅ **Compile-time**: No dependency cycle
✅ **Runtime**: Dynamic permission-based filtering
✅ **User switching**: Handled automatically
✅ **Testability**: Mock strategy for unit tests
✅ **Maintainability**: Clear separation of concerns
✅ **Extensibility**: Easy to add new roles/permissions

