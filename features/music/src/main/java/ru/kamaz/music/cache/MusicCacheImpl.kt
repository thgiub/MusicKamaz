package ru.kamaz.music.cache

import ru.kamaz.music.data.MusicCache

class MusicCacheImpl (private val prefsManager: SharedPrefsManager):MusicCache {
    override fun getLastMusic(): String = prefsManager.getLastMusic()
    override fun saveLastMusic(lastMusic: String)  = prefsManager.saveMusicInfo(lastMusic)
}