plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.devtools.ksp)
}

android {
    signingConfigs {
        create("test-release") {
            storeFile = file("testConfig.jks")
            storePassword = "testConfig"
            keyAlias = "testConfig"
            keyPassword = "testConfig"
        }
    }
    namespace = "com.fiscal.compass"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fiscal.compass"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField("Boolean", "USE_EMULATOR", "true")
            buildConfigField("String", "EMULATOR_HOST", "\"localhost\"")
            // Match the ports from your emulator output
            buildConfigField("int", "AUTH_EMULATOR_PORT", "9099")
            buildConfigField("int", "FIRESTORE_EMULATOR_PORT", "8080")
            buildConfigField("int", "STORAGE_EMULATOR_PORT", "9199")
            buildConfigField("int", "FUNCTIONS_EMULATOR_PORT", "5001")



            resValue("string", "app_name", "FiscalCompass Dev")
        }

        create("prod") {
            dimension = "environment"

            buildConfigField("Boolean", "USE_EMULATOR", "false")
            buildConfigField("String", "EMULATOR_HOST", "\"\"")
            buildConfigField("int", "FIRESTORE_EMULATOR_PORT", "0")
            buildConfigField("int", "AUTH_EMULATOR_PORT", "0")

            resValue("string", "app_name", "FiscalCompass")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("test-release")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.hilt.android)
    implementation(libs.androidx.compose.runtime)
    debugImplementation(libs.ui.tooling)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.animation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.android.material)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.charts)

    //Room Libraries
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    //Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    //Coil dependencies
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}