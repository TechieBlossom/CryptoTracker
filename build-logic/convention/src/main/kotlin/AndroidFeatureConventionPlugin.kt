import com.techieblossom.cryptotracker.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

/**
 * `cryptotracker.android.feature` — the one-stop plugin every feature module applies.
 * Mirrors NiA's AndroidFeatureImplConventionPlugin (collapsed: we don't split api/impl).
 * Bundles library + compose + hilt + serialization, then auto-adds the deps every feature needs:
 * core:domain (entities/repo interface), core:designsystem (theme/format/HtmlText), Compose
 * navigation + lifecycle + Hilt-navigation, and Material3.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "cryptotracker.android.library")
            apply(plugin = "cryptotracker.android.library.compose")
            apply(plugin = "cryptotracker.android.hilt")
            // Features declare @Serializable nav routes, so each needs the serialization plugin.
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:designsystem"))

                "implementation"(libs.findLibrary("androidx-compose-material3").get())
                "implementation"(libs.findLibrary("androidx-lifecycle-runtime-compose").get())
                "implementation"(libs.findLibrary("navigation-compose").get())
                "implementation"(libs.findLibrary("hilt-navigation-compose").get())
                "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}
