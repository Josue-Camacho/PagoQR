package org.example.pagoqr.payment.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun StripePaymentSheet(
    clientSecret: String?,
    onCompleted: () -> Unit,
    onCanceled: () -> Unit,
    onFailed: (String) -> Unit
)