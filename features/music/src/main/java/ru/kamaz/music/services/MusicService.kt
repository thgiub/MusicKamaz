  package ru.kamaz.music.services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.content.ContentValues.TAG
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.biozzlab.twmanager.domain.interfaces.BluetoothManagerListener
import ru.biozzlab.twmanager.managers.BluetoothManager
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music_api.interactor.GetMusicCover
import ru.kamaz.music_api.interactor.GetMusicPosition
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.extensions.easyLog
import javax.inject.Inject


class MusicService() : Service(), MusicServiceInterface.Service, MediaPlayer.OnCompletionListener, BluetoothManagerListener  {

    val twManager = BluetoothManager()

    companion object {

        const val ACTION_TOGGLE_PAUSE = "play"
        const val ACTION_NEXT= "next"
        const val ACTION_PREV = "prev"
        const val APP_WIDGET_UPDATE = ".appwidgetupdate"
        const val EXTRA_APP_WIDGET_NAME = "ru.kamaz.musickamaz"
        const val META_CHANGED = ".metachanged"
        const val PLAY_STATE_CHANGED = ".playstatechanged"
    }

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var mediaManager: MediaManager

    @Inject
    lateinit var getMusicCover: GetMusicCover

    @Inject
    lateinit var getMusicPosition: GetMusicPosition


    lateinit var myViewModel:MusicServiceInterface.ViewModel


    inner class MyBinder : Binder() {
        fun getService(): MusicServiceInterface.Service = this@MusicService
    }



    private var currentTrackPosition = 0
    private var tracks = ArrayList<Track>()
    private val binder = MyBinder()

    private var mode = SourceEnum.DISK

    private val _cover = MutableStateFlow("")
    val cover = _cover.asStateFlow()

    private val _name = MutableStateFlow("")
    //var name = "null"
    var duration= "00:00"
/*    private var _duration =  MutableStateFlow("")
    var duration =  _duration.asStateFlow()*/
    //var artist = "null"

    private val _title =  MutableStateFlow("Unknown")
    val title =  _title.asStateFlow()
    private val _artist =  MutableStateFlow("Unknown")
    val artist =  _artist.asStateFlow()

    private val _tickFlow = MutableSharedFlow<Unit>(replay = 0)
    val tickFlow: MutableSharedFlow<Unit> = _tickFlow

    private val _maxSeek = MutableStateFlow(0)
    val maxSeek = _maxSeek.asStateFlow()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun setViewModel(viewModel: MusicServiceInterface.ViewModel) {
       this.myViewModel=viewModel
    }

    override fun init() {
        TODO("Not yet implemented")
    }

   enum class SourceEnum(val value: Int ){
       BT(0),
       AUX(1),
       DISK(2)
   }

    override fun onBluetoothMusicDataChanged(name: String, artist: String) {
        _title.value = name
        _artist.value = artist
    }

    fun startBtMode() {
        myViewModel.selectBtMode()
        "до stopBt".easyLog(startBtMode())
        this.mode = SourceEnum.BT
        stopMediaPlayer()
        twManager.startMonitoring(applicationContext) {
            twManager.addListener(this)
            twManager.requestConnectionInfo()
        }
    }

    fun startDiskMode(){
       this.mode = SourceEnum.DISK
        "после stopBt".easyLog(startDiskMode())
        stopBtListener()
        "до stopBt".easyLog(startDiskMode())
    }


    fun stopBtListener(){
        twManager.removeListener(this)
        twManager.stopMonitoring(applicationContext)
        "до stopBt".easyLog(stopBtListener())
    }

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

    fun stopMediaPlayer(){
        mediaPlayer.stop()
    }

    override fun testPlay(track: Track) {
        updateTracks(mediaManager)
        val currentTrack = track
        val albumID: Long = currentTrack.albumId

        updateMusicName(currentTrack.title, currentTrack.artist, currentTrack.duration)

        getMusicImg(albumID)
        mediaPlayer.apply {
            stop()
            reset()
            setDataSource(track.data)
            prepare()
            start()
        }
        updateSeekBar()
    }

    override fun playOrPause(): Boolean {

      when(mode){
          SourceEnum.DISK -> {
              when (isPlaying()) {
                  true -> pause()
                  false -> resume()
              }
          }
          SourceEnum.BT -> {
              twManager.playerPlayPause()

              Log.i("PoP", "play or pause")
          }
          SourceEnum.AUX -> TODO()
      }
      return isPlaying()
    }

    private fun updateMusicName(title: String, artist: String, duration: String){
        _title.value = title
        _artist.value = artist
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
        mediaPlayer.pause()
    }

    override fun resume() {
        mediaPlayer.start()
    }

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun checkPosition(position: Int) {
        mediaPlayer.seekTo(position)
    }

    private fun updateSeekBar() {
      val duration = mediaPlayer.duration
        myViewModel.onUpdateSeekBar(duration)
    }

    override fun previousTrack(context: Context) {
        when(mode){
            SourceEnum.DISK->{
                when (currentTrackPosition + 1) {
                    tracks.size -> currentTrackPosition = 0
                    else -> currentTrackPosition++
                }
                testPlay(tracks[currentTrackPosition])
                when (isPlaying()) {
                    true -> mediaPlayer.start()
                }
            }
            SourceEnum.BT->{
                twManager.playerPrev()
            }
            SourceEnum.AUX -> TODO()
        }

    }



   fun previousTrack() {
        when (currentTrackPosition + 1) {
            tracks.size -> currentTrackPosition = 0
            else -> currentTrackPosition++
        }
        testPlay(tracks[currentTrackPosition])
        when (isPlaying()) {
            true -> mediaPlayer.start()
        }
    }


    fun initMediaPlayer() {
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setAudioAttributes(audioAttributes)
    }

    override fun nextTrack(context: Context) {
        when(mode){
            SourceEnum.DISK->{
                when (currentTrackPosition - 1) {
                    -1 -> currentTrackPosition = tracks.size - 1
                    else -> currentTrackPosition--
                }
                testPlay(tracks[currentTrackPosition])
                when (isPlaying()) {
                    true -> mediaPlayer.start()
                }
            }
            SourceEnum.BT->{
                twManager.playerNext()
                //updateMusicName(name,artist,duration)
            }
            SourceEnum.AUX->{

            }
        }

    }

   fun nextTrack() {
        when (currentTrackPosition - 1) {
            -1 -> currentTrackPosition = tracks.size - 1
            else -> currentTrackPosition--
        }
        testPlay(tracks[currentTrackPosition])
        when (isPlaying()) {
            true -> mediaPlayer.start()
        }
    }

    override fun onCreate() {
        super.onCreate()
        (application as BaseApplication).getComponent<MusicComponent>().inject(this)
        updateTracks(mediaManager)
        initMediaPlayer()
        startForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i("PLaystart", "onStartCommand: playstart")
        if (intent != null) {
            when (intent.action) {
                ACTION_TOGGLE_PAUSE -> playOrPause()
                ACTION_NEXT-> nextTrack()
                ACTION_PREV-> previousTrack()
            }
        }

        return START_STICKY //super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {

                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
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

    override fun sourceSelection(action: SourceEnum) {
        when(action){
            SourceEnum.BT-> startBtMode()
            SourceEnum.DISK-> startDiskMode()
            SourceEnum.AUX -> TODO()
        }
    }

    override fun getMusicName(): StateFlow<String> = title

    override fun getArtistName(): StateFlow<String> = artist

    override fun onCompletion(mp: MediaPlayer?) {

    }

    override fun onPlayerPlayPauseState(isPlaying: Boolean) {
        "BT Player state = $isPlaying".easyLog(this)
    }

}