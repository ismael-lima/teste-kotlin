package com.ismael.movieapp.repository

import com.ismael.movieapp.api.MovieServiceApi
import com.ismael.movieapp.database.MyDatabase
import com.ismael.movieapp.model.Movie
import com.ismael.movieapp.model.Search
import javax.inject.Inject


class MovieRepository @Inject constructor(private val  movieServiceApi :MovieServiceApi, private val db : MyDatabase) {
    private var selectedMovie: Movie? = null

    suspend fun searchMovies(title: String): List<Search>?
    {
        return movieServiceApi.getSearchMovie(title).await().Search;
    }

    suspend fun getMovie(id : String): Boolean{
        selectedMovie = movieServiceApi.getMovie(id).await()

        selectedMovie?.let{
            return !it.Title.equals("")
        }
        return false
    }

    suspend fun getMovies(title: String): List<Movie>{
        var list = db.movieDao().load("%${title}%")
        return list
    }

    fun getSelectedMovie(): Movie?{
        return selectedMovie
    }
    suspend fun deleteMovie(movie: Movie): Boolean{
        var deleted = db.movieDao().delete(movie)

        if(deleted>0)
            return true
        return false;
    }
    suspend fun saveMovie(): Boolean{
        selectedMovie?.let {
            var saved = db.movieDao().save(it)

            if(saved>0)
                return true
        }
        return false;
    }
}