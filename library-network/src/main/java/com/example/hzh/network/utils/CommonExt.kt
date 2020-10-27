package com.example.hzh.network.utils

import rxhttp.wrapper.param.RxHttpJsonParam
import java.util.*

/**
 * Create by hzh on 2020/5/7.
 */
internal fun Char.digit(radix: Int): Int = Character.digit(this, radix)

internal fun String.hexStringToByteArray(): ByteArray {
    val len = length
    val data = ByteArray(len / 2)
    for (i in 0 until len step 2) {
        data[i / 2] = ((this[i].digit(16) shl 4) + this[i + 1].digit(16)).toByte()
    }
    return data
}

internal fun ByteArray.byteArrayToHexString(): String {
    val sb = StringBuilder(size * 2)
    forEach {
        val v = it.toInt() and 0xff
        if (v < 16) sb.append("0")
        sb.append(Integer.toHexString(v))
    }
    return sb.toString().toUpperCase(Locale.US)
}

fun RxHttpJsonParam.addPairs(vararg pairs: Pair<String, Any>): RxHttpJsonParam {
    pairs.forEach { add(it.first, it.second) }
    return this
}