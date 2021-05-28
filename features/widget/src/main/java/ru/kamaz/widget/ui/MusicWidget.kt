package ru.kamaz.widget.ui

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.services.MusicService.Companion.ACTION_NEXT
import ru.kamaz.music.services.MusicService.Companion.ACTION_PREV
import ru.kamaz.music.services.MusicService.Companion.ACTION_TOGGLE_PAUSE
import ru.kamaz.music.services.MusicServiceInterface
import ru.kamaz.widget.R
import ru.kamaz.widget.ui.base.BaseAppWidget
import ru.sir.presentation.extensions.launchWhenStarted


/**
 * Implementation of App Widget functionality.
 */
class MusicWidget :BaseAppWidget(), MusicServiceInterface.ViewModel, ServiceConnection {
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var service2: MusicServiceInterface.Service? = null

    private val _service = MutableStateFlow<MusicServiceInterface.Service?>(null)
    val service = _service.asStateFlow()
    val artist: StateFlow<String> by lazy { service.value?.getArtistName() ?: MutableStateFlow("amigos") }

    val title: StateFlow<String> by lazy { service.value?.getMusicName() ?: MutableStateFlow("diaz") }



    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.i("ttt", "onStartCommand: resiverstart")
        if (intent != null) {
            Log.i("ttt", "onStartCommand: nenull")
            when (intent.action) {

                ACTION_TOGGLE_PAUSE -> {
                    Log.i("ttt", "onStartCommand: play")
                }

            }
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }

    override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.music_widget)
        linkButtons(context, appWidgetView)
        pushUpdate(context, appWidgetIds, appWidgetView)
        appWidgetView.setTextViewText(R.id.music_name,title.toString())
        appWidgetView.setTextViewText(R.id.artist_name,artist.toString())
    }

    override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(service.packageName, R.layout.music_widget)
        appWidgetView.setTextViewText(R.id.music_name,title.value)
        Log.i("didi", "${title.value}")
        appWidgetView.setTextViewText(R.id.artist_name,artist.value)

        Log.i("didi", "${artist.value}")

        linkButtons(service, appWidgetView)

        pushUpdate(service.applicationContext,appWidgetIds,appWidgetView)



    }

    fun updateWidget(){
        MusicWidget.Companion.instance
    }

    private fun linkButtons(context: Context, views: RemoteViews) {
        var pendingIntent: PendingIntent
        val serviceName = ComponentName(context, MusicService::class.java)

        pendingIntent = buildPendingIntent(context,ACTION_TOGGLE_PAUSE, serviceName)
        views.setOnClickPendingIntent(R.id.play_pause_widget, pendingIntent)

        pendingIntent = buildPendingIntent(context, ACTION_NEXT, serviceName)
        views.setOnClickPendingIntent(R.id.next_widget, pendingIntent)

        pendingIntent = buildPendingIntent(context, ACTION_PREV, serviceName)
        views.setOnClickPendingIntent(R.id.prev_widget, pendingIntent)
        views.setTextViewText(R.id.music_name,title.value)
        views.setTextViewText(R.id.artist_name,artist.value)
        Log.i("linkBTN", "linkBTN")
          updateWidget()
    /*    views.setString(R.id.music_name, ACTION_NEXT, title.toString())
        views.setString(R.id.artist_name, ACTION_NEXT, artist.toString())*/
    }

    companion object {

        const val NAME: String = "app_widget_small"

        private var mInstance: MusicWidget? = null
        private var imageSize = 0
        private var cardRadius = 0f

        val instance: MusicWidget
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = MusicWidget()
                }
                return mInstance!!
            }
    }

/*    override fun updateMusicName(title: String, artist: String, duration: String) {
        TODO("Not yet implemented")
    }

    override fun del2() {
        TODO("Not yet implemented")
    }*/

    override fun addListener() {
        TODO("Not yet implemented")
    }

    override fun removeListener() {
        TODO("Not yet implemented")
    }

    override fun onCheckPosition(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onUpdateSeekBar(duration: Int) {
        TODO("Not yet implemented")
    }

    override fun selectBtMode() {
        TODO("Not yet implemented")
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
     Log.i("serWStart", "onServiceConnected: po")
        this.service2 = (service as MusicService.MyBinder).getService()
        this.service2?.setViewModel(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    service2 = null
    }



}