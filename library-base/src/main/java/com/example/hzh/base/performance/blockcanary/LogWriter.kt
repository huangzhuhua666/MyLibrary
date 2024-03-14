package com.example.hzh.base.performance.blockcanary

import com.example.hzh.base.performance.blockcanary.data.BlockInfo
import com.example.hzh.base.util.closeSafely
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create by hzh on 2024/3/14.
 */
internal object LogWriter {

    private const val OBSOLETE_DURATION = 2 * 24 * 60 * 60 * 1000L
    private val FILE_NAME_FORMATTER = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.US)
    private val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    private val mLock by lazy { Object() }

    /**
     * save log to file
     *
     * @param str block info string
     * @return log file path
     */
    fun save(str: String): String {
        synchronized(mLock) {
            return save("looper", str)
        }
    }

    private fun save(logFileName: String, str: String): String {
        var path = ""
        var writer: BufferedWriter? = null

        try {
            val file = BlockCanaryInternal.detectedBlockDirectory()
            val time = System.currentTimeMillis()

            path = "${file.absolutePath}/$logFileName-${FILE_NAME_FORMATTER.format(time)}.log"

            val out = OutputStreamWriter(FileOutputStream(path, true), "UTF-8")
            writer = BufferedWriter(out).also {
                it.write(BlockInfo.SEPARATOR)
                it.write("**********************")
                it.write(BlockInfo.SEPARATOR)
                it.write("${TIME_FORMATTER.format(time)}(write log time)")
                it.write(BlockInfo.SEPARATOR)
                it.write(BlockInfo.SEPARATOR)
                it.write(str)
                it.write(BlockInfo.SEPARATOR)

                it.flush()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            writer?.closeSafely()
        }

        return path
    }

    /**
     * delete obsolete log files, which is by default 2 days.
     */
    fun cleanObsolete() {
        HandlerThreadFactory.getWriteLogThreadHandler().post {
            val now = System.currentTimeMillis()

            BlockCanaryInternal.getLogFiles()?.let {
                if (it.isEmpty()) {
                    return@post
                }

                synchronized(mLock) {
                    it.asSequence()
                        .filter { file ->
                            now - file.lastModified() > OBSOLETE_DURATION
                        }
                        .forEach { file ->
                            file.delete()
                        }
                }
            }
        }
    }

    fun deleteAll() {
        synchronized(mLock) {
            try {
                BlockCanaryInternal.getLogFiles()?.let {
                    if (it.isEmpty()) {
                        return
                    }

                    it.forEach { file ->
                        file.delete()
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun generateTempZip(filename: String) = File("${BlockCanaryInternal.getPath()}/$filename.zip")
}