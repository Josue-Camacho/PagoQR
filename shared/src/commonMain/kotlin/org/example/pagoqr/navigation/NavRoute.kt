package org.example.pagoqr.navigation

object NavRoute {
    const val LOGIN = "login"
    const val HOME = "home"

    const val PAYMENT = "payment"

    const val PAYMENT_QR =
        "payment_qr?amount={amount}&description={description}"

    const val HISTORY = "history"
    const val GENERATE_QR = "generate_qr"
    const val SCAN_QR = "scan_qr"
}