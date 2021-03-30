package ru.kamaz.music.view_models

import android.app.Application
import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music_api.interactor.LoadData
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None
import ru.sir.presentation.base.BaseViewModel
import javax.inject.Inject

class MusicFragmentViewModel @Inject constructor(
    application: Application,
    private val loadData: LoadData,
    private val mediaManager: MediaManager
) : BaseViewModel(application),MediaPlayer.OnCompletionListener {
    private var tracks = ArrayList<Track>()
    private var currentTrackPosition = 0
   private val mediaPlayer: MediaPlayer = MediaPlayer()

    private val _isPlaying = MutableStateFlow(mediaPlayer.isPlaying)
    val isPlaying = _isPlaying.asStateFlow()

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
    override fun init() {
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setAudioAttributes(audioAttributes)
        super.init()
    }
    override fun onCreate() {
        updateTracks(mediaManager)
        super.onCreate()
    }
    fun startTrack(){
        updateTracks(mediaManager)
        val id: Long = tracks[currentTrackPosition].id
        val myUri: Uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )
        mediaPlayer.apply {
            stop()
            reset()
            setDataSource(context, myUri)
            prepare()
        }
    }

    fun playOrPause() {
        when(isPlaying.value) {
            true -> pause()
            false -> resume()
        }
    }

    fun pause() {
        _isPlaying.value = false
        mediaPlayer.pause()
    }

    fun resume() {
        _isPlaying.value = true
       mediaPlayer.start()
    }

    fun previousTrack() {

        when(currentTrackPosition + 1) {
            tracks.size -> currentTrackPosition = 0
            else -> currentTrackPosition++
        }
        startTrack()
        when (isPlaying.value ){
            true-> mediaPlayer.start()
        }
    }

    fun nextTrack() {
        when(currentTrackPosition-1){
            -1 -> currentTrackPosition = tracks.size - 1
            else -> currentTrackPosition--
        }
        startTrack()
        when (isPlaying.value ){
            true-> mediaPlayer.start()
        }



    }

    fun updateTrackInfo(track:Track){

    }



    fun updateTracks(mediaManager: MediaManager) {
        val result = mediaManager.scanTracks()
        if (result is Either.Right) {
            tracks.addAll(result.r)
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextTrack()
    }



}