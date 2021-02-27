package com.test.pocketaces.di.base

import android.app.Application

object BaseInjector {
    private val baseComponent: BaseComponent? = null

    fun getBaseComponent(application: Application): BaseComponent {
        return baseComponent
            ?: DaggerBaseComponent.builder()
                .baseModule(BaseModule(application)).build()
    }
}