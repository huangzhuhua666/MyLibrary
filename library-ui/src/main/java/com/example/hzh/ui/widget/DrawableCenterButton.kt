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

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            val textWidth = paint.measureText(text.toString())

            compoundDrawables[0]?.run { // DrawableStart
                val contentWidth =
                    textWidth + compoundDrawablePadding + intrinsicWidth + paddingStart + paddingEnd
                it.translate((width - contentWidth) / 2, 0f)
            }

            compoundDrawables[2]?.run { // DrawableEnd
                val contentWidth = textWidth + compoundDrawablePadding + intrinsicWidth
                setPadding(0, paddingTop, (width - contentWidth).toInt(), paddingBottom)
                it.translate((width - contentWidth) / 2, 0f)
            }
        }

        super.onDraw(canvas)
    }
}