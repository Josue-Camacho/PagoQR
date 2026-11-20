package org.example.pagoqr.payment.domain.model

data class StripePaymentIntent(
    val paymentIntentId: String,
    val clientSecret: String,
    val status: String
)