package org.example.pagoqr.history.domain.model

data class HistoryTransaction(
    val id: String,
    val amount: Double,
    val description: String,
    val status: String,
    val timestamp: Long
)