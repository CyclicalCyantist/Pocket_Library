package com.example.pocket_library

import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface LibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("image_type") imageType: String = "photo",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): LibraryResponse
}

@JsonClass(generateAdapter = true)
data class LibraryResponse(
    @Json(name = "numFound") val numFound: Int,
    @Json(name = "docs") val docs: List<BookDoc>
)

@JsonClass(generateAdapter = true)
data class BookDoc(
    @Json(name = "key") val key: String,
    @Json(name = "title") val title: String,
    @Json(name = "author_name") val authorName: List<String>?,
    @Json(name = "first_publish_year") val firstPublishYear: Int?,
    @Json(name = "cover_i") val coverId: Int?
)

