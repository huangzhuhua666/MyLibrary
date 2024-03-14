package com.example.hzh.base.util

import android.content.Context
import android.os.Build
import com.example.hzh.base.application.BaseApplication
import kotlin.math.abs

/**
 * Create by hzh on 2024/3/13.
 */
object ResourcesUtils {

    fun dp2px(dp: Number): Int {
        val scale = BaseApplication.instance.resources.displayMetrics.density
        val dpInDouble = dp.toDouble()
        return if (dp.toDouble() < 0) {
            -(abs(dpInDouble) * scale + 0.5f).toInt()
        } else {
            (dpInDouble * scale + 0.5f).toInt()
        }
    }

    /**
     * 从color id 获取color值
     * @param resId 资源文件中定义的颜色id
     */
    fun getColor(resId: Int): Int {
        return getColor(BaseApplication.instance, resId)
    }

    /**
     * 从color id 获取color值
     * @param context 用于获取主题, 如果传入null, 则使用App默认主题
     * @param resId 资源文件中定义的颜色id
     */
    fun getColor(context: Context?, resId: Int): Int {
        val realContext = context ?: BaseApplication.instance
        return if (Build.VERSION.SDK_INT >= 23) {
            realContext.resources.getColor(resId, realContext.theme)
        } else {
            realContext.resources.getColor(resId)
        }
    }
}