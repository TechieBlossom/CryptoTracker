package com.techieblossom.cryptotracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.domain.model.CoinDetail

/**
 * Binance /ticker/24hr response. Every numeric value is delivered as a String
 * (e.g. "70712.00000000"), so we parse to Double in the mappers with a safe fallback.
 */
@JsonClass(generateAdapter = true)
data class Ticker24hrDto(
    val symbol: String,
    @Json(name = "lastPrice")
    val lastPrice: String?,
    @Json(name = "priceChangePercent")
    val priceChangePercent: String?,
    @Json(name = "highPrice")
    val highPrice: String?,
    @Json(name = "lowPrice")
    val lowPrice: String?,
    @Json(name = "volume")
    val volume: String?,
    @Json(name = "quoteVolume")
    val quoteVolume: String?,
)

/** Quote assets we recognise when splitting a symbol into base/quote. USDT-first. */
private val KNOWN_QUOTE_ASSETS = listOf("USDT", "BUSD", "USDC", "BTC", "ETH", "BNB")

/**
 * Splits a Binance symbol into (base, quote) by matching a known quote suffix.
 * "BTCUSDT" -> ("BTC", "USDT"). Falls back to (symbol, "") if no suffix matches.
 */
fun parseSymbol(symbol: String): Pair<String, String> {
    val quote = KNOWN_QUOTE_ASSETS.firstOrNull { symbol.endsWith(it) && symbol.length > it.length }
    return if (quote != null) symbol.removeSuffix(quote) to quote else symbol to ""
}

fun Ticker24hrDto.toCoin(): Coin {
    val (base, quote) = parseSymbol(symbol)
    return Coin(
        symbol = symbol,
        baseAsset = base,
        quoteAsset = quote,
        currentPrice = lastPrice?.toDoubleOrNull() ?: 0.0,
        priceChangePercentage24h = priceChangePercent?.toDoubleOrNull(),
        quoteVolume = quoteVolume?.toDoubleOrNull() ?: 0.0,
    )
}

fun Ticker24hrDto.toCoinDetail(): CoinDetail {
    val (base, quote) = parseSymbol(symbol)
    return CoinDetail(
        symbol = symbol,
        baseAsset = base,
        quoteAsset = quote,
        currentPrice = lastPrice?.toDoubleOrNull(),
        priceChangePercentage24h = priceChangePercent?.toDoubleOrNull(),
        high24h = highPrice?.toDoubleOrNull(),
        low24h = lowPrice?.toDoubleOrNull(),
        volume = volume?.toDoubleOrNull(),
        quoteVolume = quoteVolume?.toDoubleOrNull(),
    )
}
