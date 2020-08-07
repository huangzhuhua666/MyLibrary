package com.example.hzh.base.util

import java.io.*

/**
 * Create by hzh on 2019/09/06.
 */
fun InputStream.toByteArray(): ByteArray = try {
    ByteArrayOutputStream().use {
        it.write(this, 4096)
        it.toByteArray()
    }
} catch (e: IOException) {
    throw e
} finally {
    closeSafely()
}

fun OutputStream.write(inputStream: InputStream, size: Int = 1024, block: (Int) -> Unit = {}) =
    try {
        var len: Int
        val buffer = ByteArray(size)
        while (inputStream.read(buffer).also { len = it } != -1) {
            write(buffer, 0, len)
            flush()
            block(len)
        }
    } catch (e: IOException) {
        throw e
    } finally {
        closeSafely()
    }

fun Closeable.closeSafely() = try {
    close()
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * 获取文件夹大小
 */
@Throws(IOException::class)
fun File.getFolderSize(): Long {
    var size = 0L
    try {
        listFiles()?.forEach {
            size += it.isDirectory.yes { it.getFolderSize() }.no { it.length() }
        }
        return size
    } catch (e: IOException) {
        throw e
    }
}

/**
 * 删除文件
 */
fun File.deleteFile(): Boolean {
    isDirectory.yes {
        listFiles()?.forEach {
            val success = it.deleteFile()
            (!success).yes { return false }
        }
    }

    return delete()
}