package com.ismael.movieapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ismael.movieapp.R
import com.ismael.movieapp.model.Movie
import com.ismael.movieapp.model.Search
import com.ismael.movieapp.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SearchMoviesViewModel @Inject constructor(private val movieRepository: MovieRepository,
                                                private val context: Context
) : ViewModel() {
    private var movieSearchList = MutableLiveData<List<Search>>()
    private var movieSelected = MutableLiveData<Boolean>()
    private var loading = MutableLiveData<Boolean>()
    private var goBack = MutableLiveData<Boolean>()
    private var snackbarmessage = MutableLiveData<String>()
    private var lastSearch = "";

    init{
        loading.value = false
    }
    fun getSearchMovies(): LiveData<List<Search>> {
        return movieSearchList
    }

    fun isLoading(): LiveData<Boolean> {
        return loading;
    }

    fun isGoingBack(): LiveData<Boolean> {
        return goBack;
    }

    fun isMovieSelected(): LiveData<Boolean> {
        return movieSelected;
    }

    fun getSnackbarMessage(): LiveData<String> {
        return snackbarmessage
    }

    fun messageDone(){
        snackbarmessage.value=""
    }

    fun goBack() {
        goBack.value = true;
    }
    suspend fun searchByTitle(title:String)
    {
        if(!lastSearch.equals(title)) {
            withContext(Dispatchers.Main){
                loading.value = true
            }
            lastSearch = title
            var list = movieRepository.searchMovies(title)
            withContext(Dispatchers.Main){
                movieSearchList.value = list
                loading.value = false
                if(list == null)
                    snackbarmessage.value=context.getString(R.string.search_movie_error_msg)
            }
        }
    }

    fun close()
    {
        movieSearchList.value = null
        movieSelected.value = false
        loading.value = false
        lastSearch = ""
        snackbarmessage.value=""
        goBack.value = false;
    }


    suspend fun selectMovie(id :String){
        withContext(Dispatchers.Main){
            loading.value = true
        }
        var selected = movieRepository.getMovie(id)
        withContext(Dispatchers.Main){
            loading.value = false
            movieSelected.value = selected
            if(!selected)
                snackbarmessage.value=context.getString(R.string.get_movie_error_msg)
            else
                goBack.value = true
        }

    }
}