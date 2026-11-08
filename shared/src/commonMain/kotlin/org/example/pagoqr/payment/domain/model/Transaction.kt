package org.example.pagoqr.payment.domain.model

data class Transaction(
    val id: String,
    val amount: Double,
    val description: String,
    val status: PaymentStatus,
    val timestamp: Long
)

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}