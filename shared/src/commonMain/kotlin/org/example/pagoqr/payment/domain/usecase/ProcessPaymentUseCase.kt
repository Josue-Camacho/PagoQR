package org.example.pagoqr.payment.domain.usecase

import org.example.pagoqr.payment.domain.model.Transaction
import org.example.pagoqr.payment.domain.repository.PaymentRepository

class ProcessPaymentUseCase(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(
        amount: Double,
        description: String
    ): Transaction {
        return repository.processPayment(
            amount = amount,
            description = description
        )
    }
}