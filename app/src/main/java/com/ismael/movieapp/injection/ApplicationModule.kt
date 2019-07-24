package com.ismael.movieapp.injection

import android.content.Context
import androidx.room.Room
import com.ismael.movieapp.BuildConfig
import com.ismael.movieapp.api.ImodbInterceptor
import com.ismael.movieapp.api.MovieServiceApi
import com.ismael.movieapp.repository.MovieRepository
import dagger.Module
import dagger.Provides
import com.ismael.movieapp.database.MyDatabase
import com.ismael.test.di.scope.AppScope
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient




@Module
class ApplicationModule(private val context: Context) {

    @Provides
    @AppScope
    fun provideMovieRepository(api : MovieServiceApi, db : MyDatabase) : MovieRepository
    {
        return MovieRepository(api, db)
    }


    @Provides
    @AppScope
    fun provideContext() : Context
    {
        return context
    }

    @Provides
    @AppScope
    fun provideDatabase() : MyDatabase
    {
        val db = Room.databaseBuilder(context, MyDatabase::class.java, "movies.db").build()
        return db;
    }

    @AppScope
    @Provides
    fun provideMovieServiceApi(): MovieServiceApi {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(ImodbInterceptor())

        var retrofit =  Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        return retrofit.create(MovieServiceApi::class.java)
    }
}