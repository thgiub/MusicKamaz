package ru.kamaz.music.ui.producers

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kamaz.music.databinding.PlaylistItemBinding
import ru.kamaz.music.ui.producers.ItemType.RV_ITEM_MUSIC_PLAYLIST
import ru.kamaz.music.view_models.music_category.ItemPlayListViewModel
import ru.kamaz.music_api.models.PlayListModel
import ru.sir.presentation.base.recycler_view.ViewHolderProducer


class MusicPlayListViewHolder : ViewHolderProducer<PlayListModel, ItemPlayListViewModel, PlaylistItemBinding>(
    RV_ITEM_MUSIC_PLAYLIST , PlayListModel::class.java, ItemPlayListViewModel::class.java
) {
    override fun initBinding(inflater: LayoutInflater, parent: ViewGroup)= PlaylistItemBinding.inflate(inflater, parent, false)
}