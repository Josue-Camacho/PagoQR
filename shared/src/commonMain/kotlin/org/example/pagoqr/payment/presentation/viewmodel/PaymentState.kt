package org.example.pagoqr.payment.presentation.viewmodel

import org.example.pagoqr.payment.domain.model.Transaction

data class PaymentState(
    val amount: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val transaction: Transaction? = null,
    val error: String? = null
)