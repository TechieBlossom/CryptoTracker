package com.techieblossom.cryptotracker.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Enables Compose for a module and wires the shared Compose dependencies via the BOM.
 * Mirrors NiA's configureAndroidCompose, trimmed of its compose-metrics/reports/stability
 * tooling (NiA-internal). material3 + activity-compose are added in the consuming module/
 * feature plugin as needed; here we add the always-needed BOM + tooling-preview.
 *
 * AGP 9 note: `buildFeatures.apply { compose = true }` (accessor + .apply), not the
 * lambda-DSL `buildFeatures { }` form.
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            "implementation"(platform(bom))
            "androidTestImplementation"(platform(bom))
            "implementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
        }
    }
}
