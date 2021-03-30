package ru.kamaz.music.data

import ru.kamaz.music_api.interfaces.Repository

import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None


class RepositoryImpl(private val media:MediaManager): Repository{
    override fun loadData(): Either<None, List<Track>> = media.scanTracks()
}