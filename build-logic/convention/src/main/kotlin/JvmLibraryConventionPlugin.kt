import com.techieblossom.cryptotracker.buildlogic.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * `cryptotracker.jvm.library` — pure Kotlin/JVM module, NO Android dependency.
 * Used by core:domain (entities/use cases stay framework-free, mirroring Flutter core/domain).
 * Mirrors NiA's JvmLibraryConventionPlugin, trimmed of lint/spotless/kotlin.test.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.jvm")
            configureKotlinJvm()
        }
    }
}
