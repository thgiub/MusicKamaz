package ru.kamaz.music.di.modules

import dagger.Module
import dagger.Provides
import ru.kamaz.music_api.interactor.GetMusicCover
import ru.kamaz.music_api.interactor.GetMusicPosition
import ru.kamaz.music_api.interactor.InsertFavoriteMusic
import ru.kamaz.music_api.interactor.LoadData
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
}