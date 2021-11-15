package ru.kamaz.music.media

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import ru.kamaz.music.R
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music_api.SourceType
import ru.kamaz.music_api.models.AllFolderWithMusic
import ru.kamaz.music_api.models.CategoryMusicModel
import ru.kamaz.music_api.models.ModelTest
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None
import java.io.File
import java.io.IOException
import javax.inject.Inject

class AppMediaManager  @Inject constructor(val context: Context)
    : MediaManager {

    override fun scanTracks(type:Int):Either<None, List<Track>> {
        val array = ArrayList<Track>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM

        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC}  != 0"
        val sortOrder = "${MediaStore.Audio.AudioColumns.TITLE} COLLATE LOCALIZED ASC"

        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)

        if(cursor != null) {
            cursor.moveToFirst()

            while(!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)).toLong()
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val duration = Track.convertDuration(
                    cursor.getString(
                        cursor.getColumnIndex(
                            MediaStore.Audio.Media.DURATION
                        )
                    ).toLong()
                )
                val albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toLong()

                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))

                cursor.moveToNext()

                array.add(
                    Track(
                        id,
                        title,
                        artist,
                        data,
                        duration,
                        albumId,
                        album
                    )
                )
            }
            cursor.close()
        }
        Log.i("trackList", "scanTracks: $array")
        return Either.Right(array)
    }

    override fun scanUSBTracks(): Either<None, List<Track>> {
        val array = ArrayList<Track>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC}  != 0"
        val sortOrder = "${MediaStore.Audio.AudioColumns.TITLE} COLLATE LOCALIZED ASC"

        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)

        if (cursor != null) {
            cursor.moveToFirst()

            while (!cursor.isAfterLast) {
                val id =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)).toLong()
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val duration = Track.convertDuration(
                    cursor.getString(
                        cursor.getColumnIndex(
                            MediaStore.Audio.Media.DURATION
                        )
                    ).toLong()
                )
                val albumId =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        .toLong()
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))

                cursor.moveToNext()

                array.add(
                    Track(
                        id,
                        title,
                        artist,
                        data,
                        duration,
                        albumId,
                        album
                    )
                )
            }

            cursor.close()
        }

        return Either.Right(array)
    }



    override fun getAlbumImagePath(albumID: Long): Either<None, String> {
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ART)
        val selection = MediaStore.Audio.Albums._ID + "=?"
        val args = arrayOf(albumID.toString())

        val cursor = context.contentResolver.query(uri, projection, selection, args, null)

        var albumPath: String? = null

        if(cursor != null) {
            if(cursor.moveToFirst()) albumPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
        }

        cursor?.close()

        return if (albumPath == null) Either.Left(None()) else Either.Right(albumPath)
    }

    override fun getCategory(): Either<None, List<CategoryMusicModel>> {

        val array = ArrayList<CategoryMusicModel>()

        val category = listOf(
            CategoryMusicModel(R.drawable.ic_like_false, "Избранное",0),
            CategoryMusicModel(R.drawable.ic_like_false, "Исполнители",1),
            CategoryMusicModel(R.drawable.ic_like_false, "Жанры",2),
            CategoryMusicModel(R.drawable.ic_like_false, "Альбомы",3),
            CategoryMusicModel(R.drawable.ic_like_false, "Плейлисты",4),
        )
        array.addAll(category)
        return Either.Right(array)
    }




    override fun getAllFolder(  ): Either<None, List<AllFolderWithMusic>> {

        var result = ArrayList<AllFolderWithMusic>()

        val directories = LinkedHashMap<String, ArrayList<ModelTest>>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"

        val order = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"

        val cursor = context.getContentResolver().query(uri, null, selection, null, order)

        if(cursor != null) {

           /* cursor?.let {
                it.moveToFirst()
                val pathIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)

                do {
                    val path = it.getString(pathIndex)
                    val file = File(path)
                    if (!file.exists()) {
                        continue
                    }

                    val fileDir = file.getParent()

                    var songURL = it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA))
                    var songAuth = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    var songName = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE))

                    if (directories.containsKey(fileDir)) {
                        var songs = directories.getValue(fileDir);

                        var song = ModelTest(songURL, songAuth, songName)

                        songs.add(song)

                        directories.put(fileDir, songs)
                    } else {
                        var song =ModelTest(songURL, songAuth, songName)

                        var songs = ArrayList<ModelTest>()
                        songs.add(song)

                        directories.put(fileDir, songs)
                    }
                } while (it.moveToNext())


                for (dir in directories) {
                    var dirInfo: AllFolderWithMusic = AllFolderWithMusic(dir.key, dir.value)

                    result.add(dirInfo)
                }
            }*/
        }


        return Either.Right(result)
    }

    override fun scanMediaFiles(sourceType: SourceType): List<File> {
        TODO("Not yet implemented")
    }

}