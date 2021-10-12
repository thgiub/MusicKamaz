package ru.kamaz.music.view_models.music_category

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music.ui.MainListMusicFragment
import ru.kamaz.music_api.models.FavoriteSongs
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseItem
import ru.sir.presentation.extensions.launchWhenStarted

class ItemFavoriteViewModel : RecyclerViewBaseItem<FavoriteSongs, TestTextItemBinding>(){
    private val id = MutableStateFlow(0)
    private val dataMusic = MutableStateFlow("")
    private lateinit var data: FavoriteSongs

    override fun initVars() {
        id.launchWhenStarted(parent.lifecycleScope){
            binding.artistName.text= it.toString()
        }
        dataMusic.launchWhenStarted(parent.lifecycleScope){
            binding.musicName.text=it
        }

        binding.root.setOnClickListener {

        }
    }

    override fun bindData(data: FavoriteSongs) {
        this.data = data
        id.value = data.idSong
        dataMusic.value = data.data

    }
}