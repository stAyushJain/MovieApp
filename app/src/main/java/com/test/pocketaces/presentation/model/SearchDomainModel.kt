package com.test.pocketaces.presentation.model

import kotlin.random.Random

data class SearchDomainModel(
    val poster: String,
    val title: String,
    val description: String,
    val year: String,
    val rating: Int = Random.nextInt(10)
)