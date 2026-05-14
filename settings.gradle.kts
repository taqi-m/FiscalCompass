pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

rootProject.name = "FiscalCompass"
include(":app")
include(":core:common")
include(":domain")
include(":data")
include(":data:database")
include(":data:network")
include(":ui")
include(":features:auth")
include(":features:home")
include(":features:transactions")
include(":features:analytics")
include(":features:people")
include(":features:categories")
include(":features:settings")
include(":features:search")
