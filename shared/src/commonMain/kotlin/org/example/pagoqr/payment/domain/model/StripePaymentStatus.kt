package org.example.pagoqr.payment.domain.model

data class StripePaymentStatus(
    val paymentIntentId: String,
    val status: String,
    val errorCode: String?,
    val declineCode: String?,
    val message: String?
)