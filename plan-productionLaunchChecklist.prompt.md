# 🚀 FiscalCompass Production Launch Checklist

**Project:** FiscalCompass  
**Prepared:** February 17, 2026

---

## 🔴 CRITICAL - Must Fix Before Launch

### 1. Security & Signing
- [ ] **Create production signing keystore** - Currently using `testConfig.jks` with weak passwords (`testConfig`)
  - Generate new keystore with strong passwords
  - Store keystore securely (NOT in version control)
  - Back up in multiple secure locations
- [ ] **Remove test keystore** from repository (`app/testConfig.jks`)
- [ ] **Update `build.gradle.kts`** to use environment variables for signing credentials

### 2. Code Minification & Optimization
- [ ] **Enable ProGuard/R8** - Currently `isMinifyEnabled = false` in release build
- [ ] **Enable resource shrinking** (`isShrinkResources = true`)
- [ ] **Add missing ProGuard rules** for: Firebase Analytics/Crashlytics, Hilt, Gson, Compose
- [ ] **Test release build thoroughly** after enabling minification

### 3. Incomplete Code (18 TODOs found)
- [ ] **IncomeRepositoryImpl.kt** - 4 unimplemented methods
- [ ] **ExpenseRepositoryImpl.kt** - 3 unimplemented methods  
- [ ] **UserRepositoryImpl.kt** - 5 unimplemented methods
- [ ] **JobsViewModel.kt** - 2 TODO events
- [ ] **Remove `QUICK_START.kt`** sample code

---

## 🟠 HIGH PRIORITY

### 4. Firebase Configuration
- [ ] Verify production Firebase project configured
- [ ] Review Firestore Security Rules for production
- [ ] Enable Firebase App Check
- [ ] Configure Crashlytics alerts

### 5. Version & Release
- [ ] Update `versionCode` and `versionName` 
- [ ] Create release notes
- [ ] Tag release in version control

### 6. API Keys & Secrets
- [ ] Review `google-services.json` for production
- [ ] Restrict API keys in Google Cloud Console
- [ ] Remove hardcoded secrets

### 7. Testing
- [ ] Run full test suite
- [ ] Manual QA on prod release build
- [ ] Test on min SDK (25) and target SDK (36)
- [ ] Test offline functionality
- [ ] Test sync with real Firebase

---

## 🟡 MEDIUM PRIORITY

### 8. App Store Requirements
- [ ] App icon ready ✅
- [ ] Create feature graphic (1024x500)
- [ ] Prepare screenshots
- [ ] Write app description
- [ ] **Create Privacy Policy** ❌ (REQUIRED - not found)
- [ ] Content rating questionnaire

### 9. Legal & Compliance
- [ ] Privacy Policy (required for data collection)
- [ ] Terms of Service
- [ ] GDPR compliance (if EU users)

### 10. Performance
- [ ] Profile for memory leaks
- [ ] Check startup time
- [ ] Review database query performance

---

## 🟢 ALREADY DONE ✅
- ✅ Network security config (cleartext disabled in prod)
- ✅ Analytics disabled in dev flavor
- ✅ Crashlytics disabled in dev flavor
- ✅ Separate dev/prod build flavors
- ✅ ProGuard rules for Room database

---

## 📋 Pre-Release Commands

```powershell
# Build production release
.\gradlew clean assembleProdRelease

# Run tests
.\gradlew testProdReleaseUnitTest

# Generate App Bundle for Play Store
.\gradlew :app:bundleProdRelease
```

---

## 📝 Key Issues Found

1. 🔴 Test signing keystore with weak passwords in repository
2. 🔴 ProGuard/R8 disabled for release builds
3. 🔴 18 TODO/unimplemented methods in production code
4. 🟠 No Privacy Policy (required for Play Store)

---

## 🔗 Resources

- [Google Play Console](https://play.google.com/console)
- [Firebase Console](https://console.firebase.google.com)
- [Android App Bundle Requirements](https://developer.android.com/guide/app-bundle)
- [Play Store Launch Checklist](https://developer.android.com/distribute/best-practices/launch/launch-checklist)

