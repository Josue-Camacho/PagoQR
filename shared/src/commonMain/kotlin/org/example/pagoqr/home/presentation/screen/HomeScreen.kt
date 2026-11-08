package org.example.pagoqr.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onPaymentClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onGenerateQrClick: () -> Unit,
    onScanQrClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PagoQR")

        Button(onClick = onPaymentClick) {
            Text("Realizar pago")
        }

        Button(onClick = onHistoryClick) {
            Text("Historial")
        }

        Button(onClick = onGenerateQrClick) {
            Text("Generar QR de cobro")
        }

        Button(onClick = onScanQrClick) {
            Text("Escanear QR")
        }
    }
}



















