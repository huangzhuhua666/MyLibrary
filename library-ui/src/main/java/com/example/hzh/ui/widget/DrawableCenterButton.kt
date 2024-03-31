package com.example.hzh.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Create by hzh on 2020/07/24.
 */
class DrawableCenterButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas) {
        val textWidth = paint.measureText(text.toString())

        // DrawableStart
        compoundDrawables[0]?.let {
            val contentWidth = textWidth + compoundDrawablePadding + it.intrinsicWidth + paddingStart + paddingEnd
            canvas.translate((width - contentWidth) / 2, 0f)
        }

        // DrawableEnd
        compoundDrawables[2]?.let {
            val contentWidth = textWidth + compoundDrawablePadding + it.intrinsicWidth
            setPadding(0, paddingTop, (width - contentWidth).toInt(), paddingBottom)
            canvas.translate((width - contentWidth) / 2, 0f)
        }

        super.onDraw(canvas)
    }
}