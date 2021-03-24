 package ru.kamaz.music.date.media

import android.content.Context
import android.provider.MediaStore
import ru.kamaz.music.date.media.model.Track
import javax.inject.Inject

class AppMediaManager  @Inject constructor( val context: Context)
    : MediaManager {

    override fun scanTracks(): ArrayList<Track> {
        val array = ArrayList<Track>()

        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID)

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
                val duration = Track.convertDuration(cursor.getString(cursor.getColumnIndex(
                    MediaStore.Audio.Media.DURATION)).toLong())
                val albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toLong()

                cursor.moveToNext()

                array.add(Track(id, title, artist, data, duration, albumId))
            }

            cursor.close()
        }

        return array
    }
}