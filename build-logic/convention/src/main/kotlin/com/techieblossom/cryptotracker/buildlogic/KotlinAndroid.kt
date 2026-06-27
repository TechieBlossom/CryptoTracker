package com.techieblossom.cryptotracker.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Shared Android + Kotlin baseline for every Android module (app + libraries).
 *
 * Structure mirrors the official Now in Android sample
 * (build-logic/convention/.../KotlinAndroid.kt), adapted for BarakaDroid:
 *  - minSdk 26 (NiA uses 23) → core library desugaring dropped (not needed at 26).
 *  - keeps our `-Xannotation-default-target=param-property` arg from the wizard output.
 *
 * AGP 9 note: CommonExtension is non-generic, and config blocks are reached via the
 * accessor + `.apply { }` (e.g. `defaultConfig.apply { }`), NOT the lambda-DSL form
 * `defaultConfig { }` — that's the pattern NiA uses and what compiles on AGP 9.
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig.apply {
            minSdk = 26
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    configureKotlin<KotlinAndroidProjectExtension>()
}

/** Configure base Kotlin options for a pure JVM (non-Android) module — used by core:domain. */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    configureKotlin<KotlinJvmProjectExtension>()
}

/** Shared Kotlin compiler options for both Android and JVM modules. */
private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() = configure<T> {
    val compilerOptions = when (this) {
        is KotlinAndroidProjectExtension -> compilerOptions
        is KotlinJvmProjectExtension -> compilerOptions
        else -> error("Unsupported project extension $this ${T::class}")
    }
    compilerOptions.apply {
        jvmTarget.set(JvmTarget.JVM_11)
        // Applies annotation defaults to both the constructor parameter and the generated
        // property (kept identical to the wizard-generated :app config).
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}
