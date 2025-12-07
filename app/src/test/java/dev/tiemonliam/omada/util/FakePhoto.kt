package dev.tiemonliam.omada.util

import dev.tiemonliam.omada.data.Photo
import dev.tiemonliam.omada.data.PhotoDetail

object FakePhoto {

    fun fakePhoto(
        id: String = "id",
        farm: Int = 1,
        isfamily: Int = 0,
        isfriend: Int = 0,
        ispublic: Int = 1,
        owner: String = "owner",
        secret: String = "secret",
        server: String = "server",
        title: String = "title",
        url: String = "url",
    ) = Photo(
        id = id,
        farm = farm,
        isfamily = isfamily,
        isfriend = isfriend,
        ispublic = ispublic,
        owner = owner,
        secret = secret,
        server = server,
        title = title,
        url = url
    )

    fun fakePhotoDetail(
        id: String,
        owner: PhotoDetail.Owner = PhotoDetail.Owner(
            username = "username"
        ),
        title: String = "title",
        views: String = "100",
        commentCount: String = "10",
        dates: PhotoDetail.Dates = PhotoDetail.Dates(
            posted = "1765143767",
            taken = "2025-12-06 13:12:12"
        ),
        url: String = "url"
    ) = PhotoDetail(
        id = id,
        owner = owner,
        title = title,
        views = views,
        commentCount = commentCount,
        dates = dates,
        url = url
    )

}