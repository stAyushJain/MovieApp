package com.test.pocketaces.di.base

import android.app.Application

object BaseInjector {

    private lateinit var baseComponent: BaseComponent

    fun getBaseComponent(application: Application): BaseComponent {

        baseComponent = if (::baseComponent.isInitialized ) baseComponent else DaggerBaseComponent.builder()
                .baseModule(BaseModule(application)).build()
        return baseComponent
    }
}