package com.test.pocketaces.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.pocketaces.di.base.ViewModelFactory
import com.test.pocketaces.di.base.ViewModelKey
import com.test.pocketaces.presentation.MovieSearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MovieSearchViewModel::class)
    internal abstract fun postListViewModel(viewModel: MovieSearchViewModel): ViewModel
}