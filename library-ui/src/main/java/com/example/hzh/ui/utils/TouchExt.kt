package com.example.hzh.ui.utils

import android.graphics.Point
import android.os.Build
import android.view.MotionEvent
import android.view.View

/**
 * Create by hzh on 2020/09/28.
 */

fun MotionEvent.getRawTouchPoint(rootView: View, pointerIndex: Int, point: Point) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        point.set(getRawX(pointerIndex).toInt(), getRawY(pointerIndex).toInt())
    } else {
        IntArray(2).let {
            rootView.getLocationOnScreen(it)
            point.set(it[0] + getX(pointerIndex).toInt(), it[1] + getY(pointerIndex).toInt())
        }
    }
}