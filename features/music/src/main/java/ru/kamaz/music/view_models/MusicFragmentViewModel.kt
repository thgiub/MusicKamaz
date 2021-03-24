package ru.kamaz.music.view_models

import android.app.Application
import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kamaz.music.date.media.MediaManager
import ru.kamaz.music.date.media.model.Track
import ru.sir.presentation.base.BaseViewModel
import java.lang.Exception
import javax.inject.Inject

class MusicFragmentViewModel @Inject constructor(
    application: Application,
    private val mediaManager: MediaManager
) : BaseViewModel(application),MediaPlayer.OnCompletionListener {
    private var tracks = ArrayList<Track>()
    private var currentTrackPosition = 0
    private var mediaPlayer: MediaPlayer= MediaPlayer()

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }


    override fun init() {
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()


        super.init()
    }

    override fun onCreate() {
        updateTracks(mediaManager)
        super.onCreate()
    }

    fun startTrack(){

        val id: Long = tracks[currentTrackPosition].id
        val myUri: Uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )

        val mediaPlayer = MediaPlayer().apply {
            stop()
            reset()
            setDataSource(context, myUri)
            prepare()
            start()
        }
    }
    fun isPlaying(): Boolean = mediaPlayer.isPlaying

    fun playOrPause() {
        when(isPlaying()) {
            true -> pause()
            false -> resume()
        }
    }

    fun pause() {
        mediaPlayer.pause()

    }

    fun resume() {
       mediaPlayer.start()
    }

    fun previousTrack() {

        when(currentTrackPosition + 1) {
            tracks.size -> currentTrackPosition = 0
            else -> currentTrackPosition++
        }
        startTrack()

    }

    fun nextTrack() {
        when(currentTrackPosition-1){
            -1 -> currentTrackPosition = tracks.size - 1
            else -> currentTrackPosition--
        }
        startTrack()
    }


    fun randomSong() {
        mediaPlayer.apply {
            stop()
            reset()
            setDataSource(tracks[currentPosition].data)
            prepare()
            start()

        }

    }

    fun updateTracks(mediaManager: MediaManager) {
        tracks = mediaManager.scanTracks()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("Not yet implemented")
    }


}