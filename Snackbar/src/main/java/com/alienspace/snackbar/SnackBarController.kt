package com.alienspace.snackbar

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


data class SnackBarEvent(
    val msg: String? = "",
    val action: SnackBarAction? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)

data class SnackBarAction(
    val name: String,
    val action: () -> Unit,
)


object SnackBarController {
    private val _event = Channel<SnackBarEvent>()
    val event = _event.receiveAsFlow()

    suspend fun sendEvent(event: SnackBarEvent) {
        _event.send(event)
    }

}