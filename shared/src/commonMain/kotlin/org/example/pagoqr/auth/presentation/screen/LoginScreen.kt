package org.example.pagoqr.auth.presentation.screen

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WelcomeQrIcon()
                Text("PagoQR", style = typography.headlineLarge)
                Text(
                    "Pagos con códigos QR y Stripe",
                    color = colors.onSurfaceVariant,
                    style = typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Surface(
                modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth(),
                color = colors.surface,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Bienvenido", style = typography.headlineSmall)
                    Text(
                        "Accede para continuar con tus pagos y escenarios de prueba.",
                        color = colors.onSurfaceVariant,
                        style = typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                    ) {
                        Text("Ingresar")
                    }
                    Text(
                        "Entorno de demostración",
                        color = colors.onSurfaceVariant,
                        style = typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeQrIcon() {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.size(72.dp),
        color = colors.primaryContainer,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(32.dp)) {
                val unit = size.width / 11f
                val ink = colors.onPrimaryContainer
                val finderOrigins = listOf(Offset(1f, 1f), Offset(7f, 1f), Offset(1f, 7f))
                for (origin in finderOrigins) {
                    drawRect(ink, origin * unit, Size(3f * unit, 3f * unit), style = Stroke(unit))
                    drawRect(ink, (origin + Offset(1f, 1f)) * unit, Size(unit, unit))
                }
                drawRect(ink, Offset(6.5f, 6.5f) * unit, Size(2f * unit, 2f * unit))
                drawRect(ink, Offset(9.5f, 6.5f) * unit, Size(unit, 4f * unit))
                drawRect(ink, Offset(6.5f, 9.5f) * unit, Size(2f * unit, unit))
            }
        }
    }
}
