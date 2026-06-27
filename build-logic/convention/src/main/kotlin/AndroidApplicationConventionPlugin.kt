import com.android.build.api.dsl.ApplicationExtension
import com.techieblossom.cryptotracker.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * `cryptotracker.android.application` — base application config (no Compose).
 * Mirrors NiA's AndroidApplicationConventionPlugin, trimmed of lint/dependency-guard/
 * gradle-managed-devices/badging.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            // No org.jetbrains.kotlin.android: AGP 9 provides built-in Kotlin (enabled by
            // default). Applying it would fail with "extension 'kotlin' already registered".

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
                defaultConfig.applicationId = "com.techieblossom.cryptotracker"
                defaultConfig.versionCode = 1
                defaultConfig.versionName = "1.0"
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
        }
    }
}
