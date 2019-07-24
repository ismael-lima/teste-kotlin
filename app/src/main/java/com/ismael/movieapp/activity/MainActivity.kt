package com.ismael.movieapp.activity


import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ismael.movieapp.R
import com.ismael.movieapp.customview.MyToolbar
import com.ismael.movieapp.model.Movie
import com.ismael.movieapp.fragments.MovieDetailFragment
import com.ismael.movieapp.fragments.MyMoviesFragment
import com.ismael.movieapp.injection.AndroidApplication
import com.ismael.movieapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_item_list.*
import org.jetbrains.anko.design.snackbar

import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import javax.inject.Inject



public class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var mainViewModel: MainViewModel
    private val SEARCH_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as AndroidApplication).component.inject(this)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                mainViewModel.getMovies(query)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        mainViewModel.getCurrentFragmentIndex().observe(this, Observer<Int>{ index ->
            setActiveFragment(index);
        })

        mainViewModel.isSearching().observe(this, Observer<Boolean>{ isSearching ->
            if(isSearching)
            {
                mainViewModel.searched()
                startActivityForResult(intentFor<SearchMoviesActivity>().singleTop(), SEARCH_CODE)
            }
        })
        mainViewModel.isClosing().observe(this, Observer<Boolean>{ closing ->
            if(closing)
            {
                finish()
            }
        })

        mainViewModel.getSnackbarMessage().observe(this, Observer<String>{ msg ->
            if(!msg.equals("")) {
                (toolbar as View).snackbar(msg)
                mainViewModel.messageDone()
            }
        })
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { mainViewModel.myMovies() }
    }

    fun setActiveFragment(index: Int)
    {
        var fragment : Fragment?

        when(index){
            1 -> {
                mainViewModel.getDetailMovie()?.value?.let {
                    toolbar.title = "${it.Title} (${it.Year})"
                    toolbar.setTitleSize(MyToolbar.TitleSize.SMALL)}
                fragment = MovieDetailFragment()
            }
            else -> {
                fragment = MyMoviesFragment()
                toolbar.setTitle(R.string.title_my_movies)
                toolbar.setTitleSize(MyToolbar.TitleSize.NORMAL)
            }
        }
        invalidateOptionsMenu();
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.content,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        mainViewModel.goBack()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_movies, menu)

        for(i in 0 until menu.size())
        {
            var mi = menu.get(i)

            if(mi.itemId == R.id.mi_save)
                mi.setVisible((mainViewModel.getCurrentFragmentIndex().value!=0)&&(mainViewModel.isInserting().value?:false))
            else if(mi.itemId == R.id.mi_delete)
                mi.setVisible(mainViewModel.getCurrentFragmentIndex().value!=0)
            else {
                if(mainViewModel.getCurrentFragmentIndex().value==0)
                {
                    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
                    (mi.actionView as SearchView).apply {
                        setSearchableInfo(searchManager.getSearchableInfo(componentName))
                        setIconifiedByDefault(false)
                        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String): Boolean {
                                mainViewModel.getMovies(query)
                                return true
                            }

                            override fun onQueryTextChange(newText: String): Boolean {
                                mainViewModel.getMovies(newText)
                                return true
                            }
                        })
                    }

                }
                else
                    mi.setVisible(false)
            }
        }

        if(mainViewModel.getCurrentFragmentIndex().value==0)
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(false);
        else
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when (item.itemId) {
            R.id.mi_delete ->  {
                mainViewModel.deleteMovie()
                return true
            }
            R.id.mi_save -> {
                mainViewModel.saveMovie()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode == SEARCH_CODE) && (resultCode == Activity.RESULT_OK))
        {
            mainViewModel.InsertMovie()
        }
    }

    override fun onDestroy() {
        mainViewModel.closed()
        super.onDestroy()
    }
}
