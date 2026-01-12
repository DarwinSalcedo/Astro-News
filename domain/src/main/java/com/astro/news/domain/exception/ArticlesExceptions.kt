package com.astro.news.domain.exception

import retrofit2.HttpException
import java.io.IOException


class NotFoundArticleException(id: String) : Exception("Article $id not found")
class NetworkException(cause: Throwable? = null) : Exception("No internet connection", cause)
class ServerException( code: Int, cause: Throwable? = null) : Exception("Server error $code", cause)
class ClientException( code: Int, cause: Throwable? = null) : Exception("Client error $code", cause)
class UnknownException(cause: Throwable? = null) : Exception("Unknown error", cause)

 fun mapToDomainError(t: Throwable): Exception {
    return when (t) {
        is IOException -> NetworkException(t)
        is HttpException -> when (t.code()) {
            in 400..499 -> ClientException(t.code(), t)
            in 500..599 -> ServerException(t.code(), t)
            else -> UnknownException(t)
        }
        else -> UnknownException(t)
    }
}