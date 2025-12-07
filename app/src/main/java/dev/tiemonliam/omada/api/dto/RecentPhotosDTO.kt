package dev.tiemonliam.omada.api.dto


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecentPhotosDTO(
    val photos: PhotosDTO,
    val stat: String
)