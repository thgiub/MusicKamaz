package ru.kamaz.music.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import ru.kamaz.music.R
import ru.kamaz.music.databinding.FragmentPlayerBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.NavAction.OPEN_TRACK_LIST_FRAGMENT

import ru.kamaz.music.view_models.MusicFragmentViewModel
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment
import ru.sir.presentation.extensions.launchWhenStarted
import ru.sir.presentation.navigation.UiAction

class MusicFragment :
    BaseFragment<MusicFragmentViewModel, FragmentPlayerBinding>(MusicFragmentViewModel::class.java) {

    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)=
        FragmentPlayerBinding.inflate(inflater, container, false).apply {
            viewModel = this@MusicFragment.viewModel
        }

    override fun setListeners() {
        binding.next.setOnClickListener {
            viewModel.nextTrack()
        }
        binding.playPause.setOnClickListener {
            viewModel.playOrPause()
        }
        binding.rotate.setOnClickListener {
            viewModel.startTrack()
        }
        binding.prev.setOnClickListener {
            viewModel.previousTrack()
        }
        binding.openListFragment.setOnClickListener {
            navigator.navigateTo(UiAction(OPEN_TRACK_LIST_FRAGMENT))
        }
        super.setListeners()
    }

    override fun initVars() {
        viewModel.isPlaying.launchWhenStarted(lifecycleScope) { isPlaying ->
            if (isPlaying)   binding.playPause.setImageResource(R.drawable.ic_pause_circle)
            else     binding.playPause.setImageResource(R.drawable.ic_play_circle)
        }

    }





}

