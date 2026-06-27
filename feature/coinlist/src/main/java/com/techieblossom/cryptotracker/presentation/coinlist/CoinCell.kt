package com.techieblossom.cryptotracker.presentation.coinlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.ui.format.formatAsCompactUsd
import com.techieblossom.cryptotracker.ui.format.formatAsPercent
import com.techieblossom.cryptotracker.ui.format.formatAsUsd
import com.techieblossom.cryptotracker.ui.theme.CryptoTrackerTheme
import com.techieblossom.cryptotracker.ui.theme.Green
import com.techieblossom.cryptotracker.ui.theme.Red
import com.techieblossom.cryptotracker.ui.theme.Typography

@Composable
fun CoinCell(coin: Coin, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(coin.baseAsset, style = Typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "/${coin.quoteAsset}",
                    style = Typography.bodySmall,
                    color = Color.Gray,
                )
            }
            Text(
                "Vol ${coin.quoteVolume.formatAsCompactUsd()}",
                style = Typography.bodySmall,
                color = Color.Gray,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(coin.currentPrice.formatAsUsd(), style = Typography.bodyMedium)
            Text(
                coin.priceChangePercentage24h.formatAsPercent(),
                style = Typography.bodySmall,
                color = coin.priceChangePercentage24h?.let { if (it < 0) Red else Green }
                    ?: Color.Gray,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinCellPreview() {
    CryptoTrackerTheme {
        CoinCell(
            coin = Coin(
                symbol = "BTCUSDT",
                baseAsset = "BTC",
                quoteAsset = "USDT",
                currentPrice = 70712.0,
                priceChangePercentage24h = -0.68272,
                quoteVolume = 873_000_000.0,
            ),
            onClick = {},
        )
    }
}
