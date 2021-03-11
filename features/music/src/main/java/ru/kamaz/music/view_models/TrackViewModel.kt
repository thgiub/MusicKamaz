package ru.kamaz.music.view_models

import android.app.Application
import ru.kamaz.music.date.media.MediaManager
import ru.sir.presentation.base.BaseViewModel
import ru.sir.presentation.extensions.easyLog
import javax.inject.Inject

class TrackViewModel @Inject constructor (
    application: Application,
    private val mediaManager: MediaManager
) : BaseViewModel(application){

    override fun onCreate() {
        "OnCreate".easyLog(this)
        val tracks = mediaManager.scanTracks()

        var log = ""
        for (track in tracks)
            log += "$track \n"

        log.easyLog(this)
    }
}