package ru.kamaz.music.view_models.music_category

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kamaz.music.view_models.TrackViewModel
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.BaseViewModel
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseDataModel
import javax.inject.Inject

class MusicCategoryViewModel @Inject constructor(
    application: Application)
    : BaseViewModel(application) {
/*    companion object {
        private const val RV_ITEM = 2
    }
    private val _items = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var items = _items.asStateFlow()

    private fun List<Track>.toRecyclerViewItems(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach { newList.add(RecyclerViewBaseDataModel(it, MusicCategoryViewModel.RV_ITEM)) }
        return newList
    }*/
}