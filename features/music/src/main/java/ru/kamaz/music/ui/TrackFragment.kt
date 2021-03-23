package ru.kamaz.music.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.kamaz.itis.phoneapp.ui.pojo.RecyclerViewItem
import ru.kamaz.music.databinding.FragmentListMusicBinding
import ru.kamaz.music.date.media.MediaManager
import ru.kamaz.music.date.media.model.Track
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.adapters.AllMusicRvAdapter
import ru.kamaz.music.view_models.TrackViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment

class TrackFragment :
    BaseFragment<TrackViewModel, FragmentListMusicBinding>(TrackViewModel::class.java ) {
    lateinit var mediaManager:MediaManager
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)

    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentListMusicBinding =
        FragmentListMusicBinding.inflate(inflater, container, false).apply {
            viewModel = this@TrackFragment.viewModel
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listItem= mutableListOf<RecyclerViewItem>()
        listItem.addAll(getTrack())
        binding.rvAllMusic.apply {
            adapter= AllMusicRvAdapter(listItem)
        }
    }


    fun getTrack():MutableList<RecyclerViewItem>{

        val list = mutableListOf<RecyclerViewItem>()

        return list
    }


}

