package ru.kamaz.widget.view_model

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.services.MusicServiceInterface

class WidgetViewModel: MusicServiceInterface.ViewModel, ServiceConnection
     {


    private var service: MusicServiceInterface.Service? = null
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

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.service = (service as MusicService.MyBinder).getService()
        this.service?.setViewModel(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }


}