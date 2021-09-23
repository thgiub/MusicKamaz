package ru.kamaz.music.data

import ru.kamaz.music_api.models.CategoryMusicModel
import ru.kamaz.music_api.models.FavoriteSongs
import ru.kamaz.music_api.models.FolderMusicModel
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None

interface MediaManager {
    fun scanTracks(): Either<None, List<Track>>
    fun scanUSBTracks(): Either<None, List<Track>>
    fun getAlbumImagePath(albumID: Long): Either<None, String>
    fun getCategory():Either<None, List<CategoryMusicModel>>
   // fun getFolderWithMusic():Either<None, List<FolderMusicModel>>

}