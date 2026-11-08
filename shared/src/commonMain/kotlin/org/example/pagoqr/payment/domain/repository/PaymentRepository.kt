package org.example.pagoqr.payment.domain.repository

import org.example.pagoqr.payment.domain.model.Transaction

interface PaymentRepository {
    suspend fun processPayment(
        amount: Double,
        description: String
    ): Transaction
}