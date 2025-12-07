package dev.tiemonliam.omada.api

import dev.tiemonliam.omada.api.dto.PhotoInfoDTO
import dev.tiemonliam.omada.api.dto.RecentPhotosDTO
import dev.tiemonliam.omada.api.dto.SearchPhotosDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    @GET("?method=flickr.photos.getRecent")
    suspend fun getRecent(
        @Query("page") page: Int,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") nojsoncallback: Int = 1,
    ): RecentPhotosDTO

    @GET("?method=flickr.photos.search")
    suspend fun getSearch(
        @Query("text") text: String? = null,
        @Query("page") page: Int = 1,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") nojsoncallback: Int = 1,
    ) : SearchPhotosDTO

    @GET("?method=flickr.photos.getInfo")
    suspend fun getPhotoInfo(
        @Query("photo_id") photoId: String,
        @Query("secret") secret: String? = null,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") nojsoncallback: Int = 1,
    ) : PhotoInfoDTO
}