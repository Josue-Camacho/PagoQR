package org.example.pagoqr.history.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.pagoqr.history.domain.model.HistoryTransaction
import org.example.pagoqr.history.domain.repository.HistoryRepository

class ObserveHistoryUseCase(
    private val repository: HistoryRepository
) {

    operator fun invoke(): Flow<List<HistoryTransaction>> {
        return repository.observeHistory()
    }
}