package org.example.pagoqr.history.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val description: String,
    val status: String,
    val timestamp: Long
)