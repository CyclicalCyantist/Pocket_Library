package com.example.recycleview_simple

import android.R.mipmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

enum class ItemCategories{
    ALL,
    FAVOURITES,
    APPETISER,
    BREAKFAST,
    DESSERT,
    DRINK,
    MAIN,
    SIDE,
    SNACK
}

@Parcelize
data class Item(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val category: ItemCategories = ItemCategories.ALL,
    val description: String = "",
    val imageSrc: Int = R.mipmap.ic_logo,
    val imageBackground: Int = R.mipmap.ic_logo,
    val isFavourite: Boolean = false
): Parcelable

