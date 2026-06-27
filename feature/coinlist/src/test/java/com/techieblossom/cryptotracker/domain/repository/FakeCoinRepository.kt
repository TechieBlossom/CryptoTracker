package com.techieblossom.cryptotracker.domain.repository

import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.domain.model.CoinDetail

class FakeCoinRepository : CoinRepository {

    var coins: List<Coin> = emptyList()
    var coinDetail: CoinDetail? = null
    var shouldThrow: Exception? = null

    override suspend fun getCoins(): List<Coin> {
        shouldThrow?.let { throw it }
        return coins
    }

    override suspend fun getCoinDetail(coinId: String): CoinDetail {
        shouldThrow?.let { throw it }
        return coinDetail ?: error("FakeCoinRepository.coinDetail not set")
    }

}