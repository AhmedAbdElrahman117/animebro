package com.example.animbro.di

import com.example.animbro.domain.repository.AnimeRepository
import com.example.animbro.repositories.AnimeRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAnimeRepository(
        animeRepositoryImp: AnimeRepositoryImp
    ): AnimeRepository
}

