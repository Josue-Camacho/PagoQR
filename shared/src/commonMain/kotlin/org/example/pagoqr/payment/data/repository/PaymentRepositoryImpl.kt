package org.example.pagoqr.payment.data.repository

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.example.pagoqr.payment.data.datasource.PaymentRemoteDataSource
import org.example.pagoqr.payment.domain.model.PaymentStatus
import org.example.pagoqr.payment.domain.model.Transaction
import org.example.pagoqr.payment.domain.repository.PaymentRepository

class PaymentRepositoryImpl(
    private val dataSource: PaymentRemoteDataSource
) : PaymentRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun processPayment(
        amount: Double,
        description: String
    ): Transaction {

        val success = dataSource.processPayment(amount)

        val timestamp = Clock.System.now()
            .toEpochMilliseconds()

        return Transaction(
            id = "TX-$timestamp",
            amount = amount,
            description = description,
            status = if (success) {
                PaymentStatus.SUCCESS
            } else {
                PaymentStatus.FAILED
            },
            timestamp = timestamp
        )
    }
}