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
import android.media.AudioManager
import android.media.MediaPlayer
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
import ru.kamaz.music.cache.db.dao.Playback
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.TestWidget
import ru.kamaz.music_api.BaseConstants.ACTION_NEXT
import ru.kamaz.music_api.BaseConstants.ACTION_PREV
import ru.kamaz.music_api.BaseConstants.ACTION_TOGGLE_PAUSE
import ru.kamaz.music_api.BaseConstants.APP_WIDGET_UPDATE
import ru.kamaz.music_api.BaseConstants.EXTRA_APP_WIDGET_NAME
import ru.kamaz.music_api.interactor.*
import ru.kamaz.music_api.models.FavoriteSongs
import ru.kamaz.music_api.models.HistorySongs
import ru.kamaz.music_api.models.Track
import ru.kamaz.widget.ui.MusicWidget
import ru.sir.core.Either
import ru.sir.core.None
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.extensions.easyLog
import ru.sir.presentation.extensions.launchOn
import javax.inject.Inject


class MusicService : Service(), MusicServiceInterface.Service, MediaPlayer.OnCompletionListener,
    MusicManagerListener, BluetoothManagerListener {

    val twManager = BluetoothManager()
    lateinit var device: BluetoothDevice

    private val _isNotConnected = MutableStateFlow(true)
    val isNotConnected = _isNotConnected.asStateFlow()
    private val _isNotUSBConnected = MutableStateFlow(false)
    val isNotUSBConnected = _isNotUSBConnected.asStateFlow()

    private var lifecycleJob = Job()
    private lateinit var lifecycleScope: CoroutineScope

    private var audioManager: AudioManager? = null

    val context: Context? = null

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var mediaManager: MediaManager

    @Inject
    lateinit var getMusicCover: GetMusicCover

    @Inject
    lateinit var getMusicPosition: GetMusicPosition

    @Inject
    lateinit var insertFavoriteMusic: InsertFavoriteMusic

    @Inject
    lateinit var insertLastMusic: InsertLastMusic

    @Inject
    lateinit var queryLastMusic: QueryLastMusic

    @Inject
    lateinit var queryFavoriteMusic: QueryFavoriteMusic

    @Inject
    lateinit var deleteFavoriteMusic: DeleteFavoriteMusic

    private val widget = MusicWidget.instance

    private val widgettest = TestWidget.instance
    lateinit var myViewModel: MusicServiceInterface.ViewModel

    private var currentTrackPosition = 0
    private var tracks = ArrayList<Track>()
    private val binder = MyBinder()
    private var likeTrack = ArrayList<FavoriteSongs>()

    private var mode = SourceEnum.DISK

    private var repeatMode = RepeatMusicEnum.REPEAT_OFF

    private val _cover = MutableStateFlow("")
    val cover = _cover.asStateFlow()

    private val _title = MutableStateFlow("Unknown")
    val title = _title.asStateFlow()

    private val _artist = MutableStateFlow("Unknown")
    val artist = _artist.asStateFlow()

    private val _repeatHowNow = MutableStateFlow(0)
    val repeatHowNow = _repeatHowNow.asStateFlow()
    private val _data = MutableStateFlow("")
    val data = _data.asStateFlow()

    private val _isPlaying = MutableStateFlow<Boolean>(true)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isBtModeOn = MutableStateFlow<Boolean>(false)
    val isBtModeOn = _isBtModeOn.asStateFlow()

    private val _isDiskModeOn = MutableStateFlow<Boolean>(false)
    val isDiskModeOn = _isDiskModeOn.asStateFlow()

    private val _duration = MutableStateFlow("00:00")
    val duration = _duration.asStateFlow()

    private val _idSong = MutableStateFlow(1)
    val idSong = _idSong.asStateFlow()
    private val _tickFlow = MutableSharedFlow<Unit>(replay = 0)
    val tickFlow: MutableSharedFlow<Unit> = _tickFlow

    private val _maxSeek = MutableStateFlow(0)
    val maxSeek = _maxSeek.asStateFlow()

    private val _isFavorite = MutableStateFlow<Boolean>(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val _btDeviceIsConnecting = MutableStateFlow<Boolean>(false)
    val btDeviceIsConnecting = _btDeviceIsConnecting.asStateFlow()
    private val _musicEmpty = MutableStateFlow<Boolean>(false)
    val musicEmpty = _musicEmpty.asStateFlow()

    val _isShuffleStatus = MutableStateFlow(false)
    val isShuffleStatus= _isShuffleStatus.asStateFlow()

    fun currentPosition(){
        CoroutineScope(Dispatchers.IO).launch {
            while (true){

                updateTracks(mediaManager)
                val currentPosition = mediaPlayer.currentPosition
                val pp = Track.convertDuration(currentPosition.toLong())
                val tt = duration.value
                Log.i("Current", "currentPosition:$pp ")
                Log.i("Current", "currentPosition:$tt")
                delay(500)
                if (tt<=pp){
                    nextTrack()
                }
            }

        }
    }



  /*  private fun bubbleSortFromFool(items: MutableList){
        do{
            var swapped = false
            for(index in 1 until items.size){
                val previousItem = items[index-1]
                val currentItem = items[index]
                if(previousItem.compare(currentItem)){
                    items.swapItems(index-1, index)
                    swapped = true
                }
            }
        }while (swapped)
    }*/

    override fun onSdStatusChanged(path: String, isAdded: Boolean) {
    }

    override fun onUsbStatusChanged(path: String, isAdded: Boolean) {
        _isNotUSBConnected.value = isAdded

    }

    private fun queryLastMusic() {
        Log.i("queryLastMusic", "funWork")
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("queryLastMusic", "duration${this}")
            val it = queryLastMusic.run(None())

            it.either({
                Log.i("queryLastMusic", "Failure${it}")
            }, {
                Log.i("queryLastMusic", "duration${it}")
                (if (it.isEmpty()) initTrack(
                    tracks[currentTrackPosition],
                    data.value
                ) else checkCurrentPosition(it))

            })
        }
    }

    private fun queryMusicTime() {

    }

    fun checkEndMusic() {

    }

    private fun queryFavoriteMusic() {
        Log.i("queryFavoriteMusic", "funWork")
        CoroutineScope(Dispatchers.IO).launch {
            Log.i("queryFavoriteMusic", "duration${this}")
            val it =  queryFavoriteMusic.run(QueryFavoriteMusic.Params(data.value))
            it.either({
                Log.i("queryFavoriteMusic", "Failure${it}")
            }, {
                Log.i("queryFavoriteMusic", "duration${it}")
                (if (it.isEmpty()) Log.i("queryFavoriteMusic", "duration${it}")
                else checkFavoriteMusic(it))
            })
        }
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

    private fun getAudioManager(): AudioManager {
        if (audioManager == null) {
            audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        }
        return audioManager as AudioManager
    }


    override fun onBind(intent: Intent?): IBinder = binder

    override fun setViewModel(viewModel: MusicServiceInterface.ViewModel) {
        Log.i("testPlayTrack", "testPlay init ")
        this.myViewModel = viewModel
    }

    /**/
    override fun init() {
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setAudioAttributes(audioAttributes)
        Log.i("testPlayTrack", "testPlay init ")
    }

    enum class SourceEnum(val value: Int) {
        BT(0),
        AUX(1),
        DISK(2),
        USB(3)
    }

    enum class RepeatMusicEnum(val value: Int) {
        REPEAT_OFF(0),
        REPEAT_ONE_SONG(1),
        REPEAT_ALL(2)
    }

    override fun onBluetoothMusicDataChanged(name: String, artist: String) {

        Log.i("name", "name $name")
        Log.i("name", "name $artist")
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

    fun startDiskMode(btOn: Boolean) {
        if (btOn) stopBtListener()
        this.mode = SourceEnum.DISK
        _isDiskModeOn.tryEmit(true)
        _isDiskModeOn.tryEmit(false)
    }

    fun startUsbMode(usbOn: Boolean) {
        if (usbOn) stopBtListener()
        this.mode = SourceEnum.DISK
        _isDiskModeOn.tryEmit(true)
        _isDiskModeOn.tryEmit(false)
    }


    fun stopBtListener() {
        Log.i("test", "stopBtListener")
        try {
            twManager.playerPlayPause()
            twManager.removeListener(this)
            twManager.stopMonitoring(applicationContext)

        } catch (e: Exception) {

        }

    }

    fun startBtListener() {

        twManager.startMonitoring(applicationContext) {
            twManager.addListener(this)
            twManager.requestConnectionInfo()

        }
    }

    fun startMusicListener() {
        // twManagerMusic.addListener(this)
    }

    fun stopMusicListener() {
        //  twManagerMusic.removeListener(this)
    }

    fun initTrack(track: Track, data1: String) {
        Log.i("testPlayTrack", "$track$data1 ")
        _isFavorite.value = false
        val currentTrack = track
        updateTracks(mediaManager)
        val albumID: Long = currentTrack.albumId
        _idSong.value = currentTrack.id.toInt()
        Log.i("testPlayTrack", "${_idSong.value} ")
        updateMusicName(currentTrack.title, currentTrack.artist, currentTrack.duration)
        _data.value = track.data
        getMusicImg(albumID)
        mediaPlayer.apply {
            stop()
            reset()
            setDataSource(if (data1.isEmpty()) track.data else data1)
            prepare()
        }
        queryFavoriteMusic()
    }

    fun stopMediaPlayer() {
        mediaPlayer.stop()
    }

    override fun testPlay(track: Track) {
        Log.i("testPlayTrack", "testPlay ")
        updateTracks(mediaManager)
        val currentTrack = track
        val albumID: Long = currentTrack.albumId
        _idSong.value = currentTrack.id.toInt()
        updateMusicName(currentTrack.title, currentTrack.artist, currentTrack.duration)

        getMusicImg(albumID)
        mediaPlayer.apply {
            stop()
            reset()
            setDataSource(track.data)
            prepare()
        }
        updateSeekBar()

    }

    fun checkCurrentPosition(data: String) {
        var i = 0
        var q = 0
        CoroutineScope(Dispatchers.IO).launch {
            while (i in tracks.indices) {
                if (tracks[q].data != data) {
                    q++
                    i++
                } else {
                    Log.i(TAG, "checkCurrentPosition $q$data")
                    initTrack(tracks[q], data)
                    break
                }
            }
        }
        currentTrackPosition = q
    }

    override fun firstOpenTrackFound(track: Track) {
        updateTracks(mediaManager)
        val currentTrack = track
        updateMusicName(currentTrack.title, currentTrack.artist, currentTrack.duration)

    }

    override fun playOrPause(): Boolean {

        when (mode) {
            SourceEnum.DISK -> {
                updateSeekBar()
                when (isPlaying()) {
                    true -> pause()
                    false -> resume()
                }
            }
            SourceEnum.BT -> {
                twManager.playerPlayPause()
            }
            SourceEnum.AUX -> TODO()
        }
        return isPlaying()
    }

    private fun updateMusicName(title: String, artist: String, duration: String) {
        _title.value = title
        _artist.value = artist
        _duration.value = duration
        _isPlaying.value = mediaPlayer.isPlaying
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
        _isPlaying.value = mediaPlayer.isPlaying
    }

    override fun resume() {
        mediaPlayer.start()
        _isPlaying.value = mediaPlayer.isPlaying
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

                    when (isPlaying()) {
                        true -> {
                            initTrack(
                                tracks[currentTrackPosition],
                                tracks[currentTrackPosition].data
                            )
                            mediaPlayer.start()
                        }
                        false -> initTrack(
                            tracks[currentTrackPosition],
                            tracks[currentTrackPosition].data
                        )
                    }
                    Log.i("isPlaying", "${isPlaying()}")

                }
            }
            SourceEnum.BT -> {
                twManager.playerPrev()
            }
            SourceEnum.AUX -> TODO()
            SourceEnum.USB -> TODO()
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
                    when (isPlaying()) {
                        true -> {
                            initTrack(
                                tracks[currentTrackPosition],
                                tracks[currentTrackPosition].data
                            )
                            mediaPlayer.start()
                        }
                        false -> initTrack(
                            tracks[currentTrackPosition],
                            tracks[currentTrackPosition].data
                        )
                    }
                }

            }
            SourceEnum.BT -> {
                twManager.playerNext()
            }
            SourceEnum.AUX -> {

            }

            SourceEnum.USB -> {

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
    var playback: Playback? = null

    override fun onCreate() {
        super.onCreate()

        (application as BaseApplication).getComponent<MusicComponent>().inject(this)
        init()
        registerReceiver(widgetIntentReceiver, IntentFilter(APP_WIDGET_UPDATE))
        initMediaPlayer()
        startForeground()
        startMusicListener()
        initLifecycleScope()
        updateTracks(mediaManager)

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

        isPlaying.launchOn(lifecycleScope) {
            widgettest.updatePlayPauseImg(this, it)
        }
        if (tracks.isEmpty()) {
            Log.d("mediaPlayer", "no")
            _musicEmpty.value = true
            _musicEmpty.value = false
        } else {
            // initTrack(tracks[currentTrackPosition], data.value)
        }
        queryLastMusic()
        currentPosition()
        Log.i("testPlayTrack", "testPlay ")
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
        val notification = notificationBuilder
            .setOngoing(true)
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

    override fun changeRepeatMode() {

        when (repeatHowNow.value) {
            0 -> _repeatHowNow.value = 1
            1 -> _repeatHowNow.value = 2
            2 -> _repeatHowNow.value = 0
        }
    }




    fun howRepeatMode() {
        when (repeatHowNow.value) {
            0 -> repeatOff()
            1 -> oneSongRepeat()
            2 -> allSongsRepeat()
        }
    }

    private fun oneSongRepeat() {

    }

    private fun allSongsRepeat() {

    }

    private fun repeatOff() {

    }

    override fun sourceSelection(action: SourceEnum) {
        when (action) {
            SourceEnum.BT -> {
                if (isNotConnected.value) {
                    Log.i("test", "bt off")
                    getToastConnectBtDevice(true)
                } else {
                    Log.i("test", "bt on")
                    startBtMode()
                }

            }
            SourceEnum.DISK ->
                if (isNotConnected.value) {
                    startDiskMode(false)
                } else {
                    startDiskMode(true)
                }
            SourceEnum.USB ->
                if (isNotUSBConnected.value) {
                    startUsbMode(true)
                } else {
                    startUsbMode(false)
                }
        }
    }

    fun getToastConnectBtDevice(btDevise: Boolean) {
        _btDeviceIsConnecting.value = btDevise
        _btDeviceIsConnecting.value = !btDevise
    }

    override fun getMusicName(): StateFlow<String> = title

    override fun getArtistName(): StateFlow<String> = artist

    override fun getRepeat(): StateFlow<Int> = repeatHowNow

    override fun getMusicDuration(): StateFlow<String> = duration

    override fun isFavoriteMusic(): StateFlow<Boolean> = isFavorite


    override fun isShuffleOn(): StateFlow<Boolean> = isShuffleStatus

    override fun checkDeviceConnection(): StateFlow<Boolean> = isNotConnected
    override fun checkUSBConnection(): StateFlow<Boolean> = isNotUSBConnected
    override fun checkBTConnection(): StateFlow<Boolean> = isNotConnected
    override fun updateWidget(): StateFlow<Boolean> = isNotConnected
    override fun btModeOn(): StateFlow<Boolean> = isBtModeOn
    override fun diskModeOn(): StateFlow<Boolean> = _isDiskModeOn
    override fun usbModeOn(): StateFlow<Boolean> = isNotUSBConnected

    override fun dialogFragment(): StateFlow<Boolean> = btDeviceIsConnecting
    override fun musicEmpty(): StateFlow<Boolean> = musicEmpty

    override fun insertFavoriteMusic() {
        queryFavoriteMusic()
        if (isFavorite.value) {
            deleteFavoriteMusic()
        } else {
            _isFavorite.value = true
            val music = FavoriteSongs(idSong.value, data.value)
            Log.i("insertFavoriteMusic", music.data)
            insertFavoriteMusic(InsertFavoriteMusic.Params(music))
        }
    }

    override fun shuffleStatusChange() {
     if (isShuffleStatus.value){
         _isShuffleStatus.value = false
     }else{
         _isShuffleStatus.value = true
     }
    }

    override fun deleteFavoriteMusic() {
        _isFavorite.value = false
        val music = FavoriteSongs(idSong.value, data.value)
        Log.i("insertFavoriteMusic", music.data)
        deleteFavoriteMusic(DeleteFavoriteMusic.Params(music))
    }

    fun checkFavoriteMusic(data: String) {
        var i = 0
        CoroutineScope(Dispatchers.IO).launch {
            while (i in tracks.indices) {
                if (tracks[i].data != data) {
                    i++
                } else {
                    Log.i("checkFavoriteMusic", "checkCurrentPosition $i$data")
                    _isFavorite.value = true
                    break
                }
            }
        }
    }

    override fun insertLastMusic() {
        val music = HistorySongs(
            18,
            idSong.value,
            title.value,
            228,
            1,
            1,
            data.value,
            1,
            1,
            title.value,
            1,
            artist.value,
            artist.value,
            1
        )
        Log.i("insertLastMusic", music.data)

        insertLastMusic(InsertLastMusic.Params(music))
    }

    fun checkRepeatState() {

    }

    override fun onCompletion(mp: MediaPlayer?) {

    }


    override fun onPlayerPlayPauseState(isPlaying: Boolean) {
        "BT Player state = $isPlaying".easyLog(this)
    }


    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Pause playback
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower the volume, keep playing
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
            }
        }
    }


}