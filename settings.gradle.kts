pluginManagement {
    // Compile + contribute our convention plugins (cryptotracker.*) from the build-logic build.
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Lets modules reference each other as projects.core.domain instead of project(":core:domain").
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CryptoTracker"
include(":app")
include(":core:domain")
include(":core:network")
include(":core:data")
include(":core:designsystem")
include(":feature:coinlist")
include(":feature:coindetail")
