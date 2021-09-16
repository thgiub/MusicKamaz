package ru.kamaz.music.data

import ru.kamaz.music.domain.FavoriteSongsEntity
import ru.kamaz.music.domain.HistorySongsEntity
import ru.kamaz.music_api.Failure
import ru.kamaz.music_api.models.FavoriteSongs
import ru.sir.core.Either
import ru.sir.core.None

interface MusicCache {
    fun getLastMusic():String
    fun saveLastMusic(lastMusic:String)
    fun insertFavoriteSong(song: FavoriteSongsEntity): Either<Failure, None>
    fun deleteFavoriteSong(song: FavoriteSongsEntity): Either<Failure, None>
    fun insertHistorySong(song: HistorySongsEntity): Either<Failure, None>
    fun queryFavoriteSongs(data:String) :  Either<Failure, String>
    fun queryHistorySongs(): Either<Failure, String>
}