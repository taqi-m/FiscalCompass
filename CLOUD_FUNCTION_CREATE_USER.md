# Firebase Cloud Function: Create User

## Overview

This document provides complete context for implementing a Firebase Cloud Function that allows admin users in the FiscalCompass Android app to create new user accounts without affecting their own authentication session.

## Problem Statement

In Firebase Authentication, when you call `createUserWithEmailAndPassword()` on the client-side, it automatically signs in the newly created user, logging out the current admin. To maintain the admin's session while creating new users, we need a server-side solution using Firebase Cloud Functions with the Admin SDK.

## Function Requirements

### Function Name
`createUser`

### Type
HTTPS Callable Function (not HTTP trigger)

### Authentication
- Must be authenticated (Firebase Auth token required)
- Caller must have `admin` role in Firestore `users` collection

### Input Parameters
```typescript
interface CreateUserRequest {
  name: string;       // Display name for the new user
  email: string;      // Email address for authentication
  password: string;   // Password (min 6 characters)
  userType: string;   // Role: "admin" or "employee"
}
```

### Output
```typescript
interface CreateUserResponse {
  success: boolean;
  uid?: string;       // Firebase Auth UID of created user
  message?: string;   // Error message if failed
}
```

## Firestore Data Structure

### Users Collection Schema
```
/users/{userId}
{
  name: string,
  email: string,
  userType: "admin" | "employee",
  createdAt?: Timestamp,
  createdBy?: string  // UID of admin who created this user
}
```

## Implementation Guide

### 1. Project Setup

```bash
# Initialize Firebase Functions in your project
firebase init functions

# Select TypeScript
# Install dependencies
cd functions
npm install
```

### 2. Required Dependencies

```json
{
  "dependencies": {
    "firebase-admin": "^11.11.0",
    "firebase-functions": "^4.5.0"
  }
}
```

### 3. Function Implementation

Create `src/index.ts`:

```typescript
import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

// Initialize Firebase Admin SDK
admin.initializeApp();

interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  userType: string;
}

interface CreateUserResponse {
  success: boolean;
  uid?: string;
  message?: string;
}

/**
 * Cloud Function to create a new user.
 * Only callable by authenticated admin users.
 */
export const createUser = functions.https.onCall(
  async (data: CreateUserRequest, context): Promise<CreateUserResponse> => {
    // 1. Authentication Check
    if (!context.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "Must be authenticated to create users."
      );
    }

    const callerUid = context.auth.uid;

    // 2. Authorization Check - Verify caller is admin
    try {
      const callerDoc = await admin
        .firestore()
        .collection("users")
        .doc(callerUid)
        .get();

      if (!callerDoc.exists) {
        throw new functions.https.HttpsError(
          "permission-denied",
          "User document not found."
        );
      }

      const callerData = callerDoc.data();
      if (callerData?.userType !== "admin") {
        throw new functions.https.HttpsError(
          "permission-denied",
          "Only admins can create users."
        );
      }
    } catch (error) {
      if (error instanceof functions.https.HttpsError) {
        throw error;
      }
      throw new functions.https.HttpsError(
        "internal",
        "Failed to verify admin status."
      );
    }

    // 3. Input Validation
    const { name, email, password, userType } = data;

    if (!name || typeof name !== "string" || name.trim().length === 0) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Name is required."
      );
    }

    if (!email || typeof email !== "string") {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Valid email is required."
      );
    }

    if (!password || typeof password !== "string" || password.length < 6) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Password must be at least 6 characters."
      );
    }

    const validUserTypes = ["admin", "employee"];
    const normalizedUserType = userType?.toLowerCase() || "employee";
    if (!validUserTypes.includes(normalizedUserType)) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "User type must be 'admin' or 'employee'."
      );
    }

    // 4. Create Firebase Auth User
    let userRecord: admin.auth.UserRecord;
    try {
      userRecord = await admin.auth().createUser({
        email: email,
        password: password,
        displayName: name.trim(),
        emailVerified: false,
      });
    } catch (error: any) {
      console.error("Error creating auth user:", error);
      
      if (error.code === "auth/email-already-exists") {
        throw new functions.https.HttpsError(
          "already-exists",
          "A user with this email already exists."
        );
      }
      if (error.code === "auth/invalid-email") {
        throw new functions.https.HttpsError(
          "invalid-argument",
          "Invalid email format."
        );
      }
      if (error.code === "auth/weak-password") {
        throw new functions.https.HttpsError(
          "invalid-argument",
          "Password is too weak."
        );
      }
      
      throw new functions.https.HttpsError(
        "internal",
        "Failed to create user authentication."
      );
    }

    // 5. Create Firestore User Document
    try {
      await admin
        .firestore()
        .collection("users")
        .doc(userRecord.uid)
        .set({
          name: name.trim(),
          email: email.toLowerCase(),
          userType: normalizedUserType,
          createdAt: admin.firestore.FieldValue.serverTimestamp(),
          createdBy: callerUid,
        });
    } catch (error) {
      console.error("Error creating Firestore document:", error);
      
      // Rollback: Delete the auth user if Firestore write fails
      try {
        await admin.auth().deleteUser(userRecord.uid);
      } catch (deleteError) {
        console.error("Failed to rollback auth user:", deleteError);
      }
      
      throw new functions.https.HttpsError(
        "internal",
        "Failed to create user profile."
      );
    }

    // 6. Return Success
    return {
      success: true,
      uid: userRecord.uid,
    };
  }
);
```

### 4. Firestore Security Rules

Ensure your Firestore rules allow the function to write to the users collection:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      // Allow read for authenticated users
      allow read: if request.auth != null;
      
      // Allow write only from Admin SDK (Cloud Functions)
      // Client writes are handled separately based on your existing rules
      allow write: if request.auth != null && 
                      get(/databases/$(database)/documents/users/$(request.auth.uid)).data.userType == 'admin';
    }
  }
}
```

### 5. Deployment

```bash
# Deploy only the createUser function
firebase deploy --only functions:createUser

# Or deploy all functions
firebase deploy --only functions
```

### 6. Testing with Emulator

```bash
# Start emulators
firebase emulators:start --only functions,auth,firestore

# The function will be available at:
# http://localhost:5001/YOUR_PROJECT_ID/us-central1/createUser
```

## Android Client Integration

The Android app calls this function using:

```kotlin
// In CreateUserUseCase.kt
val data = hashMapOf(
    "name" to name,
    "email" to email,
    "password" to password,
    "userType" to role.name.lowercase()
)

val result = functions
    .getHttpsCallable("createUser")
    .call(data)
    .await()

val responseData = result.data as? Map<String, Any>
val userId = responseData?.get("uid") as? String
```

## Error Handling

The function throws `HttpsError` with these codes:
- `unauthenticated` - No auth token provided
- `permission-denied` - Caller is not an admin
- `invalid-argument` - Missing or invalid input parameters
- `already-exists` - Email already registered
- `internal` - Server-side error

## Security Considerations

1. **Authentication Required**: Function checks `context.auth` before proceeding
2. **Admin Authorization**: Verifies caller's `userType` is `admin` in Firestore
3. **Input Validation**: All inputs are validated before use
4. **Rollback on Failure**: If Firestore write fails, the Auth user is deleted
5. **Lowercase Email**: Emails are normalized to lowercase for consistency

## Environment Variables (Optional)

If you need configuration:

```bash
firebase functions:config:set app.environment="production"
```

Access in code:
```typescript
const environment = functions.config().app?.environment || "development";
```

## Logging

The function logs errors to Cloud Functions logs. View with:

```bash
firebase functions:log
```

Or in Firebase Console → Functions → Logs

## Region Configuration (Optional)

By default, functions deploy to `us-central1`. To deploy to a different region:

```typescript
export const createUser = functions
  .region("europe-west1")
  .https.onCall(async (data, context) => {
    // ...
  });
```

Update Android client to use the same region:
```kotlin
val functions = FirebaseFunctions.getInstance("europe-west1")
```

---

## Summary

This Cloud Function:
1. Authenticates the caller via Firebase Auth token
2. Authorizes by checking admin role in Firestore
3. Validates all input parameters
4. Creates Firebase Auth user with Admin SDK
5. Creates corresponding Firestore user document
6. Returns the new user's UID on success
7. Handles errors gracefully with proper rollback

