package ru.kamaz.widget.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.ViewModelProvider
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.services.MusicService.Companion.ACTION_NEXT
import ru.kamaz.music.services.MusicService.Companion.ACTION_PREV
import ru.kamaz.music.services.MusicService.Companion.ACTION_TOGGLE_PAUSE
import ru.kamaz.music.services.MusicServiceInterface
import ru.kamaz.widget.R
import ru.kamaz.widget.ui.base.BaseAppWidget
import ru.kamaz.widget.view_model.WidgetViewModel


/**
 * Implementation of App Widget functionality.
 */
class MusicWidget :BaseAppWidget(), MusicServiceInterface.ViewModel, ServiceConnection {
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var service: MusicServiceInterface.Service? = null




    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.i("widget", "onStartCommand: resiverstart")
        if (intent != null) {
            Log.i("widget", "onStartCommand: nenull")
            when (intent.action) {

                ACTION_TOGGLE_PAUSE -> {
                    Log.i("widget", "onStartCommand: play")
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
    }

    override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(service.packageName, R.layout.music_widget)
        linkButtons(service, appWidgetView)

    }


    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.music_widget)
        appWidgetManager.updateAppWidget(appWidgetId, views)
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

    override fun updateMusicName(title: String, artist: String, duration: String) {
        TODO("Not yet implemented")
    }

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

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.i("serWStart", "onServiceConnected: po")
        this.service = (service as MusicService.MyBinder).getService()
        this.service?.setViewModel(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }



}