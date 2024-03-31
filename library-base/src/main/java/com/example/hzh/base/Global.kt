package com.example.hzh.base

import android.app.Application
import android.content.pm.PackageManager
import com.example.hzh.base.application.BaseApplication

/**
 * Create by hzh on 2024/3/14.
 */
@Suppress("UNCHECKED_CAST")
object Global {

    @Volatile
    private var sContext = BaseApplication.instance

    fun setContext(context: Application) {
        sContext = context
    }

    fun getApplication() = sContext

    fun getPackageName() = sContext.packageName ?: ""

    fun getPackageManager(): PackageManager? = sContext.packageManager

    fun <T> getSystemService(name: String) = sContext.getSystemService(name) as? T
}