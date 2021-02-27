package com.test.pocketaces.di

import com.test.pocketaces.data.MovieSearchUseCaseImp
import com.test.pocketaces.data.MovieServices
import com.test.pocketaces.presentation.MovieSearchUseCase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class SearchModule {

    @Provides
    fun provideMovieApiService(retrofit: Retrofit) = retrofit.create(MovieServices::class.java)

    @Provides
    fun provideRepository(movieSearchUseCaseImp: MovieSearchUseCaseImp): MovieSearchUseCase = movieSearchUseCaseImp
}