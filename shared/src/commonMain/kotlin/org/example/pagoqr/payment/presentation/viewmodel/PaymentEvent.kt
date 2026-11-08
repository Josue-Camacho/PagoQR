package org.example.pagoqr.payment.presentation.viewmodel

sealed interface PaymentEvent {

    data class OnAmountChange(
        val amount: String
    ) : PaymentEvent

    data class OnDescriptionChange(
        val description: String
    ) : PaymentEvent

    data object OnPay : PaymentEvent

    data object OnClearResult : PaymentEvent
}