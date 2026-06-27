package com.techieblossom.cryptotracker.domain.repository

import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.domain.model.CoinDetail

interface CoinRepository {
    suspend fun getCoins() : List<Coin>
    suspend fun getCoinDetail(coinId: String) : CoinDetail
}