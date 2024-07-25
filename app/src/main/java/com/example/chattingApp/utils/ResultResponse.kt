package com.example.chattingApp.utils

sealed class ResultResponse <T>(
    data: T? = null,
    exception: Exception? = null
) {
    data class Success <T>(val data: T) : ResultResponse<T>(data, null)

    data class Failed <T>(
        val exception: Exception
    ) : ResultResponse<T>(null, exception)

    // use to transform Result<T> to Result<R> when T -> TR transform is possible
    inline fun <R> map(transform: (T) -> R): ResultResponse<R> {
        return when (this) {
            is Success -> try {
                Success(transform(data))
            } catch (e: Exception) {
                Failed(e)
            }
            is Failed -> Failed(exception)
        }
    }

}

fun <T> ResultResponse<T>.handle(
    onSuccess: (T) -> Unit,
    onError: (Exception) -> Unit
) {
    when (this) {
        is ResultResponse.Success -> onSuccess(this.data)
        is ResultResponse.Failed -> onError(this.exception)
    }
}

inline fun <T> ResultResponse<T>.onSuccess(action: (T) -> Unit): ResultResponse<T> {
    if (this is ResultResponse.Success) action(data)
    return this
}
inline fun <T> ResultResponse<T>.onFailure(action: (Exception) -> Unit): ResultResponse<T> {
    if (this is ResultResponse.Failed) action(exception)
    return this
}

// can also make a SafeApi call/or anything, higher order function which will execute api function
// and save result in ApiResponse, but i don't like it

inline fun <T> safeApiCall(apiCall: () -> T): ResultResponse<T> {
    return try {
        ResultResponse.Success(apiCall.invoke())
    } catch (e: Exception) {
        ResultResponse.Failed(e)
    }
}