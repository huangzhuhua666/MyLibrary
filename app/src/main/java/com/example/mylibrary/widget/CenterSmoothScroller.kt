package com.example.mylibrary.widget

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

/**
 * Create by hzh on 4/9/21.
 */
class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int = boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)

    override fun calculateSpeedPerPixel(
        displayMetrics: DisplayMetrics?
    ): Float = 50f / displayMetrics!!.densityDpi
}