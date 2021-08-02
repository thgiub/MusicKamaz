package ru.kamaz.music.cache

import ru.kamaz.music.cache.db.AppDatabase
import ru.kamaz.music.data.MusicCache
import ru.kamaz.music.domain.FavoriteSongsEntity
import ru.kamaz.music_api.Failure
import ru.sir.core.Either
import ru.sir.core.None

class MusicCacheImpl (private val prefsManager: SharedPrefsManager, private val db: AppDatabase):MusicCache {
    override fun getLastMusic(): String = prefsManager.getLastMusic()
    override fun saveLastMusic(lastMusic: String)  = prefsManager.saveMusicInfo(lastMusic)
    override fun insertFavoriteSong(song: FavoriteSongsEntity): Either<Failure, None> {
        db.userDao().insertAll(song)
        return Either.Right(None())
    }

    override fun queryFavoriteSongs(): List<FavoriteSongsEntity> {
        TODO("Not yet implemented")
    }
}