package ru.kamaz.music.view_models

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kamaz.music.R
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.services.MusicServiceInterface
import ru.kamaz.music_api.interactor.CategoryLoadRV
import ru.kamaz.music_api.interactor.FavoriteMusicRV
import ru.kamaz.music_api.interactor.LoadData
import ru.kamaz.music_api.interactor.QueryFavoriteMusic
import ru.kamaz.music_api.models.CategoryMusicModel
import ru.kamaz.music_api.models.FavoriteSongs
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None
import ru.sir.presentation.base.BaseViewModel
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseDataModel
import javax.inject.Inject

class MainListMusicViewModel @Inject constructor(
    application: Application,
    private val loadData: LoadData,
    private val categoryData: CategoryLoadRV,
    private val favoriteMusicData: FavoriteMusicRV

) : BaseViewModel(application), ServiceConnection {

    companion object {
        private const val RV_ITEM = 2
        private const val RV_ITEM_MUSIC_CATEGORY = 3
        private const val RV_ITEM_MUSIC_FAVORITE = 4
    }

    private var service: MusicServiceInterface.Service? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _items = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var items = _items.asStateFlow()

    private val _huitems = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var huitems = _huitems.asStateFlow()

    private val _favorite= MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var favorite = _favorite.asStateFlow()

    override fun init() {
        _isLoading.value = true
        loadData(None()) { it.either({}, ::onDataLoaded) }
        categoryData(None()) { it.either({}, ::onCategoryLoaded) }
        favoriteMusicData
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    private fun onDataLoaded(data: List<Track>) {
        _items.value = data.toRecyclerViewItems()
        _isLoading.value = false
    }
   private fun onCategoryLoaded(category: List<CategoryMusicModel>){
        _huitems.value= category.toRecyclerViewItemsCategory()
    }

    private fun onFavoriteLoaded(favorite: List<FavoriteSongs>){
        _favorite.value= favorite.toRecyclerViewItemsFavorite()
    }

    fun onItemClick(track: Track) {
        service?.intMediaPlayer()
        service?.testPlay(track)
    }

    private fun List<Track>.toRecyclerViewItems(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach { newList.add(RecyclerViewBaseDataModel(it, RV_ITEM)) }
        return newList
    }


    private fun List<CategoryMusicModel>.toRecyclerViewItemsCategory(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach { newList.add(RecyclerViewBaseDataModel(it, RV_ITEM_MUSIC_CATEGORY)) }
        return newList
    }


    private fun List<FavoriteSongs>.toRecyclerViewItemsFavorite(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach { newList.add(RecyclerViewBaseDataModel(it, RV_ITEM_MUSIC_FAVORITE )) }
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