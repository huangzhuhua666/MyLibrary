package com.example.hzh.base.performance.blockcanary.data

import android.os.Build
import com.example.hzh.base.Global
import com.example.hzh.base.performance.blockcanary.BlockCanaryInternal
import com.example.hzh.base.util.PerformanceUtils
import com.example.hzh.base.util.ProcessUtils
import com.example.hzh.base.util.SystemUtils
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create by hzh on 2024/3/14.
 */
class BlockInfo {

    companion object {

        const val SEPARATOR = "\r\n"
        const val KV = " = "

        const val KEY_QUA = "qua"
        const val KEY_MODEL = "model"
        const val KEY_API = "api-level"
        const val KEY_IMEI = "imei"
        const val KEY_UID = "uid"
        const val KEY_CPU_CORE = "cpu-core"
        const val KEY_CPU_BUSY = "cpu-busy"
        const val KEY_CPU_RATE = "cpu-rate"
        const val KEY_TIME_COST = "time"
        const val KEY_THREAD_TIME_COST = "thread-time"
        const val KEY_TIME_COST_START = "time-start"
        const val KEY_TIME_COST_END = "time-end"
        const val KEY_STACK = "stack"
        const val KEY_PROCESS = "process"
        const val KEY_VERSION_NAME = "versionName"
        const val KEY_VERSION_CODE = "versionCode"
        const val KEY_NETWORK = "network"
        const val KEY_TOTAL_MEMORY = "totalMemory"
        const val KEY_FREE_MEMORY = "freeMemory"

        val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)

        var sCpuCoreNum = -1
        var sApiLevel = ""
        var sImei = ""
        var sModel = ""
        var sQualifier = ""

        init {
            sCpuCoreNum
            sApiLevel = "${Build.VERSION.SDK_INT} ${Build.VERSION.RELEASE}"
            sModel = Build.MODEL
            sQualifier = BlockCanaryInternal.getContext()?.provideQualifier() ?: ""
            sImei = SystemUtils.getDeviceId()
        }

        fun newInstance() = BlockInfo().also {
            val context = BlockCanaryInternal.getContext()
            if (it.versionName.trim().isEmpty()) {
                try {
                    val pkInfo = Global.getPackageManager().getPackageInfo(Global.getPackageName(), 0)
                    it.versionCode = pkInfo.versionCode
                    it.versionName = pkInfo.versionName
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            it.cpuCoreNum = sCpuCoreNum
            it.apiLevel = sApiLevel
            it.imei = sImei
            it.model = sModel
            it.qualifier = sQualifier

            it.uid = context?.provideUid() ?: ""
            it.processName = ProcessUtils.getCurProcessName()
            it.network = context?.provideNetworkType() ?: ""
            it.freeMemory = PerformanceUtils.getFreeMemory().toString()
            it.totalMemory = PerformanceUtils.getTotalMemory().toString()
        }
    }

    private val mSbBasic by lazy { StringBuilder() }
    private val mSbCpu by lazy { StringBuilder() }
    private val mSbTime by lazy { StringBuilder() }
    private val mSbStack by lazy { StringBuilder() }

    var cpuCoreNum = -1
    var apiLevel = ""
    var imei = ""
    var model = ""
    var qualifier = ""

    var uid = ""
    var processName = ""
    var network = ""
    var freeMemory = ""
    var totalMemory = ""

    var cpuBusy = false
    var cpuRateInfo = ""
    var timeCost = 0L
    var threadTimeCost = 0L
    var timeStart = ""
    var timeEnd = ""
    var threadStackEntries = emptyList<String>()

    var versionCode = 0
    var versionName = ""

    fun setCpuBusyFlag(busy: Boolean): BlockInfo {
        cpuBusy = busy
        return this
    }

    fun setRecentCpuRate(info: String): BlockInfo {
        cpuRateInfo = info
        return this
    }

    fun setThreadStackEntries(threadStackEntries: List<String>): BlockInfo {
        this.threadStackEntries = threadStackEntries
        return this
    }

    fun setMainThreadTimeCost(
        realTimeStart: Long,
        realTimeEnd: Long,
        threadTimeStart: Long,
        threadTimeEnd: Long
    ): BlockInfo {
        timeCost = realTimeEnd - realTimeStart
        threadTimeCost = threadTimeEnd - threadTimeStart
        timeStart = TIME_FORMATTER.format(realTimeStart)
        timeEnd = TIME_FORMATTER.format(realTimeEnd)
        return this
    }

    fun flushString(): BlockInfo {
        mSbBasic.let {
            it.append("$KEY_QUA$KV$qualifier$SEPARATOR")
            it.append("$KEY_VERSION_NAME$KV$versionName$SEPARATOR")
            it.append("$KEY_VERSION_CODE$KV$versionCode$SEPARATOR")
            it.append("$KEY_IMEI$KV$imei$SEPARATOR")
            it.append("$KEY_UID$KV$uid$SEPARATOR")
            it.append("$KEY_NETWORK$KV$network$SEPARATOR")
            it.append("$KEY_MODEL$KV$model$SEPARATOR")
            it.append("$KEY_API$KV$apiLevel$SEPARATOR")
            it.append("$KEY_CPU_CORE$KV$cpuCoreNum$SEPARATOR")
            it.append("$KEY_PROCESS$KV$processName$SEPARATOR")
            it.append("$KEY_FREE_MEMORY$KV$freeMemory$SEPARATOR")
            it.append("$KEY_TOTAL_MEMORY$KV$totalMemory$SEPARATOR")
        }

        mSbTime.let {
            it.append("$KEY_TIME_COST$KV$timeCost$SEPARATOR")
            it.append("$KEY_THREAD_TIME_COST$KV$threadTimeCost$SEPARATOR")
            it.append("$KEY_TIME_COST_START$KV$timeStart$SEPARATOR")
            it.append("$KEY_TIME_COST_END$KV$timeEnd$SEPARATOR")
        }

        mSbCpu.let {
            it.append("$KEY_CPU_BUSY$KV$cpuBusy$SEPARATOR")
            it.append("$KEY_CPU_RATE$KV$cpuRateInfo$SEPARATOR")
        }

        if (threadStackEntries.isNotEmpty()) {
            val sb = StringBuilder()
            threadStackEntries.forEach {
                sb.append("$it$SEPARATOR")
            }

            mSbStack.append("$KEY_STACK$KV$sb$SEPARATOR")
        }

        return this
    }

    fun getBasicString() = mSbBasic.toString()

    fun getCpuString() = mSbCpu.toString()

    fun getTimeString() = mSbTime.toString()

    override fun toString(): String {
        return "$mSbBasic$mSbTime$mSbCpu$mSbStack"
    }
}