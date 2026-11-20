package org.example.pagoqr.payment.domain.usecase

import org.example.pagoqr.payment.domain.model.StripePaymentIntent
import org.example.pagoqr.payment.domain.repository.StripePaymentGateway

class CreateStripePaymentIntentUseCase(
    private val gateway: StripePaymentGateway
) {

    suspend operator fun invoke(
        amount: Double,
        description: String
    ): StripePaymentIntent {
        return gateway.createPaymentIntent(
            amount = amount,
            description = description
        )
    }
}