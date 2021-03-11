package ru.kamaz.music.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.kamaz.music.date.media.AppMediaManager
import ru.kamaz.music.date.media.MediaManager

@Module
class DataModule {
    @Provides
    fun provideMediaManager(context: Context):MediaManager{
        return AppMediaManager(context)
    }
}