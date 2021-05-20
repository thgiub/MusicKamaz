         package ru.kamaz.music.view_models

import android.app.Application
import android.content.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
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
import ru.sir.presentation.extensions.launchOn
import javax.inject.Inject
import kotlin.collections.ArrayList

class MusicFragmentViewModel @Inject constructor(
    application: Application,
    private val mediaPlayer: MediaPlayer,
    private val mediaManager: MediaManager,
    private val getMusicPosition: GetMusicPosition
) : BaseViewModel(application),MediaPlayer.OnCompletionListener, ServiceConnection,MusicServiceInterface.ViewModel {
    private var tracks = ArrayList<Track>()
    private var currentTrackPosition = 0

    private val _isPlaying = MutableStateFlow(mediaPlayer.isPlaying)
    val isPlaying = _isPlaying.asStateFlow()

    val artist: StateFlow<String> by lazy { service.value?.getArtistName() ?: MutableStateFlow("Unknown") }

    val title: StateFlow<String> by lazy { service.value?.getMusicName() ?: MutableStateFlow("Unknown") }

    private val _duration = MutableStateFlow("--:--")
    val duration= _duration.asStateFlow()

    private val _cover = MutableStateFlow("")
    val cover = _cover.asStateFlow()

    private val _mode = MutableStateFlow("")
    val mode = _mode.asStateFlow()

    private val _service = MutableStateFlow<MusicServiceInterface.Service?>(null)
    val service = _service.asStateFlow()

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
        service.value?.testPlay(tracks[currentTrackPosition])
    }

    fun playOrPause() {
        _isPlaying.value = service.value?.playOrPause() ?: false
    }


    fun checkPosition(position: Int){
         service.value?.checkPosition(position)
    }

    fun previousTrack() {
        service.value?.previousTrack(context)
    }

    fun nextTrack() {
      service.value?.nextTrack(context)
    }

    fun updateTracks(mediaManager: MediaManager) {
        val result = mediaManager.scanTracks()
        if (result is Either.Right) {
            tracks.addAll(result.r)
        }
    }

    fun vmSourceSelection(action:MusicService.SourceEnum){
       service.value?.sourceSelection(action)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextTrack()
    }

   override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
       Log.d("serWStart", "onServiceConnected: TRACK-VM")
       _service.value = (service as MusicService.MyBinder).getService()
        this.service.value?.setViewModel(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        _service.value = null
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

    override fun selectBtMode() {

    }

   /* override fun selectMode(action: MusicService.sourceEnum) {
        _mode.value = action.toString()
    }*/


    override fun onResume() {
        super.onResume()
        Log.d("serWStart", "onResume: VM")
    }

    override fun onStop() {
        super.onStop()
        Log.d("serWStart", "onStop: VM")
    }
}