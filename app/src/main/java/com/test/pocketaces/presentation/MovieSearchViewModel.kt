package com.test.pocketaces.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.pocketaces.presentation.model.MovieResponseDomainModel
import com.test.pocketaces.ui.model.MovieSearchUIModel
import com.test.pocketaces.ui.model.SearchItemsUIModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MovieSearchViewModel @Inject constructor(
        private val movieSearchUseCase: MovieSearchUseCase,
        private val mapper: MovieSearchUIMapper
): ViewModel() {
    private val searchListResultLiveData = MutableLiveData<MovieSearchUIModel>()
    var searchDisposable: CompositeDisposable = CompositeDisposable()
    fun getSearchListResultLiveData(): LiveData<MovieSearchUIModel> = searchListResultLiveData

    private var pageCount = 1
    var searchedQuery: String? = null
    var isLoading: Boolean = false
    var isLastPage: Boolean = false
    val searchViewSubject: PublishSubject<String> = PublishSubject.create<String>()

    init {
        attachQueryChangeListener()
    }

    fun searchMovie() {
        if (!searchedQuery.isNullOrEmpty())
            searchDisposable.add(movieSearchUseCase.searchMovie(searchedQuery.orEmpty(), pageCount)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        isLoading = true
                    }
                    .doFinally {
                        isLoading = false
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                onSuccess(result, true)
                            }, {
                        handleError(it.message, true)
                    }
                    ))
    }

    private fun attachQueryChangeListener() {
        searchDisposable.add(
                searchViewSubject.debounce(300, TimeUnit.MICROSECONDS)
                        .filter {
//                    Log.d("result", " inside filter")
                            it.length > 2
                        }
                        .distinctUntilChanged()
                        .switchMap {
//                    Log.d("result", " inside switchMap")
                            pageCount = 1
                            isLoading = true
                            searchedQuery = it
                            movieSearchUseCase.searchMovie(it, pageCount)
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->
//                        Log.d("result", " subscribe result")
                                    isLoading = false
                                    onSuccess(result)
                                }, {
//                        Log.d("result", " subscribe error")
                            isLoading = false
                            handleError(it.message)
                        }
                        ))
    }

    private fun onSuccess(
            result: Result<MovieResponseDomainModel>?,
            isPaginating: Boolean = false
    ) {
        when (result) {
            is Result.Success -> {
//                Log.d("result", "${result.data?.search.toString()}")
                result.data?.let {
                    handleSearchResult(it, isPaginating)
                } ?: apply {
                    handleEmptyPageState(isPaginating)
                }
                pageCount++
            }
            is Result.Error -> {
                handleError(result.message, isPaginating)
            }
        }
    }

    private fun handleError(message: String?, isPaginating: Boolean = false) {
        if (!isPaginating)
            searchListResultLiveData.value = MovieSearchUIModel(emptyList(), message)
    }

    private fun handleEmptyPageState(isPaginating: Boolean) {
        if (!isPaginating)
            searchListResultLiveData.value = MovieSearchUIModel(emptyList())
    }

    private fun handleSearchResult(movieResponseDomainModel: MovieResponseDomainModel, isPaginating: Boolean = false) {
        val searchedItems = mapper.getMoviesSearchUIModel(movieResponseDomainModel)
        val movieSearchUIModel = if (pageCount == 1) {
            MovieSearchUIModel(searchedItems)
        } else {
            MovieSearchUIModel(searchListResultLiveData.value?.let { existingItem ->
                mutableListOf<SearchItemsUIModel>().apply {
                    addAll(existingItem.searchList)
                    addAll(searchedItems)
                }
            } ?: searchedItems,
            isPaginating = isPaginating)
        }
        isLastPage = movieSearchUIModel.searchList.size == movieResponseDomainModel.totalResults
//        Log.d("result", " is last page ${movieSearchUIModel.searchList.size == movieResponseDomainModel.totalResults}")
//        Log.d("result", " page $pageCount of ${movieResponseDomainModel.totalResults}")
        searchListResultLiveData.value = movieSearchUIModel
    }

    override fun onCleared() {
        searchDisposable.dispose()
        super.onCleared()
    }
}