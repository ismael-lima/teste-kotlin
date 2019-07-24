package com.ismael.movieapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ismael.movieapp.R
import com.ismael.movieapp.model.Movie
import com.ismael.movieapp.viewmodel.MainViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.search_item_list.view.movieImage
import kotlinx.android.synthetic.main.search_item_list.view.tvTitle


class MyMoviesListAdapter(private val movies : List<Movie>, private val context : Context,
                          val viewModel: MainViewModel
) : RecyclerView.Adapter<MyMoviesListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.my_movie_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(movies[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(movie: Movie) {
            itemView.tvTitle.text = "${movie.Title}(${movie.Year})"
            Picasso.get().load(movie.Poster).placeholder(R.drawable.no_image).into(itemView.movieImage);
            itemView.setOnClickListener { v ->
                viewModel.detailMovie(movie)
            }
        }
    }

}