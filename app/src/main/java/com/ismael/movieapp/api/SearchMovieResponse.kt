package com.ismael.movieapp.api

import com.ismael.movieapp.model.Search

class SearchMovieResponse(
    var Search :List<Search>,
    var totalResults: Int,
    var Response : Boolean
)