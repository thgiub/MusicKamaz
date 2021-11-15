package ru.kamaz.music.view_models.music_category

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kamaz.music.databinding.PlayListAddNewItemBinding
import ru.kamaz.music.databinding.PlaylistItemBinding
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music_api.models.FavoriteSongs
import ru.kamaz.music_api.models.PlayListModel
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseItem
import ru.sir.presentation.extensions.launchWhenStarted


class ItemPlayListViewModel : RecyclerViewBaseItem<PlayListModel, PlaylistItemBinding>(){
    private val id = MutableStateFlow(0)
    private val dataMusic = MutableStateFlow("")
    private lateinit var data: PlayListModel

    override fun initVars() {
        id.launchWhenStarted(parent.lifecycleScope){
            binding.playListTrackNumber.text= it.toString()
        }

        binding.root.setOnClickListener {

        }
    }
    override fun bindData(data: PlayListModel) {
        this.data = data
        id.value = data.id.toInt()
        dataMusic.value = data.title

    }
}