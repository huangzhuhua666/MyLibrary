package com.example.hzh.base.manager.internal

import android.app.Activity
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import com.example.hzh.base.util.PackageUtils
import com.example.hzh.base.util.ReflectUtils

/**
 * Create by hzh on 2024/3/13.
 */
internal object ActivityStateChecker {

    /**
     * Android O 设备上透明 Floating 页面指定方向会崩溃，本方法把崩溃版本从 Android O 扩展到所有系统版本，避免测试覆盖
     * 版本遗漏导致漏侧问题
     */
    fun checkOrientation(activity: Activity) {
        PackageUtils.getActivityInfo(ComponentName(activity, activity.javaClass))?.let {
            if (!isFixOrientation(it.screenOrientation)) {
                return
            }

            val clazz = ReflectUtils.getClass("com.android.internal.R\$styleable") ?: return
            ReflectUtils.getFieldValue<IntArray>(clazz, clazz, "Window")?.let { styleable ->
                val ta = activity.obtainStyledAttributes(styleable)
                val isTranslucentOrFloating = ReflectUtils.invokeObject<Boolean>(
                    ReflectUtils.getMethod(it.javaClass, "isTranslucentOrFloating", TypedArray::class.java),
                    it,
                    ta
                ) ?: false

                if (!isTranslucentOrFloating) {
                    return
                }

                throw IllegalStateException("Only fullscreen opaque activities can request orientation")
            }
        }
    }

    private fun isFixOrientation(orientation: Int): Boolean {
        return when (orientation) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> true
            else -> false
        }
    }
}