package org.example.pagoqr.payment.data.repository

import org.example.pagoqr.payment.data.datasource.PaymentRemoteDataSource
import org.example.pagoqr.payment.domain.model.PaymentStatus
import org.example.pagoqr.payment.domain.model.Transaction
import org.example.pagoqr.payment.domain.repository.PaymentRepository

class PaymentRepositoryImpl(
    private val dataSource: PaymentRemoteDataSource
) : PaymentRepository {

    override suspend fun processPayment(
        amount: Double,
        description: String
    ): Transaction {

        val success = dataSource.processPayment(amount)

        return Transaction(
            id = "TX-${amount.toString().replace(".", "")}",
            amount = amount,
            description = description,
            status = if (success) {
                PaymentStatus.SUCCESS
            } else {
                PaymentStatus.FAILED
            },
            timestamp = 0L
        )
    }
}