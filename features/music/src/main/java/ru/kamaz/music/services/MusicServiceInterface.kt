package ru.kamaz.music.services

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music_api.models.Track


interface MusicServiceInterface{
    interface Service{
        fun setViewModel(viewModel:ViewModel)
        fun init()
        fun startTrack(context: Context)
        fun playOrPause(): Boolean
        fun testPlay(track: Track)
        fun getMusicImg(albumID: Long)
        fun pause()
        fun resume()
        fun isPlaying(): Boolean
        fun checkPosition(position: Int)
        fun previousTrack(context: Context)
        fun nextTrack(context: Context)
        /* fun updateMusic(track: Track)*/
        fun updateTracks(mediaManager: MediaManager)
        fun intMediaPlayer()
    }

    interface ViewModel{
        fun updateMusicName(title: String,artist:String,duration: String)
        fun addListener()
        fun removeListener()
        fun onCheckPosition(position: Int)
        fun onUpdateSeekBar(duration:Int)
    }

}