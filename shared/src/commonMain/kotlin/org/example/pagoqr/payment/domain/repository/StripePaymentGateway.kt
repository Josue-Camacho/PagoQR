package org.example.pagoqr.payment.domain.repository

import org.example.pagoqr.payment.domain.model.StripePaymentIntent
import org.example.pagoqr.payment.domain.model.StripePaymentStatus
import org.example.pagoqr.payment.domain.model.StripeRefundResult

interface StripePaymentGateway {

    suspend fun createPaymentIntent(
        amount: Double,
        description: String
    ): StripePaymentIntent

    suspend fun getPaymentStatus(
        paymentIntentId: String
    ): StripePaymentStatus

    suspend fun refundPayment(
        paymentIntentId: String
    ): StripeRefundResult
}