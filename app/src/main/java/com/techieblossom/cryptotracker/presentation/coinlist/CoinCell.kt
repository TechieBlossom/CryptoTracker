package com.techieblossom.cryptotracker.presentation.coinlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.techieblossom.cryptotracker.domain.model.Coin
import com.techieblossom.cryptotracker.ui.theme.CryptoTrackerTheme
import com.techieblossom.cryptotracker.ui.theme.Green
import com.techieblossom.cryptotracker.ui.theme.Red
import com.techieblossom.cryptotracker.ui.theme.Typography
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CoinCell(coin: Coin, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AsyncImage(
            modifier = Modifier.size(40.dp),
            model = coin.image,
            contentDescription = coin.name,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(coin.name, style = Typography.bodyMedium)
            Text(
                coin.symbol.uppercase(),
                style = Typography.bodySmall,
                color = Color.Gray
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                NumberFormat.getCurrencyInstance(Locale.US).format(coin.currentPrice),
                style = Typography.bodyMedium
            )
            Text(
                String.format(locale = Locale.US, "%+.2f%%", coin.priceChangePercentage24h),
                style = Typography.bodySmall,
                color = coin.priceChangePercentage24h?.let { if (it < 0) Green else Red } ?: Color.Gray
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