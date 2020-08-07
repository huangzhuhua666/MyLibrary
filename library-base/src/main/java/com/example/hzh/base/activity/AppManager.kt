package com.example.hzh.base.activity

import android.app.Activity
import androidx.collection.ArrayMap

/**
 * Create by hzh on 2020/5/8.
 *
 * 用来管理Activity的
 */
class AppManager private constructor() {

    companion object {

        fun getInstance(): AppManager = HOLDER.instance
    }

    private object HOLDER {

        val instance = AppManager()
    }

    private val mActivitySet by lazy { ArrayMap<String, Activity>() }

    private var mCurrentTag: String? = ""

    /**
     * 获取栈顶Activity
     */
    fun getTopActivity(): Activity? = mActivitySet[mCurrentTag]

    fun addActivity(activity: Activity) {
        mCurrentTag = getTag(activity)
        mActivitySet[mCurrentTag] = activity
    }

    fun removeActivity(activity: Activity) {
        val tag = getTag(activity)
        mActivitySet.remove(tag)

        // 当前activity为最后一个，清除标记
        if (tag == mCurrentTag) mCurrentTag = null
    }

    /**
     * 销毁所有Activity
     */
    fun finishAllActivity() {
        finishWithoutActivity(null)
    }

    /**
     * 销毁除了[clazz]之外的所有Activity
     *
     * @param clazz 不需要销毁的Activity
     */
    fun finishWithoutActivity(vararg clazz: Class<out Activity>?) {
        mActivitySet.forEach {
            it.value?.let { activity ->
                if (!activity.isFinishing && !clazz.contains(activity.javaClass)) {
                    activity.finish()
                    mActivitySet.remove(it.key)
                }
            }
        }
    }

    private fun getTag(obj: Any): String = obj.javaClass.name + Integer.toHexString(obj.hashCode())
}