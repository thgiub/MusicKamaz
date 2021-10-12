package ru.kamaz.music.view_models.music_category

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kamaz.music.view_models.list.ListViewModel
import ru.kamaz.music_api.interactor.*
import ru.kamaz.music_api.models.AllFolderWithMusic
import ru.kamaz.music_api.models.CategoryMusicModel
import ru.kamaz.music_api.models.FavoriteSongs
import ru.kamaz.music_api.models.Track
import ru.sir.core.None
import ru.sir.presentation.base.BaseViewModel
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseDataModel
import ru.sir.presentation.extensions.launchOn
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    application: Application,
    private val categoryData: CategoryLoadRV,
    private val favoriteData: FavoriteMusicRV,
    private val rvAllFolderWithMusic: AllFolderWithMusicRV,
    private val artistList: ArtistLoadRV,

    ) : BaseViewModel(application) {

    companion object {
        private const val RV_ITEM = 2
        private const val RV_ITEM_MUSIC_CATEGORY = 3
        private const val RV_ITEM_MUSIC_FAVORITE = 4
        private const val RV_ITEM_MUSIC_FOLDER = 5
        private const val RV_ITEM_MUSIC_ARTIST = 6
    }

    init {
        categoryData(None()) { it.either({}, ::onCategoryLoaded) }
        favoriteData(None()).launchOn(viewModelScope){
            _favorite.value = it.toRecyclerViewItemsFavorite()
        }
        rvAllFolderWithMusic(None()) { it.either({}, ::onAllFolderWithMusic) }
        artistList(None()) { it.either({}, ::onArtistListRV) }
    }

    private val _items = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList ())
    var items = _items.asStateFlow()

    private val _huitems = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var huitems = _huitems.asStateFlow()

    private val _favorite = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var favorite = _favorite.asStateFlow()

    private val _artist = MutableStateFlow<List<RecyclerViewBaseDataModel>>(emptyList())
    var artist = _artist.asStateFlow()

    private fun List<CategoryMusicModel>.toRecyclerViewItemsCategory(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach {
            newList.add(
                RecyclerViewBaseDataModel(
                    it,
                    RV_ITEM_MUSIC_CATEGORY
                )
            )
        }
        return newList
    }

    private fun onAllFolderWithMusic(folderMusic: List<AllFolderWithMusic>){
        _items.value = folderMusic.toRecyclerViewItemsFolder()
    }

    private fun onArtistListRV(artistMusic: List<Track>){
        _artist.value = artistMusic.toRecyclerViewItemsArtist()
    }
    private fun List<AllFolderWithMusic>.toRecyclerViewItemsFolder(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach { newList.add(RecyclerViewBaseDataModel(it, RV_ITEM_MUSIC_FOLDER
        )) }
        return newList
    }


    private fun List<Track>.toRecyclerViewItemsArtist(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach { newList.add(RecyclerViewBaseDataModel(it, RV_ITEM_MUSIC_ARTIST
        )) }
        return newList
    }


    private fun List<FavoriteSongs>.toRecyclerViewItemsFavorite(): List<RecyclerViewBaseDataModel> {
        val newList = mutableListOf<RecyclerViewBaseDataModel>()
        this.forEach {
            newList.add(
                RecyclerViewBaseDataModel(
                    it,
                    RV_ITEM_MUSIC_FAVORITE
                )
            )
        }
        return newList
    }

    private fun onCategoryLoaded(category: List<CategoryMusicModel>){
        _huitems.value= category.toRecyclerViewItemsCategory()
    }

    private fun  onFavoriteLoaded(favorite: List<FavoriteSongs>){
        _favorite.value = favorite.toRecyclerViewItemsFavorite()
    }

}