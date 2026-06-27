package com.techieblossom.cryptotracker.data.remote

import com.techieblossom.cryptotracker.data.remote.dto.Ticker24hrDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Binance public market-data API (base: https://api.binance.com/api/v3/).
 *
 * No auth required. All numeric fields arrive as strings and are parsed in the mappers.
 */
interface BinanceApi {

    /** All symbols' 24h rolling stats. The repository filters to USDT pairs and sorts. */
    @GET("ticker/24hr")
    suspend fun getTicker24hr(): List<Ticker24hrDto>

    /** 24h rolling stats for a single symbol, e.g. "BTCUSDT". */
    @GET("ticker/24hr")
    suspend fun getTicker24hr(
        @Query("symbol") symbol: String,
    ): Ticker24hrDto
}
