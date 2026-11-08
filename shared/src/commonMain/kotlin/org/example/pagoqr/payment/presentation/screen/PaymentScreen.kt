package org.example.pagoqr.payment.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.pagoqr.payment.domain.model.PaymentStatus
import org.example.pagoqr.payment.presentation.viewmodel.PaymentEvent
import org.example.pagoqr.payment.presentation.viewmodel.PaymentViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Pago con QR",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = state.amount,
            onValueChange = {
                viewModel.onEvent(
                    PaymentEvent.OnAmountChange(it)
                )
            },
            label = {
                Text("Monto")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = {
                viewModel.onEvent(
                    PaymentEvent.OnDescriptionChange(it)
                )
            },
            label = {
                Text("Descripción")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.onEvent(PaymentEvent.OnPay)
            },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pagar")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
            Text("Procesando pago...")
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        state.transaction?.let { transaction ->

            val message = when (transaction.status) {
                PaymentStatus.SUCCESS -> "Pago exitoso"
                PaymentStatus.FAILED -> "Pago rechazado"
                PaymentStatus.PENDING -> "Pago pendiente"
            }

            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge
            )

            Text("ID: ${transaction.id}")
            Text("Monto: Bs ${transaction.amount}")
            Text("Descripción: ${transaction.description}")
        }
    }
}