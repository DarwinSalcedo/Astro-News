package com.astro.news.domain.exception


class NotFoundArticleException(id: String) : Exception("Article $id not found")