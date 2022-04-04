package ru.kamaz.music.media

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import ru.kamaz.music.R
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music_api.FileType
import ru.kamaz.music_api.SourceType
import ru.kamaz.music_api.models.*
import ru.sir.core.Either
import ru.sir.core.None
import java.io.File
import javax.inject.Inject
import kotlin.random.Random


class AppMediaManager @Inject constructor(val context: Context) : MediaManager {


    override fun scanTracks(type: Int): Either<None, List<Track>> {
        val array = ArrayList<Track>()
        val uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val sortOrder = "${MediaStore.Audio.AudioColumns.TITLE} COLLATE LOCALIZED ASC"

        val cursor = context.contentResolver.query(uri,
            projection,
            selection,
            null,
            sortOrder)


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
        Log.i("trackList", "scanTracks: $array")
        return Either.Right(array)
    }

    fun readRecursive(root:File, extentions:List<String>):List<File> {
        val list = ArrayList<File>()
        root.listFiles()?.filter { it.isDirectory }?.forEach {
            list.addAll(readRecursive(it, extentions))
        }

        root.listFiles()?.filter { extentions.contains(it.extension) }?.forEach {
            list.add(it)
        }

        return list
    }

   /* override fun scanTracks(type: Int): Either<None, List<Track>> {

        val list = readRecursive(File("/storage/"), listOf("mp3", "wav")).sorted().map {
            Track(
                Random.nextLong(),
                it.name,
                it.name,
                it.path,
                "120",
                Random.nextLong(),
                it.name
            )
        }
        Log.i("usbMusic", "getFiles: $list")

        return Either.Right(list)

    }*/

    override fun scanUSBTracks(path: String): Either<None, List<Track>> {
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

        var DOWNLOAD_FILE_DIR =
            Environment.getExternalStorageDirectory().getPath() + "/mnt/media_rw/usbdisk0"
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                MediaStore.Audio.Media.DATA + " LIKE '" + DOWNLOAD_FILE_DIR + "/%'"
        /*val selection = "${MediaStore.Audio.Media.IS_MUSIC   + " != 0 AND " +
                MediaStore.Audio.Media.DATA + " LIKE '"+/mnt/media_rw/usbdisk0+"/%'}  */
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

        if (cursor != null) {
            if (cursor.moveToFirst()) albumPath =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
        }

        cursor?.close()

        return if (albumPath == null) Either.Left(None()) else Either.Right(albumPath)
    }

    override fun getCategory(): Either<None, List<CategoryMusicModel>> {

        val array = ArrayList<CategoryMusicModel>()

        val category = listOf(

            CategoryMusicModel(R.drawable.ic_songers, "Исполнители", 0),
            CategoryMusicModel(R.drawable.ic_guitar, "Жанры", 1),
            CategoryMusicModel(R.drawable.ic_albom, "Альбомы", 2),
            CategoryMusicModel(R.drawable.ic_play_list, "Плейлисты", 3),
            CategoryMusicModel(R.drawable.ic_like_for_list, "Избранное", 4)
        )
        array.addAll(category)
        return Either.Right(array)
    }


    override fun getAllFolder(): Either<None, List<AllFolderWithMusic>> {

        var result = ArrayList<AllFolderWithMusic>()

        val directories = LinkedHashMap<String, ArrayList<ModelTest>>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"


        //val selection= MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val cursor = context.getContentResolver().query(uri, null, selection, null, null)

        if (cursor != null) {

            cursor?.let {

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
                        var song = ModelTest(songURL, songAuth, songName)

                        var songs = ArrayList<ModelTest>()
                        songs.add(song)

                        directories.put(fileDir, songs)
                    }
                } while (it.moveToNext())


                for (dir in directories) {
                    var dirInfo: AllFolderWithMusic = AllFolderWithMusic(dir.key, dir.value)

                    result.add(dirInfo)
                }
            }
        }
        return Either.Right(result)
    }



    override fun scanFoldersWithMusic(sourceType: SourceType): List<File> {
        val resultList: MutableList<File> = mutableListOf()

        val basePath: String = when (sourceType) {
            SourceType.MEDIA_AUDIO -> MediaStore.Audio.Media.DATA
            SourceType.MEDIA_IMAGE -> MediaStore.Images.Media.DATA
            SourceType.MEDIA_VIDEO -> MediaStore.Video.Media.DATA
            else -> MediaStore.Images.Media.DATA
        }

        val uri = when (sourceType) {
            SourceType.MEDIA_AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            SourceType.MEDIA_IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            SourceType.MEDIA_VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        //перечень набора данных из таблицы
        val projection = arrayOf(basePath)
        //выбор аудио файлов
  /*      val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                MediaStore.Audio.Media.DATA + " LIKE '/storage/usbdisk0/Музыка для тестов%'"*/

        val selection = when (sourceType) {
            SourceType.MEDIA_AUDIO -> MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                    MediaStore.Audio.Media.DATA +  " LIKE '/storage/usbdisk0/%'"
            else -> null
        }

        val cursor = context.contentResolver
            .query(uri, projection, selection, null, null)

        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                //получаем Uri файла
                val data = cursor.getString(cursor.getColumnIndex(basePath))

                resultList.add(File(data))

                cursor.moveToNext()
            }
            cursor.close()
        }

        Log.i("usb", "scanMediaFiles: $resultList")

        return resultList.toList()
    }


    fun getFileModelsFromFiles(files: List<File>): List<FileModel> {
        return files.map {
            FileModel(
                it.path,
                FileType.getFileType(it),
                it.name,
                convertFileSizeToMB(it.length()),
                it.extension,
                it.listFiles()?.size
                    ?: 0
            )
        }
    }

    override fun getFilesFromPath(
        path: String,
        showHiddenFiles: Boolean,
        onlyFolders: Boolean
    ): List<File> {
        val file = File(path)
        return file.listFiles()
            .filter { showHiddenFiles || !it.name.startsWith(".") }
            .filter { !onlyFolders || it.isDirectory }
            .toList()
    }


    fun convertFileSizeToMB(sizeInBytes: Long): Double {
        return (sizeInBytes.toDouble()) / (1024 * 1024)
    }

    fun createNewFile(
        fileName: String,
        path: String,
        callback: (result: Boolean, message: String) -> Unit
    ) {
        val fileAlreadyExists = File(path).listFiles().map { it.name }.contains(fileName)
        if (fileAlreadyExists) {
            callback(false, "'${fileName}' already exists.")
        } else {
            val file = File(path, fileName)
            try {
                val result = file.createNewFile()
                if (result) {
                    callback(result, "File '${fileName}' created successfully.")
                } else {
                    callback(result, "Unable to create file '${fileName}'.")
                }
            } catch (e: Exception) {
                callback(false, "Unable to create file. Please try again.")
                e.printStackTrace()
            }
        }
    }

    fun createNewFolder(
        folderName: String,
        path: String,
        callback: (result: Boolean, message: String) -> Unit
    ) {
        val folderAlreadyExists = File(path).listFiles().map { it.name }.contains(folderName)
        if (folderAlreadyExists) {
            callback(false, "'${folderName}' already exists.")
        } else {
            val file = File(path, folderName)
            try {
                val result = file.mkdir()
                if (result) {
                    callback(result, "Folder '${folderName}' created successfully.")
                } else {
                    callback(result, "Unable to create folder '${folderName}'.")
                }
            } catch (e: Exception) {
                callback(false, "Unable to create folder. Please try again.")
                e.printStackTrace()
            }
        }
    }

    fun deleteFile(path: String) {
        val file = File(path)
        if (file.isDirectory) {
            file.deleteRecursively()
        } else {
            file.delete()
        }
    }


}