package com.example.hzh.ui.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Create by hzh on 2020/3/23.
 *
 * 可以简易地创建RecyclerView Decoration
 */
abstract class SimpleItemDecoration : RecyclerView.ItemDecoration() {

    private val decorations by lazy { mutableMapOf<Int, Decoration?>() }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = if (parent.layoutManager is StaggeredGridLayoutManager)
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex
        else parent.getChildAdapterPosition(view)
        view.tag = position

        getItemDecoration(position).let {
            decorations[position] = it

            if (it != null) outRect.set(it.left, it.top, it.right, it.bottom)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent[i]

            decorations[child.tag as Int]?.let {
                val params = child.layoutParams as RecyclerView.LayoutParams

                val left = child.left - params.marginStart
                val top = child.top - params.topMargin
                val right = child.right + params.marginEnd
                val bottom = child.bottom + params.bottomMargin

                // 左边
                it.drawItemWithOffset(c, left - it.left, top, left, bottom)

                // 上边
                it.drawItemWithOffset(c, left - it.left, top - it.top, right + it.right, top)

                // 右边
                it.drawItemWithOffset(c, right, top, right + it.right, bottom)

                // 下边
                it.drawItemWithOffset(
                    c,
                    left - it.left,
                    bottom,
                    right + it.right,
                    bottom + it.bottom
                )
            }
        }
    }

    abstract fun getItemDecoration(position: Int): Decoration?

    abstract class Decoration {

        var left: Int = 0
        var top: Int = 0
        var right: Int = 0
        var bottom: Int = 0

        abstract fun drawItemWithOffset(c: Canvas, left: Int, top: Int, right: Int, bottom: Int)
    }

    class ColorDecoration : Decoration() {

        private val mPaint by lazy {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
            }
        }

        var color: Int = Color.BLACK

        override fun drawItemWithOffset(c: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
            mPaint.color = color
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        }
    }
}

fun RecyclerView.addItemDecoration(action: (Int) -> SimpleItemDecoration.Decoration?) =
    addItemDecoration(object : SimpleItemDecoration() {
        override fun getItemDecoration(position: Int): Decoration? {
            return action.invoke(position)
        }
    })
