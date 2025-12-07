package dev.tiemonliam.omada.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dev.tiemonliam.omada.viewmodel.PhotosListViewModel

@Composable
fun PhotosListScreen(
    onPhotoClicked: (String) -> Unit = {},
    viewModel: PhotosListViewModel = hiltViewModel()
) {
    Column {
        SearchPhotosComponent(
            viewModel = viewModel,
            onSearch = viewModel::getPhotos
        )
        PhotosListComponent(
            viewModel = viewModel,
            onPhotoClicked = onPhotoClicked
        )
    }
}