package com.example.chattingApp.utils

sealed class Result <T>(
    data: T? = null,
    exception: Exception? = null
) {
    data class Success <T>(val data: T) : Result<T>(data, null)

    data class Error <T>(
        val exception: Exception
    ) : Result<T>(null, exception)

}

// can also make a SafeApi call/or anything, higher order function which will execute api function
// and save result in ApiResponse, but i don't like it

inline fun <T> safeApiCall(apiCall: () -> T): Result<T> {
    return try {
        Result.Success(apiCall.invoke())
    } catch (e: Exception) {
        Result.Error(e)
    }
}