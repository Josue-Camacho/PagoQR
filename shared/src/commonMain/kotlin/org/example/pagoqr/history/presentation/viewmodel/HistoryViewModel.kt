package org.example.pagoqr.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.pagoqr.history.domain.usecase.ClearHistoryUseCase
import org.example.pagoqr.history.domain.usecase.ObserveHistoryUseCase

class HistoryViewModel(
    private val observeHistoryUseCase: ObserveHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    init {
        observeHistory()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            HistoryEvent.OnClearHistory -> {
                clearHistory()
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            observeHistoryUseCase()
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Error al cargar el historial"
                        )
                    }
                }
                .collect { transactions ->
                    _state.update {
                        it.copy(
                            transactions = transactions,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            try {
                clearHistoryUseCase()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Error al limpiar historial"
                    )
                }
            }
        }
    }
}