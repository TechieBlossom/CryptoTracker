package com.techieblossom.cryptotracker.domain.model

/**
 * Full 24h detail for a single Binance trading pair (from /ticker/24hr?symbol=...).
 *
 * Trading-native fields only: no logo, market cap, ATH, or description (Binance has none).
 */
data class CoinDetail(
    val symbol: String,          // "BTCUSDT"
    val baseAsset: String,       // "BTC"
    val quoteAsset: String,      // "USDT"
    val currentPrice: Double?,   // lastPrice
    val priceChangePercentage24h: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val volume: Double?,         // 24h base-asset volume
    val quoteVolume: Double?,    // 24h quote-asset volume
)
