package ru.kamaz.music.view_models


import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseItem
import ru.sir.presentation.extensions.launchWhenStarted
import kotlin.math.atan

class ItemViewModel: RecyclerViewBaseItem<Track, TestTextItemBinding>(){
    private val artist = MutableStateFlow("")
    private  val title =MutableStateFlow("")

    override fun bindData(data:Track) {
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

    }
}