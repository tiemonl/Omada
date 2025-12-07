package dev.tiemonliam.omada.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dev.tiemonliam.omada.repo.PhotosRepository
import dev.tiemonliam.omada.repo.PhotosRepositoryReal

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    @ViewModelScoped
    fun bindPhotosRepository(
        photosRepositoryReal: PhotosRepositoryReal
    ) : PhotosRepository
}