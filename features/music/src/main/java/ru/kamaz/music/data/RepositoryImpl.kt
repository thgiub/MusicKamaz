package ru.kamaz.music.data

import android.media.MediaPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.kamaz.music_api.interfaces.Repository

import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None


class RepositoryImpl(private val media: MediaManager, private val mediaPlayer: MediaPlayer, private val testDBDao: MusicCache): Repository {
    override fun loadData(): Either<None, List<Track>> = media.scanTracks()
    override fun getMusicCover(albumId: Long): Either<None, String> = media.getAlbumImagePath(albumId)

    override fun getMusicPositionFlow(): Flow<Int> = flow {
        while (true) {
            val currentPosition = mediaPlayer.currentPosition
            emit(currentPosition)
            delay(1000)
        }
    }

    override fun lastTrack(): Either<None, String> {
        TODO("Not yet implemented")
    }


}