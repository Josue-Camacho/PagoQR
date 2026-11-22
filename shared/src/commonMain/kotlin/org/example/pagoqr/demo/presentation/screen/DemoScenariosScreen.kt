package org.example.pagoqr.demo.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.pagoqr.payment.domain.model.PaymentStatus

@Composable
fun DemoScenariosScreen(
    onBackClick: () -> Unit,
    onPaymentClick: (amount: String, description: String) -> Unit,
    onRefundClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBackClick) {
            Text("Volver")
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Escenarios de prueba", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Selecciona un escenario para realizar la demostración.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        DemoScenarioCard(
            number = 1,
            title = "Pago exitoso",
            description = "Simula una transacción aprobada por Stripe.",
            expectedStatus = PaymentStatus.SUCCESS,
            testCard = "4242 4242 4242 4242",
            onClick = { onPaymentClick("45", "Prueba pago exitoso") }
        )
        DemoScenarioCard(
            number = 2,
            title = "Fondos insuficientes",
            description = "Simula un rechazo por falta de fondos.",
            expectedStatus = PaymentStatus.FAILED,
            testCard = "4000 0000 0000 9995",
            onClick = { onPaymentClick("45", "Prueba fondos insuficientes") }
        )
        DemoScenarioCard(
            number = 3,
            title = "Tarjeta reportada como robada",
            description = "Simula un rechazo de seguridad.",
            expectedStatus = PaymentStatus.FAILED,
            testCard = "4000 0000 0000 9979",
            onClick = { onPaymentClick("55", "Prueba tarjeta robada") }
        )
        DemoScenarioCard(
            number = 4,
            title = "Reembolso",
            description = "Realiza un reembolso sobre un pago exitoso.",
            expectedStatus = PaymentStatus.REFUNDED,
            onClick = onRefundClick
        )
    }
}

@Composable
private fun DemoScenarioCard(
    number: Int,
    title: String,
    description: String,
    expectedStatus: PaymentStatus,
    testCard: String? = null,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "ESCENARIO $number",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Resultado esperado: ${expectedStatus.name}",
                style = MaterialTheme.typography.labelLarge
            )
            if (testCard != null) {
                Text("Tarjeta de prueba: $testCard", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
