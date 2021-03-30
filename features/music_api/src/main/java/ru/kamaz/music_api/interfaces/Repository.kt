package ru.kamaz.music_api.interfaces

import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None

interface Repository {
    fun loadData(): Either<None, List<Track>>
}