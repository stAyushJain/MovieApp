package com.test.pocketaces.data

import com.test.pocketaces.presentation.MovieSearchUseCase
import com.test.pocketaces.presentation.model.MovieResponseDomainModel
import io.reactivex.rxjava3.core.Single
import com.test.pocketaces.presentation.Result
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class MovieSearchUseCaseImp @Inject constructor(
        private val movieServices: MovieServices,
        private val mapper: MovieDataMapper
): MovieSearchUseCase {
    override fun searchMovie(query: String, page: Int): Observable<Result<MovieResponseDomainModel>> {
        return movieServices.searchMovies(query = query, page = page)
                .map {
                    if (it.isSuccessful) {
                        val responseBody = it.body()
                        if (responseBody?.response?.equals("True", true) == true) { Result.Success(mapper.getMovies(it.body())) }
                        else Result.Error(responseBody?.errorMessage)
                    } else Result.Error<MovieResponseDomainModel>(it.errorBody().toString())
                }.onErrorReturn {
                    val message = if (it is java.net.UnknownHostException) {
                        "Oops!! There is something wrong with your internet connection. Please check again"
                    }
                    else it.message
                    Result.Error(message = message)
                }
    }
}