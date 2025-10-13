package com.example.pocket_library

import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface LibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("author") author: String,
        //@Query("title") title: String,
        //@Query("author") author: String,
        @Query("limit") perPage: Int = 20,
    ): LibraryResponse

    @GET("search.json")
    suspend fun searchBooksByTitle(
        @Query("title") title: String,
        @Query("limit") perPage: Int = 20,
    ): LibraryResponse
}

@JsonClass(generateAdapter = true)
data class LibraryResponse(
    @Json(name = "numFound") val numFound: Int,
    @Json(name = "docs") val docs: List<BookDoc>
)

@JsonClass(generateAdapter = true)
data class BookDoc(
    //@Json(name = "key") val key: String,
    @Json(name = "title") val title: String,
    @Json(name = "author_name") val authorName: List<String>?,
    @Json(name = "first_publish_year") val firstPublishYear: Int?,
    @Json(name = "cover_i") val coverId: Int?
)

