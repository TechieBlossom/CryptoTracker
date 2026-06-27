plugins {
    alias(libs.plugins.cryptotracker.android.library)
    alias(libs.plugins.cryptotracker.android.hilt)
}

android {
    namespace = "com.techieblossom.cryptotracker.core.network"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi)
    implementation(libs.okhttp.logging)
}
