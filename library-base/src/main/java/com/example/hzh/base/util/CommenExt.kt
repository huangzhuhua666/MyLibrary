package com.example.hzh.base.util

import android.content.Context
import android.os.Build
import android.util.TypedValue
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create by hzh on 2019/09/05.
 */
const val MM_dd = "M-d"
const val yyyy_M_d = "yyyy-M-d"
const val yyyy_MM_dd = "yyyy-MM-dd"

fun targetVersion(version: Int, after: () -> Unit = {}, before: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= version) after()
    else before()
}

fun Char.digit(radix: Int): Int = Character.digit(this, radix)

fun Float.dp2px(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
)

fun Float.sp2px(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    context.resources.displayMetrics
)

fun Double.roundAndScale(scale: Int): Double =
    this.toBigDecimal().setScale(scale, RoundingMode.HALF_UP).toDouble()

/**
 * 当前时间与给定时间的“距离”
 */
fun Long.dateDistance(): String {
    val current = System.currentTimeMillis()
    var diff = (current - this) / 1000

    return when {
        diff / 60 == 0L -> "刚刚"
        diff / 60 / 60 == 0L -> "${diff / 60}分钟前"
        diff / 60 / 60 / 24 == 0L -> "${diff / 60 / 60}小时前"
        diff / 60 / 60 / 24 / 2 == 0L -> "昨天"
        else -> {
            diff = diff / 60 / 60 / 24
            if (diff <= 20) "${diff}天前" else this.formatDate(yyyy_M_d)
        }
    }
}

/**
 * 时间格式化
 */
fun Long.formatDate(style: String): String =
    SimpleDateFormat(style, Locale.getDefault()).format(this)

/**
 * 存储大小格式化
 */
fun Long.formatSize(): String {
    val kb = toDouble() / 1024
    val mb = kb / 1024
    if (mb < 1) return "${BigDecimal(kb).setScale(2, BigDecimal.ROUND_HALF_UP)}KB"

    val gb = mb / 1024
    if (gb < 1) return "${BigDecimal(mb).setScale(2, BigDecimal.ROUND_HALF_UP)}MB"

    val tb = gb / 1024
    if (tb < 1) return "${BigDecimal(gb).setScale(2, BigDecimal.ROUND_HALF_UP)}GB"

    return "${BigDecimal(tb).setScale(2, BigDecimal.ROUND_HALF_UP)}TB"
}

fun String.hexStringToByteArray(): ByteArray {
    val len = length
    val data = ByteArray(len / 2)
    for (i in 0 until len step 2) {
        data[i / 2] = ((this[i].digit(16) shl 4) + this[i + 1].digit(16)).toByte()
    }
    return data
}

fun String.isRealNotEmpty(): Boolean = trim().let { it.isNotEmpty() && it != " " && it != "null" }

fun String.isRealEmpty(): Boolean = !isRealNotEmpty()

fun ByteArray.byteArrayToHexString(): String {
    val sb = StringBuilder(size * 2)
    forEach {
        val v = it.toInt() and 0xff
        if (v < 16) sb.append("0")
        sb.append(Integer.toHexString(v))
    }
    return sb.toString().toUpperCase(Locale.US)
}