package org.example.pagoqr.history.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.pagoqr.history.data.dao.TransactionDao
import org.example.pagoqr.history.data.entity.TransactionEntity
import org.example.pagoqr.history.domain.model.HistoryTransaction
import org.example.pagoqr.history.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val dao: TransactionDao
) : HistoryRepository {

    override fun observeHistory(): Flow<List<HistoryTransaction>> {
        return dao.observeAll().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun save(
        transaction: HistoryTransaction
    ) {
        dao.insert(
            TransactionEntity(
                id = transaction.id,
                amount = transaction.amount,
                description = transaction.description,
                status = transaction.status,
                timestamp = transaction.timestamp
            )
        )
    }

    override suspend fun clearHistory() {
        dao.deleteAll()
    }

    private fun TransactionEntity.toDomain(): HistoryTransaction {
        return HistoryTransaction(
            id = id,
            amount = amount,
            description = description,
            status = status,
            timestamp = timestamp
        )
    }
}