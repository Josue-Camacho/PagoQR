package org.example.pagoqr.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.example.pagoqr.history.domain.model.HistoryTransaction
import org.example.pagoqr.history.domain.usecase.SaveHistoryUseCase
import org.example.pagoqr.payment.domain.model.PaymentStatus
import org.example.pagoqr.payment.domain.model.Transaction
import org.example.pagoqr.payment.domain.usecase.CreateStripePaymentIntentUseCase
import org.example.pagoqr.payment.domain.usecase.GetStripePaymentStatusUseCase
import org.example.pagoqr.payment.domain.usecase.ProcessPaymentUseCase

class PaymentViewModel(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val saveHistoryUseCase: SaveHistoryUseCase,
    private val createStripePaymentIntentUseCase: CreateStripePaymentIntentUseCase,
    private val getStripePaymentStatusUseCase: GetStripePaymentStatusUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow(PaymentState())

    val state =
        _state.asStateFlow()

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

            PaymentEvent.OnStripePay -> {
                prepareStripePayment()
            }

            PaymentEvent.OnStripePaymentCompleted -> {
                completeStripePayment()
            }

            PaymentEvent.OnStripePaymentCanceled -> {
                verifyCanceledStripePayment()
            }

            is PaymentEvent.OnStripePaymentFailed -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        stripeClientSecret = null,
                        error = event.message
                    )
                }
            }

            PaymentEvent.OnClearResult -> {
                _state.update {
                    it.copy(
                        transaction = null,
                        error = null,
                        stripeClientSecret = null,
                        stripePaymentIntentId = null
                    )
                }
            }
        }
    }

    private fun prepareStripePayment() {

        val amount =
            _state.value.amount
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

        val description =
            _state.value.description.trim()

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

                val paymentIntent =
                    createStripePaymentIntentUseCase(
                        amount = amount,
                        description = description
                    )

                _state.update {
                    it.copy(
                        isLoading = false,
                        stripeClientSecret =
                            paymentIntent.clientSecret,
                        stripePaymentIntentId =
                            paymentIntent.paymentIntentId
                    )
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        isLoading = false,
                        error =
                            e.message
                                ?: "No se pudo conectar con Stripe"
                    )
                }
            }
        }
    }

    private fun verifyCanceledStripePayment() {

        val paymentIntentId =
            _state.value.stripePaymentIntentId

        if (paymentIntentId.isNullOrBlank()) {

            _state.update {
                it.copy(
                    isLoading = false,
                    stripeClientSecret = null,
                    error = "Pago cancelado"
                )
            }

            return
        }

        viewModelScope.launch {

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            try {

                val paymentStatus =
                    getStripePaymentStatusUseCase(
                        paymentIntentId
                    )

                when {

                    paymentStatus.declineCode ==
                            "insufficient_funds" -> {

                        saveFailedStripeTransaction(
                            message = "Fondos insuficientes"
                        )
                    }

                    paymentStatus.declineCode ==
                            "stolen_card" -> {

                        saveFailedStripeTransaction(
                            message = "Pago rechazado. Utiliza otro método de pago."
                        )
                    }

                    paymentStatus.errorCode ==
                            "card_declined" -> {

                        saveFailedStripeTransaction(
                            message = "Tarjeta rechazada"
                        )
                    }

                    paymentStatus.status ==
                            "succeeded" -> {

                        completeStripePayment()
                    }

                    else -> {

                        _state.update {
                            it.copy(
                                isLoading = false,
                                stripeClientSecret = null,
                                error = "Pago cancelado"
                            )
                        }
                    }
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        isLoading = false,
                        stripeClientSecret = null,
                        error =
                            e.message
                                ?: "No se pudo verificar el pago"
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun saveFailedStripeTransaction(
        message: String
    ) {

        val currentState =
            _state.value

        val amount =
            currentState.amount
                .replace(",", ".")
                .toDoubleOrNull()
                ?: return

        val paymentIntentId =
            currentState.stripePaymentIntentId
                ?: return

        val timestamp =
            Clock.System
                .now()
                .toEpochMilliseconds()

        saveHistoryUseCase(
            HistoryTransaction(
                id = paymentIntentId,
                amount = amount,
                description =
                    currentState.description.trim(),
                status =
                    PaymentStatus.FAILED.name,
                timestamp = timestamp
            )
        )

        _state.update {
            it.copy(
                isLoading = false,
                stripeClientSecret = null,
                error = message
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun completeStripePayment() {

        val currentState =
            _state.value

        val amount =
            currentState.amount
                .replace(",", ".")
                .toDoubleOrNull()
                ?: return

        val paymentIntentId =
            currentState.stripePaymentIntentId
                ?: return

        val timestamp =
            Clock.System
                .now()
                .toEpochMilliseconds()

        val transaction =
            Transaction(
                id = paymentIntentId,
                amount = amount,
                description =
                    currentState.description.trim(),
                status =
                    PaymentStatus.SUCCESS,
                timestamp = timestamp
            )

        viewModelScope.launch {

            saveHistoryUseCase(
                HistoryTransaction(
                    id = transaction.id,
                    amount = transaction.amount,
                    description =
                        transaction.description,
                    status =
                        transaction.status.name,
                    timestamp =
                        transaction.timestamp
                )
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    transaction = transaction,
                    stripeClientSecret = null,
                    error = null
                )
            }
        }
    }

    private fun processPayment() {

        val amount =
            _state.value.amount
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

        val description =
            _state.value.description.trim()

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

                val transaction =
                    processPaymentUseCase(
                        amount,
                        description
                    )

                if (
                    transaction.status ==
                    PaymentStatus.SUCCESS
                ) {

                    saveHistoryUseCase(
                        HistoryTransaction(
                            id = transaction.id,
                            amount =
                                transaction.amount,
                            description =
                                transaction.description,
                            status =
                                transaction.status.name,
                            timestamp =
                                transaction.timestamp
                        )
                    )
                }

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
                        error =
                            e.message
                                ?: "Error procesando el pago"
                    )
                }
            }
        }
    }
}