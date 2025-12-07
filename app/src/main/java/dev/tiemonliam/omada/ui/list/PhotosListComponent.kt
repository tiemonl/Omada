package dev.tiemonliam.omada.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import dev.tiemonliam.omada.data.Photo
import dev.tiemonliam.omada.ui.common.Error
import dev.tiemonliam.omada.ui.common.Loading
import dev.tiemonliam.omada.viewmodel.PhotosListViewModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PhotosListComponent(
    viewModel: PhotosListViewModel = hiltViewModel(),
    onPhotoClicked: (String) -> Unit = {},
) {
    val state = viewModel.photos.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        if (state.value is PhotosListViewModel.PhotosState.Loading) {
            viewModel.getPhotos()
        }
    }
    PhotosListPhotosListComponentImpl(
        state = state.value,
        onPhotoClicked = onPhotoClicked,
        onLoadMore = { viewModel.loadMorePhotos() }
    )
}

@Composable
fun PhotosListPhotosListComponentImpl(
    state: PhotosListViewModel.PhotosState,
    onPhotoClicked: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    when (state) {
        is PhotosListViewModel.PhotosState.Error -> {
            Error(message = state.message)
        }

        PhotosListViewModel.PhotosState.Loading -> {
            Loading()
        }

        is PhotosListViewModel.PhotosState.Success -> {
            PhotosList(
                photos = state.photos,
                isLoadingMore = state.isLoadingMore,
                hasMorePages = state.hasMorePages,
                onPhotoClicked = onPhotoClicked,
                onLoadMore = onLoadMore
            )
        }
    }
}

@Composable
fun PhotosList(
    photos: ImmutableList<Photo>,
    isLoadingMore: Boolean,
    hasMorePages: Boolean,
    onPhotoClicked: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyGridState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount

            lastVisibleItem != null && 
            lastVisibleItem.index >= totalItems - 10 &&
            !isLoadingMore && 
            hasMorePages
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            photos,
            key = { index, photo -> "${photo.id}_${photo.secret}_$index" }) { _, photo ->
            PhotoGridSlot(photo, onPhotoClicked)
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Loading()
                }
            }
        }
    }
}

@Composable
fun PhotoGridSlot(photo: Photo, onPhotoClicked: (String) -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .clickable(onClick = { onPhotoClicked(photo.id) }),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.url)
                .build(),
            contentDescription = photo.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}