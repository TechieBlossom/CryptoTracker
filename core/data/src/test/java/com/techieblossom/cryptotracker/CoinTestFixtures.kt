package com.techieblossom.cryptotracker

import com.techieblossom.cryptotracker.data.remote.dto.Ticker24hrDto

fun fakeTicker24hrDto(
    symbol: String = "BTCUSDT",
    lastPrice: String = "70712.00000000",
    priceChangePercent: String = "-0.682",
    highPrice: String = "71250.00000000",
    lowPrice: String = "69800.00000000",
    volume: String = "12345.678",
    quoteVolume: String = "873000000.00",
) = Ticker24hrDto(
    symbol = symbol,
    lastPrice = lastPrice,
    priceChangePercent = priceChangePercent,
    highPrice = highPrice,
    lowPrice = lowPrice,
    volume = volume,
    quoteVolume = quoteVolume,
)
