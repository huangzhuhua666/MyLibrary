package com.example.hzh.base.performance.blockcanary

import android.os.Debug
import android.os.SystemClock
import android.util.Printer

/**
 * Create by hzh on 2024/3/14.
 *
 * Looper 的 printer，用来监控是否发生了卡顿
 */
internal class LooperMonitor @JvmOverloads constructor(
    private val blockThresholdMillis: Long = DEFAULT_BLOCK_THRESHOLD_MILLIS,
    private val stopWhenDebugging: Boolean = false,
    private val blockListener: BlockListener,
) : Printer {

    companion object {

        private const val DEFAULT_BLOCK_THRESHOLD_MILLIS = 3000L
    }

    private var mPrintingStarted = false
    private var mStartTimestamp = 0L
    private var mStartThreadTimestamp = 0L

    override fun println(x: String?) {
        if (stopWhenDebugging && Debug.isDebuggerConnected()) {
            return
        }

        if (!mPrintingStarted) {
            mStartTimestamp = System.currentTimeMillis()
            mStartThreadTimestamp = SystemClock.currentThreadTimeMillis()
            mPrintingStarted = true

            startDump()
        } else {
            val endTime = System.currentTimeMillis()
            mPrintingStarted = false
            if (isBlock(endTime)) {
                notifyBlockEvent(endTime)
            }

            stopDump()
        }
    }

    private fun startDump() {
        BlockCanaryInternal.getInstance().start()
    }

    private fun isBlock(endTime: Long): Boolean {
        return endTime - mStartTimestamp > blockThresholdMillis
    }

    private fun notifyBlockEvent(endTime: Long) {
        val startTime = mStartTimestamp
        val startThreadTime = mStartThreadTimestamp
        val endThreadTime = SystemClock.currentThreadTimeMillis()
        HandlerThreadFactory.getWriteLogThreadHandler().post {
            blockListener.onBlockEvent(startTime, endTime, startThreadTime, endThreadTime)
        }
    }

    private fun stopDump() {
        BlockCanaryInternal.getInstance().stop()
    }

    interface BlockListener {

        fun onBlockEvent(
            realStartTime: Long,
            realEndTime: Long,
            threadStartTime: Long,
            threadEndTime: Long,
        )
    }
}