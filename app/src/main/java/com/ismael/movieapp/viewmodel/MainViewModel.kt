package com.ismael.movieapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ismael.movieapp.model.Movie
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.ismael.movieapp.R
import com.ismael.movieapp.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.net.ConnectivityManager




open class MainViewModel @Inject constructor(private val movieRepository: MovieRepository,
                                        private val context: Context
) : ViewModel(){
    private var movieList = MutableLiveData<List<Movie>>()
    private var currentMovie = MutableLiveData<Movie>()
    private var detailMovie = MutableLiveData<Movie>()
    private var currentFragmentIndex = MutableLiveData<Int>()
    private var inSearch = MutableLiveData<Boolean>()
    private var inserting = MutableLiveData<Boolean>()
    private var close = MutableLiveData<Boolean>()
    private var snackbarmessage = MutableLiveData<String>()
    private var lastSearch = ""


    init {
        startValues()
    }

    fun startValues()
    {
        currentFragmentIndex.value =0
        inSearch.value = false
        inserting.value = false
        close.value = false
        currentMovie.value = null
        detailMovie.value = null
        snackbarmessage.value = ""
        lastSearch = ""
        getMovies(lastSearch)
    }

    fun getMovies(title : String)
    {
        GlobalScope.launch {
            var list = movieRepository.getMovies(title)

            withContext(Dispatchers.Main){
                movieList.value = list
                if((currentMovie.value==null) && (list.size>0))
                    currentMovie.value = list[0]

            }
        }
    }

    fun getMovies(): LiveData<List<Movie>> {
        return movieList
    }

    fun getSnackbarMessage(): LiveData<String> {
        return snackbarmessage
    }

    fun messageDone(){
        snackbarmessage.value=""
    }

    fun isSearching(): LiveData<Boolean> {
        return inSearch
    }

    fun searched(){
        inSearch.value = false
    }

    fun isClosing(): LiveData<Boolean> {
        return close
    }
    fun goBack(){
        if((currentFragmentIndex.value?:1)>0)
            currentFragmentIndex.value = 0
        else
            close.value = true;
    }

    fun closed(){
        close.value = false;
    }

    fun isInserting(): LiveData<Boolean> {
        return inserting
    }

    fun getCurrentFragmentIndex(): LiveData<Int> {
        return currentFragmentIndex
    }

    fun getCurrentMovie(): LiveData<Movie> {
        return currentMovie
    }

    fun getDetailMovie(): LiveData<Movie> {
        return detailMovie
    }

    fun searchMovie(){
        if(hasInternet())
            inSearch.value = true
        else
            snackbarmessage.value= context.getString(R.string.no_internet_connection)
    }

    open fun hasInternet(): Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val netInfo = cm?.activeNetworkInfo
        return netInfo?.isConnected?:false
    }


    fun myMovies()
    {
        currentFragmentIndex.value = 0
    }
    fun InsertMovie(){
        inserting.value = true
        detailMovie.value = movieRepository.getSelectedMovie();
        currentFragmentIndex.value = 1
    }
    fun getDetailCurrentMovie(){
        currentMovie.value?.let {
            detailMovie(it)
        }
    }
    fun detailMovie(movie : Movie)
    {
        currentMovie.value = movie
        detailMovie.value = movie
        inserting.value = false
        currentFragmentIndex.value = 1
    }
    fun deleteMovie()
    {
        detailMovie.value?.let {
            if (!(inserting.value ?: false)) {
                GlobalScope.launch {
                    var deleted = movieRepository.deleteMovie(it)
                    withContext(Dispatchers.Main)
                    {
                        if (deleted) {
                            currentMovie.value = null
                            getMovies(lastSearch)
                            currentFragmentIndex.value = 0
                            snackbarmessage.value = context.getString(R.string.delete_success)
                        } else
                            snackbarmessage.value = context.getString(R.string.delete_error)
                    }
                }
            }
        }
        currentFragmentIndex.value = 0
    }
    fun saveMovie(){
        if(inserting.value?:false)
        {
            GlobalScope.launch {
                var saved = movieRepository.saveMovie()

                withContext(Dispatchers.Main){
                    if (saved)
                    {
                        currentMovie.value = movieRepository.getSelectedMovie()
                        getMovies(lastSearch)
                        currentFragmentIndex.value = 0
                        snackbarmessage.value = context.getString(R.string.save_success)
                    }
                    else
                        snackbarmessage.value = context.getString(R.string.save_error)
                }
            }
        }

    }
}