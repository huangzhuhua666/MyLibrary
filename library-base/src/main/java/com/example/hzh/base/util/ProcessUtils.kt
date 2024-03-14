package com.example.hzh.base.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import com.example.hzh.base.application.BaseApplication

/**
 * Create by hzh on 2024/3/14.
 */
object ProcessUtils {

    fun getProcessImportance(): Int {
        try {
            (BaseApplication.instance.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
                ?.runningAppProcesses?.find {
                    it.pid == Process.myPid()
                }?.let {
                    return it.importance
                }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return Int.MAX_VALUE
    }
}