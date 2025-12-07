package dev.tiemonliam.omada.api

import dev.tiemonliam.omada.data.Photo

sealed class SearchResponseState {
    data class Success(
        val photos: List<Photo>,
        val page: Int,
        val pages: Int,
        val total: Int
    ) : SearchResponseState()
    data class Error(val message: String) : SearchResponseState()
}