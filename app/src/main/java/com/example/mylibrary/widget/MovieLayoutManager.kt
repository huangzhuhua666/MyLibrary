package com.example.mylibrary.widget

import android.util.Log
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView

/**
 * Create by hzh on 4/9/21.
 */
class MovieLayoutManager : RecyclerView.LayoutManager() {

    companion object {

        private const val TAG = "MovieLayoutManager"
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (itemCount < 1) return
        if (state?.isPreLayout == true) return

        detachAndScrapAttachedViews(recycler!!) // 分离视图放入scrap缓存中，准备对view进行排版

        var index = 0
        var left = paddingStart
        while (true) {
            recycler.getViewForPosition(index % itemCount).let { itemView ->
                left += itemView.marginStart

                if (left >= width - paddingEnd) return // itemView的left超过RecyclerView宽度就停止

                addView(itemView) // 添加子view
                measureChildWithMargins(itemView, 0, 0) // 测量子view

                val top = paddingTop + itemView.marginTop
                val right = left + getDecoratedMeasuredWidth(itemView)
                val bottom = top + getDecoratedMeasuredHeight(itemView) - paddingBottom

                layoutDecorated(itemView, left, top, right, bottom) // 摆放子view

                Log.d(TAG, "laid out child at position $index, with left:$left, top:$top, right:$right, bottom:$bottom")

                left = right + itemView.marginEnd // 下个子view的left
            }

            ++index
        }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        recycler?.let {
            if (dx > 0) fillEnd(it) // 左滑，填充右边的空间
            else fillStart(it) // 右滑，填充左边的空间
        }

        offsetChildrenHorizontal(-dx)

        recycler?.let {
            if (dx > 0) recyclerStart(recycler) // 左滑，回收左边的子view
            else recycleEnd(recycler) // 右滑，回收右边的子view
        }

        return dx
    }

    /**
     * 填充右边
     */
    private fun fillEnd(recycler: RecyclerView.Recycler) {
        if (childCount == 0) return

        var anchorView = getChildAt(childCount - 1) ?: return
        val anchorPosition = getPosition(anchorView)

        while (anchorView.right < width - paddingEnd) {
            var position = (anchorPosition + 1) % itemCount
            if (position < 0) position += itemCount

            val scrapItem = recycler.getViewForPosition(position)
            addView(scrapItem)
            measureChildWithMargins(scrapItem, 0, 0)

            val left = anchorView.right + anchorView.marginEnd + scrapItem.marginStart
            val top = paddingTop + scrapItem.marginTop
            val right = left + getDecoratedMeasuredWidth(scrapItem)
            val bottom = top + getDecoratedMeasuredHeight(scrapItem) - paddingBottom

            layoutDecorated(scrapItem, left, top, right, bottom)

            Log.d(TAG, "fill end with position $position, with left:$left, top:$top, right:$right, bottom:$bottom")

            anchorView = scrapItem
        }
    }

    /**
     * 填充左边
     */
    private fun fillStart(recycler: RecyclerView.Recycler) {
        if (childCount == 0) return

        var anchorView = getChildAt(0) ?: return
        val anchorPosition = getPosition(anchorView)

        while (anchorView.left > paddingStart) {
            var position = (anchorPosition - 1) % itemCount
            if (position < 0) position += itemCount

            val scrapItem = recycler.getViewForPosition(position)
            addView(scrapItem, 0)
            measureChildWithMargins(scrapItem, 0, 0)

            val right = anchorView.left - anchorView.marginStart - scrapItem.marginEnd
            val left = right - getDecoratedMeasuredWidth(scrapItem)
            val top = paddingTop + scrapItem.marginTop
            val bottom = top + getDecoratedMeasuredHeight(scrapItem) - paddingBottom

            layoutDecorated(scrapItem, left, top, right, bottom)

            Log.d(TAG, "fill start with position $position, with left:$left, top:$top, right:$right, bottom:$bottom")

            anchorView = scrapItem
        }
    }

    /**
     * 回收右边的子view
     */
    private fun recycleEnd(recycler: RecyclerView.Recycler) {
        var position = childCount - 1

        while (true) {
            getChildAt(position)?.let {
                if (it.left > width - paddingEnd) removeAndRecycleView(it, recycler)
            } ?: return

            --position
        }
    }

    /**
     * 回收左边的子view
     */
    private fun recyclerStart(recycler: RecyclerView.Recycler) {
        var position = 0

        while (true) {
            getChildAt(position)?.let {
                if (it.right < paddingStart) removeAndRecycleView(it, recycler)
            } ?: return

            ++position
        }
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        startSmoothScroll(CenterSmoothScroller(recyclerView!!.context).apply {
            targetPosition = position
        })
    }
}