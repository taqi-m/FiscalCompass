[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-8E24AA?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2021+-43A047?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-FFC107?style=for-the-badge)](LICENSE)

# Fiscal Compass

## Overview

Fiscal Compass is an Android finance management application built with Kotlin that helps users track their expenses and income. The app provides features for budget management, transaction categorization, and financial reporting.

**Status**: This project is currently under development.

## Features

- **User Authentication**: Secure login and registration using Firebase Authentication
- **Expense Tracking**: Add, update, and delete expenses with detailed information
- **Income Management**: Track various sources of income
- **Category Management**: Organize transactions with customizable categories
- **Transaction History**: View and filter transaction history by date or category
- **Multi-user Support**: Different user types (employee, admin) with appropriate permissions
- **Local Data Storage**: Offline capability with Room database
- **User Preferences**: Personalized app settings and preferences

## Technology Stack

[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%26%20Auth-FFA000?style=for-the-badge&logo=firebase&logoColor=white)](https://firebase.google.com)
[![Hilt](https://img.shields.io/badge/Hilt-DI-1976D2?style=for-the-badge&logo=dagger&logoColor=white)](https://dagger.dev/hilt/)
[![Room](https://img.shields.io/badge/Room-Database-00796B?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)

- **Kotlin**: Modern programming language for Android development
- **MVVM Architecture**: Clean separation of concerns with Model-View-ViewModel pattern
- **Hilt**: Dependency injection for better code organization and testability
- **Room Database**: Local persistence for offline access to data
- **Firebase Authentication**: Secure user authentication
- **Firebase Firestore**: Cloud database for data synchronization
- **Coroutines & Flow**: Asynchronous programming for smooth user experience
- **Repository Pattern**: Clean abstraction of data sources

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

- Android Studio Arctic Fox or later
- Android SDK 21 or higher
- Kotlin 1.8.0 or higher
- Google Services account for Firebase

### Installation

1. Clone the repository:
```bash
git clone https://github.com/taqi-m/FiscalCompass.git
```

2. Open the project in Android Studio

3. Create a Firebase project and add the `google-services.json` file to the app module

4. Build and run the application

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

*Last updated: 2025-11-27 03:09:01 UTC by [taqi-m](https://github.com/taqi-m)*
