package org.example.pagoqr.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onDemoScenariosClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onGenerateQrClick: () -> Unit,
    onScanQrClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("PagoQR", style = typography.headlineLarge)
            Text(
                "Pagos con códigos QR y Stripe",
                color = colors.onSurfaceVariant,
                style = typography.bodyLarge
            )
        }

        Card(
            onClick = onDemoScenariosClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = colors.surface,
                contentColor = colors.onSurface
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(DemoIcon, contentDescription = null, tint = colors.primary)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Modo demo", style = typography.labelLarge, color = colors.primary)
                    Text("Escenarios de prueba", style = typography.titleLarge)
                    Text(
                        "Ejecuta los 4 casos preparados para la demostración.",
                        style = typography.bodyMedium
                    )
                }
                Icon(ChevronIcon, contentDescription = null)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Modo libre", style = typography.titleLarge)
                Text(
                    "Usa las funciones de PagoQR sin datos precargados.",
                    color = colors.onSurfaceVariant,
                    style = typography.bodyMedium
                )
            }

            HomeActionCard("Realizar pago", PaymentIcon, onPaymentClick)
            HomeActionCard("Generar QR", QrIcon, onGenerateQrClick)
            HomeActionCard("Escanear QR", ScanIcon, onScanQrClick)
            HomeActionCard("Historial", HistoryIcon, onHistoryClick)
        }
    }
}

@Composable
private fun HomeActionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,
            contentColor = colors.onSurface
        )
    ) {
        Row(
            modifier = Modifier.heightIn(min = 52.dp).padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant)
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Icon(ChevronIcon, contentDescription = null, tint = colors.onSurfaceVariant)
        }
    }
}

private fun homeIcon(name: String, draw: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = draw
        )
    }.build()

private val DemoIcon = homeIcon("Demo") {
    moveTo(9f, 3f)
    horizontalLineTo(15f)
    moveTo(10f, 3f)
    verticalLineTo(9f)
    lineTo(4f, 19f)
    quadTo(3f, 21f, 5f, 21f)
    horizontalLineTo(19f)
    quadTo(21f, 21f, 20f, 19f)
    lineTo(14f, 9f)
    verticalLineTo(3f)
    moveTo(7f, 14f)
    horizontalLineTo(17f)
}

private val PaymentIcon = homeIcon("Pago") {
    moveTo(3f, 5f)
    horizontalLineTo(21f)
    verticalLineTo(19f)
    horizontalLineTo(3f)
    close()
    moveTo(3f, 10f)
    horizontalLineTo(21f)
    moveTo(6f, 15f)
    horizontalLineTo(10f)
}

private val QrIcon = homeIcon("QR") {
    moveTo(3f, 3f)
    horizontalLineTo(9f)
    verticalLineTo(9f)
    horizontalLineTo(3f)
    close()
    moveTo(15f, 3f)
    horizontalLineTo(21f)
    verticalLineTo(9f)
    horizontalLineTo(15f)
    close()
    moveTo(3f, 15f)
    horizontalLineTo(9f)
    verticalLineTo(21f)
    horizontalLineTo(3f)
    close()
    moveTo(15f, 15f)
    verticalLineTo(21f)
    horizontalLineTo(21f)
    moveTo(21f, 15f)
    verticalLineTo(18f)
}

private val ScanIcon = homeIcon("Escanear") {
    moveTo(3f, 8f)
    verticalLineTo(3f)
    horizontalLineTo(8f)
    moveTo(16f, 3f)
    horizontalLineTo(21f)
    verticalLineTo(8f)
    moveTo(21f, 16f)
    verticalLineTo(21f)
    horizontalLineTo(16f)
    moveTo(8f, 21f)
    horizontalLineTo(3f)
    verticalLineTo(16f)
    moveTo(3f, 12f)
    horizontalLineTo(21f)
}

private val HistoryIcon = homeIcon("Historial") {
    moveTo(3f, 11f)
    arcTo(9f, 9f, 0f, true, true, 5f, 18f)
    moveTo(3f, 6f)
    verticalLineTo(11f)
    horizontalLineTo(8f)
    moveTo(12f, 7f)
    verticalLineTo(12f)
    lineTo(15f, 14f)
}

private val ChevronIcon = homeIcon("Abrir") {
    moveTo(9f, 6f)
    lineTo(15f, 12f)
    lineTo(9f, 18f)
}
