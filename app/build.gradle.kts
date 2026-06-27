plugins {
    alias(libs.plugins.cryptotracker.android.application)
    alias(libs.plugins.cryptotracker.android.application.compose)
    alias(libs.plugins.cryptotracker.android.hilt)
    // MainActivity's NavHost references the features' @Serializable routes.
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.techieblossom.cryptotracker"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // Feature modules wired into the NavHost + the shared design system.
    implementation(projects.feature.coinlist)
    implementation(projects.feature.coindetail)
    implementation(projects.core.designsystem)
    // app's nav callback (onCoinClick) handles the domain Coin type, which the features
    // expose only via `implementation` — so app needs core:domain on its own classpath.
    implementation(projects.core.domain)
    // The composition root: app must depend on the IMPLEMENTATION modules so their Hilt
    // @Modules (RepositoryModule/DataModule/NetworkModule) are on the SingletonComponent's
    // graph. Features depend only on core:domain (the interface), never on core:data — so
    // the concrete CoinRepository binding is wired here, at the app, not in the features.
    implementation(projects.core.data)
    implementation(projects.core.network)

    // App-level UI/navigation/DI glue.
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
