package dev.tiemonliam.omada.repo

import dev.tiemonliam.omada.api.FlickrApi
import dev.tiemonliam.omada.api.PhotoInfoResponseState
import dev.tiemonliam.omada.api.RecentPhotosResponseState
import dev.tiemonliam.omada.api.SearchResponseState
import dev.tiemonliam.omada.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PhotosRepositoryReal @Inject constructor(
    private val flickrApi: FlickrApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PhotosRepository {
    override suspend fun getRecent(page: Int) = withContext(ioDispatcher) {
        return@withContext try {
            val result = flickrApi.getRecent(page)
            RecentPhotosResponseState.Success(
                photos = result.photos.mapToPhotos(),
                page = result.photos.page,
                pages = result.photos.pages,
                total = result.photos.total
            )
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> "${HTTP_EXCEPTION_ERROR}${e.code()}, ${e.message()}"
                is IOException -> "$IO_EXCEPTION${e.message}"
                else -> "$UNKNOWN_ERROR${e.message}"
            }
            RecentPhotosResponseState.Error(errorMessage)
        }
    }

    override suspend fun getSearch(term: String, page: Int): SearchResponseState =
        withContext(ioDispatcher) {
            return@withContext try {
                val result = flickrApi.getSearch(text = term, page = page)
                SearchResponseState.Success(
                    photos = result.photos.mapToPhotos(),
                    page = result.photos.page,
                    pages = result.photos.pages,
                    total = result.photos.total
                )
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is HttpException -> "${HTTP_EXCEPTION_ERROR}${e.code()}, ${e.message()}"
                    is IOException -> "$IO_EXCEPTION${e.message}"
                    else -> "$UNKNOWN_ERROR${e.message}"
                }
                SearchResponseState.Error(errorMessage)
            }
        }

    override suspend fun getPhotoInfo(photoId: String): PhotoInfoResponseState =
        withContext(ioDispatcher) {
            return@withContext try {
                val result = flickrApi.getPhotoInfo(photoId = photoId)
                PhotoInfoResponseState.Success(result.mapToPhotoDetail())
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is HttpException -> "${HTTP_EXCEPTION_ERROR}${e.code()}, ${e.message()}"
                    is IOException -> "$IO_EXCEPTION${e.message}"
                    else -> "$UNKNOWN_ERROR${e.message}"
                }
                PhotoInfoResponseState.Error(errorMessage)
            }
        }

    companion object {
        private const val HTTP_EXCEPTION_ERROR = "HTTP Exception: "
        private const val IO_EXCEPTION = "Network Error: "
        private const val UNKNOWN_ERROR = "Unknown Error: "
    }
}