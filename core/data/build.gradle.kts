plugins {
    alias(libs.plugins.cryptotracker.android.library)
    alias(libs.plugins.cryptotracker.android.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.techieblossom.cryptotracker.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.network)

    implementation(libs.retrofit)
    implementation(libs.moshi)
    ksp(libs.moshi.codegen)

    implementation(libs.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
