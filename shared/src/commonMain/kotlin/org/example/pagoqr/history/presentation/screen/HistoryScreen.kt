package org.example.pagoqr.history.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.pagoqr.history.domain.model.HistoryTransaction
import org.example.pagoqr.history.presentation.viewmodel.HistoryEvent
import org.example.pagoqr.history.presentation.viewmodel.HistoryViewModel
import org.example.pagoqr.payment.domain.model.PaymentStatus
import org.example.pagoqr.theme.PagoQrStatusColors
import org.example.pagoqr.theme.StatusColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val onRefund: (HistoryTransaction) -> Unit = { transaction ->
        viewModel.onEvent(HistoryEvent.OnRefund(transaction))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Historial", style = typography.headlineMedium)
                Text(
                    "Todas las transacciones",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
            TextButton(
                onClick = { viewModel.onEvent(HistoryEvent.OnClearHistory) },
                enabled = state.transactions.isNotEmpty()
            ) {
                Text("Limpiar")
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let { error ->
            Text(error, color = colors.error)
        }

        if (!state.isLoading && state.transactions.isEmpty()) {
            Text("No hay transacciones todavía", color = colors.onSurfaceVariant)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.transactions, key = { it.id }) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    isRefunding = state.refundingTransactionId == transaction.id,
                    onClick = { selectedId = transaction.id },
                    onRefund = { onRefund(transaction) }
                )
            }
        }
    }

    state.transactions.firstOrNull { it.id == selectedId }?.let { transaction ->
        AlertDialog(
            onDismissRequest = { selectedId = null },
            title = { Text("Detalle de la transacción") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(transaction.description, style = typography.titleMedium)
                    Text("Monto: ${transaction.amountLabel}")
                    TransactionStatusBadge(transaction.status)
                    Text("ID: ${transaction.id}")
                    Text(formatHistoryTimestamp(transaction.timestamp))
                    state.error?.let { Text(it, color = colors.error) }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedId = null }) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                RefundButton(
                    transaction = transaction,
                    isRefunding = state.refundingTransactionId == transaction.id,
                    onClick = { onRefund(transaction) }
                )
            }
        )
    }
}

@Composable
private fun TransactionCard(
    transaction: HistoryTransaction,
    isRefunding: Boolean,
    onClick: () -> Unit,
    onRefund: () -> Unit
) {
    val typography = MaterialTheme.typography
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    transaction.description,
                    modifier = Modifier.weight(1f),
                    style = typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(transaction.amountLabel, style = typography.titleMedium)
            }
            Text(
                formatHistoryTimestamp(transaction.timestamp),
                style = typography.bodySmall,
                color = muted
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    transaction.shortId,
                    modifier = Modifier.weight(1f),
                    style = typography.bodySmall,
                    color = muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TransactionStatusBadge(transaction.status)
            }
            RefundButton(
                transaction = transaction,
                isRefunding = isRefunding,
                modifier = Modifier.align(Alignment.End),
                onClick = onRefund
            )
        }
    }
}

@Composable
private fun TransactionStatusBadge(status: String) {
    val colors = MaterialTheme.colorScheme
    val statusColors = when (status) {
        PaymentStatus.SUCCESS.name -> PagoQrStatusColors.success
        PaymentStatus.FAILED.name -> PagoQrStatusColors.failed
        PaymentStatus.REFUNDED.name -> PagoQrStatusColors.refunded
        else -> StatusColors(colors.onSurfaceVariant, colors.surfaceVariant)
    }

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = statusColors.background,
        contentColor = statusColors.accent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(6.dp).background(statusColors.accent, CircleShape))
            Text(status, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun RefundButton(
    transaction: HistoryTransaction,
    isRefunding: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (transaction.status == PaymentStatus.SUCCESS.name && transaction.id.startsWith("pi_")) {
        TextButton(onClick = onClick, enabled = !isRefunding, modifier = modifier) {
            Text(if (isRefunding) "Reembolsando..." else "Reembolsar")
        }
    }
}

private val HistoryTransaction.amountLabel: String
    get() = "Bs $amount"

private val HistoryTransaction.shortId: String
    get() = if (id.length > 16) "${id.take(9)}...${id.takeLast(4)}" else id

internal expect fun formatHistoryTimestamp(timestamp: Long): String
