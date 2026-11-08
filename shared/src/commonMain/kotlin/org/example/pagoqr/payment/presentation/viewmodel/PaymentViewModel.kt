package org.example.pagoqr.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.pagoqr.payment.domain.usecase.ProcessPaymentUseCase

class PaymentViewModel(
    private val processPaymentUseCase: ProcessPaymentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state = _state.asStateFlow()

    fun onEvent(event: PaymentEvent) {
        when (event) {

            is PaymentEvent.OnAmountChange -> {
                _state.update {
                    it.copy(
                        amount = event.amount,
                        error = null
                    )
                }
            }

            is PaymentEvent.OnDescriptionChange -> {
                _state.update {
                    it.copy(
                        description = event.description,
                        error = null
                    )
                }
            }

            PaymentEvent.OnPay -> {
                processPayment()
            }

            PaymentEvent.OnClearResult -> {
                _state.update {
                    it.copy(
                        transaction = null,
                        error = null
                    )
                }
            }
        }
    }

    private fun processPayment() {

        val amount = _state.value.amount
            .replace(",", ".")
            .toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _state.update {
                it.copy(
                    error = "Ingresa un monto válido"
                )
            }
            return
        }

        val description = _state.value.description.trim()

        if (description.isEmpty()) {
            _state.update {
                it.copy(
                    error = "Ingresa una descripción"
                )
            }
            return
        }

        viewModelScope.launch {

            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    transaction = null
                )
            }

            try {

                val transaction = processPaymentUseCase(
                    amount = amount,
                    description = description
                )

                _state.update {
                    it.copy(
                        isLoading = false,
                        transaction = transaction
                    )
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al procesar el pago"
                    )
                }
            }
        }
    }
}