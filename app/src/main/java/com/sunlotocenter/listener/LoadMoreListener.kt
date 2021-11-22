package com.sunlotocenter.listener

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Hani AlMomani on 28,November,2019
 */

internal class LoadMoreListener(layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    companion object{
        val SIZE_PER_PAGE= 20
    }

    private var visibleThreshold = SIZE_PER_PAGE
    private lateinit var mOnLoadMoreListener: OnLoadMoreListener
    private var isLoading: Boolean = false
    private var isFinish: Boolean = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var mLayoutManager: LinearLayoutManager = layoutManager

    fun setLoaded() {
        isLoading = false
    }

    fun setFinished(finished:Boolean= true) {
        isFinish = finished
    }


    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy <= 0) return

        totalItemCount = mLayoutManager.itemCount
        lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()

        if (!isFinish && !isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
            mOnLoadMoreListener.onLoadMore()
            isLoading = true
        }

    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }
}