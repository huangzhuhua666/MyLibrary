package com.example.hzh.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Create by hzh on 2019/09/10.
 *
 * 可以设置能否滑动的ViewPager
 */
@Suppress("ClickableViewAccessibility")
class MyViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    var isScroll = true

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean =
        isScroll && super.onInterceptTouchEvent(ev)

    override fun onTouchEvent(ev: MotionEvent?): Boolean = !isScroll || super.onTouchEvent(ev)
}