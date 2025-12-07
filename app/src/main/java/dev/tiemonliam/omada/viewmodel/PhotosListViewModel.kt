package dev.tiemonliam.omada.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tiemonliam.omada.api.RecentPhotosResponseState
import dev.tiemonliam.omada.api.SearchResponseState
import dev.tiemonliam.omada.data.Photo
import dev.tiemonliam.omada.repo.PhotosRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosListViewModel @Inject constructor(
    private val repository: PhotosRepository
) : ViewModel() {

    val searchTextState = TextFieldState()
    private val _photos = MutableStateFlow<PhotosState>(PhotosState.Loading)
    val photos = _photos.asStateFlow()

    private var currentPage = 1
    private var totalPages = Int.MAX_VALUE
    private var isLoadingMore = false

    fun getPhotos() = viewModelScope.launch {
        _photos.value = PhotosState.Loading
        currentPage = 1
        totalPages = Int.MAX_VALUE
        loadPhotosPage(currentPage)
    }

    fun loadMorePhotos() {
        val currentState = _photos.value
        if (isLoadingMore || currentState !is PhotosState.Success || currentPage >= totalPages) return

        isLoadingMore = true
        currentPage++

        viewModelScope.launch {
            _photos.value = currentState.copy(isLoadingMore = true)
            loadPhotosPage(currentPage, append = true)
            isLoadingMore = false
        }
    }

    private suspend fun loadPhotosPage(page: Int, append: Boolean = false) {
        val existingPhotos = if (append && _photos.value is PhotosState.Success) {
            (_photos.value as PhotosState.Success).photos
        } else {
            persistentListOf()
        }

        if (searchTextState.text.isEmpty()) {
            when (val response = repository.getRecent(page)) {
                is RecentPhotosResponseState.Success -> {
                    totalPages = response.pages
                    val newPhotos = (existingPhotos + response.photos).toImmutableList()
                    _photos.value = PhotosState.Success(
                        photos = newPhotos,
                        isLoadingMore = false,
                        hasMorePages = response.page < response.pages
                    )
                }

                is RecentPhotosResponseState.Error -> {
                    if (append) {
                        _photos.value = (_photos.value as PhotosState.Success).copy(
                            isLoadingMore = false
                        )
                    } else {
                        _photos.value = PhotosState.Error(response.message)
                    }
                }
            }
        } else {
            when (val response = repository.getSearch(searchTextState.text.toString(), page)) {
                is SearchResponseState.Success -> {
                    totalPages = response.pages
                    val newPhotos = (existingPhotos + response.photos).toImmutableList()
                    _photos.value = PhotosState.Success(
                        photos = newPhotos,
                        isLoadingMore = false,
                        hasMorePages = response.page < response.pages
                    )
                }

                is SearchResponseState.Error -> {
                    if (append) {
                        _photos.value = (_photos.value as PhotosState.Success).copy(
                            isLoadingMore = false
                        )
                    } else {
                        _photos.value = PhotosState.Error(response.message)
                    }
                }
            }
        }
    }


    sealed interface PhotosState {
        data object Loading : PhotosState
        data class Success(
            val photos: ImmutableList<Photo>,
            val isLoadingMore: Boolean = false,
            val hasMorePages: Boolean = true
        ) : PhotosState
        data class Error(val message: String) : PhotosState
    }
}