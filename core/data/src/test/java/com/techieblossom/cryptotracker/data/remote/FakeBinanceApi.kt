package com.techieblossom.cryptotracker.data.remote

import com.techieblossom.cryptotracker.data.remote.dto.Ticker24hrDto

class FakeBinanceApi : BinanceApi {

    var tickers: List<Ticker24hrDto> = emptyList()
    var tickerBySymbol: Ticker24hrDto? = null
    var shouldThrow: Exception? = null

    override suspend fun getTicker24hr(): List<Ticker24hrDto> {
        shouldThrow?.let { throw it }
        return tickers
    }

    override suspend fun getTicker24hr(symbol: String): Ticker24hrDto {
        shouldThrow?.let { throw it }
        return tickerBySymbol
            ?: tickers.firstOrNull { it.symbol == symbol }
            ?: error("FakeBinanceApi has no ticker for symbol=$symbol")
    }
}
