package dev.tiemonliam.omada.api.dto

import com.squareup.moshi.JsonClass
import dev.tiemonliam.omada.data.Photo

@JsonClass(generateAdapter = true)
data class PhotosDTO(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val photo: List<PhotoDTO>,
    val total: Int
) {
    @JsonClass(generateAdapter = true)
    data class PhotoDTO(
        val farm: Int,
        val id: String,
        val isfamily: Int,
        val isfriend: Int,
        val ispublic: Int,
        val owner: String,
        val secret: String,
        val server: String,
        val title: String
    ) {
        fun mapToPhoto(): Photo = Photo(
            farm = farm,
            id = id,
            isfamily = isfamily,
            isfriend = isfriend,
            ispublic = ispublic,
            owner = owner,
            secret = secret,
            server = server,
            title = title,
            url = photoUrl()
        )

        private fun photoUrl() =
            "https://live.staticflickr.com/${this.server}/${this.id}_${this.secret}.jpg"
    }
    fun mapToPhotos(): List<Photo> = photo.map { it.mapToPhoto() }
}