package org.example.pagoqr.qr.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.example.pagoqr.qr.domain.model.QrPaymentData
import org.example.pagoqr.qr.domain.usecase.GenerateQrPaymentUseCase
import org.koin.compose.koinInject

@Composable
fun GenerateQrScreen(
    generateQrPaymentUseCase: GenerateQrPaymentUseCase = koinInject()
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var qrContent by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Generar QR de cobro",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
                error = null
            },
            label = { Text("Monto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                error = null
            },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val amountValue = amount
                    .replace(",", ".")
                    .toDoubleOrNull()

                if (amountValue == null || amountValue <= 0) {
                    error = "Ingresa un monto válido"
                    return@Button
                }

                if (description.isBlank()) {
                    error = "Ingresa una descripción"
                    return@Button
                }

                val data = QrPaymentData(
                    paymentId = "PAY-${amountValue.toString().replace(".", "")}",
                    amount = amountValue,
                    description = description.trim()
                )

                qrContent = generateQrPaymentUseCase(data)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar QR")
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        qrContent?.let { content ->

            Text(
                text = "QR listo para cobrar",
                style = MaterialTheme.typography.titleMedium
            )

            Image(
                painter = rememberQrCodePainter(content),
                contentDescription = "QR de cobro",
                modifier = Modifier.size(240.dp)
            )

            Text(content)
        }
    }
}