package com.techieblossom.cryptotracker.data.repository

import com.techieblossom.cryptotracker.data.remote.BinanceApi
import com.techieblossom.cryptotracker.data.remote.dto.toCoin
import com.techieblossom.cryptotracker.data.remote.dto.toCoinDetail
import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.domain.model.CoinDetail
import com.techieblossom.cryptotracker.domain.repository.CoinRepository
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(private val binanceApi: BinanceApi) :
    CoinRepository {

    /**
     * Binance returns every trading pair from /ticker/24hr. We surface the top USDT pairs by
     * 24h quote volume (Binance has no "rank"; volume is the closest proxy for prominence).
     */
    override suspend fun getCoins(): List<Coin> =
        binanceApi.getTicker24hr()
            .filter { it.symbol.endsWith(QUOTE_ASSET) }
            .sortedByDescending { it.quoteVolume?.toDoubleOrNull() ?: 0.0 }
            .take(PAGE_SIZE)
            .map { it.toCoin() }

    override suspend fun getCoinDetail(coinId: String): CoinDetail =
        binanceApi.getTicker24hr(symbol = coinId).toCoinDetail()

    private companion object {
        const val QUOTE_ASSET = "USDT"
        const val PAGE_SIZE = 20
    }
}
