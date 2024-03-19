package com.example.hzh.base.util

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import com.example.hzh.base.Global
import kotlin.math.abs

/**
 * Create by hzh on 2024/3/13.
 */
object ResourcesUtils {

    fun dp2px(dp: Number): Int {
        val scale = Global.getApplication().resources.displayMetrics.density
        val dpInDouble = dp.toDouble()
        return if (dp.toDouble() < 0) {
            -(abs(dpInDouble) * scale + 0.5f).toInt()
        } else {
            (dpInDouble * scale + 0.5f).toInt()
        }
    }

    /**
     * 从color id 获取color值
     *
     * @param resId 资源文件中定义的颜色id
     */
    fun getColor(resId: Int): Int {
        return getColor(Global.getApplication(), resId)
    }

    /**
     * 从color id 获取color值
     *
     * @param context 用于获取主题, 如果传入null, 则使用App默认主题
     * @param resId 资源文件中定义的颜色id
     */
    fun getColor(context: Context?, resId: Int): Int {
        val realContext = (context ?: Global.getApplication()
        return if (Build.VERSION.SDK_INT >= 23) {
            realContext.resources.getColor(resId, realContext.theme)
        } else {
            realContext.resources.getColor(resId)
        }
    }

    /**
     * string id 获取color值
     *
     * @param resId 资源文件中定义的颜色id
     * @param context 用于获取主题, 如果传入null, 则使用App默认主题
     */
    fun getString(resId: Int, context: Context? = null): String {
        return (context ?: Global.getApplication()).resources.getString(resId)
    }

    /**
     * 获取String资源对应的字符串
     */
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return Global.getApplication().resources.getString(resId, *formatArgs) ?: ""
    }
}