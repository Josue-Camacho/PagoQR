package org.example.pagoqr.payment.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.example.pagoqr.payment.domain.model.PaymentStatus
import org.example.pagoqr.payment.domain.model.Transaction
import org.example.pagoqr.payment.presentation.components.StripePaymentSheet
import org.example.pagoqr.payment.presentation.viewmodel.PaymentEvent
import org.example.pagoqr.payment.presentation.viewmodel.PaymentViewModel
import org.example.pagoqr.theme.PagoQrStatusColors
import org.example.pagoqr.ui.CompactPaymentField
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToLong

@Composable
fun PaymentScreen(
    onHistoryClick: () -> Unit,
    onNewPaymentClick: () -> Unit,
    initialAmount: String? = null,
    initialDescription: String? = null,
    viewModel: PaymentViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()


    StripePaymentSheet(
        clientSecret =
            state.stripeClientSecret,

        onCompleted = {
            viewModel.onEvent(
                PaymentEvent.OnStripePaymentCompleted
            )
        },

        onCanceled = {
            viewModel.onEvent(
                PaymentEvent.OnStripePaymentCanceled
            )
        },

        onFailed = { message ->
            viewModel.onEvent(
                PaymentEvent.OnStripePaymentFailed(
                    message
                )
            )
        }
    )

    LaunchedEffect(initialAmount, initialDescription) {

        if (!initialAmount.isNullOrBlank()) {
            viewModel.onEvent(
                PaymentEvent.OnAmountChange(initialAmount)
            )
        }

        if (!initialDescription.isNullOrBlank()) {
            viewModel.onEvent(
                PaymentEvent.OnDescriptionChange(initialDescription)
            )
        }
    }

    val transaction = state.transaction
    if (transaction?.status == PaymentStatus.SUCCESS) {
        PaymentReceipt(transaction, onHistoryClick, onNewPaymentClick)
        return
    }

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
            Text("Pago con QR", style = typography.headlineMedium)
            Text(
                "Completa los datos para iniciar el pago.",
                color = colors.onSurfaceVariant,
                style = typography.bodyMedium
            )
        }

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
                    value = state.amount,
                    onValueChange = { viewModel.onEvent(PaymentEvent.OnAmountChange(it)) },
                    label = "Monto",
                    prefix = { Text("Bs") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                CompactPaymentField(
                    value = state.description,
                    onValueChange = { viewModel.onEvent(PaymentEvent.OnDescriptionChange(it)) },
                    label = "Descripción"
                )
            }
        }

        Button(
            onClick = {
                viewModel.onEvent(
                    PaymentEvent.OnStripePay
                )
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
        ) {

            Text(
                if (state.isLoading) {
                    "Conectando con Stripe..."
                } else {
                    "Pagar con Stripe"
                }
            )
        }

        Text(
            "Pago procesado de forma segura con Stripe",
            color = colors.onSurfaceVariant,
            style = typography.bodySmall,
            textAlign = TextAlign.Center
        )

        if (state.isLoading) {
            CircularProgressIndicator()
            Text("Procesando pago...")
        }

        state.error?.let { error ->
            if (error == "Fondos insuficientes") {
                val warning = PagoQrStatusColors.warning
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = warning.background,
                    contentColor = warning.accent
                ) {
                    Text(error, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            } else {
                Text(error, color = colors.error)
            }
        }

        state.transaction?.let { transaction ->

            val message = when (transaction.status) {
                PaymentStatus.SUCCESS -> "Pago exitoso"
                PaymentStatus.FAILED -> "Pago rechazado"
                PaymentStatus.PENDING -> "Pago pendiente"
                PaymentStatus.REFUNDED -> "Pago reembolsado"
            }

            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge
            )

            Text("ID: ${transaction.id}")
            Text("Monto: Bs ${transaction.amount}")
            Text("Descripción: ${transaction.description}")

            val qrData =
                "PAGOQR|id=${transaction.id}|amount=${transaction.amount}|description=${transaction.description}"

            Text(
                text = "QR de la transacción",
                style = MaterialTheme.typography.titleMedium
            )

            Image(
                painter = rememberQrCodePainter(qrData),
                contentDescription = "QR de la transacción",
                modifier = Modifier
                    .size(244.dp)
                    .background(colors.inverseSurface, MaterialTheme.shapes.small)
                    .padding(12.dp)
            )
        }
    }
}

@Composable
private fun PaymentReceipt(
    transaction: Transaction,
    onHistoryClick: () -> Unit,
    onNewPaymentClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val cents = (transaction.amount * 100).roundToLong()
    val amount = "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    val stripeId = transaction.id
    val shortId = if (stripeId.length > 16) {
        "${stripeId.take(9)}...${stripeId.takeLast(4)}"
    } else {
        stripeId
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = colors.surface,
                contentColor = colors.onSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "✓ Pago exitoso",
                        style = typography.titleMedium,
                        color = PagoQrStatusColors.success.accent
                    )
                    Text("Bs $amount", style = typography.headlineLarge)
                }
                Column {
                    Text("Descripción", style = typography.labelMedium)
                    Text(transaction.description, style = typography.bodyLarge)
                }
                Column {
                    Text("ID de Stripe", style = typography.labelMedium)
                    Text(shortId, style = typography.bodyMedium)
                }
            }
        }
        Button(onClick = onHistoryClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ver historial")
        }
        OutlinedButton(onClick = onNewPaymentClick, modifier = Modifier.fillMaxWidth()) {
            Text("Nuevo pago")
        }
    }
}
