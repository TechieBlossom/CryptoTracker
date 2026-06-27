package com.techieblossom.cryptotracker.presentation.coinlist

import com.techieblossom.cryptotracker.domain.model.Coin

sealed interface CoinListUiState {
    data class Success(val coins: List<Coin>) : CoinListUiState

    data object Loading : CoinListUiState

    data class Error(val message: String) : CoinListUiState
}