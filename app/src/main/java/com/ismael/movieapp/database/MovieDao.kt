package com.ismael.movieapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.ismael.movieapp.model.Movie


@Dao
interface MovieDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(movie: Movie): Long

    @Query("SELECT * FROM movie WHERE Title like :title")
    suspend fun load(title: String): List<Movie>

    @Delete
    suspend fun delete(movie: Movie): Int

    @Query("DELETE FROM movie")
    suspend fun clearTable()
}