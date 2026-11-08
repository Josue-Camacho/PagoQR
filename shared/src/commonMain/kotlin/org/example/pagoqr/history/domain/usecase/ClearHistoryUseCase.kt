package org.example.pagoqr.history.domain.usecase

import org.example.pagoqr.history.domain.repository.HistoryRepository

class ClearHistoryUseCase(
    private val repository: HistoryRepository
) {

    suspend operator fun invoke() {
        repository.clearHistory()
    }
}