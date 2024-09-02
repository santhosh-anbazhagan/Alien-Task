package com.alienspace.alientask.util

import androidx.compose.material3.SnackbarDuration

sealed class Async<out T> {
    object Loading : Async<Nothing>()

    data class Error(val errorMessage: Int) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()

}
