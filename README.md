[![Release](https://img.shields.io/badge/Release-v1.0.0-2ea44f?style=for-the-badge)](https://github.com/taqi-m/FiscalCompass/releases/tag/v1.0.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21-8E24AA?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2025+-43A047?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-FFC107?style=for-the-badge)](LICENSE)

# Fiscal Compass

## Overview

Fiscal Compass is an Android finance management application built with **Kotlin** and **Jetpack Compose** that helps users track their expenses and income. The app provides features for budget management, transaction categorization, cloud synchronization, and financial analytics — all wrapped in a modern Material 3 UI.

## Features

- **User Authentication** — Secure login and registration via Firebase Auth with role-based access control (Admin / Employee)
- **Expense & Income Tracking** — Add, update, and delete transactions with date, amount, category, and notes
- **Category Management** — Create and manage custom categories for organized transaction tracking
- **Search & Filtering** — Advanced search across transactions with category and date filters
- **Multi-User Support** — Admin and employee roles with permission-based feature gating
- **Cloud Sync** — Automatic and manual Firestore synchronization with offline-first Room database
- **Analytics & Charts** — Interactive financial visualizations and monthly reports
- **In-App Updates** — Self-update system via GitHub Releases with SemVer comparison
- **Personalized Settings** — DataStore-backed user preferences and app configuration

## Technology Stack

[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%26%20Auth-FFA000?style=for-the-badge&logo=firebase&logoColor=white)](https://firebase.google.com)
[![Hilt](https://img.shields.io/badge/Hilt-DI-1976D2?style=for-the-badge&logo=dagger&logoColor=white)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/Room-Database-00796B?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)

| Category | Technologies |
|---|---|
| **Language** | Kotlin 2.1.21 |
| **UI** | Jetpack Compose · Material 3 |
| **Architecture** | Clean Architecture · MVVM · Repository Pattern |
| **DI** | Hilt (Dagger) |
| **Local Storage** | Room · DataStore Preferences |
| **Backend** | Firebase Auth · Firestore · Cloud Functions · Crashlytics · Analytics |
| **Networking** | OkHttp |
| **Image Loading** | Coil 3 |
| **Serialization** | Kotlinx Serialization · Gson |
| **Charts** | dautovicharis/Charts 2.0 |
| **Async** | Kotlin Coroutines · Flow |

## Project Structure

The project follows a Clean Architecture approach with the following main packages:

- **data**: Contains repositories, data models, and data sources
  - **datasource**: Local and remote data sources
  - **model**: Data models
  - **repository**: Implementation of domain repositories
- **domain**: Business logic and interfaces
  - **model**: Domain models
  - **repository**: Repository interfaces
  - **usecase**: Business logic use cases
- **di**: Dependency injection modules
- **ui**: User interface components
- **presentation**: Views, layouts, and presentation logic

## Getting Started

### Prerequisites

- Android Studio Ladybug or later
- JDK 11+
- Android SDK with API 36 (compile) and minimum API 25
- A Firebase project with `google-services.json`

### Download

Download the latest APK from the [**Releases**](https://github.com/taqi-m/FiscalCompass/releases) page.

### Build from Source

1. Clone the repository:
```bash
git clone https://github.com/taqi-m/FiscalCompass.git
```

2. Open the project in Android Studio

3. Create a Firebase project and add the `google-services.json` file to the `app/` module

4. Build and run:
```bash
# Dev build (uses Firebase Emulator Suite)
./gradlew assembleDevDebug

# Production release build
./gradlew assembleProdRelease
```

## Versioning

This project follows [**Semantic Versioning 2.0.0**](https://semver.org). Version is centralized in `gradle.properties`:

```
VERSION_MAJOR.VERSION_MINOR.VERSION_PATCH  →  e.g. 1.0.0
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

*Last updated: 2026-03-12 by [taqi-m](https://github.com/taqi-m)*
