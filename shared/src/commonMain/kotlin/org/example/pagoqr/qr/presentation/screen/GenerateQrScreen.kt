package org.example.pagoqr.qr.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.example.pagoqr.qr.domain.model.QrPaymentData
import org.example.pagoqr.qr.domain.usecase.GenerateQrPaymentUseCase
import org.example.pagoqr.ui.CompactPaymentField
import org.koin.compose.koinInject
import kotlin.math.roundToLong

@Composable
fun GenerateQrScreen(
    generateQrPaymentUseCase: GenerateQrPaymentUseCase = koinInject()
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<GeneratedQr?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Generar QR de cobro", style = typography.headlineMedium)
            Text(
                "Crea un código para compartir los datos del pago.",
                style = typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }

        val generated = result
        if (generated != null) {
            GeneratedQrCard(generated)
            OutlinedButton(onClick = { result = null }, modifier = Modifier.fillMaxWidth()) {
                Text("Generar otro QR")
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactPaymentField(
                        value = amount,
                        onValueChange = {
                            amount = it
                            error = null
                        },
                        label = "Monto",
                        prefix = { Text("Bs") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    CompactPaymentField(
                        value = description,
                        onValueChange = {
                            description = it
                            error = null
                        },
                        label = "Descripción"
                    )
                }
            }

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

                    result = GeneratedQr(generateQrPaymentUseCase(data), data)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generar QR")
            }
        }

        error?.let { Text(it, color = colors.error) }
    }
}

private data class GeneratedQr(val content: String, val payment: QrPaymentData)

@Composable
private fun GeneratedQrCard(result: GeneratedQr) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val amount = result.payment.amount
    val amountText = if (amount.isFinite() && amount < Long.MAX_VALUE / 100.0) {
        val cents = (amount * 100).roundToLong()
        "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    } else {
        amount.toString()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("✓ QR generado", color = colors.primary, style = typography.titleLarge)
            Image(
                painter = rememberQrCodePainter(result.content),
                contentDescription = "QR de cobro",
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .widthIn(max = 264.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(colors.inverseSurface, MaterialTheme.shapes.small)
                    .padding(12.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Monto", color = colors.onSurfaceVariant, style = typography.labelMedium)
                Text("Bs $amountText", style = typography.titleLarge)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Descripción", color = colors.onSurfaceVariant, style = typography.labelMedium)
                Text(result.payment.description, style = typography.bodyLarge)
            }
        }
    }
}
