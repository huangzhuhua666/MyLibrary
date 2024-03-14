package com.example.hzh.base.performance.fps

import java.util.concurrent.TimeUnit

/**
 * Create by hzh on 2024/3/13.
 */
internal object FPSConfig {

    /**
     * 取样间隔时间
     */
    val sampleTimeInMs by lazy {
        TimeUnit.NANOSECONDS.convert(736, TimeUnit.MILLISECONDS)
    }

    /**
     * 设备刷新率
     */
    var refreshRate = 60f

    /**
     * 设备刷新率（毫秒单位）
     */
    var deviceRefreshRateInMs = 16.6f

    /**
     * 丢帧数报红警告的百分比
     */
    var redFlagDroppedPercentage = 0.2f

    /**
     * 丢帧数报黄警告的百分比
     */
    var yellowFlagDroppedPercentage = 0.05f

    var curXPosition = 200
    var curYPosition = 600
    var curAlpha = 1f
}