package com.techieblossom.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.presentation.coinlist.CoinListUiState
import com.techieblossom.cryptotracker.presentation.coinlist.CoinListViewModel
import com.techieblossom.cryptotracker.ui.theme.CryptoTrackerTheme
import com.techieblossom.cryptotracker.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            CryptoTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: CoinListViewModel = hiltViewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    when (uiState) {
                        CoinListUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is CoinListUiState.Success -> {
                            val successState = uiState as CoinListUiState.Success
                            LazyColumn(
                                modifier = Modifier.padding(innerPadding),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(successState.coins.size) {
                                    CoinCell(
                                        coin = successState.coins[it],
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }

                        is CoinListUiState.Error -> {
                            Text((uiState as CoinListUiState.Error).message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoinCell(coin: Coin, modifier: Modifier = Modifier) {
    Row(modifier) {
        Column {
            Text(coin.name, style = Typography.bodyMedium)
            Text(
                coin.symbol.uppercase(),
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Text(coin.currentPrice.toString(), style = Typography.bodyMedium)
            Text(
                coin.priceChangePercentage24h.toString(),
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CryptoTrackerTheme {
        CoinCell(
            coin = Coin(
                id = "bitcoin",
                symbol = "btc",
                name = "Bitcoin",
                image = "https://coin-images.coingecko.com/coins/images/1/large/bitcoin.png?1696501400",
                currentPrice = 70712.0,
                marketCap = 1414832995358,
                marketCapRank = 1,
                priceChangePercentage24h = -0.68272,
            )
        )
    }
}