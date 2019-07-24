package com.ismael.movieapp.database

import androidx.room.RoomDatabase
import androidx.room.Database
import com.ismael.movieapp.model.Movie


@Database(entities = arrayOf(Movie::class), version = 1)
abstract class MyDatabase : RoomDatabase() {

    // --- DAO ---
    abstract fun movieDao(): MovieDao

}