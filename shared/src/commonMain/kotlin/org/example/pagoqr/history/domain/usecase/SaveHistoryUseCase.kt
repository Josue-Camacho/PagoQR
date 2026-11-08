package org.example.pagoqr.history.domain.usecase

import org.example.pagoqr.history.domain.model.HistoryTransaction
import org.example.pagoqr.history.domain.repository.HistoryRepository

class SaveHistoryUseCase(
    private val repository: HistoryRepository
) {

    suspend operator fun invoke(
        transaction: HistoryTransaction
    ) {
        repository.save(transaction)
    }
}