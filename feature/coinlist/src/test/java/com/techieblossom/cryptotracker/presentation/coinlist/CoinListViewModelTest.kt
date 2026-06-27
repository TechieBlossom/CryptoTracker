package com.techieblossom.cryptotracker.presentation.coinlist

import app.cash.turbine.test
import com.techieblossom.cryptotracker.domain.repository.FakeCoinRepository
import com.techieblossom.cryptotracker.fakeCoin
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test

class CoinListViewModelTest {
    private lateinit var fakeRepository: FakeCoinRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeRepository = FakeCoinRepository()
    }

    @Test
    fun `initial load emits Loading then Success`() = runTest {
        fakeRepository.coins = listOf(fakeCoin(), fakeCoin(symbol = "ETHUSDT", baseAsset = "ETH"))

        val viewModel = CoinListViewModel(fakeRepository)

        viewModel.uiState.test {
            val state = awaitItem()

            assert(state is CoinListUiState.Success)
            assertEquals(2, (state as CoinListUiState.Success).coins.size)
            assertEquals("BTCUSDT", state.coins[0].symbol)
        }
    }

    @Test
    fun `initial load emits Error on failure`() = runTest {
        fakeRepository.shouldThrow = IOException("Network Error")

        val viewModel = CoinListViewModel(fakeRepository)

        viewModel.uiState.test {
            val state = awaitItem()

            assert(state is CoinListUiState.Error)
            assertEquals("Network Error", (state as CoinListUiState.Error).message)
        }
    }

    @Test
    fun `refresh updates with new data`() = runTest {
        fakeRepository.coins = listOf(fakeCoin())

        val viewModel = CoinListViewModel(fakeRepository)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(1, (initial as CoinListUiState.Success).coins.size)

            fakeRepository.coins = listOf(fakeCoin(), fakeCoin(symbol = "ETHUSDT", baseAsset = "ETH"))
            viewModel.refresh()

            val refreshed = awaitItem()
            assertEquals(2, (refreshed as CoinListUiState.Success).coins.size)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
