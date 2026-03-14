package com.techieblossom.cryptotracker.data.repository

import com.techieblossom.cryptotracker.data.remote.CoinGeckoApi
import com.techieblossom.cryptotracker.data.remote.dto.toDomain
import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.domain.repository.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(private val coinGeckoApi: CoinGeckoApi) :
    CoinRepository {
    override suspend fun getCoins(): List<Coin> =
        coinGeckoApi.getCoins().map { coinDto ->
            coinDto.toDomain()
        }.toList()
}