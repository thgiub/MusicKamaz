package ru.kamaz.music.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import ru.kamaz.music.R
import ru.kamaz.music.services.MusicService
import ru.kamaz.music_api.BaseConstants
import ru.kamaz.widget.ui.MusicWidget
import ru.kamaz.widget.ui.base.BaseAppWidget

class TestWidget : BaseAppWidget() {
    private var pendingIntent: PendingIntent? = null
    override fun performUpdate(context: Context, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.test_widget)

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, MusicService::class.java)

        if (pendingIntent == null) {
            pendingIntent =
                PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT)
        }
        manager.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime(),
            60000,
            pendingIntent
        )

        linkButtons(context, appWidgetView)
        pushUpdate(context, appWidgetIds, appWidgetView)
    }

    fun updateTestArtist(context: Context, artist: String) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.test_widget)

        appWidgetView.setTextViewText(ru.kamaz.widget.R.id.music_name, artist)

        pushUpdate(context, null, appWidgetView)
    }
    fun updateTestTitle(context: Context, title: String) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.test_widget)

        appWidgetView.setTextViewText(ru.kamaz.widget.R.id.artist_name, title)

        pushUpdate(context, null, appWidgetView)
    }

    fun updateTestDuration(context: Context, title: String) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.test_widget)

        appWidgetView.setTextViewText(ru.kamaz.widget.R.id.duration_widget, title)

        pushUpdate(context, null, appWidgetView)
    }

    private fun linkButtons(context: Context, views: RemoteViews) {
        var pendingIntent: PendingIntent
        val serviceName = ComponentName(context, "ru.kamaz.music.services.MusicService")

        pendingIntent = buildPendingIntent(context, BaseConstants.ACTION_TOGGLE_PAUSE, serviceName)
        views.setOnClickPendingIntent(ru.kamaz.widget.R.id.play_pause_widget, pendingIntent)

        pendingIntent = buildPendingIntent(context, BaseConstants.ACTION_NEXT, serviceName)
        views.setOnClickPendingIntent(ru.kamaz.widget.R.id.next_widget, pendingIntent)

        pendingIntent = buildPendingIntent(context, BaseConstants.ACTION_PREV, serviceName)
        views.setOnClickPendingIntent(ru.kamaz.widget.R.id.prev_widget, pendingIntent)

        Log.i("linkBTN", "linkBTN")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }
    companion object {
        private var mInstance: TestWidget? = null
        val appWidgetViewId =R.layout.test_widget

        val instance: TestWidget
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = TestWidget()
                }
                return mInstance!!
            }
    }
}