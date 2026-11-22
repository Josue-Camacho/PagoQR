package org.example.pagoqr.qr.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.pagoqr.qr.domain.model.QrPaymentData
import org.example.pagoqr.qr.domain.usecase.ParseQrPaymentUseCase
import org.example.pagoqr.qr.presentation.components.QrScannerButton
import org.koin.compose.koinInject

@Composable
fun ScanQrScreen(
    onPayClick: (QrPaymentData) -> Unit,
    parseQrPaymentUseCase: ParseQrPaymentUseCase = koinInject()
) {
    var paymentData by remember { mutableStateOf<QrPaymentData?>(null) }
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
        ScannerIllustration()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Escanear QR", style = typography.headlineMedium)
            Text(
                "Lee un código generado por PagoQR para cargar los datos del pago.",
                color = colors.onSurfaceVariant,
                style = typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Escanea un código PagoQR", style = typography.titleMedium)
                Text(
                    "Se abrirá la cámara para detectar el código.",
                    color = colors.onSurfaceVariant,
                    style = typography.bodyMedium
                )
            }
        }

        QrScannerButton(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
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
            onError = { error = it }
        )

        Text(
            "Compatible con códigos generados por PagoQR",
            color = colors.onSurfaceVariant,
            style = typography.bodySmall,
            textAlign = TextAlign.Center
        )

        paymentData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("QR válido", color = colors.primary, style = typography.titleMedium)
                    Text("Monto: Bs ${data.amount}", style = typography.titleLarge)
                    Text("Descripción: ${data.description}")
                    Text("ID: ${data.paymentId}", color = colors.onSurfaceVariant, style = typography.bodySmall)
                    Button(onClick = { onPayClick(data) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Pagar Bs ${data.amount}")
                    }
                }
            }
        }

        error?.let { Text(it, color = colors.error) }
    }
}

@Composable
private fun ScannerIllustration() {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.size(84.dp),
        color = colors.primaryContainer,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(44.dp)) {
                val start = size.width * 0.08f
                val end = size.width - start
                val arm = size.width / 4
                val frame = Path().apply {
                    moveTo(start, start + arm)
                    lineTo(start, start)
                    lineTo(start + arm, start)
                    moveTo(end - arm, start)
                    lineTo(end, start)
                    lineTo(end, start + arm)
                    moveTo(end, end - arm)
                    lineTo(end, end)
                    lineTo(end - arm, end)
                    moveTo(start + arm, end)
                    lineTo(start, end)
                    lineTo(start, end - arm)
                }
                drawPath(
                    frame,
                    colors.onPrimaryContainer,
                    style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawLine(
                    colors.onPrimaryContainer,
                    start = Offset(start, center.y),
                    end = Offset(end, center.y),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
