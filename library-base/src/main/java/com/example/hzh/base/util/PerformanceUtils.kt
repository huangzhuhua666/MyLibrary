package com.example.hzh.base.util

import android.app.ActivityManager
import android.content.Context
import com.example.hzh.base.Global
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * Create by hzh on 2024/3/14.
 */
object PerformanceUtils {

    private const val BUFFER_SIZE = 8 * 1024
    private const val MEMORY_READ_FILE_NAME = "/proc/meminfo"

    private var sTotalMemo = 0L

    fun getFreeMemory(): Long {
        Global.getSystemService<ActivityManager>(Context.ACTIVITY_SERVICE)?.let {
            val memoryInfo = ActivityManager.MemoryInfo()
            it.getMemoryInfo(memoryInfo)
            return memoryInfo.availMem / 1024
        }

        return 0L
    }

    fun getTotalMemory(): Long {
        if (sTotalMemo == 0L) {
            var reader: BufferedReader? = null
            var initMemory = 0L

            try {
                reader = BufferedReader(FileReader(MEMORY_READ_FILE_NAME), BUFFER_SIZE)
                reader.readLine()?.split("\\s+")?.getOrNull(1)?.let {
                    initMemory = it.toLong()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                reader?.closeSafely()
            }

            sTotalMemo = initMemory
        }

        return sTotalMemo
    }
}