package com.test.pocketaces.data

import com.test.pocketaces.data.model.MovieResponseDTO
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieServices {

    @GET("https://www.omdbapi.com")
    fun searchMovies(
        @Query("apikey") apiKey: String = "YOUR_API_KEY",
        @Query("s") query: String,
        @Query("page") page: Int
    ): Observable<Response<MovieResponseDTO>>
}