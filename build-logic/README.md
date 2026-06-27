# build-logic — Convention Plugins

> How BarakaDroid's Gradle modules stay configured consistently without each one
> repeating 40+ lines of build config.

## The core idea in one sentence

**`build-logic` is a mini-project that produces *plugins*, and your modules apply those
plugins to get configured — instead of each module writing its own config.**

Everything below is just the plumbing that makes that sentence work.

---

## The mental model: three separate "builds"

A multi-module Android project actually contains **three distinct Gradle builds** that don't
see each other's internals:

```
BarakaDroid/
│
├── build-logic/          ← BUILD #1: "the factory that makes plugins"
│   └── convention/       ←   a normal Kotlin project; its output is a JAR of plugin classes
│
├── settings.gradle.kts   ← BUILD #2: "the main app build" (root)
├── app/                  ←   a module
│   └── (later) core/, feature/  ← more modules
│
└── gradle/libs.versions.toml  ← shared by both builds (the "versions phone book")
```

- **Build #1 (`build-logic`)** compiles *first*, before anything else. Its job is to produce
  plugin classes like `AndroidApplicationConventionPlugin`. It has its own `settings.gradle.kts`
  because it is genuinely its own build.
- **Build #2 (root)** is the actual app. It *consumes* the plugins Build #1 produced.
- They're linked by **one line**: `includeBuild("build-logic")` in the root
  `settings.gradle.kts`. That line says "before doing anything, build that other thing, and let
  me use the plugins it makes."

**Why separate them?** Plugins must be **compiled before** the modules that use them. You can't
configure `:app` with a plugin that hasn't been built yet. `includeBuild` enforces that ordering
automatically.

---

## How a plugin gets from "a Kotlin class" to "applied to your app"

Follow one plugin — `barakadroid.android.application` — through its whole lifecycle:

**Step 1 — Write the class** (`convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt`):
```kotlin
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) { /* configure the project */ }
}
```
Just Kotlin code implementing Gradle's `Plugin<Project>` interface. `apply(target)` runs against
whatever module applies it — `target` *is* that module (`:app`).

**Step 2 — Give it an ID** (`convention/build.gradle.kts`):
```kotlin
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "barakadroid.android.application"                       // ← the public name
            implementationClass = "AndroidApplicationConventionPlugin"   // ← which class runs
        }
    }
}
```
The crucial mapping: **string ID → Kotlin class**. When any module says "apply
`barakadroid.android.application`", Gradle looks up this table and runs that class's `apply()`.

**Step 3 — Make the ID typo-proof** (`gradle/libs.versions.toml`):
```toml
[plugins]
barakadroid-android-application = { id = "barakadroid.android.application" }
```
Optional sugar. Lets you write `alias(libs.plugins.barakadroid.android.application)` instead of
the raw string. The catalog maps a Kotlin-friendly accessor to the same ID.

**Step 4 — A module applies it** (`app/build.gradle.kts`):
```kotlin
plugins {
    alias(libs.plugins.barakadroid.android.application)
}
```
Now, when Gradle configures `:app`, it runs `AndroidApplicationConventionPlugin.apply(target = :app)`,
which sets `compileSdk`, `minSdk`, applies AGP, etc.

The chain:
```
your Kotlin class
   ↓ register() gives it an ID
ID string "barakadroid.android.application"
   ↓ catalog maps it to a typed accessor
libs.plugins.barakadroid.android.application
   ↓ app applies it
app gets configured
```

---

## What a plugin *does* when it runs

Inside `apply(target)`, two kinds of things happen:

**1. It applies *other* plugins** (composition):
```kotlin
apply(plugin = "com.android.application")   // brings in AGP
```
Convention plugins are mostly **bundlers** — they apply the real plugins (AGP, Compose, KSP, Hilt)
so individual modules don't have to.

**2. It configures the extensions those plugins registered:**
```kotlin
extensions.configure<ApplicationExtension> {
    compileSdk = 36
    defaultConfig.apply { minSdk = 26 }
}
```
When AGP is applied, it registers an `android { }` block (an `ApplicationExtension`). The plugin
reaches into that same extension *in code* and sets values. **`android { compileSdk = 36 }` in a
build file and `extensions.configure<ApplicationExtension> { compileSdk = 36 }` in a plugin are
the exact same thing** — one in DSL, one in Kotlin.

That's the whole trick: anything you'd normally type in a module's `android { }` block, the
plugin does for you, once, for every module.

---

## The files in this module

| File | Role |
|---|---|
| `settings.gradle.kts` | Defines the included build; loads the root `libs` catalog so plugins can read versions |
| `convention/build.gradle.kts` | JDK 17 target; plugin classpath (`gradle-api`, `tools:common`, compose/kotlin/ksp); registers every plugin (ID → class) |
| `convention/src/main/kotlin/com/baraka/droid/KotlinAndroid.kt` | **Shared helper** — SDK/JDK/compiler baseline (`compileSdk 36`, `minSdk 26`, JDK 11, compiler args). One source of truth. |
| `…/com/baraka/droid/AndroidCompose.kt` | **Shared helper** — enables Compose + BOM/tooling deps |
| `…/com/baraka/droid/ProjectExtensions.kt` | **Shared helper** — the `Project.libs` catalog accessor |
| `…/AndroidApplicationConventionPlugin.kt` | `:app` base config |
| `…/AndroidApplicationComposeConventionPlugin.kt` | adds Compose to `:app` |
| `…/AndroidLibraryConventionPlugin.kt` | base config for every library module |
| `…/AndroidLibraryComposeConventionPlugin.kt` | adds Compose to a library |
| `…/AndroidHiltConventionPlugin.kt` | KSP + Hilt (Hilt runtime added only for Android modules) |
| `…/AndroidFeatureConventionPlugin.kt` | bundles library + compose + hilt and auto-adds core deps (inert until the first feature exists) |
| `…/JvmLibraryConventionPlugin.kt` | pure Kotlin/JVM (no Android) — for `core:domain` |

### Plugins vs. helpers — why two groups?

| | Plugin classes (`*ConventionPlugin.kt`) | Helpers (`com/baraka/droid/*.kt`) |
|---|---|---|
| **Package** | default (no package) | `com.baraka.droid` |
| **Why** | Gradle resolves `implementationClass` by simple name; default package is the convention | normal namespaced Kotlin |
| **Role** | the *entry points* Gradle calls | *shared logic* multiple plugins reuse |

`configureKotlinAndroid()` lives in a helper because **both** the application and library plugins
call it — that's the DRY win. Change `minSdk` once in `KotlinAndroid.kt` and every module follows.
Plugins are thin; helpers hold the shared substance.

---

## Where `libs.versions.toml` fits (and why build-logic reads it too)

The catalog is the **single phone book of versions**, with two consumers:

1. **Modules** read it for dependencies: `implementation(libs.hilt.android)`.
2. **`build-logic` itself** reads it — `libs.findLibrary("hilt-android")` inside the Hilt plugin —
   which is why `build-logic/settings.gradle.kts` contains:
   ```kotlin
   versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
   ```
   Build #1 and Build #2 are separate, so build-logic must be *told* to load the same catalog file
   (`../` points back up to the root). Without that line, `libs` wouldn't exist inside the plugins.

The catalog is the one thing genuinely **shared across both builds**.

---

## What happens on `./gradlew :app:assembleDebug`

```
1. Gradle reads root settings.gradle.kts
2. Sees includeBuild("build-logic")  → builds build-logic FIRST
       → compiles the plugin classes into a JAR
       → reads the gradlePlugin{} table: ID → class mapping
3. Configures :app
       → app/build.gradle.kts: apply barakadroid.android.application
       → Gradle finds that ID in the table, runs the class's apply()
       → apply() applies AGP + sets compileSdk/minSdk via the helper
       → second plugin (…application.compose) enables Compose + adds deps
4. :app is fully configured  → compiles & assembles the APK
```

The payoff — a new library module's **entire** `build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.barakadroid.android.library)
    alias(libs.plugins.barakadroid.android.library.compose)
}
android { namespace = "com.baraka.droid.core.designsystem" }
```
Four lines, and it inherits the identical SDK, JDK, compiler flags, and Compose setup as every
other module. That consistency, enforced in code, is the entire reason multi-module Android uses
this pattern.

---

## AGP 9 gotchas (why this code looks the way it does)

These adaptations vs. older Now in Android / tutorials are **not** in any single official doc —
they come from the AGP 9.0 release notes + the Kotlin "Update your projects for AGP 9" blog. We're
on **AGP 9.2.1**:

1. **`CommonExtension` lost its type parameters.** Use `CommonExtension`, not
   `CommonExtension<*, *, *, *, *, *>` (the AGP 8.x shape).
2. **Config blocks use the accessor + `.apply { }` form** on a `CommonExtension`:
   `defaultConfig.apply { minSdk = 26 }`, `buildFeatures.apply { compose = true }` — *not* the
   lambda-DSL `defaultConfig { }` / `buildFeatures { }` form.
3. **Built-in Kotlin is enabled by default.** Applying `com.android.application` /
   `com.android.library` already registers the `kotlin` extension, so we do **NOT** apply
   `org.jetbrains.kotlin.android` ourselves (it fails with *"extension 'kotlin' already
   registered"*). Only the pure-JVM plugin applies `org.jetbrains.kotlin.jvm`, because there's no
   AGP there to bring Kotlin in.

---

## Reference

This module mirrors the **Now in Android** sample's `build-logic`
(<https://github.com/android/nowinandroid>), trimmed of NiA-internal concerns (Spotless, lint,
Jacoco, Firebase, Room, flavors, core-library desugaring) and adapted for AGP 9.2.1.

Architecture rationale: [Guide to Android app modularization](https://developer.android.com/topic/modularization).
