package com.techieblossom.cryptotracker.presentation.coinlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techieblossom.cryptotracker.domain.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CoinListUiState>(CoinListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                coinRepository.getCoins()
            }.onSuccess { coins ->
                _uiState.value = CoinListUiState.Success(coins)
            }.onFailure { e ->
                if (e is CancellationException) {
                    throw e
                } else {
                    _uiState.value = CoinListUiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }
}