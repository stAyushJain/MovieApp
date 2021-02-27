package com.test.pocketaces.presentation

import com.test.pocketaces.presentation.model.MovieResponseDomainModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface MovieSearchUseCase {
    fun searchMovie(query: String, page: Int): Observable<Result<MovieResponseDomainModel>>
}