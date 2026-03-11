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
