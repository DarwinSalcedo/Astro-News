package com.astro.news.util

import androidx.annotation.StringRes
import com.astro.feature.R
import com.astro.news.domain.exception.ClientException
import com.astro.news.domain.exception.NetworkException
import com.astro.news.domain.exception.NotFoundArticleException
import com.astro.news.domain.exception.ServerException
import com.astro.news.domain.exception.UnknownException

@StringRes
fun Throwable.asStringRes(): Int {
    return when (this) {
        is NetworkException -> R.string.error_network
        is ServerException -> R.string.error_server
        is ClientException -> R.string.error_client
        is NotFoundArticleException -> R.string.error_not_found
        is UnknownException -> R.string.unknown_error
        else -> R.string.unknown_error
    }
}
