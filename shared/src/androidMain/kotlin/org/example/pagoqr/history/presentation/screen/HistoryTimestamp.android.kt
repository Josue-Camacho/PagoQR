package org.example.pagoqr.history.presentation.screen

import java.text.DateFormat
import java.util.Date

internal actual fun formatHistoryTimestamp(timestamp: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        .format(Date(timestamp))
