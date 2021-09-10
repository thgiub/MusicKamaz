package ru.kamaz.music.cache

import android.util.Log
import ru.kamaz.music.cache.db.AppDatabase
import ru.kamaz.music.data.MusicCache
import ru.kamaz.music.domain.FavoriteSongsEntity
import ru.kamaz.music.domain.HistorySongsEntity
import ru.kamaz.music_api.Failure
import ru.kamaz.music_api.models.ErrorMessage
import ru.sir.core.Either
import ru.sir.core.None
import java.lang.Exception

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

    override fun queryHistorySongs(): Either<Failure, String> {
        return try {
            Log.i("database", "queryHistorySongs try")
            Either.Right(db.historySongsDao().getLastMusic().data)
        } catch (e: Exception) {
            Log.i("database", "queryHistorySongs false")
            Either.Left(Failure.AuthorizationError(ErrorMessage(404, e.message.toString(), e.localizedMessage ?: "")))
        }
    }

    override fun insertHistorySong(song: HistorySongsEntity): Either<Failure, None> {
        Log.i("insertHistorySong", "insertHistorySong: ${song.data}")
         db.historySongsDao().insertAll(song)
        return Either.Right(None())
    }

   /* override fun queryHistorySongs(): Either<Failure,List<HistorySongsEntity>> {
      val list  =db.historySongsDao().loadAll()
        return Either.Right(list)
    }*/
}