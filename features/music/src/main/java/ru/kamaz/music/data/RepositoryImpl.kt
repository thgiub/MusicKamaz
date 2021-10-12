package ru.kamaz.music.data

import android.media.MediaPlayer
import android.util.Log
import androidx.room.Relation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.kamaz.music.domain.FavoriteSongsEntity
import ru.kamaz.music.domain.HistorySongsEntity
import ru.kamaz.music_api.Failure
import ru.kamaz.music_api.interfaces.Repository
import ru.kamaz.music_api.models.*

import ru.sir.core.Either
import ru.sir.core.None


class RepositoryImpl(private val media: MediaManager, private val mediaPlayer: MediaPlayer, private val testDBDao: MusicCache): Repository {
    override fun loadData(): Either<None, List<Track>> = media.scanTracks(0)
    override fun rvArtist(): Either<None, List<Track>> = media.scanTracks(1)

    override fun rvCategory(): Either<None, List<CategoryMusicModel>> = media.getCategory()
    override fun rvFavorite():  Flow<List<FavoriteSongs>> =testDBDao.getAllFavoriteSongs()
    override fun rvAllFolderWithMusic(): Either<None, List<AllFolderWithMusic>> = media.getAllFolder()

    override fun getMusicCover(albumId: Long): Either<None, String> = media.getAlbumImagePath(albumId)

    override fun getMusicPositionFlow(): Flow<Int> = flow {
        while (true) {
            val currentPosition = mediaPlayer.currentPosition
           // Log.i("getMusicPositionFlow", "INSERT: $currentPosition")
            emit(currentPosition)
            delay(1000)
        }
    }

    override fun lastTrack(): Either<None, String> {
        TODO("Not yet implemented")
    }

    override fun insertFavoriteSong(song: FavoriteSongs): Either<Failure, None> = testDBDao.insertFavoriteSong(song.toDao())
    override fun deleteFavoriteSong(song: FavoriteSongs): Either<Failure, None> = testDBDao.deleteFavoriteSong(song.toDao())

    override fun insertHistorySong(song: HistorySongs): Either<Failure, None> {
        val r = testDBDao.insertHistorySong(song.toDao())
      //  Log.i("insertHistorySong", "INSERT: $r")
        return r
    }

    override fun queryFavoriteSongs(data: String): Either<Failure, String> = testDBDao.queryFavoriteSongs(data)

   // override fun queryFavoriteSongs():  Either<Failure, String> = testDBDao.queryFavoriteSongs()

    override fun queryHistorySongs(): Either<Failure, String> = testDBDao.queryHistorySongs()
    private fun FavoriteSongs.toDao() = FavoriteSongsEntity(this.idSong, this.data)
    private fun HistorySongs.toDao() = HistorySongsEntity(this.dbID,this.idCursor,this.title,this.trackNumber,this.year,this.duration,this.data,this.dateModified,this.albumId,this.albumName,this.artistId,this.artistName,this.albumArtist,this.albumArtist,this.timePlayed)

}