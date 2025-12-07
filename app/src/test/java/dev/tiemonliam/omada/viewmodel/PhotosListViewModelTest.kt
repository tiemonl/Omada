package dev.tiemonliam.omada.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.tiemonliam.omada.api.RecentPhotosResponseState
import dev.tiemonliam.omada.api.SearchResponseState
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
class PhotosListViewModelTest {
    private lateinit var viewModel: PhotosListViewModel
    private val mockRepository: PhotosRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PhotosListViewModel(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN new viewmodel WHEN created THEN initial state is Loading`() = runTest {
    assertThat(viewModel.photos.value).isEqualTo(PhotosListViewModel.PhotosState.Loading)
    }

    @Test
    fun `GIVEN empty search text WHEN getPhotos called THEN returns recent photos success`() =
        runTest {
        val testPhotos = listOf(
            FakePhoto.fakePhoto(id = "1"),
            FakePhoto.fakePhoto(id = "2"),
            FakePhoto.fakePhoto(id = "3")
        )
        coEvery { mockRepository.getRecent(1) } returns RecentPhotosResponseState.Success(
            photos = testPhotos,
            page = 1,
            pages = 5,
            total = 100
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)

            viewModel.getPhotos()

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)

            val success = successState as PhotosListViewModel.PhotosState.Success
            assertThat(success.photos).hasSize(3)
            assertThat(success.photos[0].id).isEqualTo("1")
            assertThat(success.isLoadingMore).isFalse()
            assertThat(success.hasMorePages).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN search text WHEN getPhotos called THEN returns search results success`() = runTest {
    val searchText = "cats"
        val testPhotos = listOf(
            FakePhoto.fakePhoto(id = "1", title = "cat photo 1"),
            FakePhoto.fakePhoto(id = "2", title = "cat photo 2")
        )

        viewModel.searchTextState.edit {
            append(searchText)
        }

        coEvery { mockRepository.getSearch(searchText, 1) } returns SearchResponseState.Success(
            photos = testPhotos,
            page = 1,
            pages = 3,
            total = 50
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)

            viewModel.getPhotos()

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)

            val success = successState as PhotosListViewModel.PhotosState.Success
            assertThat(success.photos).hasSize(2)
            assertThat(success.photos[0].title).isEqualTo("cat photo 1")
            assertThat(success.hasMorePages).isTrue()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN repository returns error WHEN getPhotos called for recent photos THEN error state is emitted`() =
        runTest {
        val errorMessage = "Network error"
        coEvery { mockRepository.getRecent(1) } returns RecentPhotosResponseState.Error(errorMessage)

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)

            viewModel.getPhotos()

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(PhotosListViewModel.PhotosState.Error::class.java)

            val errorData = errorState as PhotosListViewModel.PhotosState.Error
            assertThat(errorData.message).isEqualTo(errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN repository returns error WHEN getPhotos called for search THEN error state is emitted`() =
        runTest {
        val searchText = "test"
        val errorMessage = "Search failed"

        viewModel.searchTextState.edit {
            append(searchText)
        }

        coEvery { mockRepository.getSearch(searchText, 1) } returns SearchResponseState.Error(errorMessage)

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)

            viewModel.getPhotos()

            val errorState = awaitItem()
            assertThat(errorState).isInstanceOf(PhotosListViewModel.PhotosState.Error::class.java)

            val errorData = errorState as PhotosListViewModel.PhotosState.Error
            assertThat(errorData.message).isEqualTo(errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN empty search WHEN getPhotos called THEN repository getRecent is called with correct page`() =
        runTest {
        val testPhotos = listOf(FakePhoto.fakePhoto())
        coEvery { mockRepository.getRecent(1) } returns RecentPhotosResponseState.Success(
            photos = testPhotos,
            page = 1,
            pages = 5,
            total = 100
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)

            viewModel.getPhotos()

            awaitItem()

            coVerify(exactly = 1) { mockRepository.getRecent(page = 1) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN search text WHEN getPhotos called THEN repository getSearch is called with correct parameters`() =
        runTest {
        val searchText = "sunset"
        val testPhotos = listOf(FakePhoto.fakePhoto())

        viewModel.searchTextState.edit {
            append(searchText)
        }

        coEvery { mockRepository.getSearch(searchText, 1) } returns SearchResponseState.Success(
            photos = testPhotos,
            page = 1,
            pages = 3,
            total = 50
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)

            viewModel.getPhotos()

            awaitItem()

            coVerify(exactly = 1) { mockRepository.getSearch(term = searchText, page = 1) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN successful first page load WHEN loadMorePhotos called THEN photos from next page are appended`() =
        runTest {
        val firstPagePhotos = listOf(
            FakePhoto.fakePhoto(id = "1"),
            FakePhoto.fakePhoto(id = "2")
        )
        val secondPagePhotos = listOf(
            FakePhoto.fakePhoto(id = "3"),
            FakePhoto.fakePhoto(id = "4")
        )

        coEvery { mockRepository.getRecent(1) } returns RecentPhotosResponseState.Success(
            photos = firstPagePhotos,
            page = 1,
            pages = 5,
            total = 100
        )
        coEvery { mockRepository.getRecent(2) } returns RecentPhotosResponseState.Success(
            photos = secondPagePhotos,
            page = 2,
            pages = 5,
            total = 100
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)

            viewModel.getPhotos()

            val successState1 = awaitItem()
            assertThat(successState1).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)

            val successData1 = successState1 as PhotosListViewModel.PhotosState.Success
            assertThat(successData1.photos).hasSize(2)
            assertThat(successData1.photos[0].id).isEqualTo("1")
            assertThat(successData1.photos[1].id).isEqualTo("2")
            assertThat(successData1.isLoadingMore).isFalse()
            assertThat(successData1.hasMorePages).isTrue()

            viewModel.loadMorePhotos()

            val loadingMoreState = awaitItem()
            assertThat(loadingMoreState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)
            assertThat((loadingMoreState as PhotosListViewModel.PhotosState.Success).isLoadingMore).isTrue()

            val successState2 = awaitItem()
            assertThat(successState2).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)

            val successData2 = successState2 as PhotosListViewModel.PhotosState.Success
            assertThat(successData2.photos).hasSize(4)
            assertThat(successData2.photos[2].id).isEqualTo("3")
            assertThat(successData2.photos[3].id).isEqualTo("4")
            assertThat(successData2.isLoadingMore).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN on last page WHEN loadMorePhotos called THEN no additional loading occurs`() =
        runTest {
        val testPhotos = listOf(FakePhoto.fakePhoto(id = "1"))

        coEvery { mockRepository.getRecent(1) } returns RecentPhotosResponseState.Success(
            photos = testPhotos,
            page = 1,
            pages = 1,
            total = 1
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)
            viewModel.getPhotos()
            val successState = awaitItem()
            assertThat(successState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)

            val successData = successState as PhotosListViewModel.PhotosState.Success
            assertThat(successData.photos).hasSize(1)
            assertThat(successData.photos[0].id).isEqualTo("1")
            assertThat(successData.hasMorePages).isFalse()

            viewModel.loadMorePhotos()
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN currently loading more WHEN loadMorePhotos called THEN additional load is prevented`() =
        runTest {
        val firstPagePhotos = listOf(FakePhoto.fakePhoto(id = "1"))
        val secondPagePhotos = listOf(FakePhoto.fakePhoto(id = "2"))

        coEvery { mockRepository.getRecent(1) } returns RecentPhotosResponseState.Success(
            photos = firstPagePhotos,
            page = 1,
            pages = 5,
            total = 100
        )
        coEvery { mockRepository.getRecent(2) } returns RecentPhotosResponseState.Success(
            photos = secondPagePhotos,
            page = 2,
            pages = 5,
            total = 100
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)
            viewModel.getPhotos()

            val successState1 = awaitItem()
            assertThat(successState1).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)
            val successData1 = successState1 as PhotosListViewModel.PhotosState.Success
            assertThat(successData1.photos).hasSize(1)
            assertThat(successData1.photos[0].id).isEqualTo("1")
            assertThat(successData1.isLoadingMore).isFalse()
            assertThat(successData1.hasMorePages).isTrue()

            viewModel.loadMorePhotos()
            viewModel.loadMorePhotos()

            val loadingMoreState = awaitItem()
            assertThat(loadingMoreState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)
            assertThat((loadingMoreState as PhotosListViewModel.PhotosState.Success).isLoadingMore).isTrue()

            val successState2 = awaitItem()
            assertThat(successState2).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)
            val successData2 = successState2 as PhotosListViewModel.PhotosState.Success
            assertThat(successData2.photos).hasSize(2)
            assertThat(successData2.isLoadingMore).isFalse()

            coVerify(exactly = 1) { mockRepository.getRecent(page = 2) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN last page reached WHEN checking hasMorePages THEN returns false`() = runTest {
    val testPhotos = listOf(FakePhoto.fakePhoto())

        coEvery { mockRepository.getRecent(1) } returns RecentPhotosResponseState.Success(
            photos = testPhotos,
            page = 3,
            pages = 3,
            total = 60
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)
            viewModel.getPhotos()

            val successState = awaitItem()
            assertThat(successState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)

            val successData = successState as PhotosListViewModel.PhotosState.Success
            assertThat(successData.hasMorePages).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN multiple pages loaded WHEN getPhotos called again THEN pagination state is reset`() =
        runTest {
        val firstPagePhotos = listOf(FakePhoto.fakePhoto(id = "1"))
        val secondPagePhotos = listOf(FakePhoto.fakePhoto(id = "2"))

        coEvery { mockRepository.getRecent(1) } returns RecentPhotosResponseState.Success(
            photos = firstPagePhotos,
            page = 1,
            pages = 5,
            total = 100
        )
        coEvery { mockRepository.getRecent(2) } returns RecentPhotosResponseState.Success(
            photos = secondPagePhotos,
            page = 2,
            pages = 5,
            total = 100
        )

        viewModel.photos.test {
            assertThat(awaitItem()).isEqualTo(PhotosListViewModel.PhotosState.Loading)
            viewModel.getPhotos()

            val firstPageState = awaitItem()
            assertThat(firstPageState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)
            assertThat((firstPageState as PhotosListViewModel.PhotosState.Success).photos).hasSize(1)

            viewModel.loadMorePhotos()

            val loadingMoreState = awaitItem()
            assertThat(loadingMoreState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)
            assertThat((loadingMoreState as PhotosListViewModel.PhotosState.Success).isLoadingMore).isTrue()

            val appendedState = awaitItem()
            assertThat(appendedState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)
            assertThat((appendedState as PhotosListViewModel.PhotosState.Success).photos).hasSize(2)

            viewModel.getPhotos()

            val resetLoadingState = awaitItem()
            assertThat(resetLoadingState).isEqualTo(PhotosListViewModel.PhotosState.Loading)

            val resetSuccessState = awaitItem()
            assertThat(resetSuccessState).isInstanceOf(PhotosListViewModel.PhotosState.Success::class.java)
            val successData = resetSuccessState as PhotosListViewModel.PhotosState.Success
            assertThat(successData.photos).hasSize(1)
            assertThat(successData.photos[0].id).isEqualTo("1")

            cancelAndIgnoreRemainingEvents()
        }
    }
}