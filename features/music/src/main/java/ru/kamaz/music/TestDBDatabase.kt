package ru.kamaz.music

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.kamaz.music.data.TestDBData

@Database(entities = [TestDBData::class], version = 1, exportSchema = false)
abstract class TestDBDatabase : RoomDatabase() {

    abstract fun testDBDao(): TestDBDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var wordDao = database.testDBDao()

                    // Delete all content here.
                    wordDao.deleteAll()

                    // Add sample words.
                    var word = TestDBData("Hello")
                    wordDao.insert(word)

                }
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: TestDBDatabase? = null

        fun getDatabase(context: Context): TestDBDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TestDBDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}