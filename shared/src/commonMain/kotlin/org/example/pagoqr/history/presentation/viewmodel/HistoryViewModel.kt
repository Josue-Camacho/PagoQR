package org.example.pagoqr.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.pagoqr.history.domain.model.HistoryTransaction
import org.example.pagoqr.history.domain.usecase.ClearHistoryUseCase
import org.example.pagoqr.history.domain.usecase.ObserveHistoryUseCase
import org.example.pagoqr.history.domain.usecase.SaveHistoryUseCase
import org.example.pagoqr.payment.domain.model.PaymentStatus
import org.example.pagoqr.payment.domain.usecase.RefundStripePaymentUseCase

class HistoryViewModel(
    private val observeHistoryUseCase: ObserveHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val saveHistoryUseCase: SaveHistoryUseCase,
    private val refundStripePaymentUseCase: RefundStripePaymentUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow(HistoryState())

    val state =
        _state.asStateFlow()

    init {
        observeHistory()
    }

    fun onEvent(event: HistoryEvent) {

        when (event) {

            HistoryEvent.OnClearHistory -> {
                clearHistory()
            }

            is HistoryEvent.OnRefund -> {
                refundPayment(
                    event.transaction
                )
            }
        }
    }

    private fun observeHistory() {

        viewModelScope.launch {

            observeHistoryUseCase()
                .catch { exception ->

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error =
                                exception.message
                                    ?: "Error cargando historial"
                        )
                    }
                }
                .collect { transactions ->

                    _state.update {
                        it.copy(
                            transactions = transactions,
                            isLoading = false
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
                        error =
                            e.message
                                ?: "No se pudo limpiar el historial"
                    )
                }
            }
        }
    }

    private fun refundPayment(
        transaction: HistoryTransaction
    ) {

        if (transaction.status != PaymentStatus.SUCCESS.name) {
            return
        }

        viewModelScope.launch {

            _state.update {
                it.copy(
                    refundingTransactionId =
                        transaction.id,
                    error = null
                )
            }

            try {

                val refund =
                    refundStripePaymentUseCase(
                        transaction.id
                    )

                if (refund.status == "succeeded") {

                    saveHistoryUseCase(
                        transaction.copy(
                            status =
                                PaymentStatus.REFUNDED.name
                        )
                    )

                } else {

                    _state.update {
                        it.copy(
                            error =
                                "El reembolso quedó en estado ${refund.status}"
                        )
                    }
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        error =
                            e.message
                                ?: "No se pudo realizar el reembolso"
                    )
                }

            } finally {

                _state.update {
                    it.copy(
                        refundingTransactionId = null
                    )
                }
            }
        }
    }
}