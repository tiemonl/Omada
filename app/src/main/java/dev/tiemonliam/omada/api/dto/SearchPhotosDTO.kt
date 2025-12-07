package dev.tiemonliam.omada.api.dto


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchPhotosDTO(
    val photos: PhotosDTO,
    val stat: String
)