package ru.kamaz.music.cache

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kamaz.music.cache.db.AppDatabase
import ru.kamaz.music.data.MusicCache
import ru.kamaz.music.domain.FavoriteSongsEntity
import ru.kamaz.music.domain.HistorySongsEntity
import ru.kamaz.music_api.Failure
import ru.kamaz.music_api.models.ErrorMessage
import ru.kamaz.music_api.models.FavoriteSongs
import ru.sir.core.Either
import ru.sir.core.None
import java.lang.Exception

class MusicCacheImpl (private val prefsManager: SharedPrefsManager, private val db: AppDatabase):MusicCache {
    override fun getLastMusic(): String = prefsManager.getLastMusic()
    override fun saveLastMusic(lastMusic: String)  = prefsManager.saveMusicInfo(lastMusic)
    override fun insertFavoriteSong(song: FavoriteSongsEntity): Either<Failure, None> {
   //     Log.i("insertFavoriteMusic", song.data)
        db.userDao().insertAll(song)
        return Either.Right(None())
    }

    override fun deleteFavoriteSong(song: FavoriteSongsEntity): Either<Failure, None> {
        //Log.i("deleteFavoriteSong", "insertHistorySong: ${song.data}")
        db.userDao().delete(song)
        return Either.Right(None())
    }

    override fun queryHistorySongs(): Either<Failure, String> {
        return try {
           // Log.i("database", "queryHistorySongs try")
            Either.Right(db.historySongsDao().getLastMusic().data)
        } catch (e: Exception) {
           // Log.i("database", "queryHistorySongs false")
            Either.Left(Failure.AuthorizationError(ErrorMessage(404, e.message.toString(), e.localizedMessage ?: "")))
        }
    }

    override fun insertHistorySong(song: HistorySongsEntity): Either<Failure, None> {
      //  Log.i("insertHistorySong", "insertHistorySong: ${song.data}")
         db.historySongsDao().insertAll(song)
        return Either.Right(None())
    }

    override fun queryFavoriteSongs(data: String): Either<Failure, String> {
        return try {
           // Log.i("database", "queryHistorySongs try")
            Either.Right(db.userDao().loadAll(data).data)
        } catch (e: Exception) {
          //  Log.i("database", "queryHistorySongs false")
            Either.Left(Failure.AuthorizationError(ErrorMessage(404, e.message.toString(), e.localizedMessage ?: "")))
        }

    }

    override fun  getAllFavoriteSongs():  Flow<List<FavoriteSongs>>{
     /*   return try {
            Either.Right(db.userDao().getData().map { convertEntityListFavoriteModelList(it) })
        } catch (e: Exception) {
            Log.i("database", "queryHistorySongs false")
            Either.Left(Failure.AuthorizationError(ErrorMessage(404, e.message.toString(), e.localizedMessage ?: "")))
        }*/

        return db.userDao().getData().map { convertEntityListFavoriteModelList(it) }
    }

    private fun convertEntityListFavoriteModelList(entity: List<FavoriteSongsEntity>):List<FavoriteSongs>{
        val data = mutableListOf<FavoriteSongs>()
        entity.forEach { data.add(FavoriteSongs(it.idSong,it.data)) }
        return data
    }

    /* override fun queryFavoriteSongs(): Either<Failure, String> {
         return try {
             Log.i("database", "queryHistorySongs try")
             Either.Right(db.userDao().loadAll)
         } catch (e: Exception) {
             Log.i("database", "queryHistorySongs false")
             Either.Left(Failure.AuthorizationError(ErrorMessage(404, e.message.toString(), e.localizedMessage ?: "")))
         }
     }*/

    /* override fun queryHistorySongs(): Either<Failure,List<HistorySongsEntity>> {
       val list  =db.historySongsDao().loadAll()
         return Either.Right(list)
     }*/
}