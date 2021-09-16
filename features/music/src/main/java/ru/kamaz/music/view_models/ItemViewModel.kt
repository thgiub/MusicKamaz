package ru.kamaz.music.view_models


import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music.ui.MainListMusicFragment
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseItem
import ru.sir.presentation.extensions.launchWhenStarted

class ItemViewModel: RecyclerViewBaseItem<Track, TestTextItemBinding>(){
    private val artist = MutableStateFlow("")
    private val title =MutableStateFlow("")
    private lateinit var data: Track

    override fun bindData(data: Track) {
        this.data = data
        artist.value= data.artist
        title.value= data.title
    }

    override fun initVars() {
       artist.launchWhenStarted(parent.lifecycleScope){
           binding.artistName.text=it
       }
       title.launchWhenStarted(parent.lifecycleScope){
           binding.musicName.text=it
       }

        binding.root.setOnClickListener {
            (parent as MainListMusicFragment).onTrackClicked(data)
        }
    }


}