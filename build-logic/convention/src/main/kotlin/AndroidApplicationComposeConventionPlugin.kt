import com.android.build.api.dsl.ApplicationExtension
import com.techieblossom.cryptotracker.buildlogic.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

/**
 * `cryptotracker.android.application.compose` — adds Compose to an application module.
 * Verbatim NiA structure: applies the AGP application plugin (idempotent) + the Compose
 * compiler plugin, then enables Compose + shared deps. :app applies BOTH the base
 * application plugin and this one.
 */
class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}
