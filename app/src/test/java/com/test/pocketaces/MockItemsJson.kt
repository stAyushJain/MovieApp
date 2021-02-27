package com.test.pocketaces

import com.test.pocketaces.presentation.model.MovieResponseDomainModel
import com.test.pocketaces.presentation.model.SearchDomainModel
import com.test.pocketaces.ui.model.MovieSearchUIModel
import com.test.pocketaces.ui.model.SearchItemsUIModel

class MockItemsJson {

    fun getMockSearchedItem(): MovieResponseDomainModel {
        return MovieResponseDomainModel(
                listOf(
                        SearchDomainModel(
                                "my_poster",
                                "Spider man",
                                "This is amazing movie",
                                "2015",
                                10
                        ), SearchDomainModel(
                        "my_poster",
                        "Spider man2",
                        "This is amazing movie",
                        "2015",
                        3
                ), SearchDomainModel(
                        "my_poster",
                        "Spider man3",
                        "This is amazing movie",
                        "2015",
                        2
                ), SearchDomainModel(
                        "my_poster",
                        "Spider ma4",
                        "This is amazing movie",
                        "2015",
                        1
                )
                ), 24
        )
    }

    fun getMockPaginatedearchedItem(): MovieResponseDomainModel {
        return MovieResponseDomainModel(
                listOf(
                        SearchDomainModel(
                                "my_poster",
                                "New Pagination Result",
                                "This is amazing movie",
                                "2015",
                                10
                        ), SearchDomainModel(
                        "my_poster",
                        "New Pagination Result",
                        "This is amazing movie",
                        "2015",
                        3
                ), SearchDomainModel(
                        "my_poster",
                        "New Pagination Result",
                        "This is amazing movie",
                        "2015",
                        2
                ), SearchDomainModel(
                        "my_poster",
                        "New Pagination Result",
                        "This is amazing movie",
                        "2015",
                        1
                )
                ), 24
        )
    }

    fun getEmptyResult(): MovieResponseDomainModel {
        return MovieResponseDomainModel(emptyList(), 0)
    }

    fun getUIModel(): MovieSearchUIModel {
        return MovieSearchUIModel(
                listOf(
                        SearchItemsUIModel(
                                "my_poster",
                                "Spider man",
                                "This is amazing movie",
                                "2015",
                                "10"
                        ), SearchItemsUIModel(
                        "my_poster",
                        "Spider man2",
                        "This is amazing movie",
                        "2015",
                        "3"
                ), SearchItemsUIModel(
                        "my_poster",
                        "Spider man3",
                        "This is amazing movie",
                        "2015",
                        "2"
                ), SearchItemsUIModel(
                        "my_poster",
                        "Spider ma4",
                        "This is amazing movie",
                        "2015",
                        "11"
                )
                ), ""
        )
    }

    fun getMockSearchUIModel() = listOf(
            SearchItemsUIModel(
                    "my_poster",
                    "Spider man",
                    "This is amazing movie",
                    "2015",
                    "10"
            ), SearchItemsUIModel(
            "my_poster",
            "Spider man2",
            "This is amazing movie",
            "2015",
            "3"
    ), SearchItemsUIModel(
            "my_poster",
            "Spider man3",
            "This is amazing movie",
            "2015",
            "2"
    ), SearchItemsUIModel(
            "my_poster",
            "Spider ma4",
            "This is amazing movie",
            "2015",
            "11"
    ))

    fun getMockPaginationSearchUIModel() = listOf(
            SearchItemsUIModel(
                    "New Pagination Result",
                    "Spider man",
                    "This is amazing movie",
                    "2015",
                    "10"
            ), SearchItemsUIModel(
            "New Pagination Result",
            "Spider man2",
            "This is amazing movie",
            "2015",
            "3"
    ), SearchItemsUIModel(
            "New Pagination Result",
            "Spider man3",
            "This is amazing movie",
            "2015",
            "2"
    ), SearchItemsUIModel(
            "New Pagination Result",
            "Spider ma4",
            "This is amazing movie",
            "2015",
            "11"
    ))
}
