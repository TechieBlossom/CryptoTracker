plugins {
    alias(libs.plugins.cryptotracker.android.library)
    alias(libs.plugins.cryptotracker.android.library.compose)
}

android {
    namespace = "com.techieblossom.cryptotracker.designsystem"
}

dependencies {
    // PURE Compose/UI utilities (theme, formatters, HtmlText). No domain, no Hilt.
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.core.ktx) // HtmlCompat in HtmlText
}
