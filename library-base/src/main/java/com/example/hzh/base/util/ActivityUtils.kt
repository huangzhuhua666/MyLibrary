package com.example.hzh.base.util

import android.app.Activity

/**
 * Create by hzh on 2024/3/13.
 */
object ActivityUtils {

    /**
     * 判断Activity是否已销毁
     * @param activity 目标activity
     */
    fun isFinished(activity: Activity?) = activity == null || activity.isFinishing || activity.isDestroyed
}