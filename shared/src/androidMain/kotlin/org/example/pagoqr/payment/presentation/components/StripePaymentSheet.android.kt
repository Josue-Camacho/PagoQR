package org.example.pagoqr.payment.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.example.pagoqr.payment.config.StripeConfig

@Composable
actual fun StripePaymentSheet(
    clientSecret: String?,
    onCompleted: () -> Unit,
    onCanceled: () -> Unit,
    onFailed: (String) -> Unit
) {

    val context = LocalContext.current

    var stripeInitialized by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        PaymentConfiguration.init(
            context,
            StripeConfig.PUBLISHABLE_KEY
        )

        stripeInitialized = true
    }

    val paymentSheet =
        PaymentSheet.Builder { result ->

            when (result) {

                is PaymentSheetResult.Completed -> {
                    onCompleted()
                }

                is PaymentSheetResult.Canceled -> {
                    onCanceled()
                }

                is PaymentSheetResult.Failed -> {
                    onFailed(
                        result.error.message
                            ?: "Error procesando el pago"
                    )
                }
            }
        }.build()

    LaunchedEffect(
        clientSecret,
        stripeInitialized
    ) {

        if (
            stripeInitialized &&
            !clientSecret.isNullOrBlank()
        ) {

            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret =
                    clientSecret,

                configuration =
                    PaymentSheet.Configuration(
                        merchantDisplayName =
                            "PagoQR"
                    )
            )
        }
    }
}