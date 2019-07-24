package com.ismael.movieapp.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.ismael.movieapp.R
import com.ismael.movieapp.customview.MyToolbar
import com.ismael.movieapp.injection.AndroidApplication
import com.ismael.movieapp.model.Movie
import com.ismael.movieapp.viewmodel.MainViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import javax.inject.Inject

class MovieDetailFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as AndroidApplication).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }
    override fun onStart() {
        super.onStart()
        mainViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        mainViewModel.getDetailMovie().observe(this, Observer<Movie>{ movie ->
            (activity as AppCompatActivity)?.supportActionBar?.title = "${movie.Title} (${movie.Year})"
            tvDirector.text = movie.Director
            tvActor.text = movie.Actors
            tvPlot.text = movie.Plot
            //if(movie.Picture == null)
                Picasso.get().load(movie.Poster).placeholder(R.drawable.no_image).into(movieImage);
        })
    }

}
