package org.example.pagoqr.navigation

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun encodeRouteText(value: String): String {
    return Base64.UrlSafe.encode(
        value.encodeToByteArray()
    )
}

@OptIn(ExperimentalEncodingApi::class)
fun decodeRouteText(value: String): String {
    return try {
        Base64.UrlSafe
            .decode(value)
            .decodeToString()
    } catch (e: Exception) {
        value
    }
}