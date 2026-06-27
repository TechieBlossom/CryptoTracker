package com.techieblossom.cryptotracker.presentation.coindetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techieblossom.cryptotracker.domain.model.CoinDetail
import com.techieblossom.cryptotracker.ui.format.formatAsCompactUsd
import com.techieblossom.cryptotracker.ui.format.formatAsPercent
import com.techieblossom.cryptotracker.ui.format.formatAsUsd
import com.techieblossom.cryptotracker.ui.theme.CryptoTrackerTheme
import com.techieblossom.cryptotracker.ui.theme.Green
import com.techieblossom.cryptotracker.ui.theme.Red
import com.techieblossom.cryptotracker.ui.theme.Typography

@Composable
fun CoinDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: CoinDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) {
        CoinDetailContent(
            uiState = uiState,
            onRetry = viewModel::retry,
            modifier = modifier,
        )
    }
}

@Composable
private fun CoinDetailContent(
    uiState: CoinDetailUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        CoinDetailUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is CoinDetailUiState.Success -> {
            CoinDetailSuccess(
                coinDetail = uiState.coinDetail,
                modifier = modifier,
            )
        }

        is CoinDetailUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(uiState.message)
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun CoinDetailSuccess(
    coinDetail: CoinDetail,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item { CoinDetailHeader(coinDetail = coinDetail) }
        item { CoinDetailHero(coinDetail = coinDetail) }
        item { CoinDetailStats(coinDetail = coinDetail) }
    }
}

@Composable
private fun CoinDetailHeader(
    coinDetail: CoinDetail,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = coinDetail.baseAsset,
            style = Typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "/${coinDetail.quoteAsset}",
            style = Typography.titleMedium,
            color = Color.Gray,
        )
    }
}

@Composable
private fun CoinDetailHero(
    coinDetail: CoinDetail,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = coinDetail.currentPrice.formatAsUsd(),
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        PriceChangePill(percent = coinDetail.priceChangePercentage24h)
    }
}

@Composable
private fun PriceChangePill(
    percent: Double?,
    modifier: Modifier = Modifier,
) {
    val color = when {
        percent == null -> Color.Gray
        percent < 0 -> Red
        else -> Green
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = color.copy(alpha = 0.15f),
    ) {
        Text(
            text = percent.formatAsPercent(),
            style = Typography.labelLarge,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun CoinDetailStats(
    coinDetail: CoinDetail,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "24H HIGH",
                value = coinDetail.high24h.formatAsUsd(),
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "24H LOW",
                value = coinDetail.low24h.formatAsUsd(),
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "24H VOLUME (${coinDetail.baseAsset})",
                value = coinDetail.volume.formatAsCompactUsd(),
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "24H VOLUME (${coinDetail.quoteAsset})",
                value = coinDetail.quoteVolume.formatAsCompactUsd(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = Typography.labelSmall,
                color = Color.Gray,
            )
            Text(
                text = value,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Preview(name = "Coin Detail Loading", showBackground = true)
@Composable
private fun CoinDetailContent_LoadingPreview() {
    CryptoTrackerTheme {
        CoinDetailContent(
            uiState = CoinDetailUiState.Loading,
            onRetry = {},
        )
    }
}

@Preview(name = "Coin Detail Success", showBackground = true)
@Composable
private fun CoinDetailContent_SuccessPreview() {
    CryptoTrackerTheme {
        CoinDetailContent(
            uiState = CoinDetailUiState.Success(
                coinDetail = CoinDetail(
                    symbol = "BTCUSDT",
                    baseAsset = "BTC",
                    quoteAsset = "USDT",
                    currentPrice = 70712.0,
                    priceChangePercentage24h = -0.68272,
                    high24h = 71250.0,
                    low24h = 69800.0,
                    volume = 12345.678,
                    quoteVolume = 873_000_000.0,
                ),
            ),
            onRetry = {},
        )
    }
}

@Preview(name = "Coin Detail Success - Nulls", showBackground = true)
@Composable
private fun CoinDetailContent_SuccessNullsPreview() {
    CryptoTrackerTheme {
        CoinDetailContent(
            uiState = CoinDetailUiState.Success(
                coinDetail = CoinDetail(
                    symbol = "NEWUSDT",
                    baseAsset = "NEW",
                    quoteAsset = "USDT",
                    currentPrice = null,
                    priceChangePercentage24h = null,
                    high24h = null,
                    low24h = null,
                    volume = null,
                    quoteVolume = null,
                ),
            ),
            onRetry = {},
        )
    }
}

@Preview(name = "Coin Detail Error", showBackground = true)
@Composable
private fun CoinDetailContent_ErrorPreview() {
    CryptoTrackerTheme {
        CoinDetailContent(
            uiState = CoinDetailUiState.Error(message = "Something went wrong"),
            onRetry = {},
        )
    }
}
