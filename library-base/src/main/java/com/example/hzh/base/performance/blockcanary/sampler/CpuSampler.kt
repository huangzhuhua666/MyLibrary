package com.example.hzh.base.performance.blockcanary.sampler

import android.os.Process
import com.example.hzh.base.performance.blockcanary.data.BlockInfo
import com.example.hzh.base.util.closeSafely
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader


/**
 * Create by hzh on 2024/3/14.
 */
internal class CpuSampler(sampleInterval: Long) : BaseSampler(sampleInterval) {

    companion object {

        private const val MAX_ENTRY_COUNT = 10
        private const val BUFFER_SIZE = 1024
    }

    private val mBusyTime by lazy { (mSampleInterval * 1.2f).toInt() }
    private val mCpuInfoEntries by lazy { LinkedHashMap<Long, String>() }

    private var mPid = 0
    private var mLastUser = 0L
    private var mLastSystem = 0L
    private var mLastIdle = 0L
    private var mLastIoWait = 0L
    private var mLastTotal = 0L
    private var mLastAppCpuTime = 0L

    override fun doSample() {
        var cpuReader: BufferedReader? = null
        var pidReader: BufferedReader? = null

        try {
            cpuReader = BufferedReader(
                InputStreamReader(FileInputStream("/proc/stat")),
                BUFFER_SIZE
            )
            val cpuRate = cpuReader.readLine() ?: ""

            if (mPid == 0) {
                mPid = Process.myPid()
            }
            pidReader = BufferedReader(
                InputStreamReader(FileInputStream("/proc/$mPid/stat")),
                BUFFER_SIZE
            )
            val pidCpuRate = pidReader.readLine() ?: ""

            parse(cpuRate, pidCpuRate)
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            cpuReader?.closeSafely()
            pidReader?.closeSafely()
        }
    }

    private fun parse(cpuRate: String, pidCpuRate: String) {
        val cpuInfoList = cpuRate.split(" ")
        if (cpuInfoList.size < 9) {
            return
        }

        val pidCpuInfoList = pidCpuRate.split(" ")
        if (pidCpuInfoList.size < 17) {
            return
        }

        val user = cpuInfoList[2].toLong()
        val nice = cpuInfoList[3].toLong()
        val system = cpuInfoList[4].toLong()
        val idle = cpuInfoList[5].toLong()
        val ioWait = cpuInfoList[6].toLong()
        val total = user + nice + system + idle + ioWait + cpuInfoList[7].toLong() + cpuInfoList[8].toLong()

        val appCpuTime = pidCpuInfoList[13].toLong() + pidCpuInfoList[14].toLong() + pidCpuInfoList[15].toLong() + pidCpuInfoList[16].toLong()

        if (mLastTotal != 0L) {
            val idleTime = idle - mLastIdle
            val totalTime = total - mLastTotal

            val cpuInfoEntry = StringBuilder().also {
                it.append("cpu:${(totalTime - idleTime) * 100L / totalTime}% ")
                it.append("app:${(appCpuTime - mLastAppCpuTime) * 100L / totalTime}% ")
                it.append("[")
                it.append("user:${(user - mLastUser) * 100L / totalTime}% ")
                it.append("system:${(system - mLastSystem) * 100L / totalTime}% ")
                it.append("ioWait:${(ioWait - mLastIoWait) * 100L / totalTime}%")
                it.append("]")
            }.toString()

            synchronized(mCpuInfoEntries) {
                mCpuInfoEntries[System.currentTimeMillis()] = cpuInfoEntry
                if (mCpuInfoEntries.size > MAX_ENTRY_COUNT) {
                    mCpuInfoEntries.remove(mCpuInfoEntries.entries.first().key)
                }
            }
        }

        mLastUser = user
        mLastSystem = system
        mLastIdle = idle
        mLastIoWait = ioWait
        mLastTotal = total
        mLastAppCpuTime = appCpuTime
    }

    override fun start() {
        super.start()
        reset()
    }

    private fun reset() {
        mLastUser = 0
        mLastSystem = 0
        mLastIdle = 0
        mLastIoWait = 0
        mLastTotal = 0
        mLastAppCpuTime = 0
    }

    fun getCpuRateInfo(): String {
        val sb = StringBuilder()
        synchronized(mCpuInfoEntries) {
            mCpuInfoEntries.forEach {
                sb.append("${BlockInfo.TIME_FORMATTER.format(it.key)} ${it.value}${BlockInfo.SEPARATOR}")
            }
        }
        return sb.toString()
    }

    fun isCpuBusy(start: Long, end: Long): Boolean {
        if (end - start <= mSampleInterval) {
            return false
        }

        val s = start - mSampleInterval
        val e = start + mSampleInterval
        var last = 0L

        synchronized(mCpuInfoEntries) {
            mCpuInfoEntries.forEach {
                val time = it.key

                if (time in (s + 1) until e) {
                    if (last != 0L && time - last > mBusyTime) {
                        return true
                    }

                    last = time
                }
            }
        }

        return false
    }
}