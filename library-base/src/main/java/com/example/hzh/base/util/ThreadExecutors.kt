package com.example.hzh.base.util

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Create by hzh on 2024/3/14.
 */
object ThreadExecutors {

    const val THREAD_NAME_PREFIX = "HZH-"

    fun newFixedThreadPool(
        maximumPoolSize: Int,
        name: String,
    ) = newFixedThreadPool(maximumPoolSize, maximumPoolSize, Thread.NORM_PRIORITY, name)

    fun newFixedThreadPool(
        corePoolSize: Int,
        maximumPoolSize: Int,
        priority: Int,
        name: String,
    ) = ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        60L,
        TimeUnit.SECONDS,
        LinkedBlockingQueue(),
        MyThreadFactory(name = name, priority = priority),
        ThreadPoolExecutor.CallerRunsPolicy()
    )

    open class DefaultThreadFactory @JvmOverloads constructor(
        group: String? = null,
        private val name: String,
        private val priority: Int,
    ) : ThreadFactory {

        private val mThreadNumber by lazy { AtomicInteger(1) }

        private val mGroup by lazy {
            if (group == null) {
                System.getSecurityManager()?.threadGroup ?: Thread.currentThread().threadGroup
            } else {
                ThreadGroup(group)
            }
        }

        override fun newThread(r: Runnable) = Thread(
            mGroup,
            r,
            "$name-${mThreadNumber.getAndIncrement()}",
            0
        ).also {
            if (it.isDaemon) {
                it.isDaemon = false
            }

            if (it.priority != priority) {
                it.priority = priority
            }
        }
    }

    open class MyThreadFactory @JvmOverloads constructor(
        group: String? = null,
        name: String,
        priority: Int,
    ) : DefaultThreadFactory(group, "$THREAD_NAME_PREFIX$name", priority)
}