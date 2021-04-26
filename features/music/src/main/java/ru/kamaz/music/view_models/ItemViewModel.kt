package ru.kamaz.music.view_models


import android.net.Uri
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.kamaz.music.R
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music.ui.TrackFragment
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseItem
import ru.sir.presentation.extensions.launchWhenStarted
import java.io.File
import kotlin.math.atan

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
            (parent as TrackFragment).onTrackClicked(data)
        }
    }


}