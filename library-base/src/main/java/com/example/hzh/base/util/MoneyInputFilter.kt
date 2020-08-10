package com.example.hzh.base.util

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import java.util.regex.Pattern

/**
 * Create by hzh on 2020/08/10.
 */
class MoneyInputFilter : InputFilter {

    companion object {

        /**
         * 正则表达式：以0或正整数开头后跟0或1个(小数点后面跟0到2位数字)
         */
        private const val FORMAT = "^(0|[1-9]\\d*)(\\.\\d{0,%s})?$"

        /**
         * 正则表达式：0-9.之外的字符
         */
        private val SOURCE_PATTERN = Pattern.compile("[^0-9.]")

        /**
         * 默认保留小数点后2位
         */
        private var mPattern = Pattern.compile(String.format(FORMAT, "2"))
    }

    /**
     * 允许输入的最大金额
     */
    var maxValue = Double.MAX_VALUE

    /**
     * 当系统使用source的start到end的字串替换dest字符串中的dstart到dend位置的内容时，会调用本方法
     *
     * @param source 新输入的字符串
     * @param start 新输入的字符串起始下标，一般为0（删除时例外）
     * @param end 新输入的字符串终点下标，一般为source长度-1（删除时例外）
     * @param dest 输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为dest长度（删除时例外）
     * @param dend 原内容终点坐标，一般为dest长度（删除时例外）
     * @return 你希望输入的内容，比如当新输入的字符串为“恨”时，你希望把“恨”变为“爱”，则return "爱"
     */
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        // 删除时不用处理
        if (TextUtils.isEmpty(source)) return ""

        source?.let {
            // 不接受数字、小数点之外的字符
            if (SOURCE_PATTERN.matcher(it).find()) return ""

            return SpannableStringBuilder(dest).run {
                replace(dstart, dend, it, start, end)

                val matcher = mPattern.matcher(this)
                matcher.find().yes {
                    val str = matcher.group()

                    //验证输入金额的大小
                    (str.toDouble() > maxValue).yes { "" }.no { it }
                }.no { "" }
            }
        }

        return ""
    }

    /**
     * 设置保留小数点后的位数，默认保留2位
     * @param length
     */
    fun setDecimalLength(length: Int) {
        mPattern = Pattern.compile(String.format(FORMAT, length))
    }
}