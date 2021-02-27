package com.test.pocketaces.di.base

import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [BaseModule::class])
interface BaseComponent {
    fun exposeRetrofit(): Retrofit
}