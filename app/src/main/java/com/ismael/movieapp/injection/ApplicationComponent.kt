package com.ismael.movieapp.injection

import com.ismael.movieapp.activity.MainActivity
import com.ismael.movieapp.activity.SearchMoviesActivity
import com.ismael.movieapp.fragments.MovieDetailFragment
import com.ismael.movieapp.fragments.MyMoviesFragment
import com.ismael.movieapp.repository.MovieRepository
import com.ismael.movieapp.viewmodel.MainViewModel
import com.ismael.test.di.scope.AppScope
import dagger.Component

@AppScope
@Component(modules = arrayOf(ApplicationModule::class,
                              ViewModelModule::class))
interface ApplicationComponent {
    fun inject(target : MainViewModel)
    fun inject(target : MovieRepository)
    fun inject(target : MainActivity)
    fun inject(target : SearchMoviesActivity)
    fun inject(target : MyMoviesFragment)
    fun inject(target : MovieDetailFragment)


}
