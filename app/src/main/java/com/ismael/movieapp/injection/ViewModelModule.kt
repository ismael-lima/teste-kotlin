package com.ismael.movieapp.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ismael.movieapp.viewmodel.MainViewModel
import com.ismael.movieapp.viewmodel.SearchMoviesViewModel
import com.ismael.test.di.scope.AppScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class ViewModelModule{
    @AppScope
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(userViewModel: MainViewModel): ViewModel

    @AppScope
    @Binds
    @IntoMap
    @ViewModelKey(SearchMoviesViewModel::class)
    abstract fun bindSearchMoviesViewModel(userViewModel: SearchMoviesViewModel): ViewModel

    // Factory
    @AppScope
    @Binds abstract fun bindViewModelFactory(vmFactory: ViewModelFactory): ViewModelProvider.Factory
}
