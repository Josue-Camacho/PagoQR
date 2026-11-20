package org.example.pagoqr.payment.domain.model

data class StripeRefundResult(
    val refundId: String,
    val paymentIntentId: String,
    val status: String,
    val amount: Long,
    val currency: String
)