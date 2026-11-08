package org.example.pagoqr.payment.data.datasource

import kotlinx.coroutines.delay

class PaymentRemoteDataSource {

    suspend fun processPayment(amount: Double): Boolean {
        delay(1000)

        return amount > 0
    }
}