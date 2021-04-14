package ru.kamaz.music.services

import android.app.Service
import android.content.ContentUris
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.PermissionUtils
import ru.kamaz.music_api.interactor.GetMusicCover
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.extensions.easyLog
import javax.inject.Inject

class MusicService() : Service(), MusicServiceInterface, MediaPlayer.OnCompletionListener {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var mediaManager: MediaManager

    @Inject
    lateinit var getMusicCover: GetMusicCover

    inner class MyBinder : Binder() {
        fun getService(): MusicServiceInterface = this@MusicService
    }


    private var currentTrackPosition = 0
    private var tracks = ArrayList<Track>()
    private val binder = MyBinder()
    private val _cover = MutableStateFlow("")
    val cover = _cover.asStateFlow()
    private val _maxSeek = MutableStateFlow(0)
    val maxSeek = _maxSeek.asStateFlow()
    override fun onBind(intent: Intent?): IBinder = binder

    override fun startTrack(context: Context) {

        updateTracks(mediaManager)
        val currentTrack = tracks[currentTrackPosition]
        val id: Long = currentTrack.id
        val albumID: Long = currentTrack.albumId
        val myUri: Uri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )
        getMusicImg(albumID)
        mediaPlayer.apply {
            stop()
            setDataSource(context, myUri)
            reset()
            prepare()
        }
    }

    override fun testPlay(track: Track) {
        updateTracks(mediaManager)
        val currentTrack = track
        val albumID: Long = currentTrack.albumId

        getMusicImg(albumID)
        mediaPlayer.apply {
            stop()
            reset()
            setDataSource(track.data)
            prepare()
            start()
        }
    }



    override fun playOrPause() {
        when (isPlaying()) {
            true -> pause()
            false -> resume()
        }
    }

    override fun getMusicImg(albumID: Long) {
        if (getMusicCover.isActive())
            getMusicCover.unsubscribe()

        getMusicCover(GetMusicCover.Params(albumID)) {
            "Cover loaded: $it".easyLog(this)
            _cover.value = when (it) {
                is Either.Left -> ""
                is Either.Right -> {
                    "New cover = ${it.r}".easyLog(this)
                    it.r
                }
                else -> ""
            }
        }
    }


    override fun pause() {
        //_isPlaying.value = false
        mediaPlayer.pause()
    }

    override fun resume() {
        //_isPlaying.value = true
        mediaPlayer.start()
    }

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun checkPosition(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun updateSeekBar() {
        _maxSeek.value = mediaPlayer.duration
    }

    override fun previousTrack(context: Context) {
        when (currentTrackPosition + 1) {
            tracks.size -> currentTrackPosition = 0
            else -> currentTrackPosition++
        }
        startTrack(context)
        when (isPlaying()) {
            true -> mediaPlayer.start()
        }
    }

    fun init() {

        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setAudioAttributes(audioAttributes)
    }

    override fun nextTrack(context: Context) {
        when (currentTrackPosition - 1) {
            -1 -> currentTrackPosition = tracks.size - 1
            else -> currentTrackPosition--
        }
        startTrack(context)
        when (isPlaying()) {
            true -> mediaPlayer.start()
        }
    }

 /*   override fun updateMusic(track: Track) {
        this.tracks = track
    }*/

    override fun onCreate() {
        super.onCreate()
        (application as BaseApplication).getComponent<MusicComponent>().inject(this)
        updateTracks(mediaManager)
        init()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun updateTracks(mediaManager: MediaManager) {
        val result = mediaManager.scanTracks()
        if (result is Either.Right) {
            tracks.addAll(result.r)
        }
    }

    override fun intMediaPlayer() {
        Log.d(TAG, "init")
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setAudioAttributes(audioAttributes)
    }

    override fun onCompletion(mp: MediaPlayer?) {

    }


}