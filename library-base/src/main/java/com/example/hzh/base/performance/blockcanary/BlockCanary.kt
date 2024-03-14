package com.example.hzh.base.performance.blockcanary

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Looper
import com.example.hzh.base.Global
import com.example.hzh.base.util.ThreadExecutors

/**
 * Create by hzh on 2024/3/14.
 */
class BlockCanary private constructor() {

    companion object {

        @Volatile
        private var mIsInstall = false

        fun install(blockCanaryContext: BlockCanaryContext): BlockCanary {
            BlockCanaryContext.init(blockCanaryContext)
            // TODO setEnable(DisplayActivity::class.java, BlockCanaryContext.get().displayNotification())
            return getInstance()
        }

        fun getInstance() = Holder.instance
    }

    private object Holder {

        val instance = BlockCanary().also {
            mIsInstall = true
        }
    }

    private val mBlockCanaryCore by lazy { BlockCanaryInternal.getInstance() }
    private val mFileIOExecutor by lazy {
        ThreadExecutors.newFixedThreadPool(1, "BlockCanary-File-IO")
    }

    private var mMonitorStarted = false

    init {
        BlockCanaryInternal.setContext(BlockCanaryContext.get())
        mBlockCanaryCore.addBlockInterceptor(BlockCanaryContext.get())

        if (BlockCanaryContext.get().displayNotification()) {
            // TODO mBlockCanaryCore.addBlockInterceptor(DisplayService())
        }
    }

    fun start() {
        if (mMonitorStarted) {
            return
        }

        mMonitorStarted = true
        Looper.getMainLooper().setMessageLogging(mBlockCanaryCore.monitor)
    }

    fun stop() {
        if (!mMonitorStarted) {
            return
        }

        mMonitorStarted = false
        Looper.getMainLooper().setMessageLogging(null)
        mBlockCanaryCore.stop()
    }

    fun upload() {
        BlockCanaryUploader.zipAndUpload()
    }

    fun setEnable(componentClass: Class<*>, enable: Boolean) {
        mFileIOExecutor.execute {
            setEnableBlocking(componentClass, enable)
        }
    }

    private fun setEnableBlocking(componentClass: Class<*>, enable: Boolean) {
        val newState = if (enable) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        Global.getPackageManager()?.setComponentEnabledSetting(
            ComponentName(Global.getApplication(), componentClass),
            newState,
            PackageManager.DONT_KILL_APP
        )
    }

    fun isInstall() = mIsInstall
}