package com.ismael.movieapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.ismael.movieapp.R
import com.ismael.movieapp.injection.DaggerApplicationComponent
import com.ismael.movieapp.model.Movie
import com.ismael.movieapp.model.Search
import com.ismael.movieapp.viewmodel.SearchMoviesViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.search_item_list.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchMovieListAdapter(private val searchs : List<Search>,
                             private val context: Context,
                             val viewModel: SearchMoviesViewModel) : RecyclerView.Adapter<SearchMovieListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return searchs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(searchs[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(search: Search) {
            itemView.tvTitle.text = "${search.Title} (${search.Year})"
            Picasso.get().load(search.Poster).placeholder(R.drawable.no_image).into(itemView.movieImage);
            itemView.setOnClickListener { v ->
                GlobalScope.launch {
                    viewModel.selectMovie(search.imdbID)
                }
            }
        }
    }

}