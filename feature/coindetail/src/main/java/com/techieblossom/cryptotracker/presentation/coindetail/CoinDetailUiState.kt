package com.techieblossom.cryptotracker.presentation.coindetail

import com.techieblossom.cryptotracker.domain.model.CoinDetail

sealed interface CoinDetailUiState {
    data class Success(val coinDetail: CoinDetail) : CoinDetailUiState

    data object Loading : CoinDetailUiState

    data class Error(val message: String) : CoinDetailUiState
}