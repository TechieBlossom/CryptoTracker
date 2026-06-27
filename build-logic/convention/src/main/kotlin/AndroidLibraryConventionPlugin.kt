import com.android.build.api.dsl.LibraryExtension
import com.techieblossom.cryptotracker.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * `cryptotracker.android.library` — base for every Android library module (core:* / feature:*).
 * Mirrors NiA's AndroidLibraryConventionPlugin, trimmed of lint/flavors/resource-prefix/
 * gradle-managed-devices.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            // No org.jetbrains.kotlin.android: AGP 9 built-in Kotlin is enabled by default.

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.testInstrumentationRunner =
                    "androidx.test.runner.AndroidJUnitRunner"
                // No targetSdk: it's an app-level concept in AGP 9; libraries inherit it.
            }
        }
    }
}
