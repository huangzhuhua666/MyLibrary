package com.example.hzh.base.performance.blockcanary

import android.os.Environment
import android.os.Looper
import com.example.hzh.base.Global
import com.example.hzh.base.performance.blockcanary.data.BlockInfo
import com.example.hzh.base.performance.blockcanary.sampler.CpuSampler
import com.example.hzh.base.performance.blockcanary.sampler.StackSampler
import java.io.File
import java.io.FilenameFilter
import java.util.LinkedList

/**
 * Create by hzh on 2024/3/14.
 */
internal class BlockCanaryInternal private constructor() {

    companion object {

        private var sContext: BlockCanaryContext? = null

        fun getInstance() = Holder.instance

        fun setContext(context: BlockCanaryContext) {
            sContext = context
        }

        fun getContext() = sContext

        fun getLogFiles(): Array<File>? {
            val file = detectedBlockDirectory()
            if (file.exists() && file.isDirectory) {
                return file.listFiles(BlockLogFileFilter())
            }
            return null
        }

        internal fun detectedBlockDirectory(): File {
            val directory = File(getPath())
            if (!directory.exists()) {
                directory.mkdirs()
            }
            return directory
        }

        internal fun getPath(): String {
            val context = getContext()
            val logPath = context?.providePath() ?: ""
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED &&
                Environment.getExternalStorageDirectory().canWrite()) {
                return "${Environment.getExternalStorageDirectory().path}$logPath"
            }

            context?.providePath()?.let {
                return "${Global.getApplication().filesDir}$it"
            }

            return ""
        }
    }

    private object Holder {

        val instance = BlockCanaryInternal()
    }

    val monitor by lazy {
        LooperMonitor(0, false, object : LooperMonitor.BlockListener {

            override fun onBlockEvent(
                realStartTime: Long,
                realEndTime: Long,
                threadStartTime: Long,
                threadEndTime: Long
            ) {
                val threadStackEntries = mStackSampler.getThreadStackEntries(realStartTime, realEndTime)
                if (threadStackEntries.isEmpty()) {
                    return
                }

                BlockInfo.newInstance().also {
                    it.setMainThreadTimeCost(realStartTime, realEndTime, threadStartTime, threadEndTime)
                    it.setCpuBusyFlag(mCpuSampler.isCpuBusy(realStartTime, realEndTime))
                    it.setRecentCpuRate(mCpuSampler.getCpuRateInfo())
                    it.setThreadStackEntries(threadStackEntries)
                    it.flushString()

                    LogWriter.save(it.toString())

                    if (mInterceptorChain.isNotEmpty()) {
                        mInterceptorChain.forEach { interceptor ->
                            interceptor.onBlock(Global.getApplication(), it)
                        }
                    }
                }
            }
        })
    }

    private val mStackSampler by lazy {
        StackSampler(
            thread = Looper.getMainLooper().thread,
            sampleIntervalMills = sContext?.provideDumpInterval() ?: 1000L
        )
    }
    private val mCpuSampler by lazy {
        CpuSampler(sContext?.provideDumpInterval() ?: 1000L)
    }

    private val mInterceptorChain by lazy { LinkedList<BlockInterceptor>() }

    init {
        LogWriter.cleanObsolete()
    }

    fun start() {
        mStackSampler.start()
        mCpuSampler.start()
    }

    fun stop() {
        mStackSampler.stop()
        mCpuSampler.stop()
    }

    fun addBlockInterceptor(interceptor: BlockInterceptor) {
        mInterceptorChain.add(interceptor)
    }

    fun getSampleDelay() = ((getContext()?.provideBlockThreshold() ?: 1000L) * 0.8f).toLong()

    private class BlockLogFileFilter: FilenameFilter {

        companion object {

            private const val TYPE = ".log"
        }

        override fun accept(dir: File?, name: String?): Boolean {
            return name?.endsWith(TYPE) ?: false
        }
    }
}