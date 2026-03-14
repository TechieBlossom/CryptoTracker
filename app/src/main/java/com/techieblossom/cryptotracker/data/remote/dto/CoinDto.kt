package com.techieblossom.cryptotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.techieblossom.cryptotracker.domain.model.Coin

@JsonClass(generateAdapter = true)
data class CoinDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String?,
    @Json(name = "current_price")
    val currentPrice: Double?,
    @Json(name = "market_cap")
    val marketCap: Long?,
    @Json(name = "market_cap_rank")
    val marketCapRank: Int?,
    @Json(name = "price_change_percentage_24h")
    val priceChangePercentage24h: Double?
)

fun CoinDto.toDomain() = Coin(
    id = id,
    symbol = symbol,
    name = name,
    image = image,
    currentPrice = currentPrice ?: 0.0,
    marketCap = marketCap ?: 0L,
    marketCapRank = marketCapRank ?: 0,
    priceChangePercentage24h = priceChangePercentage24h
)
