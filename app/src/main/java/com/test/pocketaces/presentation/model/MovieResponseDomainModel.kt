package com.test.pocketaces.presentation.model

data class MovieResponseDomainModel(
    val search: List<SearchDomainModel>,
    val totalResults: Int
)