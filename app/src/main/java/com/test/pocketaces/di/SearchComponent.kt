package com.test.pocketaces.di

import com.test.pocketaces.ui.SearchActivity
import com.test.pocketaces.di.base.BaseComponent
import dagger.Component

@FeatureScope
@Component(modules = [SearchModule::class, ViewModelModule::class], dependencies = [BaseComponent::class])
interface SearchComponent {
    fun inject(searchActivity: SearchActivity)
}