package ru.kamaz.music_api.interfaces

import kotlinx.coroutines.flow.Flow
import ru.kamaz.music_api.Failure
import ru.kamaz.music_api.models.*
import ru.sir.core.Either
import ru.sir.core.None

interface Repository {
    fun loadData(): Either<None, List<Track>>
    fun rvArtist(): Either<None, List<Track>>
    fun rvCategory():Either<None,List<CategoryMusicModel>>
    fun rvFavorite():Flow<List<FavoriteSongs>>
    fun rvAllFolderWithMusic():Either<None, List<AllFolderWithMusic>>
    fun getMusicCover(albumId: Long): Either<None, String>
    fun getMusicPositionFlow(): Flow<Int>
    fun lastTrack(): Either<None, String>
    fun insertFavoriteSong(song: FavoriteSongs): Either<Failure, None>
    fun deleteFavoriteSong(song: FavoriteSongs): Either<Failure, None>
    fun insertHistorySong(song: HistorySongs): Either<Failure, None>
    fun queryFavoriteSongs(data:String) :  Either<Failure, String>
    fun queryHistorySongs() : Either<Failure, String>


}