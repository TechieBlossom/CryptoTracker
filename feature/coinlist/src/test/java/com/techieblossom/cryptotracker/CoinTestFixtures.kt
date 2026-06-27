package com.techieblossom.cryptotracker

import com.techieblossom.cryptotracker.domain.model.Coin

fun fakeCoin(
    symbol: String = "BTCUSDT",
    baseAsset: String = "BTC",
    quoteAsset: String = "USDT",
    currentPrice: Double = 70000.0,
    priceChangePercentage24h: Double? = -0.5,
    quoteVolume: Double = 873_000_000.0,
) = Coin(
    symbol = symbol,
    baseAsset = baseAsset,
    quoteAsset = quoteAsset,
    currentPrice = currentPrice,
    priceChangePercentage24h = priceChangePercentage24h,
    quoteVolume = quoteVolume,
)
