import com.android.build.api.dsl.LibraryExtension
import com.techieblossom.cryptotracker.buildlogic.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * `cryptotracker.android.library.compose` — adds Compose to a library module
 * (designsystem, features). Verbatim NiA structure. A UI library applies BOTH the base
 * library plugin and this one (or the feature plugin, which bundles them).
 */
class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}
