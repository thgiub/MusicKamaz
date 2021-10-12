package ru.kamaz.music.media

import android.app.PendingIntent.getActivity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kamaz.music.R
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music_api.models.AllFolderWithMusic
import ru.kamaz.music_api.models.CategoryMusicModel
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.core.None
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
            MediaStore.Audio.Media.ALBUM_ID
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


                cursor.moveToNext()

                array.add(
                    Track(
                        id,
                        title,
                        artist,
                        data,
                        duration,
                        albumId
                    )
                )
            }
            cursor.close()
        }
        Log.i("trackList", "scanTracks: $array")
        if (type==1){
            Log.i("AppMediaManager", "scanTracks: ${array.joinToString { it.title }}")
            val sortedFilteredArray = array.fold(array){ acc, track -> arrayListOf(*acc.filter { it.artist != track.artist }.toTypedArray()) }
            Log.i("AppMediaManager", "scanTracks: ${sortedFilteredArray.joinToString { it.title }}")
            return Either.Right(sortedFilteredArray)
        }
        return Either.Right(array)
    }


/*    fun artistSorted(artist:String){
        var i = 0
        var q = 0
        CoroutineScope(Dispatchers.IO).launch {
            while (i in array.indices) {
                if (tracks[q].data != data) {
                    q++
                    i++
                } else {
                    Log.i(ContentValues.TAG, "checkCurrentPosition $q$data")
                    initTrack(tracks[q], data)
                    break
                }
            }
        }
        currentTrackPosition = q
    }*/

    override fun scanUSBTracks(): Either<None, List<Track>> {
        val array = ArrayList<Track>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
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

                cursor.moveToNext()

                array.add(
                    Track(
                        id,
                        title,
                        artist,
                        data,
                        duration,
                        albumId
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

    override fun getAllFolder(): Either<None, List<AllFolderWithMusic>> {

        val array = ArrayList<AllFolderWithMusic>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA,
                MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
        val orderBy = MediaStore.Audio.Media.DATE_TAKEN
        val cursor = context.getContentResolver().query(uri, projection, null, null, "$orderBy DESC")

        if(cursor != null) {
            cursor.moveToFirst()

            while(!cursor.isAfterLast) {
               val  data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
               val  bucket = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                cursor.moveToNext()

                array.add(
                    AllFolderWithMusic(
                        data,
                        bucket
                    )
                )
            }
            cursor.close()
        }

        return Either.Right(array)
    }


}