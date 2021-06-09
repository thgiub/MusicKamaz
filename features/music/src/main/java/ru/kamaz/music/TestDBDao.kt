package ru.kamaz.music

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kamaz.music.data.TestDBData

@Dao
interface TestDBDao {

    @Query("SELECT * FROM 'test_db'")
    fun testGetQuery(): String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(testDBData: TestDBData)

    @Query("DELETE FROM 'test_db'")
    suspend fun deleteAll()
}