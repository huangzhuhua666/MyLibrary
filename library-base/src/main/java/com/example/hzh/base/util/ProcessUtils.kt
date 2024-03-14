package com.example.hzh.base.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import com.example.hzh.base.Global
import java.io.BufferedReader
import java.io.FileReader

/**
 * Create by hzh on 2024/3/14.
 */
object ProcessUtils {

    @Volatile
    private var sProcessName: String? = null

    fun getProcessImportance(): Int {
        try {
            (Global.getApplication().getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
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

    fun getPidByName(processName: String): Int {
        try {
            return Global.getSystemService<ActivityManager>(Context.ACTIVITY_SERVICE)
                ?.runningAppProcesses?.find {
                    it.processName == processName
                }?.pid ?: -1
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return -1
    }

    fun isMainProcess() = Global.getPackageName() == getCurProcessName()

    fun getCurProcessName(): String {
        sProcessName?.let {
            return it
        }

        val pid = Process.myPid()
        var reader: BufferedReader? = null

        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (processName.trim().isNotEmpty()) {
                processName = processName.trim()
            }

            sProcessName = processName
            return processName
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            reader?.closeSafely()
        }

        return Global.getApplication().packageName
    }
}