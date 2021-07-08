package ru.kamaz.music.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import ru.kamaz.music.R
import ru.kamaz.music.databinding.FragmentPlayerBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.ui.NavAction.OPEN_TEST_FRAGMENT

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

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        FragmentPlayerBinding.inflate(inflater, container, false)

    override fun setListeners() {
        binding.next.setOnClickListener {
            viewModel.nextTrack()
        }

        binding.controlPanel.playPause
            .setOnClickListener {
                viewModel.playOrPause()
            }
        binding.controlPanel.rotate.setOnClickListener {
            viewModel.startTrack()
        }
        binding.prev.setOnClickListener {
            viewModel.previousTrack()
        }
        binding.openListFragment.setOnClickListener {
            navigator.navigateTo(UiAction(OPEN_TEST_FRAGMENT))
        }
        binding.folder.setOnClickListener {
            changeSource(binding.controlPanel.pop)
            changeSource(binding.sourceSelection.pap)
        }
        binding.sourceSelection.btnBt.setOnClickListener {
            Log.i("Test", "musicFragment")
            viewModel.vmSourceSelection(MusicService.SourceEnum.BT)
            btModeActivation()
        }
        binding.sourceSelection.disk.setOnClickListener {
            viewModel.vmSourceSelection(MusicService.SourceEnum.DISK)
            diskModeActivation()
        }
        binding.sourceSelection.aux.setOnClickListener {
            auxModeActivation()
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

    fun changeSource(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    override fun initVars() {
        viewModel.service.launchWhenStarted(lifecycleScope) {
            if (it == null) return@launchWhenStarted
            initServiceVars()
        }

        viewModel.firstOpenTrackFound()
    }

    private fun initServiceVars() {
        viewModel.isPlaying.launchWhenStarted(lifecycleScope) { isPlaying ->
            if (isPlaying) binding.controlPanel.playPause.setImageResource(R.drawable.ic_pause_music)
            else binding.controlPanel.playPause.setImageResource(R.drawable.ic_play)
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

        viewModel.btModeActivation.launchWhenStarted(lifecycleScope) {
            Log.i("fragmentTest", "$it")
            btModeActivation()
        }

        viewModel.isNotConnected.launchWhenStarted(lifecycleScope) {
            if (it) {
                Log.i("bt_frag_isNotConnected", "bt on")
                diskModeActivation()
            } else {
                Log.i("bt_frag_isNotConnected", "bt off")
                viewModel.vmSourceSelection(MusicService.SourceEnum.BT)
                btModeActivation()
            }
        }

        viewModel.isNotConnectedUsb.launchWhenStarted(lifecycleScope){
            if (it) {
                usbModeActivation()
            } else {
                diskModeActivation()
            }
        }
    }

    private fun updateTrackCover(coverPath: String) {
        Log.i("diaz", "IMG + $coverPath ")

        if (coverPath.isEmpty()) {
            /* Picasso.with(context)
                 .load(R.drawable.png)
                 .into(binding.picture)*/
        } else {
            Picasso.with(context)
                .load(Uri.fromFile(File(coverPath)))
                .into(binding.picture)
        }
    }

    fun btModeActivation() {
        changeSource(binding.controlPanel.pop)
        changeSource(binding.sourceSelection.pap)
        binding.controlPanel.repeat.visibility = View.INVISIBLE
        binding.controlPanel.rotate.visibility = View.INVISIBLE
        binding.controlPanel.star.visibility = View.INVISIBLE
        binding.controlPanel.addToFolder.visibility = View.INVISIBLE
        binding.controlPanel.playPause.visibility = View.VISIBLE
        binding.openListFragment.visibility = View.INVISIBLE
        binding.seek.visibility = View.INVISIBLE
        binding.nextPrev.visibility = View.VISIBLE
        binding.artist.visibility = View.VISIBLE
        binding.song.visibility = View.VISIBLE
        binding.times.visibility = View.INVISIBLE
        binding.sourceSelection.disk.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.aux.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.btnBt.setBackgroundResource(R.drawable.back_item_on)
        binding.picture.setBackgroundResource(R.drawable.bluetooth_back)
        binding.textUsb.visibility= View.INVISIBLE

    }

    fun diskModeActivation() {
        changeSource(binding.controlPanel.pop)
        changeSource(binding.sourceSelection.pap)
        binding.controlPanel.addToFolder.visibility = View.VISIBLE
        binding.controlPanel.repeat.visibility = View.VISIBLE
        binding.controlPanel.rotate.visibility = View.VISIBLE
        binding.controlPanel.star.visibility = View.VISIBLE
        binding.controlPanel.playPause.visibility = View.VISIBLE
        binding.openListFragment.visibility = View.VISIBLE
        binding.controlPanel.addToFolder.visibility = View.VISIBLE
        binding.seek.visibility = View.VISIBLE
        binding.nextPrev.visibility = View.VISIBLE
        binding.artist.visibility = View.VISIBLE
        binding.song.visibility = View.VISIBLE
        binding.times.visibility = View.VISIBLE
        binding.sourceSelection.disk.setBackgroundResource(R.drawable.back_item_on)
        binding.sourceSelection.aux.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.btnBt.setBackgroundResource(R.drawable.back_item)
        binding.picture.setBackgroundResource(R.drawable.albom)
        binding.textUsb.visibility= View.INVISIBLE
        //   viewModel.vmSourceSelection(MusicService.SourceEnum.DISK)
    }

    fun auxModeActivation() {
        changeSource(binding.controlPanel.pop)
        changeSource(binding.sourceSelection.pap)
        binding.controlPanel.repeat.visibility = View.INVISIBLE
        binding.controlPanel.rotate.visibility = View.INVISIBLE
        binding.controlPanel.star.visibility = View.INVISIBLE
        binding.controlPanel.playPause.visibility = View.INVISIBLE
        binding.openListFragment.visibility = View.INVISIBLE
        binding.controlPanel.addToFolder.visibility = View.INVISIBLE
        binding.seek.visibility = View.INVISIBLE
        binding.nextPrev.visibility = View.INVISIBLE
        binding.artist.visibility = View.INVISIBLE
        binding.song.visibility = View.INVISIBLE
        binding.times.visibility = View.INVISIBLE
        binding.sourceSelection.disk.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.aux.setBackgroundResource(R.drawable.back_item_on)
        binding.sourceSelection.btnBt.setBackgroundResource(R.drawable.back_item)
        binding.picture.setBackgroundResource(R.drawable.auxx)
        binding.textUsb.visibility= View.INVISIBLE

    }

    fun usbModeActivation() {
        changeSource(binding.controlPanel.pop)
        changeSource(binding.sourceSelection.pap)
        binding.controlPanel.addToFolder.visibility = View.VISIBLE
        binding.controlPanel.repeat.visibility = View.VISIBLE
        binding.controlPanel.rotate.visibility = View.VISIBLE
        binding.controlPanel.star.visibility = View.VISIBLE
        binding.controlPanel.playPause.visibility = View.VISIBLE
        binding.openListFragment.visibility = View.VISIBLE
        binding.controlPanel.addToFolder.visibility = View.VISIBLE
        binding.seek.visibility = View.VISIBLE
        binding.nextPrev.visibility = View.VISIBLE
        binding.artist.visibility = View.VISIBLE
        binding.song.visibility = View.VISIBLE
        binding.times.visibility = View.VISIBLE
        binding.textUsb.visibility= View.VISIBLE
        binding.sourceSelection.disk.setBackgroundResource(R.drawable.back_item_on)
        binding.sourceSelection.aux.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.btnBt.setBackgroundResource(R.drawable.back_item)
        binding.picture.setBackgroundResource(R.drawable.albom)
        //   viewModel.vmSourceSelection(MusicService.SourceEnum.DISK)
    }


}

