package com.example.hzh.base.performance.blockcanary

import android.content.Context
import com.example.hzh.base.performance.blockcanary.data.BlockInfo
import java.io.File

/**
 * Create by hzh on 2024/3/14.
 */
open class BlockCanaryContext : BlockInterceptor {

    companion object {

        private var sInstance: BlockCanaryContext? = null

        fun init(blockCanaryContext: BlockCanaryContext) {
            sInstance = blockCanaryContext
        }

        fun get(): BlockCanaryContext {
            return sInstance ?: throw RuntimeException("BlockCanaryContext null, please call fun init!")
        }
    }

    /**
     * block interceptor, developer may provide their own actions.
     */
    override fun onBlock(context: Context, blockInfo: BlockInfo) {

    }

    /**
     * implement in your project.
     *
     * @return Qualifier which can specify this installation, like version + flavor.
     */
    open fun provideQualifier() = "unknown"

    /**
     * implement in your project.
     *
     * @return user id
     */
    open fun provideUid() = "uid"

    /**
     * network type
     *
     * @return [String] like 2G, 3G, 4G, wifi, etc.
     */
    open fun provideNetworkType() = "unknown"

    /**
     * config monitor duration, after this time BlockCanary will stop, use
     * with [BlockCanary]'s isMonitorDurationEnd
     *
     * @return monitor last duration (in hour)
     */
    open fun provideMonitorDuration() = -1

    /**
     * Config block threshold (in millis), dispatch over this duration is regarded as a BLOCK. You may set it
     * from performance of device.
     *
     * @return threshold in mills
     */
    open fun provideBlockThreshold() = 1000L

    /**
     * Thread stack dump interval, use when block happens, BlockCanary will dump on main thread
     * stack according to current sample cycle.
     *
     * Because the implementation mechanism of Looper, real dump interval would be longer than
     * the period specified here (especially when cpu is busier).
     *
     * @return dump interval (in millis)
     */
    open fun provideDumpInterval() = provideBlockThreshold()

    /**
     * path to save log, like "/blockcanary/", will save to sdcard if can.
     *
     * @return path of log files
     */
    open fun providePath() = "/blockcanary/"

    /**
     * if need notification to notice block.
     *
     * @return true if need, else if not need.
     */
    open fun displayNotification() = true

    /**
     * implement in your project, bundle files into a zip file.
     *
     * @param src  files before compress
     * @param dest files compressed
     * @return true if compression is successful
     */
    open fun zip(src: Array<File>, dest: File) = false

    /**
     * implement in your project, bundled log files.
     *
     * @param zippedFile zipped file
     */
    open fun upload(zippedFile: File) {
        throw UnsupportedOperationException()
    }

    /**
     * packages that developer concern, by default it uses process name,
     * put high priority one in pre-order.
     *
     * @return null if simply concern only package with process name.
     */
    open fun concernPackages(): List<String>? {
        return null
    }

    /**
     * filter stack without any in concern package, used with [concernPackages].
     *
     * @return true if filter, false it not.
     */
    open fun filterNonConcernStack() = false

    /**
     * provide white list, entry in white list will not be shown in ui list.
     *
     * @return return null if you don't need white-list filter.
     */
    open fun provideWhiteList(): List<String> {
        return listOf("org.chromium")
    }

    /**
     * whether to delete files whose stack is in white list, used with white-list.
     *
     * @return true if delete, false it not.
     */
    open fun deleteFilesInWhiteList() = true

    /**
     * whether to stop monitoring when in debug mode.
     *
     * @return true if stop, false otherwise
     */
    open fun stopWhenDebugging() = true
}