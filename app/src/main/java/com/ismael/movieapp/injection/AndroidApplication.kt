package com.ismael.movieapp.injection

import android.app.Application

class AndroidApplication : Application() {
    lateinit var component: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this.applicationContext))
            .build()
    }
}