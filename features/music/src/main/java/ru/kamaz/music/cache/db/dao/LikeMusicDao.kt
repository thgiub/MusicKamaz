package ru.kamaz.music.cache.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kamaz.music.domain.FavoriteSongsEntity

@Dao
interface LikeMusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: FavoriteSongsEntity)

    @Query("SELECT * FROM like_songs")
    fun loadAll(): List<FavoriteSongsEntity>

    @Query("SELECT * FROM like_songs WHERE id_song LIKE :name")
    fun getId(name: String): FavoriteSongsEntity

}