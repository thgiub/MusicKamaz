package ru.kamaz.music.di.modules

import dagger.Module
import dagger.Provides
import ru.kamaz.music_api.interactor.LoadData
import ru.kamaz.music_api.interfaces.Repository

@Module
class DomainModule {

    @Provides
    fun provideLoadData(repository: Repository): LoadData = LoadData(repository)
}