package com.techieblossom.cryptotracker.presentation.coinlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun CoinListScreen(uiState: CoinListUiState, modifier: Modifier) {
    when (uiState) {
        CoinListUiState.Loading -> {
            CircularProgressIndicator(
                modifier = modifier
            )
        }

        is CoinListUiState.Success -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.coins) { coin ->
                    CoinCell(
                        coin = coin,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        is CoinListUiState.Error -> {
            Text(uiState.message)
        }
    }
}

@Preview(name = "Coin List Loading", showBackground = true)
@Composable
private fun CoinListScreen_LoadingPreview() {
    CryptoTrackerTheme {
        CoinListScreen(uiState = CoinListUiState.Loading, modifier = Modifier)
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
                        id = "bitcoin",
                        symbol = "btc",
                        name = "Bitcoin",
                        image = "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png?1696501400",
                        currentPrice = 70712.0,
                        marketCap = 1414832995358,
                        marketCapRank = 1,
                        priceChangePercentage24h = -0.68272,
                    ),
                    Coin(
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
            ), modifier = Modifier
        )
    }
}

@Preview(name = "Coin List Error", showBackground = true)
@Composable
private fun CoinListScreen_ErrorPreview() {
    CryptoTrackerTheme {
        CoinListScreen(uiState = CoinListUiState.Error("Something went wrong"), modifier = Modifier)
    }
}

