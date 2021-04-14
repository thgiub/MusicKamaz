package ru.kamaz.music.services

import android.content.Context
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music_api.models.Track


interface MusicServiceInterface{
    fun startTrack(context: Context)
    fun playOrPause()
    fun testPlay(track: Track)
    fun getMusicImg(albumID: Long)
    fun pause()
    fun resume()
    fun isPlaying(): Boolean
    fun checkPosition(position: Int)
    fun updateSeekBar()
    fun previousTrack(context: Context)
    fun nextTrack(context: Context)
   /* fun updateMusic(track: Track)*/
    fun updateTracks(mediaManager: MediaManager)
    fun intMediaPlayer()
}