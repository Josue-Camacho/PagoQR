package org.example.pagoqr.history.presentation.viewmodel

sealed interface HistoryEvent {
    data object OnClearHistory : HistoryEvent
}