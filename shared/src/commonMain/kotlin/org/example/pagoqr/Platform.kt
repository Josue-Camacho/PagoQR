package org.example.pagoqr

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform