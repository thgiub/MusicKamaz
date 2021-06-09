package ru.kamaz.music.di.modules

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.kamaz.music.TestDBDatabase
import javax.inject.Singleton


@Module
public class RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): TestDBDatabase {
        return TestDBDatabase.getDatabase(context)
    }

}