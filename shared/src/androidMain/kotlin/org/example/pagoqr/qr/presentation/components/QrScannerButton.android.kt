package org.example.pagoqr.qr.presentation.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
actual fun QrScannerButton(
    onScanned: (String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current

    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE
        )
        .enableAutoZoom()
        .build()

    val scanner = GmsBarcodeScanning.getClient(
        context,
        options
    )

    Button(
        onClick = {
            scanner.startScan()
                .addOnSuccessListener { barcode ->

                    val value = barcode.rawValue

                    if (value != null) {
                        onScanned(value)
                    } else {
                        onError("El QR no contiene información")
                    }
                }
                .addOnCanceledListener {
                    onError("Escaneo cancelado")
                }
                .addOnFailureListener { error ->
                    onError(
                        error.message
                            ?: "No se pudo escanear el QR"
                    )
                }
        }
    ) {
        Text("Escanear QR")
    }
}