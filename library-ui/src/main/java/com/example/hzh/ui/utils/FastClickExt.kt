package com.example.hzh.ui.utils

import android.view.View
import com.example.hzh.ui.R

/**
 * Create by hzh on 2020/5/8.
 *
 * 仿快触
 */

/**
 * 给view添加上次触发点击事件的时间
 */
private var View.triggerLastTime: Long
    get() = if (getTag(R.id.ui_triggerLastTimeKey) != null) getTag(R.id.ui_triggerLastTimeKey) as Long else 0
    set(value) = setTag(R.id.ui_triggerLastTimeKey, value)

/**
 * 给view添加点击事件屏蔽时间间隔
 */
private var View.triggerDelay: Long
    get() = if (getTag(R.id.ui_triggerDelayKey) != null) getTag(R.id.ui_triggerDelayKey) as Long else -1
    set(value) = setTag(R.id.ui_triggerDelayKey, value)

private fun View.isFastClick(): Boolean = System.currentTimeMillis().let {
    val isFastClick = it - triggerLastTime <= triggerDelay
    triggerLastTime = it
    isFastClick
}

/**
 * 防快速点击
 * @param delay 默认600ms间隔
 * @param action
 */
fun View.filterFastClickListener(delay: Long = 600L, action: (View) -> Unit) {
    triggerDelay = delay
    setOnClickListener { if (!isFastClick()) action(this) }
}