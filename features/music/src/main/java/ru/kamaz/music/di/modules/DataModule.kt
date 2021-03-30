package ru.kamaz.music.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.kamaz.music.media.AppMediaManager
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music.data.RepositoryImpl
import ru.kamaz.music_api.interfaces.Repository
import javax.inject.Singleton

@Module
class DataModule {
    @Provides
    fun provideMediaManager(context: Context): MediaManager {
        return AppMediaManager(context)
    }

    @Provides
    fun provideRepository(media:MediaManager):Repository= RepositoryImpl(media)
}