package ru.kamaz.music.view_models.music_category

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kamaz.music.databinding.PlayListAddNewItemBinding
import ru.kamaz.music.databinding.PlaylistItemBinding
import ru.kamaz.music.ui.category.CategoryFragment
import ru.kamaz.music_api.models.AddNewPlayListModel
import ru.kamaz.music_api.models.PlayListModel
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseItem
import ru.sir.presentation.extensions.launchWhenStarted

class ItemPlayListAddNewViewModel: RecyclerViewBaseItem<AddNewPlayListModel, PlayListAddNewItemBinding>(){
    private val addPlayList = MutableStateFlow("")
    private lateinit var data: AddNewPlayListModel

    override fun initVars() {
        addPlayList.launchWhenStarted(parent.lifecycleScope){
            binding.playListTrackNumber.text= it
        }

        binding.root.setOnClickListener {
            data?.let { (parent as CategoryFragment).clickAddNewPlayList() }
        }
    }

    override fun bindData(data: AddNewPlayListModel) {
        this.data = data
        addPlayList.value = data.text


    }
}