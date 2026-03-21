package com.techieblossom.cryptotracker.data.repository

import com.techieblossom.cryptotracker.data.remote.FakeCoinGeckoApi
import com.techieblossom.cryptotracker.data.remote.dto.CoinDto
import com.techieblossom.cryptotracker.fakeCoinDto
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
        fakeCoinGeckoApi.coins = listOf(
            fakeCoinDto(),
            fakeCoinDto(name = "Ethereum")
        )

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
