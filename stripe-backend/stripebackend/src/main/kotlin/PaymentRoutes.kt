package org.example

import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import io.ktor.server.routing.get
import com.stripe.model.Refund
import com.stripe.param.RefundCreateParams

@Serializable
data class CreatePaymentIntentRequest(
    val amount: Double,
    val description: String
)

@Serializable
data class CreatePaymentIntentResponse(
    val paymentIntentId: String,
    val clientSecret: String,
    val status: String,
    val amount: Long,
    val currency: String
)

@Serializable
data class PaymentErrorResponse(
    val message: String
)

@Serializable
data class PaymentIntentStatusResponse(
    val paymentIntentId: String,
    val status: String,
    val errorCode: String? = null,
    val declineCode: String? = null,
    val message: String? = null
)

@Serializable
data class RefundPaymentResponse(
    val refundId: String,
    val paymentIntentId: String,
    val status: String,
    val amount: Long,
    val currency: String
)

fun Route.paymentRoutes() {

    post("/payments/create-intent") {

        try {

            val request =
                call.receive<CreatePaymentIntentRequest>()

            if (request.amount <= 0) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    PaymentErrorResponse(
                        message = "El monto debe ser mayor a 0"
                    )
                )

                return@post
            }

            if (request.description.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    PaymentErrorResponse(
                        message = "La descripción es obligatoria"
                    )
                )

                return@post
            }

            val secretKey =
                System.getenv("STRIPE_SECRET_KEY")

            if (secretKey.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    PaymentErrorResponse(
                        message = "STRIPE_SECRET_KEY no está configurada"
                    )
                )

                return@post
            }

            Stripe.apiKey = secretKey

            val amountInMinorUnits =
                BigDecimal
                    .valueOf(request.amount)
                    .movePointRight(2)
                    .setScale(
                        0,
                        RoundingMode.HALF_UP
                    )
                    .longValueExact()

            val params =
                PaymentIntentCreateParams
                    .builder()
                    .setAmount(amountInMinorUnits)
                    .setCurrency("bob")
                    .setDescription(request.description)
                    .build()

            val paymentIntent =
                PaymentIntent.create(params)

            call.respond(
                CreatePaymentIntentResponse(
                    paymentIntentId =
                        paymentIntent.id,

                    clientSecret =
                        paymentIntent.clientSecret,

                    status =
                        paymentIntent.status,

                    amount =
                        paymentIntent.amount,

                    currency =
                        paymentIntent.currency
                )
            )

        } catch (e: StripeException) {

            call.respond(
                HttpStatusCode.BadGateway,
                PaymentErrorResponse(
                    message =
                        e.message
                            ?: "Error al comunicarse con Stripe"
                )
            )

        } catch (e: Exception) {

            call.respond(
                HttpStatusCode.InternalServerError,
                PaymentErrorResponse(
                    message =
                        e.message
                            ?: "Error interno del servidor"
                )
            )
        }
    }

    get("/payments/{paymentIntentId}") {

        try {

            val paymentIntentId =
                call.parameters["paymentIntentId"]

            if (paymentIntentId.isNullOrBlank()) {

                call.respond(
                    HttpStatusCode.BadRequest,
                    PaymentErrorResponse(
                        message = "PaymentIntent ID requerido"
                    )
                )

                return@get
            }

            val secretKey =
                System.getenv("STRIPE_SECRET_KEY")

            if (secretKey.isNullOrBlank()) {

                call.respond(
                    HttpStatusCode.InternalServerError,
                    PaymentErrorResponse(
                        message = "STRIPE_SECRET_KEY no está configurada"
                    )
                )

                return@get
            }

            Stripe.apiKey = secretKey

            val paymentIntent =
                PaymentIntent.retrieve(
                    paymentIntentId
                )

            val lastError =
                paymentIntent.lastPaymentError

            call.respond(
                PaymentIntentStatusResponse(
                    paymentIntentId =
                        paymentIntent.id,

                    status =
                        paymentIntent.status,

                    errorCode =
                        lastError?.code,

                    declineCode =
                        lastError?.declineCode,

                    message =
                        lastError?.message
                )
            )

        } catch (e: StripeException) {

            call.respond(
                HttpStatusCode.BadGateway,
                PaymentErrorResponse(
                    message =
                        e.message
                            ?: "Error consultando Stripe"
                )
            )

        } catch (e: Exception) {

            call.respond(
                HttpStatusCode.InternalServerError,
                PaymentErrorResponse(
                    message =
                        e.message
                            ?: "Error interno"
                )
            )
        }
    }



    post("/payments/{paymentIntentId}/refund") {

        try {

            val paymentIntentId =
                call.parameters["paymentIntentId"]

            if (paymentIntentId.isNullOrBlank()) {

                call.respond(
                    HttpStatusCode.BadRequest,
                    PaymentErrorResponse(
                        message = "PaymentIntent ID requerido"
                    )
                )

                return@post
            }

            val secretKey =
                System.getenv("STRIPE_SECRET_KEY")

            if (secretKey.isNullOrBlank()) {

                call.respond(
                    HttpStatusCode.InternalServerError,
                    PaymentErrorResponse(
                        message = "STRIPE_SECRET_KEY no está configurada"
                    )
                )

                return@post
            }

            Stripe.apiKey = secretKey

            val paymentIntent =
                PaymentIntent.retrieve(paymentIntentId)

            if (paymentIntent.status != "succeeded") {

                call.respond(
                    HttpStatusCode.BadRequest,
                    PaymentErrorResponse(
                        message =
                            "Solo se pueden reembolsar pagos completados"
                    )
                )

                return@post
            }

            val params =
                RefundCreateParams
                    .builder()
                    .setPaymentIntent(paymentIntentId)
                    .setReason(
                        RefundCreateParams.Reason.DUPLICATE
                    )
                    .build()

            val refund =
                Refund.create(params)

            call.respond(
                RefundPaymentResponse(
                    refundId = refund.id,
                    paymentIntentId =
                        refund.paymentIntent,
                    status = refund.status,
                    amount = refund.amount,
                    currency = refund.currency
                )
            )

        } catch (e: StripeException) {

            call.respond(
                HttpStatusCode.BadGateway,
                PaymentErrorResponse(
                    message =
                        e.message
                            ?: "Error realizando el reembolso"
                )
            )

        } catch (e: Exception) {

            call.respond(
                HttpStatusCode.InternalServerError,
                PaymentErrorResponse(
                    message =
                        e.message
                            ?: "Error interno"
                )
            )
        }
    }
}