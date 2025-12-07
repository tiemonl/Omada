package dev.tiemonliam.omada.api

import dev.tiemonliam.omada.data.Photo

sealed class RecentPhotosResponseState {
    data class Success(
        val photos: List<Photo>,
        val page: Int,
        val pages: Int,
        val total: Int
    ) : RecentPhotosResponseState()
    data class Error(val message: String) : RecentPhotosResponseState()
}