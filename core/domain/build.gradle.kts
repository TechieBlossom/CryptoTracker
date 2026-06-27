plugins {
    // Pure Kotlin/JVM — no Android. Entities + the CoinRepository interface; deps point INWARD here.
    alias(libs.plugins.cryptotracker.jvm.library)
}
