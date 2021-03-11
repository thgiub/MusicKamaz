package ru.kamaz.music.date.media

import ru.kamaz.music.date.media.model.Track

interface MediaManager {


    fun scanTracks(): ArrayList<Track>

}