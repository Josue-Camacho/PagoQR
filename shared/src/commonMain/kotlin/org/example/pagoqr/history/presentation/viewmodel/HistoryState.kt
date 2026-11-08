package org.example.pagoqr.history.presentation.viewmodel

import org.example.pagoqr.history.domain.model.HistoryTransaction

data class HistoryState(
    val transactions: List<HistoryTransaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)