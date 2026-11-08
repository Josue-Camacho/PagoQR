package org.example.pagoqr.qr.domain.usecase

import org.example.pagoqr.qr.domain.model.QrPaymentData

class GenerateQrPaymentUseCase {

    operator fun invoke(
        data: QrPaymentData
    ): String {
        return buildString {
            append("PAGOQR")
            append("|id=${data.paymentId}")
            append("|amount=${data.amount}")
            append("|description=${data.description}")
        }
    }
}