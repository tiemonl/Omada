package dev.tiemonliam.omada.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Routes(val routeId: String) : NavKey {
    @Serializable
    data object PhotoList : Routes(routeId = "photoList")
    @Serializable
    data class PhotoDetail(val id: String) : Routes(routeId = "photoDetail/${id}")

}