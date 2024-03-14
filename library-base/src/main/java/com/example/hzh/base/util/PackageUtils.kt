package com.example.hzh.base.util

import android.content.ComponentName
import android.content.pm.ActivityInfo
import com.example.hzh.base.Global

/**
 * Create by hzh on 2024/3/13.
 */
object PackageUtils {

    fun getActivityInfo(componentName: ComponentName?): ActivityInfo? {
        componentName ?: return null

        try {
            return Global.getPackageManager().getActivityInfo(componentName, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}