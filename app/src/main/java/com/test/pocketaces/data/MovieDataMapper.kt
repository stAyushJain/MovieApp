package com.test.pocketaces.data

import com.test.pocketaces.data.model.MovieResponseDTO
import com.test.pocketaces.presentation.model.MovieResponseDomainModel
import com.test.pocketaces.presentation.model.SearchDomainModel
import javax.inject.Inject

class MovieDataMapper @Inject constructor() {
    fun getMovies(movies: MovieResponseDTO?): MovieResponseDomainModel {
        return movies?.let {
            val searchItems = mutableListOf<SearchDomainModel>()
            it.search?.forEach { safeSearch ->
                searchItems.add(
                    SearchDomainModel(
                        poster = safeSearch.poster.orEmpty(),
                        title = safeSearch.title.orEmpty(),
                        description = "This is ${safeSearch.type} from ${safeSearch.year} and here I am repeating my self that, This is ${safeSearch.type} from ${safeSearch.year} and again This is ${safeSearch.type} from ${safeSearch.year}",
                        year = safeSearch.year.orEmpty()
                    )
                )
            }
            MovieResponseDomainModel(searchItems, it.totalResults?.toIntOrNull() ?: 0)
        } ?: kotlin.run {
            MovieResponseDomainModel(emptyList(), 0)
        }
    }
}