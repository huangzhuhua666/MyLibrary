package com.example.mylibrary.app

import com.example.common.app.BaseApplication
import com.example.hzh.base.util.yes
import com.example.mylibrary.BuildConfig
import com.example.mylibrary.data.database.entity.MyObjectBox
import com.example.mylibrary.util.HookUtils
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser

/**
 * Create by hzh on 2020/6/15.
 */
class App : BaseApplication() {

    companion object {

        lateinit var boxStore: BoxStore
            private set
    }

    override fun onCreate() {
        super.onCreate()

        HookUtils.hookInstrumentation(applicationContext)

        boxStore = MyObjectBox.builder().androidContext(applicationContext).build()

        BuildConfig.DEBUG.yes { AndroidObjectBrowser(boxStore).start(this) }
    }
}