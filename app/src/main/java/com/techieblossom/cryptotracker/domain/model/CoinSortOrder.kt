package com.techieblossom.cryptotracker.domain.model

enum class CoinSortOrder(val value: String) {
    MARKET_CAP_DESC("market_cap_desc"),
    MARKET_CAP_ASC("market_cap_asc"),
    VOLUME_DESC("volume_desc"),
    VOLUME_ASC("volume_asc"),
}