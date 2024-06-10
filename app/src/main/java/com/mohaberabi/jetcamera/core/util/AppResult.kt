package com.mohaberabi.jetcamera.core.util


interface AppError
sealed interface AppResult<out D, out E : AppError> {
    data class Done<out D>(val data: D) : AppResult<D, Nothing>

    data class Error<out E : AppError>(val error: E) : AppResult<Nothing, E>
}
typealias EmptyDataResult<E> = AppResult<Unit, E>


enum class DataError : AppError {
    UNKNOWN,
    IO_ERROR
}