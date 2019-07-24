package com.ismael.movieapp.api

import com.ismael.movieapp.model.Movie
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieServiceApi {

    @GET("?type=movie")
    fun getSearchMovie( @Query("s") title: String): Deferred<SearchMovieResponse>


    @GET("?plot=full")
    fun getMovie(@Query("i") userId: String): Deferred<Movie>
}