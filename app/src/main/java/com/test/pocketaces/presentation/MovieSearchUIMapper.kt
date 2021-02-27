package com.test.pocketaces.presentation

import com.test.pocketaces.presentation.model.MovieResponseDomainModel
import com.test.pocketaces.ui.model.SearchItemsUIModel
import javax.inject.Inject

class MovieSearchUIMapper @Inject constructor() {
    fun getMoviesSearchUIModel(item: MovieResponseDomainModel): List<SearchItemsUIModel> {
        val searchedItems = mutableListOf<SearchItemsUIModel>()
        item.search.forEach {
            searchedItems.add(
                SearchItemsUIModel(
                    name = it.title,
                    imageUrl = it.poster,
                    description = it.description,
                    releasedDate = it.year,
                    rating = "Ratings : ${it.rating} / 10"
                )
            )
        }
        return searchedItems
    }
}