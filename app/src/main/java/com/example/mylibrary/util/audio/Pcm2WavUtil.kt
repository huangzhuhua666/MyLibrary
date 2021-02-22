package com.example.mylibrary.util.audio

import android.media.AudioFormat
import android.media.AudioRecord
import com.example.hzh.base.util.closeSafely
import com.example.hzh.base.util.no
import com.example.hzh.base.util.yes
import okio.IOException
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Create by hzh on 2/20/21.
 */
class Pcm2WavUtil(
    private val sampleRate: Int,
    private val channel: Int,
    encoding: Int
) {

    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, encoding)

    fun pcm2wav(originFilename: String, outputFilename: String) = try {
        val fis = FileInputStream(originFilename)
        val fos = FileOutputStream(outputFilename)

        val totalAudioLen = fis.channel.size()
        // 不包括前面8个字节RIFF和WAVE
        val totalDataLen = totalAudioLen + 36
        val channelNum = (channel == AudioFormat.CHANNEL_IN_MONO).yes { 1 }.no { 2 }
        val byteRate = 16 * sampleRate * channelNum / 8
        val buffer = ByteArray(bufferSize)

        writeWaveFileHeader(fos, totalAudioLen, totalDataLen, channelNum, byteRate.toLong())

        while (fis.read(buffer) != -1) {
            fos.write(buffer)
        }

        fis.closeSafely()
        fos.closeSafely()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    /**
     * 添加头部信息，详见"https://blog.csdn.net/u010126792/article/details/86493494"
     */
    @Throws(IOException::class)
    private fun writeWaveFileHeader(
        fos: FileOutputStream,
        totalAudioLen: Long,
        totalDataLen: Long,
        channelNum: Int,
        byteRate: Long
    ) {
        val header = ByteArray(44)

        // 第一"块"头信息
        // RIFF头，固定写法
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        // 数据真正大小，添加了8bit
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8).toByte()
        header[6] = (totalDataLen shr 16).toByte()
        header[7] = (totalDataLen shr 24).toByte()
        // WAVE格式，固定写法
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()

        // 第二"块"头信息
        // 'fmt'块，固定写法，最后一位空格
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        // 4 bytes -> size of 'fmt' chunk，表示PCM/WAVE格式的长度
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        // format = 1，表示格式种类（值为1时，表示数据为线性PCM编码）
        header[20] = 1
        header[21] = 0
        // 通道数，单声道为1，双声道为2
        header[22] = channelNum.toByte()
        header[23] = 0
        // 采样率
        header[24] = (sampleRate.toLong() and 0xff).toByte()
        header[25] = ((sampleRate.toLong() shr 8) and 0xff).toByte()
        header[26] = ((sampleRate.toLong() shr 16) and 0xff).toByte()
        header[27] = ((sampleRate.toLong() shr 24) and 0xff).toByte()
        // 波形数据传输速率，大小为 采样率 * 通道数 * 采样位数
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        // block align，DATA数据块长度，大小为 通道数 * 采样位数
        header[32] = (2 * 16 / 8).toByte()
        header[33] = 0
        // bits per sample，采样位数，即PCM位宽，通常为8bit或16bit
        header[34] = 16
        header[35] = 0

        // 第三"块"头信息
        // data，固定写法，表示数据标记符
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        // 表示接下来声音数据的总大小，需要减去头部的44个字节
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        fos.write(header, 0, 44)
    }
}