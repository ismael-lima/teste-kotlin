package com.ismael.movieapp.viewmodel

import androidx.room.Room
import com.ismael.movieapp.BuildConfig
import com.ismael.movieapp.activity.MainActivity
import com.ismael.movieapp.api.ImodbInterceptor
import com.ismael.movieapp.api.MovieServiceApi
import com.ismael.movieapp.database.MyDatabase
import com.ismael.movieapp.model.Movie
import com.ismael.movieapp.repository.MovieRepository
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.Robolectric
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var db : MyDatabase
    private lateinit var rep : MovieRepository

    var fakeInternet = false;

    fun createDependencies(){

        var activity :MainActivity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .resume()
            .get()
        db = Room.databaseBuilder(activity, MyDatabase::class.java, "test.db").build()


        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(ImodbInterceptor())

        var retrofit =  Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        val api = retrofit.create(MovieServiceApi::class.java)


        rep = MovieRepository(api, db)


        viewModel = object: MainViewModel(rep, activity) {
            override fun hasInternet() : Boolean{
                return fakeInternet
            }
        }
    }

    fun fillDataBase(){
        GlobalScope.launch {
            db.movieDao().save(Movie().apply {
                imdbID = "1"
                Title = "First Movie"
            })
            db.movieDao().save(Movie().apply {
                imdbID = "2"
                Title = "Second Movie"
            })
            db.movieDao().save(Movie().apply {
                imdbID = "3"
                Title = "Third Movie"
            })
            db.movieDao().save(Movie().apply {
                imdbID = "4"
                Title = "Fourth Movie"
            })
        }
    }

    fun clearDatabase(){
        GlobalScope.launch {
            db.movieDao().clearTable()
        }
    }



    @Before
    fun setUp() {
        createDependencies()
    }

    @After
    fun tearDown() {
    }


    @Test
    fun messageDone() {
        viewModel.startValues()
        viewModel.messageDone()
        assert( viewModel.getSnackbarMessage().value.equals(""))
    }

    @Test
    fun getMovies() {
        viewModel.startValues()
        clearDatabase()
        fillDataBase()
        viewModel.getMovies("")
        assert( (viewModel.getMovies()?.value?.size?:0) == 4)

        viewModel.getMovies("First")
        assert( (viewModel.getMovies()?.value?.size?:0) == 1)

        viewModel.getMovies("Second")
        assert( (viewModel.getMovies()?.value?.size?:0) == 1)

        viewModel.getMovies("d Mo")
        assert( (viewModel.getMovies()?.value?.size?:0) == 2)

        viewModel.getMovies("XXX")
        assert( (viewModel.getMovies()?.value?.size?:0) == 0)


        clearDatabase()
    }


    @Test
    fun goBack() {
        viewModel.startValues()
        viewModel.detailMovie(Movie())
        assert( (viewModel.getCurrentFragmentIndex()?.value?:0) == 1)
        viewModel.goBack()
        assert( (viewModel.getCurrentFragmentIndex()?.value?:0) == 0)
        viewModel.goBack()
        assert(viewModel.isClosing().value == true)
    }

    @Test
    fun closed() {
        viewModel.startValues()
        viewModel.closed()
        assert( viewModel.isClosing()?.value == true)
    }

    @Test
    fun searchMovie() {
        viewModel.startValues()
        fakeInternet = false
        viewModel.searchMovie()
        assert( !viewModel.getSnackbarMessage()?.value.equals(""))
        assert( viewModel.isSearching()?.value == false)
        fakeInternet = true
        assert( viewModel.isSearching()?.value == true)
    }

    @Test
    fun searched() {
        viewModel.startValues()
        viewModel.searched()
        assert( viewModel.isSearching()?.value == false)
    }

    @Test
    fun myMovies() {
        viewModel.startValues()
        viewModel.myMovies()
        assert( (viewModel.getCurrentFragmentIndex()?.value?:0) == 0)
    }

    @Test
    fun insertMovie() {
        viewModel.startValues()
        GlobalScope.launch {
            rep.getMovie("tt0120737")
        }
        viewModel.InsertMovie()
        assert( viewModel.isInserting()?.value == true)
        assert( viewModel.getDetailMovie().value?.equals(rep.getSelectedMovie())?:false)
        assert( (viewModel.getCurrentFragmentIndex()?.value?:0) == 1)
    }

    @Test
    fun detailMovie() {
        viewModel.startValues()
        var mv = Movie("1", "Tester")
        viewModel.detailMovie(mv)
        assert( (viewModel.getCurrentFragmentIndex()?.value?:0) == 1)
        assert( viewModel.isInserting()?.value == false)
        assert( viewModel.getCurrentMovie()?.value?.equals(mv)?:false)
        assert( viewModel.getDetailMovie()?.value?.equals(mv)?:false)
    }

    @Test
    fun deleteMovie() {
        viewModel.startValues()
        GlobalScope.launch {
            rep.getMovie("tt0120737")
        }
        viewModel.InsertMovie()
        viewModel.deleteMovie()
        assert( (viewModel.getCurrentFragmentIndex()?.value?:0) == 0)


        viewModel.startValues()
        var mv = Movie("1", "Test")
        viewModel.detailMovie(mv)
        viewModel.deleteMovie()
        assert( (viewModel.getCurrentFragmentIndex()?.value?:0) == 0)
        assert( !(viewModel.getSnackbarMessage()?.value?:"").equals(""))
    }

    @Test
    fun saveMovie() {
        clearDatabase()
        viewModel.startValues()
        GlobalScope.launch {
            rep.getMovie("tt0120737")
        }
        viewModel.InsertMovie()
        viewModel.saveMovie()
        assert( (viewModel.getCurrentFragmentIndex()?.value?:0) == 0)
        assert( !(viewModel.getSnackbarMessage()?.value?:"").equals(""))
        viewModel.getMovies("")
        assert( (viewModel.getMovies()?.value?.size?:0) == 1)
    }
}