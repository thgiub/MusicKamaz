package ru.kamaz.music.data

import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None

interface MediaManager {
    fun scanTracks(): Either<None, List<Track>>
}