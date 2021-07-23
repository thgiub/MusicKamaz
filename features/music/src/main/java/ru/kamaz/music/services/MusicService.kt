package ru.kamaz.music.services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothDevice
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
import ru.biozzlab.twmanager.domain.interfaces.MusicManagerListener
import ru.biozzlab.twmanager.managers.BluetoothManager
import ru.biozzlab.twmanager.managers.MusicManager

import ru.kamaz.music.data.MediaManager
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.TestWidget
import ru.kamaz.music_api.BaseConstants.ACTION_NEXT
import ru.kamaz.music_api.BaseConstants.ACTION_PREV
import ru.kamaz.music_api.BaseConstants.ACTION_TOGGLE_PAUSE
import ru.kamaz.music_api.BaseConstants.APP_WIDGET_UPDATE
import ru.kamaz.music_api.BaseConstants.EXTRA_APP_WIDGET_NAME
import ru.kamaz.music_api.interactor.GetMusicCover
import ru.kamaz.music_api.interactor.GetMusicPosition
import ru.kamaz.music_api.models.Track
import ru.kamaz.widget.ui.MusicWidget
import ru.sir.core.Either
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.extensions.easyLog
import ru.sir.presentation.extensions.launchOn
import javax.inject.Inject



class MusicService : Service(), MusicServiceInterface.Service, MediaPlayer.OnCompletionListener,
    BluetoothManagerListener, MusicManagerListener {

    val twManager = BluetoothManager()
    lateinit var device: BluetoothDevice
    private val twManagerMusic = MusicManager()

    private val _isNotConnected = MutableStateFlow(true)
    val isNotConnected = _isNotConnected.asStateFlow()
    private val _isNotUSBConnected = MutableStateFlow(true)
    val isNotUSBConnected = _isNotUSBConnected.asStateFlow()

    private var lifecycleJob = Job()
    private lateinit var lifecycleScope: CoroutineScope


    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var mediaManager: MediaManager

    @Inject
    lateinit var getMusicCover: GetMusicCover

    @Inject
    lateinit var getMusicPosition: GetMusicPosition

    private val widget = MusicWidget.instance
    private val widgettest = TestWidget.instance


    lateinit var myViewModel: MusicServiceInterface.ViewModel


    override fun onSdStatusChanged(path: String, isAdded: Boolean) {
        _isNotUSBConnected.value = !isAdded
    }

    override fun onUsbStatusChanged(path: String, isAdded: Boolean) {
        _isNotUSBConnected.value = isAdded

    }

    override fun onDeviceConnected() {
        _isNotConnected.value = false
        Log.i("bt_serv", "onDeviceConnected ${_isNotConnected.value} ")

    }

    override fun onDeviceDisconnected() {
        _isNotConnected.value = true
        Log.i("bt_serv", "onDeviceConnected ${_isNotConnected.value} ")
    }

    inner class MyBinder : Binder() {
        fun getService(): MusicServiceInterface.Service = this@MusicService
    }

    private var currentTrackPosition = 0
    private var tracks = ArrayList<Track>()
    private val binder = MyBinder()

    private var mode = SourceEnum.DISK

    private val _cover = MutableStateFlow("")
    val cover = _cover.asStateFlow()

    private val _title = MutableStateFlow("Unknown")
    val title = _title.asStateFlow()

    private val _artist = MutableStateFlow("Unknown")
    val artist = _artist.asStateFlow()

    private val _isPlaying = MutableStateFlow<Boolean>(true)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isBtModeOn = MutableStateFlow<Boolean>(false)
    val isBtModeOn = _isBtModeOn.asStateFlow()

    private val _duration = MutableStateFlow("00:00")
    val duration = _duration.asStateFlow()

    private val _tickFlow = MutableSharedFlow<Unit>(replay = 0)
    val tickFlow: MutableSharedFlow<Unit> = _tickFlow

    private val _maxSeek = MutableStateFlow(0)
    val maxSeek = _maxSeek.asStateFlow()

    private val _btDeviceIsConnecting = MutableStateFlow(1)
    val btDeviceIsConnecting = _btDeviceIsConnecting.asStateFlow()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun setViewModel(viewModel: MusicServiceInterface.ViewModel) {
        this.myViewModel = viewModel
    }

    override fun init() {

    }

    enum class SourceEnum(val value: Int) {
        BT(0),
        AUX(1),
        DISK(2)
    }

    override fun onBluetoothMusicDataChanged(name: String, artist: String) {
        _title.value = name
        _artist.value = artist
        _isPlaying.value = mediaPlayer.isPlaying
    }

    fun startBtMode() {
        stopMediaPlayer()
        startBtListener()
       this.mode = SourceEnum.BT
        _isBtModeOn.tryEmit(true)
        _isBtModeOn.tryEmit(false)
    }

    fun startDiskMode() {
        this.mode = SourceEnum.DISK
        //stopBtListener()
    }


    fun stopBtListener() {
        Log.i("test", "stopBtListener")
       // twManager.playerPlayPause()
        twManager.removeListener(this)
        twManager.stopMonitoring(applicationContext)
    }
    fun startBtListener(){
        Log.i("test", "startBtListener")
        twManager.startMonitoring(applicationContext) {
            twManager.addListener(this)
            twManagerMusic.addListener(this)
            twManager.requestConnectionInfo()
        }

    }

    fun startMusicListener(){
        twManagerMusic.addListener(this)
    }
    fun stopMusicListener(){
        twManagerMusic.removeListener(this)
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

    fun stopMediaPlayer() {
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

    override fun firstOpenTrackFound(track: Track) {
        Log.i("222", "$track ")
        updateTracks(mediaManager)
        val currentTrack = track
        updateMusicName(currentTrack.title, currentTrack.artist, currentTrack.duration)

    }

    override fun playOrPause(): Boolean {

        when (mode) {
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
        return    isPlaying()
    }

    private fun updateMusicName(title: String, artist: String, duration: String) {
        _title.value = title
        _artist.value = artist
        _duration.value=duration
        _isPlaying.value= mediaPlayer.isPlaying
    }

    override fun getMusicImg(albumID: Long) {


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
        _isPlaying.value= mediaPlayer.isPlaying
    }

    override fun resume() {
        mediaPlayer.start()
        _isPlaying.value= mediaPlayer.isPlaying
    }

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun checkPosition(position: Int) {
        mediaPlayer.seekTo(position)
    }

    private fun updateSeekBar() {
        val duration = mediaPlayer.duration
        myViewModel.onUpdateSeekBar(duration)
    }


    override fun previousTrack() {
        when (mode) {
            SourceEnum.DISK -> {
                if (tracks.isEmpty()) {

                } else {
                    when (currentTrackPosition + 1) {
                        tracks.size -> currentTrackPosition = 0
                        else -> currentTrackPosition++
                    }
                    testPlay(tracks[currentTrackPosition])
                    when (isPlaying()) {
                        true -> mediaPlayer.start()
                    }
                }
            }
            SourceEnum.BT -> {
                twManager.playerPrev()
            }
            SourceEnum.AUX -> TODO()
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


    override fun nextTrack() {
        when (mode) {
            SourceEnum.DISK -> {
                if (tracks.isEmpty()) {

                } else {
                    when (currentTrackPosition - 1) {
                        -1 -> currentTrackPosition = tracks.size - 1
                        else -> currentTrackPosition--
                    }
                    testPlay(tracks[currentTrackPosition])
                    when (isPlaying()) {
                        true -> mediaPlayer.start()
                    }
                }

            }
            SourceEnum.BT -> {
                twManager.playerNext()
                //updateMusicName(name,artist,duration)
            }
            SourceEnum.AUX -> {

            }
        }
    }

    private val widgetIntentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val command = intent.getStringExtra(EXTRA_APP_WIDGET_NAME)
            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if (command != null) {

            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        (application as BaseApplication).getComponent<MusicComponent>().inject(this)

        registerReceiver(widgetIntentReceiver, IntentFilter(APP_WIDGET_UPDATE))
        updateTracks(mediaManager)
        initMediaPlayer()
        startForeground()
        startMusicListener()
        initLifecycleScope()
        Log.i("init", "listener_work")
        twManager.startMonitoring(applicationContext) {
            twManager.addListener(this)
            twManager.requestConnectionInfo()
            Log.i("init", "listener_work")
        }

        artist.launchOn(lifecycleScope) {
            widgettest.updateTestArtist(this, it)
        }
        title.launchOn(lifecycleScope) {
            widgettest.updateTestTitle(this, it)
        }
        duration.launchOn(lifecycleScope) {
            widgettest.updateTestDuration(this, it)
        }

        isPlaying.launchOn(lifecycleScope){
            widgettest.updatePlayPauseImg(this, it)
        }

    }

    private fun initLifecycleScope() {
        unsubscribeLifecycleScope()

        lifecycleJob = Job()
        lifecycleScope = CoroutineScope(Dispatchers.Main + lifecycleJob)
    }

    fun unsubscribeLifecycleScope() {
        lifecycleJob.apply {
            cancelChildren()
            cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeLifecycleScope()
        stopMusicListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i("PLaystart", "onStartCommand: playstart")
        if (intent != null) {
            when (intent.action) {
                ACTION_TOGGLE_PAUSE -> playOrPause()
                ACTION_NEXT -> nextTrack()
                ACTION_PREV -> previousTrack()
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

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
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
        when (action) {
            SourceEnum.BT -> {
                if (isNotConnected.value) {
                    Log.i("test", "bt off")
                    getToastConnectBtDevice(0)
                } else {
                    Log.i("test", "bt on")
                    startBtMode()
                }

            }
            SourceEnum.DISK -> startDiskMode()
            SourceEnum.AUX -> TODO()
        }
    }

    fun getToastConnectBtDevice(btDevise: Int){
        _btDeviceIsConnecting.value = btDevise
    }

    override fun getMusicName(): StateFlow<String> = title

    override fun getArtistName(): StateFlow<String> = artist

    override fun getMusicDuration(): StateFlow<String> = duration



    override fun checkDeviceConnection(): StateFlow<Boolean> = isNotConnected
    override fun checkUSBConnection(): StateFlow<Boolean> = isNotUSBConnected
    override fun checkBTConnection(): StateFlow<Boolean> = isNotConnected
    override fun updateWidget(): StateFlow<Boolean> =isNotConnected
    override fun btModeOn(): StateFlow<Boolean> =isBtModeOn

    override fun onCompletion(mp: MediaPlayer?) {

    }


    override fun onPlayerPlayPauseState(isPlaying: Boolean) {
        "BT Player state = $isPlaying".easyLog(this)
    }

}