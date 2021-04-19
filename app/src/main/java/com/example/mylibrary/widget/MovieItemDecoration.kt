package com.example.mylibrary.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.hzh.base.util.dp2px
import com.example.hzh.base.util.getScreenWidth

/**
 * Create by hzh on 4/9/21.
 */
class MovieItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    private val mOriginMargin by lazy { 5f.dp2px(context).toInt() }
    private val mItemWidth by lazy { 100f.dp2px(context).toInt() }
    private val mScreenWidth by lazy { context.getScreenWidth() }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        val centerMargin = (mScreenWidth - mItemWidth) / 2

        val startMargin = if (position == 0) centerMargin else mOriginMargin
        val endMargin = if (position == itemCount - 1) centerMargin else mOriginMargin

        view.layoutParams = (view.layoutParams as RecyclerView.LayoutParams).apply {
            marginStart = startMargin
            marginEnd = endMargin
        }

        super.getItemOffsets(outRect, view, parent, state)
    }
}