package org.example.pagoqr.history.presentation.viewmodel

import org.example.pagoqr.history.domain.model.HistoryTransaction

sealed interface HistoryEvent {

    data object OnClearHistory : HistoryEvent

    data class OnRefund(
        val transaction: HistoryTransaction
    ) : HistoryEvent
}