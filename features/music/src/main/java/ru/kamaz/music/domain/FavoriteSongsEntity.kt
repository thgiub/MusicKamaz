package ru.kamaz.music.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_warehouse")
data class FavoriteSongsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_song")
    var idSong: Int,
    @ColumnInfo(name = "artist")
    var name: String
)

