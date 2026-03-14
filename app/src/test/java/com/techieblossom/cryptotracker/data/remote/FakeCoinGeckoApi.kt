package com.techieblossom.cryptotracker.data.remote

import com.techieblossom.cryptotracker.data.remote.dto.CoinDto

class FakeCoinGeckoApi : CoinGeckoApi {

    var coins: List<CoinDto> = emptyList()
    var shouldThrow: Exception? = null

    override suspend fun getCoins(
        currency: String,
        order: String,
        perPage: Int,
        page: Int
    ): List<CoinDto> {
        shouldThrow?.let { throw it }
        return coins
    }
}