package com.ismael.movieapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.ismael.movieapp.R
import com.ismael.movieapp.adapters.MyMoviesListAdapter
import com.ismael.movieapp.injection.AndroidApplication
import com.ismael.movieapp.model.Movie
import com.ismael.movieapp.viewmodel.MainViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import kotlinx.android.synthetic.main.fragment_my_movies.*
import kotlinx.android.synthetic.main.fragment_my_movies.movieImage
import javax.inject.Inject

class MyMoviesFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_my_movies, container, false)
    }

    override fun onStart() {
        super.onStart()
        mainViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        addButon.setOnClickListener {  mainViewModel.searchMovie() }
        activity?.applicationContext?.let{
            rvMovieList.layoutManager = LinearLayoutManager(it)
            rvMovieList.adapter = MyMoviesListAdapter(ArrayList<Movie>(), it, mainViewModel)
        }

        mainViewModel.getMovies().observe(this, Observer<List<Movie>>{ movies ->
            rvMovieList.adapter = activity?.applicationContext?.let { MyMoviesListAdapter(movies?:ArrayList<Movie>(), it, mainViewModel) }
        })


        mainViewModel.getCurrentMovie().observe(this, Observer<Movie>{ movies ->
            movieImage.visibility = View.INVISIBLE
            movies?.let {
                Picasso.get().load(it.Poster).placeholder(R.drawable.no_image).into(movieImage)
                movieImage.visibility = View.VISIBLE
            }
        })

        movieImage.setOnClickListener {
            mainViewModel.getDetailCurrentMovie()
        }
    }

}
