package org.example.pagoqr.history.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.pagoqr.history.domain.model.HistoryTransaction

interface HistoryRepository {

    fun observeHistory(): Flow<List<HistoryTransaction>>

    suspend fun save(
        transaction: HistoryTransaction
    )

    suspend fun clearHistory()
}