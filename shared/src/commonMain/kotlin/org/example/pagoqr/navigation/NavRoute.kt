package org.example.pagoqr.navigation

object NavRoute {
    const val LOGIN = "login"
    const val HOME = "home"
    const val DEMO_SCENARIOS = "demo_scenarios"

    const val PAYMENT = "payment"

    private const val PAYMENT_QR_PATH = "payment_qr"
    const val PAYMENT_QR = "$PAYMENT_QR_PATH?amount={amount}&description={description}"

    fun paymentWithDetails(amount: String, description: String): String =
        "$PAYMENT_QR_PATH?amount=$amount&description=${encodeRouteText(description)}"

    const val HISTORY = "history"
    const val GENERATE_QR = "generate_qr"
    const val SCAN_QR = "scan_qr"
}
