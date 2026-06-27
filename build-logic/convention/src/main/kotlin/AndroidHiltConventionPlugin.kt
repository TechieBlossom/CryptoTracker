import com.techieblossom.cryptotracker.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * `cryptotracker.android.hilt` — for any module that needs Hilt DI.
 * Mirrors NiA's HiltConventionPlugin: applies KSP, adds the hilt compiler, and applies the
 * Hilt Gradle plugin + runtime once the module is recognised as an Android module.
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")

            dependencies {
                "ksp"(libs.findLibrary("hilt-compiler").get())
            }

            // Apply the Hilt Gradle plugin + Android runtime only for Android modules.
            pluginManager.withPlugin("com.android.base") {
                apply(plugin = "dagger.hilt.android.plugin")
                dependencies {
                    "implementation"(libs.findLibrary("hilt-android").get())
                }
            }
        }
    }
}
