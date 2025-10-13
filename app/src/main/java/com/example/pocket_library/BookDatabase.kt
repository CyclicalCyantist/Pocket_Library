package com.example.pocket_library

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Book::class], version = 1, exportSchema = false)
abstract class BookDatabase : RoomDatabase() {

    // 1. DAO getters
    abstract fun bookDao(): BookDAO

    companion object {
        // 2. Singleton instance
        @Volatile
        private var INSTANCE: BookDatabase? = null

        // 3. Builder function
        fun getDatabase(context: Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_database" // 4. Database file name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}