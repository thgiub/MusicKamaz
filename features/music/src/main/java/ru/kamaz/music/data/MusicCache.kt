package ru.kamaz.music.data

interface MusicCache {
    fun getLastMusic():String
    fun saveLastMusic(lastMusic:String)
}