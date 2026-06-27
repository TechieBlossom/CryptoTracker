// build-logic is a separate Gradle build, included via includeBuild("build-logic") in the
// root settings. Its only job is to compile + publish our convention plugins.
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    // Reuse the ROOT project's version catalog so plugins read libs.* versions from one place.
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
