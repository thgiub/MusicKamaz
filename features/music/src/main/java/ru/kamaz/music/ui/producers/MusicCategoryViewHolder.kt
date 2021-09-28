package ru.kamaz.music.ui.producers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kamaz.music.databinding.ImageTextItemBinding
import ru.kamaz.music.ui.producers.ItemType.RV_ITEM_MUSIC_CATEGORY
import ru.kamaz.music.view_models.MusicCategoryViewModel
import ru.kamaz.music_api.models.CategoryMusicModel
import ru.sir.presentation.base.recycler_view.ViewHolderProducer

class MusicCategoryViewHolder: ViewHolderProducer<CategoryMusicModel, MusicCategoryViewModel, ImageTextItemBinding >(
   RV_ITEM_MUSIC_CATEGORY, CategoryMusicModel::class.java, MusicCategoryViewModel::class.java
) {
    override fun initBinding(inflater: LayoutInflater, parent: ViewGroup) =
        ImageTextItemBinding.inflate(inflater, parent, false)

}