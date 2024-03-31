package com.example.hzh.base.performance.blockcanary

import android.os.Handler
import android.os.HandlerThread

/**
 * Create by hzh on 2024/3/14.
 */
internal object HandlerThreadFactory {

    private val sLoopThread by lazy { HandlerThreadWrapper("looper") }
    private val sWriteLogThread by lazy { HandlerThreadWrapper("writer") }

    fun getTimerThreadHandler() = sLoopThread.handler

    fun getWriteLogThreadHandler() = sWriteLogThread.handler

    private class HandlerThreadWrapper(threadName: String) {

        val handler: Handler

        init {
            val handlerThread = HandlerThread("BlockCanary-$threadName").also {
                it.start()
            }
            handler = Handler(handlerThread.looper)
        }
    }
}