package ru.kamaz.music.ui.one_song_fragment

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.core.view.marginStart
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import ru.kamaz.music.R
import ru.kamaz.music.databinding.FragmentPlayerBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.domain.GlobalConstants
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.ui.NavAction.OPEN_ADD_PLAY_LIST_DIALOG
import ru.kamaz.music.ui.NavAction.OPEN_DIALOG_BT_FRAGMENT
import ru.kamaz.music.ui.NavAction.OPEN_TRACK_LIST_FRAGMENT
import ru.kamaz.music.ui.enums.PlayListFlow
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

    override fun onDestroy() {
        viewModel.isSaveLastMusic()
        super.onDestroy()
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        FragmentPlayerBinding.inflate(inflater, container, false)

    override fun onResume() {
        viewModel.startTrack()
        super.onResume()
    }

    override fun setListeners() {
        binding.next.setOnClickListener {
            viewModel.nextTrack()
        }

        binding.controlPanel.playPause
            .setOnClickListener {
                viewModel.playOrPause()
            }
        binding.controlPanel.rotate.setOnClickListener {
            viewModel.shuffleStatusChange()
        }
        binding.prev.setOnClickListener {
            viewModel.previousTrack()
        }
        binding.openListFragment.setOnClickListener {
            navigator.navigateTo(
                UiAction(
                    OPEN_TRACK_LIST_FRAGMENT,
                    bundleOf(GlobalConstants.MAIN to PlayListFlow.MAIN_WINDOW)
                )
            )
        }
        binding.folder.setOnClickListener {
            changeSourceViewButtons()
        }
        binding.sourceSelection.btnBt.setOnClickListener {
            viewModel.vmSourceSelection(MusicService.SourceEnum.BT)
        }
        binding.sourceSelection.disk.setOnClickListener {
            viewModel.vmSourceSelection(MusicService.SourceEnum.DISK)
        }
        binding.sourceSelection.aux.setOnClickListener {
            auxModeActivation()
        }

        binding.sourceSelection.usb.setOnClickListener {
            viewModel.vmSourceSelection(MusicService.SourceEnum.USB)
        }
        binding.controlPanel.like.setOnClickListener {
            viewModel.isSaveFavoriteMusic()
        }
        binding.controlPanel.repeat.setOnClickListener {
            viewModel.repeatChange()
        }
        binding.controlPanel.addToFolder.setOnClickListener {
            navigator.navigateTo(
                UiAction(
                    OPEN_TRACK_LIST_FRAGMENT,
                    bundleOf(GlobalConstants.MAIN to PlayListFlow.SECOND_WINDOW)
                )
            )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            228 -> if (resultCode == Activity.RESULT_OK) {
                data?.data?.let {
                    Log.i("onActivityResult", "onActivityResult: $it")
                }
            }
            else -> if (resultCode == Activity.RESULT_OK) {
                println("OK")
            }
        }
    }

    fun changeSourceViewButtons() {
        changeSource(binding.controlPanel.viewPlayPause)
        changeSource(binding.sourceSelection.viewChangeSource)
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
    }

    private fun initServiceVars() {
        viewModel.isPlaying.launchWhenStarted(lifecycleScope) { isPlaying ->
            if (isPlaying) binding.controlPanel.playPause.setImageResource(R.drawable.ic_pause_white)
            else binding.controlPanel.playPause.setImageResource(R.drawable.ic_play_center)

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

        viewModel.repeatHowModeNow.launchWhenStarted(lifecycleScope) {
            repeatIconChange(it)
        }

        viewModel.musicPosition.launchWhenStarted(lifecycleScope) {

            val currentPosition = if (it < 0) 0 else it
            binding.seek.progress = currentPosition
            binding.startTime.text = Track.convertDuration(currentPosition.toLong())
        }

        viewModel.test.launchWhenStarted(lifecycleScope) {
            if (it) {
                btModeActivation()

            } else {
                viewModel.vmSourceSelection(MusicService.SourceEnum.BT)
                btModeActivation()

            }

        }

        viewModel.isNotConnected.launchWhenStarted(lifecycleScope) {
            if (it) {
                diskModeActivation()
            } else {
                viewModel.vmSourceSelection(MusicService.SourceEnum.BT)
                btModeActivation()
            }
        }

        viewModel.isShuffleOn.launchWhenStarted(lifecycleScope) {
            randomSongStatus(it)
        }

        viewModel.isNotConnectedUsb.launchWhenStarted(lifecycleScope) {
            if (it) {
                usbModeActivation()
            } else {
                diskModeActivation()
            }
        }

        viewModel.isFavoriteMusic.launchWhenStarted(lifecycleScope) {
            likeStatus(it)
        }

        viewModel.isBtModeOn.launchWhenStarted(lifecycleScope) {
            if (it) btModeActivation()
        }
        viewModel.isDiskModeOn.launchWhenStarted(lifecycleScope) {
            if (it) diskModeActivation()
        }
        viewModel.isUsbModeOn.launchWhenStarted(lifecycleScope) {
            if (it) usbModeActivation()
        }

        viewModel.isDeviceNotConnectFromBt.launchWhenStarted(lifecycleScope) {
            if (it) dialog()
        }
    }

    private fun updateTrackCover(coverPath: String) {

        if (coverPath.isEmpty()) {
        } else {
            Picasso.with(context)
                .load(Uri.fromFile(File(coverPath)))
                .into(binding.pictureDevice)
        }
    }


    private fun dialog() {
        navigator.navigateTo(
            UiAction(
               OPEN_DIALOG_BT_FRAGMENT
            )
        )
    }


    private fun repeatIconChange(repeat: Int) {
        when (repeat) {
            0 -> binding.controlPanel.repeat.setImageResource(R.drawable.ic_refresh_white)
            1 -> binding.controlPanel.repeat.setImageResource(R.drawable.ic_refresh_blue_one)
            2 -> binding.controlPanel.repeat.setImageResource(R.drawable.ic_refresh_blue_all)
        }

    }

    private fun randomSongStatus(random: Boolean) {
        if (random) binding.controlPanel.rotate.setImageResource(R.drawable.ic_shuffle_blue)
        else binding.controlPanel.rotate.setImageResource(R.drawable.ic_shuffle_white)
    }

    private fun likeStatus(like: Boolean) {
        if (like) {
            binding.controlPanel.like.setImageResource(R.drawable.ic_like_true)
        } else {
            binding.controlPanel.like.setImageResource(R.drawable.ic_like_false)
        }
    }


    fun btModeActivation() {
        binding.controlPanel.viewPlayPause.visibility = View.VISIBLE
        binding.sourceSelection.viewChangeSource.visibility = View.INVISIBLE
        binding.controlPanel.repeat.visibility = View.INVISIBLE
        binding.controlPanel.rotate.visibility = View.INVISIBLE
        binding.controlPanel.like.visibility = View.INVISIBLE
        binding.controlPanel.addToFolder.visibility = View.INVISIBLE
        binding.controlPanel.playPause.visibility = View.VISIBLE
        binding.openListFragment.visibility = View.INVISIBLE
        binding.seek.visibility = View.INVISIBLE
        binding.nextPrev.visibility = View.VISIBLE
        binding.artist.visibility = View.VISIBLE
        binding.song.visibility = View.VISIBLE
        binding.times.visibility = View.INVISIBLE
        binding.picture.visibility = View.VISIBLE
        binding.pictureDevice.visibility = View.INVISIBLE
        binding.sourceSelection.disk.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.aux.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.btnBt.setBackgroundResource(R.drawable.back_item_on)
        binding.sourceSelection.usb.setBackgroundResource(R.drawable.back_item)
        binding.pictureDevice.setBackgroundResource(R.drawable.bluetooth_back)
        binding.textUsb.visibility = View.INVISIBLE

    }

    fun diskModeActivation() {
        binding.controlPanel.viewPlayPause.visibility = View.VISIBLE
        binding.sourceSelection.viewChangeSource.visibility = View.INVISIBLE
        binding.controlPanel.addToFolder.visibility = View.VISIBLE
        binding.controlPanel.repeat.visibility = View.VISIBLE
        binding.controlPanel.rotate.visibility = View.VISIBLE
        binding.controlPanel.like.visibility = View.VISIBLE
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
        binding.sourceSelection.usb.setBackgroundResource(R.drawable.back_item)
        binding.picture.visibility = View.INVISIBLE
        binding.pictureDevice.visibility = View.VISIBLE
        binding.pictureDevice.setBackgroundResource(R.drawable.music_png_bg)
        binding.textUsb.visibility = View.INVISIBLE
    }

    fun auxModeActivation() {
        binding.controlPanel.viewPlayPause.visibility = View.VISIBLE
        binding.sourceSelection.viewChangeSource.visibility = View.INVISIBLE
        binding.controlPanel.repeat.visibility = View.INVISIBLE
        binding.controlPanel.rotate.visibility = View.INVISIBLE
        binding.controlPanel.like.visibility = View.INVISIBLE
        binding.controlPanel.playPause.visibility = View.INVISIBLE
        binding.openListFragment.visibility = View.INVISIBLE
        binding.controlPanel.addToFolder.visibility = View.INVISIBLE
        binding.seek.visibility = View.INVISIBLE
        binding.nextPrev.visibility = View.INVISIBLE
        binding.artist.visibility = View.INVISIBLE
        binding.song.visibility = View.INVISIBLE
        binding.times.visibility = View.INVISIBLE
        binding.picture.visibility = View.VISIBLE
        binding.pictureDevice.visibility = View.INVISIBLE
        binding.sourceSelection.disk.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.aux.setBackgroundResource(R.drawable.back_item_on)
        binding.sourceSelection.btnBt.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.usb.setBackgroundResource(R.drawable.back_item)
        binding.picture.setBackgroundResource(R.drawable.auxx)
        binding.textUsb.visibility = View.INVISIBLE

    }

    fun usbModeActivation() {
        binding.controlPanel.viewPlayPause.visibility = View.VISIBLE
        binding.sourceSelection.viewChangeSource.visibility = View.INVISIBLE
        binding.controlPanel.addToFolder.visibility = View.VISIBLE
        binding.controlPanel.repeat.visibility = View.VISIBLE
        binding.controlPanel.rotate.visibility = View.VISIBLE
        binding.controlPanel.like.visibility = View.VISIBLE
        binding.controlPanel.playPause.visibility = View.VISIBLE
        binding.openListFragment.visibility = View.VISIBLE
        binding.controlPanel.addToFolder.visibility = View.VISIBLE
        binding.seek.visibility = View.VISIBLE
        binding.nextPrev.visibility = View.VISIBLE
        binding.artist.visibility = View.VISIBLE
        binding.song.visibility = View.VISIBLE
        binding.times.visibility = View.VISIBLE
        binding.textUsb.visibility = View.VISIBLE
        binding.pictureDevice.visibility = View.VISIBLE
        binding.picture.visibility = View.INVISIBLE
        binding.sourceSelection.disk.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.usb.setBackgroundResource(R.drawable.back_item_on)
        binding.sourceSelection.aux.setBackgroundResource(R.drawable.back_item)
        binding.sourceSelection.btnBt.setBackgroundResource(R.drawable.back_item)
        binding.pictureDevice.setBackgroundResource(R.drawable.music_png_bg)
        binding.sourceSelection
    }


}

