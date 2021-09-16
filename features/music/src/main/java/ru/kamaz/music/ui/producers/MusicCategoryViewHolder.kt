package ru.kamaz.music.ui.producers

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kamaz.music.databinding.ImageTextItemBinding
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music.view_models.ItemViewModel
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.recycler_view.ViewHolderProducer

class MusicCategoryViewHolder: ViewHolderProducer<Track, ItemViewModel, TestTextItemBinding>(
    ItemType.RV_ITEM_MUSIC_CATEGORY, Track::class.java, ItemViewModel::class.java){
    override fun initBinding(inflater: LayoutInflater, parent: ViewGroup) =
        TestTextItemBinding.inflate(inflater,parent,false)

}