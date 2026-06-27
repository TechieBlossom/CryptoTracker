package com.techieblossom.cryptotracker.data.repository

import com.techieblossom.cryptotracker.data.remote.FakeBinanceApi
import com.techieblossom.cryptotracker.fakeTicker24hrDto
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CoinRepositoryImplTest {

    private lateinit var repository: CoinRepositoryImpl
    private lateinit var fakeBinanceApi: FakeBinanceApi

    @Before
    fun setUp() {
        fakeBinanceApi = FakeBinanceApi()
        repository = CoinRepositoryImpl(fakeBinanceApi)
    }

    @Test
    fun `getCoins returns empty list when api returns nothing`() = runTest {
        val coins = repository.getCoins()

        assert(coins.isEmpty())
    }

    @Test
    fun `getCoins keeps only USDT pairs`() = runTest {
        fakeBinanceApi.tickers = listOf(
            fakeTicker24hrDto(symbol = "BTCUSDT"),
            fakeTicker24hrDto(symbol = "ETHBTC"),
            fakeTicker24hrDto(symbol = "BNBUSDT"),
        )

        val coins = repository.getCoins()

        assertEquals(2, coins.size)
        assert(coins.all { it.quoteAsset == "USDT" })
    }

    @Test
    fun `getCoins sorts by quote volume descending`() = runTest {
        fakeBinanceApi.tickers = listOf(
            fakeTicker24hrDto(symbol = "ADAUSDT", quoteVolume = "100.0"),
            fakeTicker24hrDto(symbol = "BTCUSDT", quoteVolume = "900.0"),
            fakeTicker24hrDto(symbol = "ETHUSDT", quoteVolume = "500.0"),
        )

        val coins = repository.getCoins()

        assertEquals(listOf("BTCUSDT", "ETHUSDT", "ADAUSDT"), coins.map { it.symbol })
    }

    @Test
    fun `getCoins caps the result at 20 pairs`() = runTest {
        fakeBinanceApi.tickers = (1..30).map {
            fakeTicker24hrDto(symbol = "C${it}USDT", quoteVolume = it.toString())
        }

        val coins = repository.getCoins()

        assertEquals(20, coins.size)
    }

    @Test
    fun `getCoins maps symbol into base and quote assets`() = runTest {
        fakeBinanceApi.tickers = listOf(
            fakeTicker24hrDto(symbol = "BTCUSDT", lastPrice = "70712.00000000"),
        )

        val coin = repository.getCoins().first()

        assertEquals("BTCUSDT", coin.symbol)
        assertEquals("BTC", coin.baseAsset)
        assertEquals("USDT", coin.quoteAsset)
        assertEquals(70712.0, coin.currentPrice)
    }

    @Test
    fun `getCoinDetail maps the single ticker`() = runTest {
        fakeBinanceApi.tickerBySymbol = fakeTicker24hrDto(
            symbol = "ETHUSDT",
            lastPrice = "4500.0",
            highPrice = "4600.0",
            lowPrice = "4400.0",
        )

        val detail = repository.getCoinDetail("ETHUSDT")

        assertEquals("ETHUSDT", detail.symbol)
        assertEquals("ETH", detail.baseAsset)
        assertEquals(4500.0, detail.currentPrice)
        assertEquals(4600.0, detail.high24h)
        assertEquals(4400.0, detail.low24h)
    }

    @Test
    fun `getCoins propagates api exceptions`() = runTest {
        fakeBinanceApi.shouldThrow = Exception("Test exception")

        try {
            repository.getCoins()
            assert(false) { "expected exception" }
        } catch (e: Exception) {
            assertEquals("Test exception", e.message)
        }
    }
}
