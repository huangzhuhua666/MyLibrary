package com.example.hzh.base.performance.fps

import android.view.Choreographer

/**
 * Create by hzh on 2024/3/13.
 *
 * 用以收集帧数据的类，该类在调用 [start] 方法后，每间隔 [FPSConfig.sampleTimeInMs] 左右的时间，会通过
 * [mOnSampleDataAnalysed] 回调向外吐出这段时间收集的帧数据的分析结果
 *
 * 本类的实现机制目前基于 [Choreographer] 的回调
 */
internal class FPSMonitorController(private val mOnSampleDataAnalysed: (slice: FPSSlice?) -> Unit) {

    private val mFpsFrameCallback by lazy {
        object : Choreographer.FrameCallback {

            override fun doFrame(frameTimeNanos: Long) {
                if (!mIsEnable) {
                    flush()
                    return
                }

                if (mStartSampleTimeInNs == 0L) {
                    mStartSampleTimeInNs = frameTimeNanos
                }

                if (needSample(frameTimeNanos)) {
                    analyseSampleAndSend()
                    flush()
                    mStartSampleTimeInNs = frameTimeNanos
                }

                mSampleData.add(frameTimeNanos)

                Choreographer.getInstance().postFrameCallback(this)
            }
        }
    }

    private val mSampleData = mutableListOf<Long>()

    private var mIsEnable = false
    private var mStartSampleTimeInNs = 0L

    /**
     * 开始（恢复）帧数据收集
     */
    fun start() {
        mIsEnable = true
        Choreographer.getInstance().postFrameCallback(mFpsFrameCallback)
    }

    /**
     * 暂停帧数据收集
     * 调用本方法会将目前收集到的数据清空，以防止污染下次的数据收集
     */
    fun pause() {
        mIsEnable = false
        flush()
    }

    /**
     * 清空数据
     */
    private fun flush() {
        mSampleData.clear()
    }

    /**
     * 当间隔时间大于全局配置 [FPSConfig] 中的取样时间间隔 [FPSConfig.sampleTimeInMs]，返回 true
     */
    private fun needSample(frameTimeNanos: Long): Boolean {
        return frameTimeNanos - mStartSampleTimeInNs > FPSConfig.sampleTimeInMs
    }

    /**
     * 分析样本数据，并且把结果传递给外部回调
     */
    private fun analyseSampleAndSend() {
        val slice = FPSMonitorCalculation.getInstance().calculateSliceData(mSampleData.toList())
        mOnSampleDataAnalysed.invoke(slice)
    }
}