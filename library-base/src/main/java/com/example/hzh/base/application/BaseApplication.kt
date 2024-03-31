package com.example.hzh.base.application

import android.app.Application
import com.example.hzh.base.BuildConfig
import com.example.hzh.base.manager.ActivityRecordMgr
import kotlin.properties.Delegates

/**
 * Create by hzh on 2024/3/13.
 */
open class BaseApplication : Application() {

    companion object {

        var instance by Delegates.notNull<Application>()
            private set

        val debug = BuildConfig.DEBUG
    }

    init {
        instance = this
        ActivityRecordMgr.getInstance()
    }
}