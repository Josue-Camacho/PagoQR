package org.example.pagoqr.qr.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.pagoqr.qr.domain.model.QrPaymentData
import org.example.pagoqr.qr.domain.usecase.ParseQrPaymentUseCase
import org.example.pagoqr.qr.presentation.components.QrScannerButton
import org.koin.compose.koinInject

@Composable
fun ScanQrScreen(
    parseQrPaymentUseCase: ParseQrPaymentUseCase = koinInject()
) {

    var paymentData by remember {
        mutableStateOf<QrPaymentData?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

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

        Text(
            text = "Escanear QR",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Escanea un código generado por PagoQR"
        )

        QrScannerButton(
            onScanned = { content ->

                val result = parseQrPaymentUseCase(content)

                if (result != null) {
                    paymentData = result
                    error = null
                } else {
                    paymentData = null
                    error = "QR inválido o no pertenece a PagoQR"
                }
            },
            onError = {
                error = it
            }
        )

        paymentData?.let { data ->

            Text(
                text = "QR válido",
                style = MaterialTheme.typography.titleLarge
            )

            Text("ID: ${data.paymentId}")
            Text("Monto: Bs ${data.amount}")
            Text("Descripción: ${data.description}")
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}