package ru.kamaz.music.view_models

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kamaz.music_api.interactor.LoadData
import ru.kamaz.music_api.models.Track
import ru.sir.core.None

import ru.sir.presentation.base.BaseViewModel
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseDataModel
import ru.sir.presentation.extensions.launchOn

import javax.inject.Inject

class TrackViewModel @Inject constructor (
    application: Application,
    private val loadData: LoadData
  //  private val mediaManager: MediaManager
) : BaseViewModel(application){

    companion object {
        private const val RV_ITEM = 2
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _items = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var items = _items.asStateFlow()

    override fun init() {
        _isLoading.value = true
        loadData(None()).launchOn(viewModelScope) { it.either({}, ::onDataLoaded) }
    }

    private fun onDataLoaded(data: List<Track>) {
        _items.value= data.toRecyclerViewItems()
        _isLoading.value = false
    }


    private fun List<Track>.toRecyclerViewItems(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach { newList.add(RecyclerViewBaseDataModel(it, RV_ITEM)) }
        return newList
    }
}