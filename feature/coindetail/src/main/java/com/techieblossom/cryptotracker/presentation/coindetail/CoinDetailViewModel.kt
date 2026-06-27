package com.techieblossom.cryptotracker.presentation.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.techieblossom.cryptotracker.domain.repository.CoinRepository
import com.techieblossom.cryptotracker.navigation.CoinDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val coinId: String = savedStateHandle.toRoute<CoinDetailRoute>().coinId

    private val _uiState = MutableStateFlow<CoinDetailUiState>(CoinDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchCoinDetail()
    }

    fun retry() {
        fetchCoinDetail()
    }

    private fun fetchCoinDetail() {
        viewModelScope.launch {
            _uiState.value = CoinDetailUiState.Loading
            runCatching {
                coinRepository.getCoinDetail(coinId)
            }.onSuccess { coinDetail ->
                _uiState.value = CoinDetailUiState.Success(coinDetail)
            }.onFailure { e ->
                if (e is CancellationException) {
                    throw e
                } else {
                    _uiState.value = CoinDetailUiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }
}
