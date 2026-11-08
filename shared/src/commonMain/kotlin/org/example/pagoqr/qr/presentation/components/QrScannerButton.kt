package org.example.pagoqr.qr.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun QrScannerButton(
    onScanned: (String) -> Unit,
    onError: (String) -> Unit
)