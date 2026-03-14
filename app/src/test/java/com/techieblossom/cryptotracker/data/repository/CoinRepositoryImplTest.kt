package com.techieblossom.cryptotracker.data.repository

import com.techieblossom.cryptotracker.data.remote.FakeCoinGeckoApi
import com.techieblossom.cryptotracker.data.remote.dto.CoinDto
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CoinRepositoryImplTest {

    private lateinit var repository: CoinRepositoryImpl
    private lateinit var fakeCoinGeckoApi: FakeCoinGeckoApi

    @Before
    fun setUp() {
        fakeCoinGeckoApi = FakeCoinGeckoApi()
        repository = CoinRepositoryImpl(fakeCoinGeckoApi)
    }

    @Test
    fun `getCoins returns empty list of coins`() = runTest {
        val coins = repository.getCoins()

        assert(coins.isEmpty())
    }

    @Test
    fun `getCoins returns mapped domain models`() = runTest {
        fakeCoinGeckoApi.coins = _fakeCoins

        val coins = repository.getCoins()
        assertEquals(2, coins.size)
        assertEquals("Bitcoin", coins[0].name)
        assertEquals("Ethereum", coins[1].name)
        assertEquals(70712.0, coins[0].currentPrice)
    }

    @Test
    fun `getCoins throws exception`() = runTest {
        fakeCoinGeckoApi.shouldThrow = Exception("Test exception")

        try {
            repository.getCoins()
        } catch (e: Exception) {
            assertEquals("Test exception", e.message)
        }
    }
}

private val _fakeCoins = listOf(
    CoinDto(
        id = "bitcoin",
        symbol = "btc",
        name = "Bitcoin",
        image = "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png?1696501400",
        currentPrice = 70712.0,
        marketCap = 1414832995358,
        marketCapRank = 1,
        priceChangePercentage24h = -0.68272,
    ),
    CoinDto(
        id = "ethereum",
        symbol = "eth",
        name = "Ethereum",
        image = "https://coin-images.coingecko.com/coins/images/279/large/ethereum.png?1696501400",
        currentPrice = 4500.0,
        marketCap = 480788543,
        marketCapRank = 2,
        priceChangePercentage24h = 0.57593,
    )
)