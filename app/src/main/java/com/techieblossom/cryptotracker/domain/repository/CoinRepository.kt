package com.techieblossom.cryptotracker.domain.repository

import com.techieblossom.cryptotracker.domain.model.Coin

interface CoinRepository {
    suspend fun getCoins() : List<Coin>
}