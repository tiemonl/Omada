package dev.tiemonliam.omada.api

import dev.tiemonliam.omada.data.Photo
import dev.tiemonliam.omada.data.PhotoDetail

sealed class PhotoInfoResponseState {
    data class Success(val photo: PhotoDetail) : PhotoInfoResponseState()
    data class Error(val message: String) : PhotoInfoResponseState()
}