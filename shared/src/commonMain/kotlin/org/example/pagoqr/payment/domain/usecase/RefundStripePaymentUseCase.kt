package org.example.pagoqr.payment.domain.usecase

import org.example.pagoqr.payment.domain.model.StripeRefundResult
import org.example.pagoqr.payment.domain.repository.StripePaymentGateway

class RefundStripePaymentUseCase(
    private val gateway: StripePaymentGateway
) {

    suspend operator fun invoke(
        paymentIntentId: String
    ): StripeRefundResult {
        return gateway.refundPayment(
            paymentIntentId
        )
    }
}