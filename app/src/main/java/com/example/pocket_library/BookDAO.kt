package com.example.pocket_library

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDAO {
    // Insert one or more books
    @Insert
    suspend fun insert(vararg book: Book)

    // Update one or more books
    @Update
    suspend fun update(vararg book: Book)

    // Delete one or more books
    @Delete
    suspend fun delete(vararg book: Book)

    // Get all books
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<Book>>

    // Get books by name
    @Query("SELECT * FROM books WHERE book_title = :bookName")
    suspend fun getBooksByName(bookName: String): List<Book>

    // Get book by ID
    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    suspend fun getBookById(bookId: Long): Book?
}