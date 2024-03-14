package com.example.hzh.base.manager

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.hzh.base.BuildConfig
import com.example.hzh.base.Global
import com.example.hzh.base.manager.internal.ActivityStateChecker
import com.example.hzh.base.util.ActivityLifeCycleCallbackAdapter
import com.example.hzh.base.util.ActivityUtils
import com.example.hzh.base.util.CollectionUtils
import com.example.hzh.base.util.ProcessUtils
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Create by hzh on 2024/3/13.
 */
class ActivityRecordMgr private constructor() : LifecycleObserver {

    companion object {

        private const val TAG = "ActivityRecordMgr"

        fun getInstance() = Holder.instance

        fun getTaskTopActivity(activity: Activity?): ComponentName? {
            try {
                val taskInfos = (Global.getApplication().getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.getRunningTasks(10)
                if (taskInfos.isNullOrEmpty()) {
                    return null
                }

                return taskInfos.firstOrNull {
                    it.id == activity?.taskId
                }?.topActivity
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return null
        }
    }

    private object Holder {

        val instance = ActivityRecordMgr()
    }

    /**
     * 是否是冷启动，默认是
     */
    var isColdStart = true
        private set

    /**
     * 应用是否显示过UI
     */
    @Volatile
    var hasShownUi = false
        private set

    private val mAppRecords by lazy { mutableListOf<ActivityRecord>() }
    private val mActivityLeakTrackList by lazy { CopyOnWriteArrayList<WeakReference<Activity>>() }
    private val mFragmentLeakTrackList by lazy { CopyOnWriteArrayList<WeakReference<Fragment>>() }
    private val mAppVisibleListeners by lazy { CopyOnWriteArraySet<WeakReference<AppVisibleChangeListener>>() }

    /**
     * 避免将启动 app 上报成从后台切到前台
     */
    private var mLifecycleEventOnStop = false

    init {
        Global.getApplication().registerActivityLifecycleCallbacks(object : ActivityLifeCycleCallbackAdapter() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.i(TAG, ">>>>> on activity created: ${activity.localClassName}")
                onCreate(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                Log.i(TAG, ">>>>> on activity started: ${activity.localClassName}")
                onStart(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                Log.i(TAG, ">>>>> on activity resumed: ${activity.localClassName}")
                onResume(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                Log.i(TAG, ">>>>> on activity paused: ${activity.localClassName}")
                onPause(activity)
            }

            override fun onActivityStopped(activity: Activity) {
                Log.i(TAG, ">>>>> on activity stopped: ${activity.localClassName}")
                onStop(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.i(TAG, ">>>>> on activity destroyed: ${activity.localClassName}")
                onDestroy(activity)
            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @Synchronized
    private fun onCreate(activity: Activity) {
        if (BuildConfig.DEBUG) {
            ActivityStateChecker.checkOrientation(activity)
        }

        mAppRecords.add(ActivityRecord(activity, ActivityState.OnCreate))
        CollectionUtils.addWeakReference(mActivityLeakTrackList, activity)
        dump(null, null)
    }

    @Synchronized
    private fun dump(pw: PrintWriter?, args: Array<String>?) {
        if (BuildConfig.DEBUG) {
            println(pw, "-----------------Activity stack begin---------------------")

            for (i in mAppRecords.indices.reversed()) {
                val record = mAppRecords[i]
                println(pw, "Activity Stack[$i] ${record.activity} # lifeState = ${record.state}")
            }

            getTopActivity()?.let {
                println(pw, "TopActivity = $it")
            }

            println(pw, "isColdStart = $isColdStart")
            println(pw, "-----------------Activity stack end-----------------------")
        }
    }

    private fun println(pw: PrintWriter?, log: String) {
        pw?.println(log) ?: Log.v(TAG, log)
    }

    /**
     * 获取栈顶Activity
     */
    fun getTopActivity(): Activity? {
        if (mAppRecords.isEmpty()) {
            return null
        }

        for (i in mAppRecords.indices.reversed()) {
            val record = mAppRecords[i]
            if (!ActivityUtils.isFinished(record.activity)) {
                return record.activity
            }
        }

        return null
    }

    private fun onStart(activity: Activity) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "isColdStart = $isColdStart")
        }
        changeState(activity, ActivityState.OnStart)
    }

    @Synchronized
    private fun changeState(activity: Activity, state: ActivityState) {
        for (i in mAppRecords.indices.reversed()) {
            val record = mAppRecords[i]
            if (record.activity === activity) {
                record.state = state
                break
            }
        }

        dump(null, null)
    }

    private fun onResume(activity: Activity) {
        hasShownUi = true
        changeState(activity, ActivityState.OnResume)
    }

    private fun onPause(activity: Activity) {
        changeState(activity, ActivityState.OnPause)
    }

    private fun onStop(activity: Activity) {
        changeState(activity, ActivityState.OnStop)
    }

    private fun onDestroy(activity: Activity) {
        removeActivity(activity)
    }

    @Synchronized
    private fun removeActivity(activity: Activity) {
        for (i in mAppRecords.indices.reversed()) {
            val record = mAppRecords[i]
            if (record.activity === activity) {
                Log.v(TAG, "remove Activity stack[$i] ${record.activity}")
                mAppRecords.removeAt(i)
                break
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onForeground() {
        if (mLifecycleEventOnStop) {
            isColdStart = false
            notifyAppVisibilityChanged(true)
            mLifecycleEventOnStop = false
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onBackground() {
        if (!mLifecycleEventOnStop) {
            notifyAppVisibilityChanged(false)
            // 进入过后台
            mLifecycleEventOnStop = true
        }
    }

    private fun notifyAppVisibilityChanged(isForeground: Boolean) {
        Log.d(TAG, "notify app visibility change, isVisible: $isForeground")
        mAppVisibleListeners.asSequence()
            .map {
                it.get()
            }
            .filterNotNull()
            .forEach {
                if (isForeground) {
                    it.onInForeground()
                } else {
                    it.onInBackground()
                }
            }
    }

    fun isForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    /**
     * 判断当前Activity处于Resumed状态
     */
    @Synchronized
    fun isAppResumed(): Boolean {
        for (i in mAppRecords.indices.reversed()) {
            val record = mAppRecords[i]
            if (record.state == ActivityState.OnResume) {
                return true
            }
        }

        return false
    }

    /**
     * 判断topActivity处于某ActivityState状态
     */
    @Synchronized
    private fun isTopActivityState(state: ActivityState) = state == getActivityState(getTopActivity())

    /**
     * 获取指定Activity当前的状态
     */
    fun getActivityState(activity: Activity?): ActivityState? {
        activity ?: return null

        for (i in mAppRecords.indices.reversed()) {
            val record = mAppRecords[i]
            if (activity == record.activity) {
                return record.state
            }
        }

        return null
    }

    /**
     * 判断给定Activity类是否有活跃实例
     */
    fun contains(clazz: Class<out Activity?>?): Boolean {
        clazz ?: return false

        return mAppRecords.any {
            it.activity.javaClass == clazz
        }
    }

    fun contains(className: String?): Boolean {
        className ?: return false

        return mAppRecords.any {
            it.activity.javaClass.name == className
        }
    }

    /**
     * 获取给定Activity class的实例数
     */
    fun getActivityCount(clazz: Class<out Activity?>?): Int {
        clazz ?: return 0

        return mAppRecords.count {
            it.activity.javaClass == clazz
        }
    }

    /**
     * 获取给定Activity class的实例
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Activity?> getActivities(clazz: Class<T>?): List<T> {
        clazz ?: return emptyList()

        return mAppRecords.asSequence()
            .filter {
                it.activity.javaClass == clazz
            }
            .map {
                it.activity as T
            }
            .toList()
    }

    fun getAllUnRecycledActivities(): List<Activity> {
        return mActivityLeakTrackList.asSequence()
            .map {
                it.get()
            }
            .filterNotNull()
            .toList()
    }

    fun getAllAliveActivities(): List<Activity> {
        return mActivityLeakTrackList.asSequence()
            .map {
                it.get()
            }
            .filterNotNull()
            .filter {
                !it.isFinishing
            }
            .toList()
    }

    fun getAllUnRecycledFragments(): List<Fragment> {
        return mFragmentLeakTrackList.asSequence()
            .map {
                it.get()
            }
            .filterNotNull()
            .toList()
    }

    fun onAttachFragment(fragment: Fragment?) {
        CollectionUtils.addWeakReference(mFragmentLeakTrackList, fragment)
    }

    /**
     * 添加应用前后台切换监听
     */
    fun addListener(listener: AppVisibleChangeListener?) {
        CollectionUtils.addWeakReference(mAppVisibleListeners, listener)
    }

    /**
     * 移除应用前后台切换监听
     */
    fun removeListener(listener: AppVisibleChangeListener?) {
        CollectionUtils.removeWeakReference(mAppVisibleListeners, listener)
    }

    /**
     * 获取当前活跃Activity记录
     */
    fun getActivityRecords() = mAppRecords.toList()

    /**
     * 判断App是否具有前台优先级
     */
    fun isForegroundApp(): Boolean {
        return ProcessUtils.getProcessImportance() <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }

    /**
     * 判断App是否用户可见
     */
    @Synchronized
    fun isUserVisible(): Boolean {
        for (i in mAppRecords.indices.reversed()) {
            val record = mAppRecords[i]
            if (record.state in listOf(ActivityState.OnResume, ActivityState.OnPause)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun isAppOnCreateOrResumed(): Boolean {
        for (i in mAppRecords.indices.reversed()) {
            val record = mAppRecords[i]
            if (record.state.level >= ActivityState.OnResume.level) {
                return true
            }
        }
        return false
    }

    fun getSelectedPositionActivity(pos: Int): Activity? {
        val size = mAppRecords.size
        if (size > pos - 1 && size - 1 - pos >= 0) {
            return mAppRecords[size - 1 - pos].activity
        }
        return null
    }

    class ActivityRecord(val activity: Activity, var state: ActivityState) {

        fun isTheSameClass(clazz: Class<*>) = activity.javaClass == clazz

        fun finish() {
            state
            activity.finish()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || javaClass != other.javaClass) {
                return false
            }

            return activity == (other as? ActivityRecord)?.activity
        }

        override fun hashCode() = activity.hashCode()

        override fun toString() = "ActivityRecord{ activity = $activity, state = $state }"
    }

    // level -> 可视等级
    sealed class ActivityState(val level: Int) {

        object OnCreate : ActivityState(10)

        object OnStart : ActivityState(8)

        object OnResume : ActivityState(6)

        object OnPause : ActivityState(4)

        object OnStop : ActivityState(2)

        object OnDestroy : ActivityState(0)
    }

    interface AppVisibleChangeListener {

        /**
         * 从后台切换回前台(曾经处于后台)
         */
        fun onInForeground()

        /**
         * 从前台切换到后台(曾经 onResumed 过)
         */
        fun onInBackground()
    }
}