package com.example.recycleview_simple.com.example.recycleview_simple

import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface LibraryApi {
    @GET("api/")
    suspend fun searchBooks(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("book_type") bookType: String = "book",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): LibraryResponse
}

@JsonClass(generateAdapter = true)
data class LibraryResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<Hit>
)

@JsonClass(generateAdapter = true)
data class Hit(
    val id: Int,
    @Json(name = "previewURL") val previewUrl: String,
    @Json(name = "webformatURL") val webUrl: String,
    @Json(name = "largeImageURL") val largeUrl: String,
    val tags: String
)