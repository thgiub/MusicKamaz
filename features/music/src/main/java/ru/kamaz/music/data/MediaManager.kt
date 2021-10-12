package ru.kamaz.music.data

import ru.kamaz.music_api.models.*
import ru.sir.core.Either
import ru.sir.core.None

interface MediaManager {
    fun scanTracks(type:Int): Either<None, List<Track>>
    fun scanUSBTracks(): Either<None, List<Track>>
    fun getAlbumImagePath(albumID: Long): Either<None, String>
    fun getCategory():Either<None, List<CategoryMusicModel>>
    fun getAllFolder(): Either<None, List<AllFolderWithMusic>>
   // fun getFolderWithMusic():Either<None, List<FolderMusicModel>>

}