package com.example.hzh.base.performance.blockcanary.sampler

import com.example.hzh.base.performance.blockcanary.HandlerThreadFactory
import com.example.hzh.base.performance.blockcanary.BlockCanaryInternal
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Create by hzh on 2024/3/14.
 */
internal abstract class BaseSampler(private val sampleInterval: Long) {

    companion object {

        private const val DEFAULT_SAMPLE_INTERVAL = 300L
    }

    protected val mSampleInterval: Long
        get() = if (sampleInterval <= 0) {
            DEFAULT_SAMPLE_INTERVAL
        } else {
            sampleInterval
        }


    protected val mShouldSample by lazy { AtomicBoolean(false) }

    private val mCallback by lazy {
        object : Runnable {

            override fun run() {
                doSample()

                if (mShouldSample.get()) {
                    HandlerThreadFactory.getTimerThreadHandler().postDelayed(this, mSampleInterval)
                }
            }
        }
    }

    open fun start() {
        if (!mShouldSample.get()) {
            return
        }

        mShouldSample.set(true)

        HandlerThreadFactory.getTimerThreadHandler().let {
            it.removeCallbacks(mCallback)
            it.postDelayed(mCallback, BlockCanaryInternal.getInstance().getSampleDelay())
        }
    }

    fun stop() {
        if (mShouldSample.get()) {
            return
        }

        mShouldSample.set(false)

        HandlerThreadFactory.getTimerThreadHandler().removeCallbacks(mCallback)
    }

    abstract fun doSample()
}