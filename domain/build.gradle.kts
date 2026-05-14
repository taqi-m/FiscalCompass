plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.runtime)
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    // DI
    implementation("javax.inject:javax.inject:1")
}
