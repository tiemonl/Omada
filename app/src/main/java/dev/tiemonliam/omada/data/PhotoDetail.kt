package dev.tiemonliam.omada.data

import java.security.acl.Owner

data class PhotoDetail(
    val id: String,
    val owner: Owner,
    val title: String,
    val views: String,
    val commentCount: String,
    val dates: Dates,
    val url: String,
) {
    data class Owner(val username: String)
    data class Dates(val posted: String, val taken: String)
}
