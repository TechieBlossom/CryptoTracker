package com.techieblossom.cryptotracker.domain.repository

import com.techieblossom.cryptotracker.domain.model.Coin

class FakeCoinRepository : CoinRepository {

    var coins: List<Coin> = emptyList()
    var shouldThrow: Exception? = null

    override suspend fun getCoins(): List<Coin> {
        shouldThrow?.let { throw it }
        return coins
    }

}