package com.test.pocketaces.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.test.pocketaces.R
import com.test.pocketaces.ui.model.SearchItemsUIModel

class MovieSearchAdapter: RecyclerView.Adapter<MovieSearchAdapter.MovieSearchViewHolder>() {
    private var movieSearchList: MutableList<SearchItemsUIModel>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSearchViewHolder {
        val view = if (viewType == R.layout.item_progress) LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
        else LayoutInflater.from(parent.context).inflate(R.layout.item_movie_search, parent, false)
        return MovieSearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movieSearchList?.size ?: 0
    }

    override fun onBindViewHolder(holder: MovieSearchViewHolder, position: Int) {
        if (movieSearchList?.get(position)?.showLoadingState == false)
            holder.bindItem()
    }

    override fun getItemViewType(position: Int): Int {
        return if (movieSearchList?.get(position)?.showLoadingState == true) R.layout.item_progress
        else super.getItemViewType(position)
    }

    fun updateList(moviesSearchList: List<SearchItemsUIModel>, isPaginating: Boolean = false) {
        hideLoader()
        val oldSize = movieSearchList?.size
        movieSearchList = moviesSearchList.toMutableList()
        if (isPaginating && oldSize != null) notifyItemRangeInserted(oldSize + 1, movieSearchList?.size ?: 0)
        else notifyDataSetChanged()

    }

    private fun hideLoader() {
        if (movieSearchList?.isNullOrEmpty() == false) {
            if (movieSearchList?.get(movieSearchList!!.size - 1)?.showLoadingState == true) movieSearchList?.removeAt(movieSearchList!!.size - 1)
            notifyItemRemoved(movieSearchList!!.size - 1)
        }
    }

    fun showLoaderAtLast() {
        movieSearchList?.let {
            movieSearchList!!.add(SearchItemsUIModel(showLoadingState = true))
            notifyItemInserted(movieSearchList!!.size - 1)
        }
    }

    inner class MovieSearchViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val ivPoster = view.findViewById<AppCompatImageView>(R.id.iv_poster)
        private val name = view.findViewById<AppCompatTextView>(R.id.tv_name)
        private val description = view.findViewById<AppCompatTextView>(R.id.tv_description)
        private val releaseDate = view.findViewById<AppCompatTextView>(R.id.tv_release_date)
        private val rating = view.findViewById<AppCompatTextView>(R.id.tv_rating)
        fun bindItem() {
            movieSearchList?.get(adapterPosition)?.let { item ->
                name.text = item.name
                description.text = item.description
                releaseDate.text = item.releasedDate
                rating.text = item.rating
                Glide.with(ivPoster)
                        .load(item.imageUrl)
                        .transform(CenterCrop(), RoundedCorners( 10))
                        .placeholder(R.drawable.ic_baseline_fireplace_24)
                        .error(R.drawable.ic_baseline_fireplace_24)
                        .into(ivPoster)
            }
        }
    }
}