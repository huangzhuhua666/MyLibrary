package com.example.hzh.base.util

import android.content.Context
import android.telephony.TelephonyManager
import com.example.hzh.base.Global

/**
 * Create by hzh on 2024/3/14.
 *
 * 手机系统相关工具类
 */
object SystemUtils {

    /**
     * 获取手机 deviceId
     */
    fun getDeviceId(): String {
        try {
            return Global.getSystemService<TelephonyManager>(Context.TELEPHONY_SERVICE)?.deviceId ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }
}