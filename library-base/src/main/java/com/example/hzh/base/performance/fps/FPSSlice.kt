package com.example.hzh.base.performance.fps

/**
 * Create by hzh on 2024/3/13.
 *
 * 取样周期内取到的帧样本经过分析后得到的切片数据
 * 用于监控器的数据显示和后续数据分析
 */
internal data class FPSSlice(
    val metric: Metric,
    val fps: Int,
    // 丢帧数
    val droppedFrameCount: Int,
    // 取样开始时间
    val samplingStartTimeStampMs: Long,
    // 取样间隔时长
    val samplingLengthMs: Float,
) {

    /**
     * 切片所得到的帧率的品质
     */
    sealed class Metric {

        data object Good : Metric()

        data object Bad : Metric()

        data object Medium : Metric()
    }
}