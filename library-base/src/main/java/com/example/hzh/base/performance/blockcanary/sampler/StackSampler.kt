package com.example.hzh.base.performance.blockcanary.sampler

import com.example.hzh.base.performance.blockcanary.data.BlockInfo
import java.lang.StringBuilder

/**
 * Create by hzh on 2024/3/14.
 */
internal class StackSampler @JvmOverloads constructor(
    private val thread: Thread,
    private val maxEntryCount: Int = DEFAULT_MAX_ENTRY_COUNT,
    sampleIntervalMills: Long,
) : BaseSampler(sampleIntervalMills) {

    companion object {

        private const val DEFAULT_MAX_ENTRY_COUNT = 100

        private val sStackMap by lazy { LinkedHashMap<Long, String>() }
    }

    override fun doSample() {
        val sb = StringBuilder()
        thread.stackTrace.asSequence()
            .filterNotNull()
            .forEach {
                sb.append(it.toString()).append(BlockInfo.SEPARATOR)
            }

        synchronized(sStackMap) {
            if (maxEntryCount > 0 && sStackMap.size == maxEntryCount) {
                sStackMap.remove(sStackMap.keys.iterator().next())
            }

            sStackMap[System.currentTimeMillis()] = sb.toString()
        }
    }

    fun getThreadStackEntries(startTime: Long, endTime: Long): List<String> {
        synchronized(sStackMap) {
            return sStackMap.asSequence()
                .filter {
                    it.key in (startTime + 1) until endTime
                }
                .map {
                    "${BlockInfo.TIME_FORMATTER.format(it.key)}${BlockInfo.SEPARATOR}${BlockInfo.SEPARATOR}${it.value}"
                }
                .toList()
        }
    }
}