package ru.kamaz.music.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kamaz.music.databinding.FragmentMusicBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.view_models.MusicFragmentViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment

class MusicFragment :
    BaseFragment<MusicFragmentViewModel, FragmentMusicBinding>(MusicFragmentViewModel::class.java) {

    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)=
        FragmentMusicBinding.inflate(inflater, container, false).apply {
            viewModel = this@MusicFragment.viewModel
        }

    override fun setListeners() {
        binding.icPlay.setOnClickListener {
            viewModel.playOrPause()
        }
        binding.rotate.setOnClickListener {
            viewModel.startTrack()
        }
        binding.nextButton.setOnClickListener {
            viewModel.nextTrack()
        }

        binding.prevButton.setOnClickListener {
            viewModel.previousTrack()
        }

        super.setListeners()
    }

}

