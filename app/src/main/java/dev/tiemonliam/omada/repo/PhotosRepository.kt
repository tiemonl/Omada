package dev.tiemonliam.omada.repo

import dev.tiemonliam.omada.api.PhotoInfoResponseState
import dev.tiemonliam.omada.api.RecentPhotosResponseState
import dev.tiemonliam.omada.api.SearchResponseState

interface PhotosRepository {
    suspend fun getRecent(page: Int) : RecentPhotosResponseState
    suspend fun getSearch(term: String, page: Int) : SearchResponseState

    suspend fun getPhotoInfo(photoId: String) : PhotoInfoResponseState
}