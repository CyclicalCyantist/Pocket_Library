package com.example.recycleview_simple

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "book_title")
    val title: String,

    @ColumnInfo(name = "book_author")
    val author: String,

    @ColumnInfo(name = "book_publication_year")
    val year: Int
)