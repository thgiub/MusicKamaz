package ru.kamaz.music.ui.producers

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music.ui.producers.ItemType.RV_ITEM_MUSIC_FAVORITE
import ru.kamaz.music.view_models.ItemViewModel
import ru.kamaz.music.view_models.music_category.ItemFavoriteViewModel
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.recycler_view.ViewHolderProducer

class MusicFavoriteViewHolder : ViewHolderProducer<Track, ItemFavoriteViewModel, TestTextItemBinding>(
   RV_ITEM_MUSIC_FAVORITE, Track::class.java, ItemFavoriteViewModel::class.java
) {
    override fun initBinding(inflater: LayoutInflater, parent: ViewGroup) =
        TestTextItemBinding.inflate(inflater, parent, false)

}