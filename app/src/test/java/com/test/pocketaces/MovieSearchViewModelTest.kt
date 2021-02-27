package com.test.pocketaces

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.test.pocketaces.presentation.MovieSearchUIMapper
import com.test.pocketaces.presentation.MovieSearchUseCase
import com.test.pocketaces.presentation.MovieSearchViewModel
import com.test.pocketaces.presentation.Result
import io.reactivex.rxjava3.core.Observable
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieSearchViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }



    @Mock
    private lateinit var movieSearchUseCase: MovieSearchUseCase

    @Mock
    private lateinit var mapper: MovieSearchUIMapper

    private lateinit var movieSearchViewModel: MovieSearchViewModel
    private val mockList = MockItemsJson()

    @Before
    fun setUp() {
        movieSearchViewModel = MovieSearchViewModel(movieSearchUseCase, mapper)
    }

    @Test
    fun check_list_is_updating() {
        val domainItem = mockList.getMockSearchedItem()
        Mockito.`when`(mapper.getMoviesSearchUIModel(domainItem)).thenReturn(mockList.getMockSearchUIModel())
        Mockito.`when`(movieSearchUseCase.searchMovie(Mockito.anyString(), Mockito.anyInt())).thenReturn(Observable.just(Result.Success(domainItem)))

        movieSearchViewModel.searchViewSubject.onNext("Some Query")

        assert(movieSearchViewModel.getSearchListResultLiveData().getOrAwaitValue().searchList.isNotEmpty())
    }

    @Test
    fun check_page_is_updating_if_error() {
        val domainItem = mockList.getMockSearchedItem()
        Mockito.`when`(mapper.getMoviesSearchUIModel(domainItem)).thenReturn(mockList.getMockSearchUIModel())
        Mockito.`when`(movieSearchUseCase.searchMovie(Mockito.anyString(), Mockito.anyInt())).thenReturn(Observable.just(Result.Error("domainItem")))

        movieSearchViewModel.searchViewSubject.onNext("Some Query")

        assert(movieSearchViewModel.getSearchListResultLiveData().getOrAwaitValue().searchList.isEmpty())
        assert(movieSearchViewModel.getSearchListResultLiveData().getOrAwaitValue().displayMessage?.isNotEmpty() == true)
    }

    @Test
    fun check_pagination_list_is_updating_if_throw_error() {
        val domainItem = mockList.getMockSearchedItem()
        Mockito.`when`(mapper.getMoviesSearchUIModel(domainItem)).thenReturn(mockList.getMockSearchUIModel())
        Mockito.`when`(movieSearchUseCase.searchMovie(Mockito.anyString(), Mockito.anyInt())).thenReturn(Observable.just(Result.Success(domainItem)))

        movieSearchViewModel.searchViewSubject.onNext("Some Query")

        Mockito.`when`(movieSearchUseCase.searchMovie(Mockito.anyString(), Mockito.anyInt())).thenReturn(Observable.just(Result.Error("domainItem")))

        movieSearchViewModel.searchMovie()

        // Result Should be same old list
        assert(movieSearchViewModel.getSearchListResultLiveData().getOrAwaitValue().searchList.isNotEmpty())
    }

    @Test
    fun check_pagination_list_is_updating() {
        val domainItem = mockList.getMockSearchedItem()
        val domainItemPagination = mockList.getMockPaginatedearchedItem()

        Mockito.`when`(mapper.getMoviesSearchUIModel(domainItem)).thenReturn(mockList.getMockSearchUIModel())
        Mockito.`when`(mapper.getMoviesSearchUIModel(domainItemPagination)).thenReturn(mockList.getMockPaginationSearchUIModel())
        Mockito.`when`(movieSearchUseCase.searchMovie(Mockito.anyString(), Mockito.anyInt())).thenReturn(Observable.just(Result.Success(domainItem)))

        movieSearchViewModel.searchViewSubject.onNext("Some Query")

        Mockito.`when`(movieSearchUseCase.searchMovie(Mockito.anyString(), Mockito.anyInt())).thenReturn(Observable.just(Result.Success(domainItemPagination)))

        movieSearchViewModel.searchMovie()

        val value = movieSearchViewModel.getSearchListResultLiveData().getOrAwaitValue().searchList
        assert(value[0].name == "my_poster")
        assert(value[4].name == "New Pagination Result")
    }
}
