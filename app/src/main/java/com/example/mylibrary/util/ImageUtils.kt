package com.example.mylibrary.util

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.FloatRange

/**
 * Create by hzh on 4/11/21.
 */
fun Context.gaussianBlur(
    @FloatRange(from = 1.0, to = 25.0) blurRadius: Float,
    origin: Bitmap
): Bitmap {
    val renderScript = RenderScript.create(this) // 创建RenderScript内核对象

    // RenderScript并没有使用VM来分配内存，所以需要使用Allocation类来创建和分配内存空间
    // 创建Allocation对象的时候内存是空的，需要使用copyTo()将数据填充进去
    val input = Allocation.createFromBitmap(renderScript, origin)
    val output = Allocation.createTyped(renderScript, input.type) // 创建相同类型的Allocation对象用来输出

    // 创建一个高斯模糊效果的RenderScript的工具对象
    // 第二个参数Element相当于一种像素处理的算法，高斯模糊用U8_4
    val blurScrip = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).apply {
        setRadius(blurRadius) // 设置渲染的模糊程度，25f是最大模糊度
        setInput(input) // 设置BlurScript对象的输入内存
        forEach(output) // 将输出数据保存到输出内存中
    }

    output.copyTo(origin) // 将数据填充到Bitmap中

    // 销毁它们释放内存
    input.destroy()
    output.destroy()
    blurScrip.destroy()
    renderScript.destroy()

    return origin
}