package com.example.pocket_library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "author")
    val author: String = "",

    @ColumnInfo(name = "year")
    val year: Int = 0,

    @ColumnInfo(name = "cover")
    val cover: String? = null,

    @ColumnInfo(name = "synced")
    val synced: Boolean = true,
)