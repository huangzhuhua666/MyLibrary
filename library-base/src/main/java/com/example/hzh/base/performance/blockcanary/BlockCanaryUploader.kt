package com.example.hzh.base.performance.blockcanary

import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create by hzh on 2024/3/14.
 */
internal object BlockCanaryUploader {

    private val FORMAT by lazy { SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US) }

    fun zipAndUpload() {
        HandlerThreadFactory.getWriteLogThreadHandler().post {
            val file = zip()
            if (file.exists()) {
                BlockCanaryInternal.getContext()?.upload(file)
            }
        }
    }

    private fun zip(): File {
        var timeStr = System.currentTimeMillis().toString()
        try {
            timeStr = FORMAT.format(Date())
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        val zippedFile = LogWriter.generateTempZip("BlockCanary-$timeStr")
        BlockCanaryInternal.getLogFiles()?.let {
            BlockCanaryInternal.getContext()?.zip(it, zippedFile)
        }
        LogWriter.deleteAll()
        return zippedFile
    }
}