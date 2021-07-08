package ru.kamaz.music.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import ru.biozzlab.twmanager.domain.interfaces.MusicManagerListener
import ru.biozzlab.twmanager.managers.MusicManager
import ru.biozzlab.twmanager.utils.easyLog
import ru.kamaz.music.R

class TestFragment : Fragment(), MusicManagerListener {
    private val twManager = MusicManager()

    private lateinit var usbStatusTV: AppCompatTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usbStatusTV = view.findViewById(R.id.usbStatus)
    }

    override fun onResume() {
        super.onResume()
        twManager.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        twManager.removeListener(this)
    }

    override fun onSdStatusChanged(path: String, isAdded: Boolean) {
        "MicroSD status changed: value = $path status = $isAdded".easyLog(this)
    }

    override fun onUsbStatusChanged(path: String, isAdded: Boolean) {
        "USB status changed: value = $path status = $isAdded".easyLog(this)
        usbStatusTV.text = "USB status: $isAdded ($path)"
    }
}