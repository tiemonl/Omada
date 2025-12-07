package dev.tiemonliam.omada.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.tiemonliam.omada.api.PhotoInfoResponseState
import dev.tiemonliam.omada.repo.PhotosRepository
import dev.tiemonliam.omada.util.FakePhoto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDetailViewModelTest {
    private lateinit var viewModel: PhotoDetailViewModel
    private val mockRepository: PhotosRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PhotoDetailViewModel(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN new PhotoDetailViewModel WHEN initialized THEN initial state is Loading`() =
        runTest {
        assertThat(viewModel.detail.value).isEqualTo(PhotoDetailViewModel.PhotoDetailState.Loading)
    }

    @Test
    fun `GIVEN valid photo id WHEN getPhotoDetail called THEN returns success state with photo`() =
        runTest {
        val photoId = "test_id"
        val testPhoto = FakePhoto.fakePhotoDetail(photoId)
        coEvery { mockRepository.getPhotoInfo(photoId) } returns PhotoInfoResponseState.Success(
            testPhoto
        )

        viewModel.detail.test {
            assertThat(awaitItem()).isEqualTo(PhotoDetailViewModel.PhotoDetailState.Loading)

            viewModel.getPhotoDetail(photoId)

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(PhotoDetailViewModel.PhotoDetailState.Success::class.java)

            val successPhoto = successState as PhotoDetailViewModel.PhotoDetailState.Success
            assertThat(successPhoto.photo).isEqualTo(testPhoto)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN photo id WHEN getPhotoDetail called THEN repository is called with same id`() =
        runTest {

        val photoId = "test_id"
        val testPhoto = FakePhoto.fakePhotoDetail(photoId)
        coEvery { mockRepository.getPhotoInfo(photoId) } returns PhotoInfoResponseState.Success(
            testPhoto
        )

        viewModel.detail.test {
            assertThat(awaitItem()).isEqualTo(PhotoDetailViewModel.PhotoDetailState.Loading)

            viewModel.getPhotoDetail(photoId)

            awaitItem()

            coVerify(exactly = 1) { mockRepository.getPhotoInfo(photoId = photoId) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN invalid photo id WHEN getPhotoDetail called THEN returns error state`() = runTest {
    val photoId = "test_id"
        val errorMessage = "Error message"
        coEvery { mockRepository.getPhotoInfo(photoId) } returns PhotoInfoResponseState.Error(
            errorMessage
        )

        viewModel.detail.test {
            assertThat(awaitItem()).isEqualTo(PhotoDetailViewModel.PhotoDetailState.Loading)

            viewModel.getPhotoDetail(photoId)

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(PhotoDetailViewModel.PhotoDetailState.Error::class.java)

            val errorData = errorState as PhotoDetailViewModel.PhotoDetailState.Error
            assertThat(errorData.message).isEqualTo(errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }
}