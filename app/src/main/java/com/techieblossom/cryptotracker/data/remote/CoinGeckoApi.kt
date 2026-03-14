package com.techieblossom.cryptotracker.data.remote

import com.techieblossom.cryptotracker.data.remote.dto.CoinDto
import com.techieblossom.cryptotracker.domain.model.CoinSortOrder
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = CoinSortOrder.MARKET_CAP_DESC.value,
        @Query("per_page") perPage: Int = 5,
        @Query("page") page: Int = 1,
    ): List<CoinDto>
}