package ru.kamaz.music.di.modules

import dagger.Module
import dagger.Provides
import ru.kamaz.music_api.interactor.*
import ru.kamaz.music_api.interfaces.Repository

@Module
class DomainModule {

    @Provides
    fun provideLoadData(repository: Repository): LoadData = LoadData(repository)

    @Provides
    fun provideGetMusicCover(repository: Repository): GetMusicCover = GetMusicCover(repository)

    @Provides
    fun provideGetMusicPosition(repository: Repository): GetMusicPosition = GetMusicPosition(repository)

    @Provides
    fun provideInsertFavoriteMusic(repository: Repository): InsertFavoriteMusic = InsertFavoriteMusic(repository)
    @Provides
    fun provideInsertLastMusic(repository: Repository): InsertLastMusic = InsertLastMusic(repository)
    @Provides
    fun provideQueryLastMusic(repository: Repository): QueryLastMusic = QueryLastMusic(repository)
}