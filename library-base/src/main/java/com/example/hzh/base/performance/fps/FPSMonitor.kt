package com.example.hzh.base.performance.fps

import android.content.Context
import android.view.WindowManager
import com.example.hzh.base.application.BaseApplication
import com.example.hzh.base.manager.ActivityRecordMgr

/**
 * Create by hzh on 2024/3/13.
 *
 * 帧率监控器对外暴露的类
 *
 * 调用 [configure] 方法来自定义监控器的一些参数
 */
class FPSMonitor private constructor() {

    companion object {

        fun getInstance() = Holder.instance
    }

    private object Holder {

        val instance = FPSMonitor()
    }

    private val mMonitorController by lazy {
        FPSMonitorController { slice ->
            slice?.let {
                mFpsMonitorViewManager.notifyFPSDataChanged(it)
            }
        }
    }

    private val mFpsMonitorViewManager by lazy { FPSMonitorViewManager() }

    private val mForegroundListener by lazy {
        object : ActivityRecordMgr.AppVisibleChangeListener {

            override fun onInForeground() {
                mFpsMonitorViewManager.show()
            }

            override fun onInBackground() {
                mFpsMonitorViewManager.hide()
            }
        }
    }

    init {
        setFrameRate(BaseApplication.instance)
    }

    fun show() {
        mFpsMonitorViewManager.show()
        mMonitorController.start()
        ActivityRecordMgr.getInstance().addListener(mForegroundListener)
    }

    fun hide() {
        mMonitorController.pause()
        mFpsMonitorViewManager.hide()
        ActivityRecordMgr.getInstance().removeListener(mForegroundListener)
    }

    private fun setFrameRate(context: Context) {
        (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.refreshRate?.let {
            FPSConfig.refreshRate = it
            FPSConfig.deviceRefreshRateInMs = 1000f / it
        }
    }

    fun configure(): Configuration = Configuration()

    class Configuration internal constructor() {

        fun redFlagPercentage(percentage: Float) = apply {
            FPSConfig.redFlagDroppedPercentage = percentage
        }

        fun yellowFlagPercentage(percentage: Float) = apply {
            FPSConfig.yellowFlagDroppedPercentage = percentage
        }

        fun startingXPosition(xPosition: Int) = apply {
            FPSConfig.curXPosition = xPosition
        }

        fun startingYPosition(yPosition: Int) = apply {
            FPSConfig.curYPosition = yPosition
        }
    }
}