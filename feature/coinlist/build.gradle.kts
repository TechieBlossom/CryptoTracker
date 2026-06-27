plugins {
    alias(libs.plugins.cryptotracker.android.feature)
}

android {
    namespace = "com.techieblossom.cryptotracker.feature.coinlist"
}

dependencies {
    // Coil for async coin images (AsyncImage in CoinCell).
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}
