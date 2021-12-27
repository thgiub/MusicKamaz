package ru.kamaz.music.view_models

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.services.MusicServiceInterface
import ru.kamaz.music_api.interactor.LoadData
import ru.kamaz.music_api.models.Track
import ru.sir.core.None
import ru.sir.presentation.base.BaseViewModel
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseDataModel
import javax.inject.Inject

class TrackViewModel @Inject constructor(
    application: Application,
    private val loadData: LoadData
) : BaseViewModel(application), ServiceConnection {

    companion object {
        private const val RV_ITEM = 2
    }

    private var service: MusicServiceInterface.Service? = null


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _items = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var items = _items.asStateFlow()

    private val _trackIsEmpty = MutableStateFlow(false)
    val trackIsEmpty = _trackIsEmpty.asStateFlow()


    override fun init() {
        _isLoading.value = true
        loadData(None()) { it.either({  }, ::onDataLoaded) }

        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    private fun onDataLoaded(data: List<Track>) {
        if (data.isEmpty()) {
            Log.d("mediaPlayer", "no")
            _trackIsEmpty.value = true
           // toast()
        } else _trackIsEmpty.value = false
        _items.value = data.toRecyclerViewItems()
        _isLoading.value = false

    }

   /* fun toast() {
        Toast.makeText(context, "Не найдено аудиофайлов на устройсве", Toast.LENGTH_LONG).show()
    }*/

    fun onItemClick(track: Track, data: String) {
        service?.intMediaPlayer()
        service?.initTrack(track, data)
        service?.playOrPause()
        Log.i("onTrackClicked", "onTrackClicked")
    }

    private fun List<Track>.toRecyclerViewItems(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach { newList.add(RecyclerViewBaseDataModel(it, RV_ITEM)) }
        return newList
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("serWStart", "onServiceConnected: LIST-VM")
        this.service = (service as MusicService.MyBinder).getService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }
}