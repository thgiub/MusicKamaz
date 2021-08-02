package ru.kamaz.music_api.interfaces

import kotlinx.coroutines.flow.Flow
import ru.kamaz.music_api.Failure
import ru.kamaz.music_api.models.FavoriteSongs
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None

interface Repository {
    fun loadData(): Either<None, List<Track>>
    fun getMusicCover(albumId: Long): Either<None, String>
    fun getMusicPositionFlow(): Flow<Int>
    fun lastTrack(): Either<None, String>
    fun insertFavoriteSong(song: FavoriteSongs): Either<Failure, None>
    fun queryFavoriteSongs() : List<FavoriteSongs>

}