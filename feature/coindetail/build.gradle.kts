plugins {
    alias(libs.plugins.cryptotracker.android.feature)
}

android {
    namespace = "com.techieblossom.cryptotracker.feature.coindetail"
}

dependencies {
    // Coil for the async coin image in the detail header.
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
}
