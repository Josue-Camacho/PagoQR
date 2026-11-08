package org.example.pagoqr.qr.domain.model

data class QrPaymentData(
    val paymentId: String,
    val amount: Double,
    val description: String
)