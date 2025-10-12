package com.example.pocket_library

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "books")
@Parcelize
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "book_title")
    val title: String,

    @ColumnInfo(name = "book_author")
    val author: String,

    @ColumnInfo(name = "book_publication_year")
    val year: Int

    //we need photo, right?
): Parcelable