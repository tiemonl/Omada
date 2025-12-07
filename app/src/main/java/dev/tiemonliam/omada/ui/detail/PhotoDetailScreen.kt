package dev.tiemonliam.omada.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Scale
import dev.tiemonliam.omada.data.PhotoDetail
import dev.tiemonliam.omada.ui.common.Error
import dev.tiemonliam.omada.ui.common.Loading
import dev.tiemonliam.omada.viewmodel.PhotoDetailViewModel

@Composable
fun PhotoDetailScreen(
    photoId: String,
    viewModel: PhotoDetailViewModel = hiltViewModel(),
) {
    val state = viewModel.detail.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.getPhotoDetail(photoId)
    }
    PhotoDetailScreenImpl(state.value)
}

@Composable
fun PhotoDetailScreenImpl(
    state: PhotoDetailViewModel.PhotoDetailState,
) {
    when (state) {
        is PhotoDetailViewModel.PhotoDetailState.Error -> {
            Error(message = state.message)
        }

        PhotoDetailViewModel.PhotoDetailState.Loading -> {
            Loading()
        }

        is PhotoDetailViewModel.PhotoDetailState.Success -> {
            PhotoDetailContent(state.photo)
        }
    }
}

@Composable
fun PhotoDetailContent(photo: PhotoDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.url)
                    .build(),
                contentDescription = photo.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = photo.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "By ${photo.owner.username}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(label = "Views", value = photo.views)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "Comments", value = photo.commentCount)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "Posted", value = photo.dates.posted)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "Taken", value = photo.dates.taken)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

