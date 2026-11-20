package org.example.pagoqr.payment.domain.usecase

import org.example.pagoqr.payment.domain.model.StripePaymentStatus
import org.example.pagoqr.payment.domain.repository.StripePaymentGateway

class GetStripePaymentStatusUseCase(
    private val gateway: StripePaymentGateway
) {

    suspend operator fun invoke(
        paymentIntentId: String
    ): StripePaymentStatus {
        return gateway.getPaymentStatus(
            paymentIntentId
        )
    }
}