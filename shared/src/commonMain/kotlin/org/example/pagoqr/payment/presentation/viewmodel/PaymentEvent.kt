package org.example.pagoqr.payment.presentation.viewmodel

sealed interface PaymentEvent {

    data class OnAmountChange(
        val amount: String
    ) : PaymentEvent

    data class OnDescriptionChange(
        val description: String
    ) : PaymentEvent

    // Pago simulado anterior
    data object OnPay : PaymentEvent

    // Nuevo flujo Stripe
    data object OnStripePay : PaymentEvent

    data object OnStripePaymentCompleted : PaymentEvent

    data object OnStripePaymentCanceled : PaymentEvent

    data class OnStripePaymentFailed(
        val message: String
    ) : PaymentEvent

    data object OnClearResult : PaymentEvent
}