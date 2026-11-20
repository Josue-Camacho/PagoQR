package org.example.pagoqr.payment.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.pagoqr.payment.domain.model.StripePaymentIntent
import org.example.pagoqr.payment.domain.repository.StripePaymentGateway
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import org.example.pagoqr.payment.domain.model.StripePaymentStatus
import org.example.pagoqr.payment.domain.model.StripeRefundResult

class StripePaymentGatewayImpl : StripePaymentGateway {

    override suspend fun createPaymentIntent(
        amount: Double,
        description: String
    ): StripePaymentIntent = withContext(Dispatchers.IO) {

        val url = URL(
            "http://127.0.0.1:8080/payments/create-intent"
        )

        val connection =
            url.openConnection() as HttpURLConnection

        try {

            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.doOutput = true
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000

            val requestBody =
                JSONObject()
                    .put("amount", amount)
                    .put("description", description)
                    .toString()

            connection.outputStream
                .bufferedWriter()
                .use { writer ->
                    writer.write(requestBody)
                }

            val responseCode =
                connection.responseCode

            val responseText =
                if (responseCode in 200..299) {

                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                } else {

                    connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        .orEmpty()
                }

            if (responseCode !in 200..299) {

                val message =
                    runCatching {
                        JSONObject(responseText)
                            .optString("message")
                    }.getOrNull()

                error(
                    message
                        ?.takeIf { it.isNotBlank() }
                        ?: "Error HTTP $responseCode"
                )
            }

            val json =
                JSONObject(responseText)

            StripePaymentIntent(
                paymentIntentId =
                    json.getString(
                        "paymentIntentId"
                    ),

                clientSecret =
                    json.getString(
                        "clientSecret"
                    ),

                status =
                    json.getString(
                        "status"
                    )
            )

        } finally {

            connection.disconnect()
        }
    }

    override suspend fun getPaymentStatus(
        paymentIntentId: String
    ): StripePaymentStatus = withContext(Dispatchers.IO) {

        val url = URL(
            "http://127.0.0.1:8080/payments/$paymentIntentId"
        )

        val connection =
            url.openConnection() as HttpURLConnection

        try {

            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000

            val responseCode =
                connection.responseCode

            val responseText =
                if (responseCode in 200..299) {

                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                } else {

                    connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        .orEmpty()
                }

            if (responseCode !in 200..299) {

                val message =
                    runCatching {
                        JSONObject(responseText)
                            .optString("message")
                    }.getOrNull()

                error(
                    message
                        ?.takeIf { it.isNotBlank() }
                        ?: "Error HTTP $responseCode"
                )
            }

            val json =
                JSONObject(responseText)

            StripePaymentStatus(
                paymentIntentId =
                    json.getString("paymentIntentId"),

                status =
                    json.getString("status"),

                errorCode =
                    if (json.isNull("errorCode")) {
                        null
                    } else {
                        json.getString("errorCode")
                    },

                declineCode =
                    if (json.isNull("declineCode")) {
                        null
                    } else {
                        json.getString("declineCode")
                    },

                message =
                    if (json.isNull("message")) {
                        null
                    } else {
                        json.getString("message")
                    }
            )

        } finally {

            connection.disconnect()
        }
    }

    override suspend fun refundPayment(
        paymentIntentId: String
    ): StripeRefundResult = withContext(Dispatchers.IO) {

        val url = URL(
            "http://127.0.0.1:8080/payments/$paymentIntentId/refund"
        )

        val connection =
            url.openConnection() as HttpURLConnection

        try {

            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000

            val responseCode =
                connection.responseCode

            val responseText =
                if (responseCode in 200..299) {

                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                } else {

                    connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        .orEmpty()
                }

            if (responseCode !in 200..299) {

                val message =
                    runCatching {
                        JSONObject(responseText)
                            .optString("message")
                    }.getOrNull()

                error(
                    message
                        ?.takeIf { it.isNotBlank() }
                        ?: "Error HTTP $responseCode"
                )
            }

            val json =
                JSONObject(responseText)

            StripeRefundResult(
                refundId =
                    json.getString("refundId"),

                paymentIntentId =
                    json.getString("paymentIntentId"),

                status =
                    json.getString("status"),

                amount =
                    json.getLong("amount"),

                currency =
                    json.getString("currency")
            )

        } finally {

            connection.disconnect()
        }
    }
}