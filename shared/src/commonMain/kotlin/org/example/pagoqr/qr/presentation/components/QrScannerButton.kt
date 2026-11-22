package org.example.pagoqr.qr.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun QrScannerButton(
    onScanned: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
)
