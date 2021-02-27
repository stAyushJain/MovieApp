package com.test.pocketaces.ui.model

data class SearchItemsUIModel(
    val name: String,
    val imageUrl: String,
    val description: String,
    val releasedDate: String,
    val rating: String
) {
    var showLoadingState: Boolean = false
    constructor(showLoadingState: Boolean):  this("", "", "", "", "" ) { this.showLoadingState = showLoadingState }
}

data class MovieSearchUIModel(
    val searchList: List<SearchItemsUIModel> = emptyList(),
    val displayMessage: String? = null,
    val isPaginating: Boolean = false
)