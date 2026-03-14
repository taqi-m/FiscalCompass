# 🚀 FiscalCompass v1.0.1

> **Patch Release** — Critical Firestore schema stability fix for release builds.

---

## 🐛 Bug Fixes

### 🔥 Critical: Firestore Field Names Obfuscated in Release Builds
- **Root cause:** Kotlin DTO objects were passed directly to `batch.set(docRef, dto)`. R8/ProGuard renamed property names to single-letter identifiers (`a`, `b`, `c` …) in release builds, so all synced records were stored with obfuscated keys in Firestore.
- **Fixed for:** `incomes`, `expenses`, `globalPersons`, `globalCategories` collections.
- **How:** All Firestore upload paths now use explicit `Map<String, Any?>` serializers with literal canonical keys — fully obfuscation-proof.
- **Also fixed:** `PersonSyncManager` and `CategorySyncManager` download paths now use manual `DocumentSnapshot` field parsing instead of reflection-based `toObject()`.

### 🔒 ProGuard Hardening
- Added `-keep` / `-keepclassmembernames` rules for `com.fiscal.compass.data.remote.model.**`.
- Extended `@PropertyName` keep rule to cover both fields and methods.

---

## ⚠️ Data Migration Required for Existing Users

If you used **v1.0.0** in production, existing Firestore documents may have obfuscated field names. A migration script prompt is available in `markdowns/FIRESTORE_FIELD_MIGRATION_AGENT_PROMPT.md`.

---

## 📦 Build Info

- **Version Name**: `1.0.1`
- **Version Code**: `2`
- **Build Variants**: `dev` (emulator) / `prod` (live Firebase)

---

## 📥 Installation

1. Download the **APK** from the **Assets** section below.
2. Enable **Install from Unknown Sources** on your Android device.
3. Install — the in-app updater will detect this version automatically.

---

**Full Changelog**: https://github.com/taqi-m/FiscalCompass/compare/v1.0.0...v1.0.1

---

# 🚀 FiscalCompass v1.0.0

> **First Official Release** — Your personal finance management companion for Android.

---

## ✨ Highlights

This is the initial public release of FiscalCompass — an offline-first Android finance app built with Kotlin, Jetpack Compose, and Firebase.

---

## 📋 What's Included

### 🔐 Authentication & RBAC
- Firebase Authentication (email/password registration & login)
- Role-based access control — **Admin** and **Employee** user types
- Permission-based feature gating

### 💰 Transactions
- Create, edit, and delete **expense** and **income** entries
- Detailed forms with date, amount, category, and notes
- Full transaction history with **search & filter** by date and category

### 🗂️ Categories
- Custom, user-managed transaction categories
- Category-based organization and filtering

### 👥 Multi-User Management
- Admin dashboard for managing employees / team members
- User profile and job tracking

### ☁️ Cloud Sync
- Automatic and manual **Firestore** cloud synchronization
- Role-based upload permissions (admins vs employees)
- **Offline-first** — Room local database with dependency-aware sync ordering

### 📊 Analytics
- Interactive charts and financial visualizations
- Monthly income/expense breakdowns and reports

### ⚙️ Settings & Updates
- DataStore-backed user preferences
- **In-app self-update** system via GitHub Releases (SemVer-aware version comparison)

---

## 🏗️ Tech Stack

| | |
|---|---|
| **Language** | Kotlin 2.1.21 |
| **UI** | Jetpack Compose · Material 3 |
| **Architecture** | Clean Architecture · MVVM · Repository Pattern |
| **DI** | Hilt |
| **Local DB** | Room · DataStore |
| **Backend** | Firebase Auth · Firestore · Cloud Functions · Crashlytics |
| **Min SDK** | 25 (Android 7.1) |
| **Target SDK** | 36 |

---

## 📦 Build Info

- **Version Name**: `1.0.0`
- **Version Code**: `1`
- **Build Variants**: `dev` (emulator) / `prod` (live Firebase)
- **Signing**: R8 minification + resource shrinking enabled on release builds

---

## 📥 Installation

1. Download the **APK** from the **Assets** section below.
2. Enable **Install from Unknown Sources** on your Android device.
3. Install and launch the app.

Or build from source:
```bash
git clone https://github.com/taqi-m/FiscalCompass.git
cd FiscalCompass
./gradlew assembleProdRelease
```

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

**Full Changelog**: https://github.com/taqi-m/FiscalCompass/commits/v1.0.0
