package dev.tiemonliam.omada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tiemonliam.omada.api.PhotoInfoResponseState
import dev.tiemonliam.omada.data.PhotoDetail
import dev.tiemonliam.omada.repo.PhotosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val repository: PhotosRepository
) : ViewModel() {
    val _detail = MutableStateFlow<PhotoDetailState>(PhotoDetailState.Loading)
    val detail = _detail.asStateFlow()

    fun getPhotoDetail(photoId: String) = viewModelScope.launch {
        when(val response = repository.getPhotoInfo(photoId)) {
            is PhotoInfoResponseState.Success -> {
                _detail.value = PhotoDetailState.Success(response.photo)
            }
            is PhotoInfoResponseState.Error -> {
                _detail.value = PhotoDetailState.Error(response.message)
            }
        }
    }

    sealed interface PhotoDetailState {
        data class Success(val photo: PhotoDetail) : PhotoDetailState
        data class Error(val message: String) : PhotoDetailState
        object Loading : PhotoDetailState
    }
}