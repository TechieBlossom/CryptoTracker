package com.techieblossom.cryptotracker

import com.techieblossom.cryptotracker.data.remote.dto.CoinDto
import com.techieblossom.cryptotracker.domain.model.Coin

fun fakeCoinDto(
    id: String = "bitcoin",
    symbol: String = "btc",
    name: String = "Bitcoin",
    image: String = "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png?1696501400",
    currentPrice: Double = 70712.0,
    marketCap: Long = 1414832995358,
    marketCapRank: Int = 1,
    priceChangePercentage24h: Double = -0.68272,
) = CoinDto(
    id = id,
    name = name,
    symbol = symbol,
    image = image,
    currentPrice = currentPrice,
    marketCap = marketCap,
    marketCapRank = marketCapRank,
    priceChangePercentage24h = priceChangePercentage24h,
)

fun fakeCoin(
    id: String = "bitcoin",
    name: String = "Bitcoin",
    symbol: String = "btc",
    imageUrl: String = "https://example.com/coin.png",
    currentPrice: Double = 70000.0,
    marketCap: Long = 1400000000000,
    marketCapRank: Int = 1,
    priceChangePercentage24h: Double = -0.5,
) = Coin(
    id = id,
    name = name,
    symbol = symbol,
    image = imageUrl,
    currentPrice = currentPrice,
    marketCap = marketCap,
    marketCapRank = marketCapRank,
    priceChangePercentage24h = priceChangePercentage24h,
)