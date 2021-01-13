package com.example.mylibrary.util

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.content.Context
import com.example.mylibrary.util.proxy.InstrumentationProxy

/**
 * Create by hzh on 2021/01/07.
 */
object HookUtils {

    const val INTENT_REAL_ACTIVITY = "intent_real_activity"
    const val INTENT_REAL_ACTIVITY_NAME = "intent_real_activity_name"

    @SuppressLint("PrivateApi")
    fun hookInstrumentation(context: Context) {
        val activityThreadClass = Class.forName("android.app.ActivityThread")

        val activityThreadField =
            ReflectUtils.getField(activityThreadClass, "sCurrentActivityThread")
        val activityThread = activityThreadField.get(null)

        val instrumentationField = ReflectUtils.getField(activityThreadClass, "mInstrumentation")
        val instrumentation = instrumentationField.get(activityThread) as Instrumentation
        instrumentationField.set(activityThread, InstrumentationProxy(instrumentation, context.packageManager))
    }
}