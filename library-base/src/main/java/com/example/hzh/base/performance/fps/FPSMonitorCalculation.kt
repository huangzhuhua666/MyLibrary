package com.example.hzh.base.performance.fps

import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Create by hzh on 2024/3/13.
 */
internal class FPSMonitorCalculation private constructor() {

    companion object {

        fun getInstance() = Holder.instance
    }

    private object Holder {

        val instance = FPSMonitorCalculation()
    }

    fun calculateSliceData(sampleFrameData: List<Long>): FPSSlice? {
        // 传入帧数据为空，判定为无效数据
        if (sampleFrameData.isEmpty()) {
            return null
        }

        val samplingStartTimeStampNs = sampleFrameData.first()
        val realSamplingLengthMs = (sampleFrameData.last() - samplingStartTimeStampNs).nsToMs().toFloat()

        // 实际取样间隔过大（大于一分钟）的话，可以认定是无效数据
        if (realSamplingLengthMs > 60 * 1000) {
            return null
        }

        // 实际取样的这段时间间隔内，理论上应该要展示的帧数
        val supposedFrameCount = calculateSupposedFrameCount(realSamplingLengthMs)
        if (supposedFrameCount <= 0) {
            return null
        }

        // 用来衡量 FPSSlice.Metric
        var runningOver = 0
        // 总共丢掉的帧数
        var totalDropped = 0
        createDroppedSet(sampleFrameData).forEach { drop ->
            totalDropped += drop

            if (drop > 2) {
                runningOver += drop
            }
        }

        val percentOver = runningOver.toFloat() / supposedFrameCount.toFloat()
        val metric = when {
            percentOver >= FPSConfig.redFlagDroppedPercentage -> {
                FPSSlice.Metric.Bad
            }
            percentOver >= FPSConfig.yellowFlagDroppedPercentage -> {
                FPSSlice.Metric.Medium
            }
            else -> FPSSlice.Metric.Good
        }

        // 计算真实的 fps
        val multiplier = FPSConfig.refreshRate / supposedFrameCount
        val realAnswer = (multiplier * (supposedFrameCount - totalDropped)).roundToInt()

        return FPSSlice(
            metric,
            realAnswer,
            totalDropped,
            samplingStartTimeStampNs.nsToMs(),
            realSamplingLengthMs
        )
    }

    /**
     * 计算 [realSampleLengthMs] 间隔内，理论上要展示的帧数。
     * 取决于 [FPSConfig.deviceRefreshRateInMs]
     */
    private fun calculateSupposedFrameCount(realSampleLengthMs: Float): Int {
        return (realSampleLengthMs / FPSConfig.deviceRefreshRateInMs).roundToInt()
    }

    /**
     * 计算取样数据里面每段间隔的丢帧数
     */
    private fun createDroppedSet(dataSet: List<Long>): List<Int> {
        val droppedSet = mutableListOf<Int>()
        var start = -1L

        dataSet.forEach {
            if (start == -1L) {
                start = it
                return@forEach
            }

            val droppedCount = countDroppedFrames(start, it)
            if (droppedCount > 0) {
                droppedSet.add(droppedCount)
            }
            start = it
        }

        return droppedSet
    }

    /**
     * 计算 [start] to [end] 内的丢帧数
     */
    private fun countDroppedFrames(start: Long, end: Long): Int {
        var count = 0
        val diffMs = (end - start).nsToMs()
        val devRefreshRate = FPSConfig.deviceRefreshRateInMs.roundToLong()
        if (diffMs > devRefreshRate) {
            count = (diffMs / devRefreshRate).toInt()
        }

        return count
    }

    private fun Long.nsToMs() = TimeUnit.MILLISECONDS.convert(this, TimeUnit.NANOSECONDS)
}