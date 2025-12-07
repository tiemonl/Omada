package dev.tiemonliam.omada.api.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.tiemonliam.omada.data.PhotoDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@JsonClass(generateAdapter = true)
data class PhotoInfoDTO(
    val photo: PhotoDetailDTO,
    val stat: String
) {
    @JsonClass(generateAdapter = true)
    data class PhotoDetailDTO(
        val comments: Comments,
        val dates: DatesDTO,
        val dateuploaded: String,
        val description: DescriptionDTO,
        val editability: EditabilityDTO,
        val farm: Int,
        val id: String,
        val isfavorite: Int,
        val license: String,
        val media: String,
        val notes: NotesDTO,
        val owner: OwnerDTO,
        val people: PeopleDTO,
        val publiceditability: PubliceditabilityDTO,
        val rotation: Int,
        @Json(name = "safety_level")
        val safetyLevel: String,
        val secret: String,
        val server: String,
        val tags: TagsDTO,
        val title: TitleDTO,
        val urls: UrlsDTO,
        val usage: UsageDTO,
        val views: String,
        val visibility: VisibilityDTO
    ) {
        @JsonClass(generateAdapter = true)
        data class Comments(
            @Json(name = "_content")
            val content: String
        )

        @JsonClass(generateAdapter = true)
        data class DatesDTO(
            val lastupdate: String,
            val posted: String,
            val taken: String,
            val takengranularity: Int,
            val takenunknown: String
        )

        @JsonClass(generateAdapter = true)
        data class DescriptionDTO(
            @Json(name = "_content")
            val content: String
        )

        @JsonClass(generateAdapter = true)
        data class EditabilityDTO(
            val canaddmeta: Int,
            val cancomment: Int
        )

        @JsonClass(generateAdapter = true)
        data class NotesDTO(
            val note: List<Any?>
        )

        @JsonClass(generateAdapter = true)
        data class OwnerDTO(
            val gift: Gift,
            val iconfarm: Int,
            val iconserver: String,
            val location: String?,
            val nsid: String,
            @Json(name = "path_alias")
            val pathAlias: Any?,
            val realname: String,
            val username: String
        ) {
            @JsonClass(generateAdapter = true)
            data class Gift(
                @Json(name = "eligible_durations")
                val eligibleDurations: List<String>,
                @Json(name = "gift_eligible")
                val giftEligible: Boolean,
                @Json(name = "new_flow")
                val newFlow: Boolean
            )
        }

        @JsonClass(generateAdapter = true)
        data class PeopleDTO(
            val haspeople: Int
        )

        @JsonClass(generateAdapter = true)
        data class PubliceditabilityDTO(
            val canaddmeta: Int,
            val cancomment: Int
        )

        @JsonClass(generateAdapter = true)
        data class TagsDTO(
            val tag: List<Any?>
        )

        @JsonClass(generateAdapter = true)
        data class TitleDTO(
            @Json(name = "_content")
            val content: String
        )

        @JsonClass(generateAdapter = true)
        data class UrlsDTO(
            val url: List<Url>
        ) {
            @JsonClass(generateAdapter = true)
            data class Url(
                @Json(name = "_content")
                val content: String,
                val type: String
            )
        }

        @JsonClass(generateAdapter = true)
        data class UsageDTO(
            val canblog: Int,
            val candownload: Int,
            val canprint: Int,
            val canshare: Int
        )

        @JsonClass(generateAdapter = true)
        data class VisibilityDTO(
            val isfamily: Int,
            val isfriend: Int,
            val ispublic: Int
        )
    }

    fun mapToPhotoDetail() = PhotoDetail(
        id = photo.id,
        owner = PhotoDetail.Owner(
            username = photo.owner.username
        ),
        title = photo.title.content,
        views = photo.views,
        commentCount = photo.comments.content,
        dates = PhotoDetail.Dates(
            posted = formatUnixTimestamp(photo.dates.posted),
            taken = photo.dates.taken
        ),
        url = photoUrl(),
    )

    private fun photoUrl() =
        "https://live.staticflickr.com/${this.photo.server}/${this.photo.id}_${this.photo.secret}.jpg"

    private fun formatUnixTimestamp(timestamp: String): String {
        return try {
            val date = Date(timestamp.toLong() * 1000)
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            timestamp
        }
    }
}