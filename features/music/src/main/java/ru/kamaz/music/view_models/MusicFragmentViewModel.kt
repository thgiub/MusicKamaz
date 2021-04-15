package ru.kamaz.music.view_models

import android.app.Application
import android.content.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.services.MusicServiceInterface
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
) : BaseViewModel(application),MediaPlayer.OnCompletionListener, ServiceConnection,MusicServiceInterface.ViewModel {
    private var tracks = ArrayList<Track>()
    private var currentTrackPosition = 0
    private var service: MusicServiceInterface.Service? = null

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

    var musicPosition: StateFlow<Int> = getMusicPosition().stateIn(viewModelScope, SharingStarted.Lazily, 0)

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

        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate() {
        updateTracks(   mediaManager)
        super.onCreate()
    }

    fun startTrack(){
        service?.testPlay(tracks[currentTrackPosition])
    }

    fun playOrPause() {
        _isPlaying.value = service?.playOrPause() ?: false
    }


    fun checkPosition(position: Int){
         service?.checkPosition(position)
    }

    fun previousTrack() {
        service?.previousTrack(context)
    }

    fun nextTrack() {
      service?.nextTrack(context)
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

   override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.service = (service as MusicService.MyBinder).getService()
        this.service?.setViewModel(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    override fun updateMusicName(title: String,artist:String,duration: String) {
        this._title.value=title
        _artist.value= artist
        _duration.value= duration
    }


    override fun addListener() {
        TODO("Not yet implemented")
    }

    override fun removeListener() {
        TODO("Not yet implemented")
    }

    /* override fun onUpdateSeek(progress:StateFlow<Int>) {
         this.musicPosition= progress
     }*/

    override fun onCheckPosition(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onUpdateSeekBar(duration: Int) {
        _maxSeek.value = duration
    }
}