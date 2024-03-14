package com.example.hzh.base.util

import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * Create by hzh on 2024/3/14.
 */
object ThreadPoolUtils {

    private val sThreadId by lazy { AtomicInteger() }

    fun executeOnNewThread(block: () -> Unit) {
        thread(
            name = "${ThreadExecutors.THREAD_NAME_PREFIX}Thread-${sThreadId.incrementAndGet()}",
            block = block,
        )
    }
}