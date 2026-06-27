package com.techieblossom.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techieblossom.cryptotracker.navigation.CoinDetailRoute
import com.techieblossom.cryptotracker.navigation.CoinListRoute
import com.techieblossom.cryptotracker.presentation.coindetail.CoinDetailScreen
import com.techieblossom.cryptotracker.presentation.coinlist.CoinListScreen
import com.techieblossom.cryptotracker.presentation.coinlist.CoinListViewModel
import com.techieblossom.cryptotracker.ui.theme.CryptoTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CryptoTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: CoinListViewModel = hiltViewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val isRefreshing by viewModel.pullToRefreshState.collectAsStateWithLifecycle()
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = CoinListRoute,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable<CoinListRoute> {
                            PullToRefreshBox(
                                isRefreshing = isRefreshing,
                                onRefresh = viewModel::refresh,
                            ) {
                                CoinListScreen(
                                    uiState = uiState,
                                    onCoinClick = { coin ->
                                        navController.navigate(CoinDetailRoute(coinId = coin.symbol))
                                    },
                                    onRetry = viewModel::refresh
                                )
                            }
                        }
                        composable<CoinDetailRoute> {
                            CoinDetailScreen()
                        }

                    }
                }
            }
        }
    }
}
