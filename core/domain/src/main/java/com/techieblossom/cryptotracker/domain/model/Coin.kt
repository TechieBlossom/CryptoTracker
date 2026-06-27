package com.techieblossom.cryptotracker.domain.model

/**
 * A Binance trading pair (e.g. BTCUSDT) as shown in the market list.
 *
 * Binance is an exchange, not a coin-info source: there are no logos, market caps, or
 * descriptions. The unit here is a symbol split into base/quote assets (BTC / USDT).
 */
data class Coin(
    val symbol: String,          // "BTCUSDT" — primary id
    val baseAsset: String,       // "BTC"
    val quoteAsset: String,      // "USDT"
    val currentPrice: Double,    // lastPrice
    val priceChangePercentage24h: Double?,
    val quoteVolume: Double,     // 24h quote volume — used for sorting + display
)
