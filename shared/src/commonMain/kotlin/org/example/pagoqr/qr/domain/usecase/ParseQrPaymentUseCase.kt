package org.example.pagoqr.qr.domain.usecase

import org.example.pagoqr.qr.domain.model.QrPaymentData

class ParseQrPaymentUseCase {

    operator fun invoke(
        qrContent: String
    ): QrPaymentData? {

        if (!qrContent.startsWith("PAGOQR|")) {
            return null
        }

        return try {

            val values = qrContent
                .split("|")
                .drop(1)
                .associate { part ->

                    val pieces = part.split(
                        "=",
                        limit = 2
                    )

                    pieces[0] to pieces[1]
                }

            val id = values["id"]
                ?: return null

            val amount = values["amount"]
                ?.toDoubleOrNull()
                ?: return null

            val description = values["description"]
                ?: return null

            QrPaymentData(
                paymentId = id,
                amount = amount,
                description = description
            )

        } catch (e: Exception) {
            null
        }
    }
}