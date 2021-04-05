package ru.kamaz.music.view_models

import android.app.Application
import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music_api.interactor.GetMusicCover
import ru.kamaz.music_api.interactor.GetMusicPosition
import ru.kamaz.music_api.interactor.LoadData
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.presentation.base.BaseViewModel
import ru.sir.presentation.extensions.easyLog
import javax.inject.Inject
import kotlin.collections.ArrayList

class MusicFragmentViewModel @Inject constructor(
    application: Application,
    private val loadData: LoadData,
    private val mediaPlayer: MediaPlayer,
    private val mediaManager: MediaManager,
    private val getMusicCover: GetMusicCover,
    private val getMusicPosition: GetMusicPosition
) : BaseViewModel(application),MediaPlayer.OnCompletionListener {
    private var tracks = ArrayList<Track>()
    private var currentTrackPosition = 0

    private val _isPlaying = MutableStateFlow(mediaPlayer.isPlaying)
    val isPlaying = _isPlaying.asStateFlow()

    private val _artist = MutableStateFlow("Unknown")
    val artist= _artist.asStateFlow()

    private val _title = MutableStateFlow("Unknown")
    val title = _title.asStateFlow()

    private val _duration = MutableStateFlow("--:--")
    val duration= _duration.asStateFlow()

    private val _cover = MutableStateFlow("")
    val cover = _cover.asStateFlow()

    //private val _seek = MutableStateFlow(0)
    val seek: StateFlow<Int> = getMusicPosition().stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _maxSeek = MutableStateFlow(0)
    val maxSeek = _maxSeek.asStateFlow()

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
        val currentTrack = tracks[currentTrackPosition]

        _title.value = currentTrack.title
        _artist.value= currentTrack.artist
       _duration.value= currentTrack.duration


        val id: Long = currentTrack.id
        val albumID: Long = currentTrack.albumId
        val myUri: Uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )

        getMusicImg(albumID)

        mediaPlayer.apply {
            stop()
            reset()
            setDataSource(context, myUri)
            prepare()
        }

        updateSeekBar()
    }

    fun playOrPause() {
        when(isPlaying.value) {
            true -> pause()
            false -> resume()
        }
    }

    private fun getMusicImg(albumID: Long) {
        if (getMusicCover.isActive())
            getMusicCover.unsubscribe()

        getMusicCover(GetMusicCover.Params(albumID)) {
            "Cover loaded: $it".easyLog(this)
            _cover.value = when (it) {
                is Either.Left ->  ""
                is Either.Right -> {
                    "New cover = ${it.r}".easyLog(this)
                    it.r
                }
                else -> ""
            }
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

    fun checkPosition(position: Int){
        mediaPlayer.seekTo(position)
    }

    private fun updateSeekBar() {
        _maxSeek.value = mediaPlayer.duration
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