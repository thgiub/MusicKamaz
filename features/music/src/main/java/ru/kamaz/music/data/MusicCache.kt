package ru.kamaz.music.data

import ru.kamaz.music.domain.FavoriteSongsEntity
import ru.kamaz.music_api.Failure
import ru.sir.core.Either
import ru.sir.core.None

interface MusicCache {
    fun getLastMusic():String
    fun saveLastMusic(lastMusic:String)
    fun insertFavoriteSong(song: FavoriteSongsEntity): Either<Failure, None>
    fun queryFavoriteSongs() : List<FavoriteSongsEntity>
}