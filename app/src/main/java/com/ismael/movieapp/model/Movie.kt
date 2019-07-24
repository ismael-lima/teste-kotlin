package com.ismael.movieapp.model

//import android.arch.persistence.room.Entity
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

//import java.io.Serializable

@Entity(tableName = "Movie")
class Movie(@PrimaryKey
            var imdbID: String = "",
            var Title: String = "",
            var Director: String = "",
            var Actors: String = "",
            var Year: String = "",
            var Plot: String = "",
            var Poster: String = ""){


    fun getBitmapImage(): Bitmap?
    {
        return null
    }
}