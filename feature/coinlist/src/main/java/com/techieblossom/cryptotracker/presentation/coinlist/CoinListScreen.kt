package com.techieblossom.cryptotracker.presentation.coinlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun CoinListScreen(
    uiState: CoinListUiState,
    onCoinClick: (Coin) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        CoinListUiState.Loading -> {
            CircularProgressIndicator(
                modifier = modifier
            )
        }

        is CoinListUiState.Success -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.coins) { coin ->
                    CoinCell(
                        coin = coin,
                        onClick = { onCoinClick(coin) },
                    )
                }
            }
        }

        is CoinListUiState.Error -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(uiState.message)
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Preview(name = "Coin List Loading", showBackground = true)
@Composable
private fun CoinListScreen_LoadingPreview() {
    CryptoTrackerTheme {
        CoinListScreen(
            uiState = CoinListUiState.Loading,
            modifier = Modifier,
            onCoinClick = {},
            onRetry = {})
    }
}

@Preview(name = "Coin List Success", showBackground = true)
@Composable
private fun CoinListScreen_SuccessPreview() {
    CryptoTrackerTheme {
        CoinListScreen(
            uiState = CoinListUiState.Success(
                coins = listOf(
                    Coin(
                        symbol = "BTCUSDT",
                        baseAsset = "BTC",
                        quoteAsset = "USDT",
                        currentPrice = 70712.0,
                        priceChangePercentage24h = -0.68272,
                        quoteVolume = 873_000_000.0,
                    ),
                    Coin(
                        symbol = "ETHUSDT",
                        baseAsset = "ETH",
                        quoteAsset = "USDT",
                        currentPrice = 4500.0,
                        priceChangePercentage24h = 0.57593,
                        quoteVolume = 412_000_000.0,
                    )
                )
            ), modifier = Modifier,
            onCoinClick = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Coin List Error", showBackground = true)
@Composable
private fun CoinListScreen_ErrorPreview() {
    CryptoTrackerTheme {
        CoinListScreen(
            uiState = CoinListUiState.Error("Something went wrong"),
            modifier = Modifier,
            onCoinClick = {},
            onRetry = {})
    }
}

