package ru.kamaz.music.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
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
import java.io.File

class MusicFragment :
    BaseFragment<MusicFragmentViewModel, FragmentPlayerBinding>(MusicFragmentViewModel::class.java) {

    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentPlayerBinding.inflate(inflater, container, false)

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

        binding.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    viewModel.checkPosition(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        super.setListeners()
    }

    override fun initVars() {
        viewModel.isPlaying.launchWhenStarted(lifecycleScope) { isPlaying ->
            if (isPlaying)   binding.playPause.setImageResource(R.drawable.ic_pause_music)
            else     binding.playPause.setImageResource(R.drawable.ic_play)
        }

        viewModel.title.launchWhenStarted(lifecycleScope) {
            binding.song.text = it
        }

        viewModel.artist.launchWhenStarted(lifecycleScope) {
            binding.artist.text = it
        }

        viewModel.duration.launchWhenStarted(lifecycleScope) {
            binding.endTime.text = it
        }

        viewModel.cover.launchWhenStarted(lifecycleScope) { updateTrackCover(it) }

        viewModel.maxSeek.launchWhenStarted(lifecycleScope) {
            binding.seek.max = it
        }

        viewModel.musicPosition.launchWhenStarted(lifecycleScope) {
            val currentPosition = if (it < 0) 0 else it
            binding.seek.progress = currentPosition
            binding.startTime.text = Track.convertDuration(currentPosition.toLong())
        }
    }

    private fun updateTrackCover(coverPath: String) {
        Log.i("diaz", "IMG + $coverPath ")

        if (coverPath.isEmpty()) {
            Picasso.with(context)
                .load(R.drawable.default_img_music)
                .into(binding.picture)
        } else {
            Picasso.with(context)
                .load(Uri.fromFile(File(coverPath)))
                .into(binding.picture)
        }
    }
}

