package com.test.pocketaces.ui

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.test.pocketaces.R
import com.test.pocketaces.di.DaggerSearchComponent
import com.test.pocketaces.di.base.BaseInjector
import com.test.pocketaces.di.base.ViewModelFactory
import com.test.pocketaces.presentation.MovieSearchViewModel
import org.json.JSONArray
import java.util.LinkedHashSet
import javax.inject.Inject


class SearchActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var searchList: RecyclerView
    private lateinit var noResultState: LinearLayout
    private lateinit var noResultStateText: AppCompatTextView
    private lateinit var recentGroup: ChipGroup
    private lateinit var searchView: SearchView
    private lateinit var noListMessage: AppCompatTextView
    private lateinit var searchListAdapter: MovieSearchAdapter
    private lateinit var movieSearchViewModel: MovieSearchViewModel
    private lateinit var movieSearchLayoutManager: LinearLayoutManager

    private var recentSearchSet: LinkedHashSet<String> = LinkedHashSet<String>()
    private var isNewRecentQueryAdded: Boolean = false
    private val MAX_CACHED_ITEM = 10
    private val POCKET_ACES_PREFS_FILE = "POCKET_ACES_PREFS_FILE"
    private val POCKET_ACES_RECENT_KEY = "POCKET_ACES_RECENT_KEY"
    private val movieSearchPreference: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
        getSharedPreferences(POCKET_ACES_PREFS_FILE, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchComponent = DaggerSearchComponent.builder().baseComponent(BaseInjector.getBaseComponent(application)).build()
        searchComponent.inject(this)
        initViews()

        searchListAdapter = MovieSearchAdapter()
        movieSearchViewModel = ViewModelProvider(this, viewModelFactory).get(MovieSearchViewModel::class.java)

        initSearchList(searchList)
        searchList.addOnScrollListener(recyclerViewOnScrollListener)
        addSearchResultObserver()
        updateRecentQuerySetFromPreference()
    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if(newState == RecyclerView.SCROLL_STATE_DRAGGING) hideKeyBoard(recyclerView)
                    super.onScrollStateChanged(recyclerView, newState)
                }
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount: Int = movieSearchLayoutManager.childCount
                    val totalItemCount: Int = movieSearchLayoutManager.itemCount
                    val firstVisibleItemPosition: Int = movieSearchLayoutManager.findFirstVisibleItemPosition()
                    if (!movieSearchViewModel.isLoading && !movieSearchViewModel.isLastPage) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            searchListAdapter.showLoaderAtLast()
                            movieSearchViewModel.searchMovie()
                        }
                    }
                }
            }

    private fun initViews() {
        searchList = findViewById(R.id.rv_search_list)
        noResultState = findViewById(R.id.ll_no_list_state)
        noResultStateText = findViewById(R.id.tv_no_list_message)
        recentGroup = findViewById(R.id.chip_group_recent)
        noListMessage = findViewById(R.id.tv_no_list_message)
        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolbar)
    }

    private fun addSearchResultObserver() {
        movieSearchViewModel.getSearchListResultLiveData().observe(this, Observer {
            recentGroup.visibility = View.GONE
            searchListAdapter.updateList(it.searchList, it.isPaginating)
            noResultState.visibility = if (it.searchList.isNotEmpty()) View.GONE else View.VISIBLE
            noListMessage.text = it.displayMessage
        })
    }

    private fun initSearchList(searchList: RecyclerView) {
        searchList.apply {
            movieSearchLayoutManager = LinearLayoutManager(this@SearchActivity)
            layoutManager = movieSearchLayoutManager
            adapter = searchListAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.set(0,0,0,10)
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        storeRecentSearchedInPreference()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.movie_search, menu)

        menu?.findItem(R.id.action_search)?.let { searchItem ->
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = (searchItem.actionView as SearchView).apply {
                isIconified = false
                setSearchableInfo(searchManager.getSearchableInfo(componentName))
                addQueryChangeListener(this)
                queryHint = resources.getString(R.string.search_movies)
                searchItem.expandActionView()
                if (movieSearchViewModel.searchedQuery != null) setQuery(movieSearchViewModel.searchedQuery, true)
                clearFocus()
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun addQueryChangeListener(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (query.trim().length > 2) {
                        isNewRecentQueryAdded = true
                        addQueryInRecentSet(it)
                    }
                    else Toast.makeText(this@SearchActivity, resources.getString(R.string.min_query_length), Toast.LENGTH_SHORT).show()
                }
                hideKeyBoard(view = searchView)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.trim().length > 2) {
                    movieSearchViewModel.searchViewSubject.onNext(newText.trim())
                }
                return true
            }
        })
    }

    private fun addQueryInRecentSet(query: String) {
        if (recentSearchSet.size == MAX_CACHED_ITEM) {
            recentSearchSet.remove(recentSearchSet.iterator().next())
        }
        recentSearchSet.add(query)
    }

    private fun storeRecentSearchedInPreference() {
        if (recentSearchSet.isNotEmpty() && isNewRecentQueryAdded) {
            val editor: SharedPreferences.Editor = movieSearchPreference.edit()
            val jsonArray = JSONArray(recentSearchSet)
            Log.d("result store", jsonArray.toString())
            editor.putString(POCKET_ACES_RECENT_KEY, jsonArray.toString())
            editor.apply()
        }
    }

    private fun updateRecentQuerySetFromPreference() {
        recentSearchSet.clear()
        val savedRecentQuery = movieSearchPreference.getString(POCKET_ACES_RECENT_KEY, "")
        if (!savedRecentQuery.isNullOrEmpty()) {
            val jsonRecentQuery = JSONArray(savedRecentQuery)
            Log.d("result fetch", jsonRecentQuery.toString())
            for (index in 0 until jsonRecentQuery.length()) {
                recentSearchSet.add(jsonRecentQuery.getString(index))
            }
            if (recentSearchSet.isNotEmpty()) addRecentChips()
        }
    }

    private fun addRecentChips() {
        val setIterator = recentSearchSet.iterator()
        while (setIterator.hasNext())
        {
            val query = setIterator.next()
            val mChip = layoutInflater.inflate(R.layout.item_recent_chips, null, false) as Chip
            mChip.id = ViewCompat.generateViewId()
            mChip.text = query
            mChip.setOnCheckedChangeListener { _, _ ->
                searchView.setQuery(query, true)
                movieSearchViewModel.searchViewSubject.onNext(query.trim())
            }
            recentGroup.addView(mChip)
        }
    }

    private fun hideKeyBoard(view: View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}