package com.test.pocketaces.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponseDTO(
    @SerializedName("Response")
    val response: String?,
    @SerializedName("Error")
    val errorMessage: String?,
    @SerializedName("Search")
    val search: List<Search>?,
    @SerializedName("totalResults")
    val totalResults: String?
)