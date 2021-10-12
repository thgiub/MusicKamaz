package ru.kamaz.music.view_models

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music_api.models.AllFolderWithMusic
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseItem
import ru.sir.presentation.extensions.launchWhenStarted



class FolderItemViewModel: RecyclerViewBaseItem<AllFolderWithMusic, TestTextItemBinding>(){
    private val artist = MutableStateFlow(0)
    private val title = MutableStateFlow(0)
    private lateinit var data: AllFolderWithMusic

    /*   override fun bindData(data: Track) {
           this.data = data
           artist.value= data.artist
           title.value= data.title
       }*/

    override fun initVars() {
        artist.launchWhenStarted(parent.lifecycleScope){
            binding.artistName.text= it.toString()
        }
        title.launchWhenStarted(parent.lifecycleScope){
            binding.musicName.text= it.toString()
        }

        binding.root.setOnClickListener {
            // (parent as MainListMusicFragment).onTrackClicked(data)
        }
    }

    override fun bindData(data: AllFolderWithMusic) {
        this.data = data
        artist.value= data.name
        title.value= data.type
    }


}