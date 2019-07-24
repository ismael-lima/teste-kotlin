package com.ismael.movieapp.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismael.movieapp.R
import com.ismael.movieapp.adapters.SearchMovieListAdapter
import com.ismael.movieapp.injection.AndroidApplication
import com.ismael.movieapp.model.Search
import com.ismael.movieapp.viewmodel.SearchMoviesViewModel
import kotlinx.android.synthetic.main.activity_search_movies.*
import kotlinx.android.synthetic.main.fragment_my_movies.rvMovieList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.snackbar
import javax.inject.Inject



class SearchMoviesActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var searchMoviesViewModel: SearchMoviesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movies)
        (application as AndroidApplication).component.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            searchMoviesViewModel.goBack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        searchMoviesViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchMoviesViewModel::class.java)

        searchMoviesViewModel.getSearchMovies().observe(this, Observer<List<Search>>{ searchs ->
            rvMovieList.adapter = SearchMovieListAdapter(searchs?:ArrayList<Search>(), this, searchMoviesViewModel)
        })
        searchMoviesViewModel.isMovieSelected().observe(this, Observer<Boolean>{ selected ->
            if(selected) {
                setResult(Activity.RESULT_OK)
            }
        })
        searchMoviesViewModel.isGoingBack().observe(this, Observer<Boolean>{ goBack ->
            if(goBack) {
                finish()
            }
        })
        searchMoviesViewModel.isLoading().observe(this, Observer<Boolean>{ loading ->
            if(loading)
                progressBar.visibility = View.VISIBLE
            else
                progressBar.visibility = View.GONE
        })

        searchMoviesViewModel.getSnackbarMessage().observe(this, Observer<String>{ msg ->
            if(!msg.equals("")) {
                val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(edtPesquisa.windowToken, 0)
                (edtPesquisa as View).snackbar(msg)
                searchMoviesViewModel.messageDone()
            }
        })
        edtPesquisa.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable?) {
                GlobalScope.launch {
                    var start: String = edit?.toString()?: "";
                    delay(2000)
                    if((edit?.toString()?: "").equals(start)) //debounce
                        searchMoviesViewModel.searchByTitle(edit?.toString() ?: "")
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        rvMovieList.layoutManager = LinearLayoutManager(this)
        rvMovieList.adapter = SearchMovieListAdapter(ArrayList<Search>(), this, searchMoviesViewModel)
    }

    override fun onDestroy() {
        searchMoviesViewModel.close()
        super.onDestroy()
    }
}
