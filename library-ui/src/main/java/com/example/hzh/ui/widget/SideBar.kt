package com.example.hzh.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import com.example.hzh.ui.R
import com.example.hzh.ui.utils.sp2px

/**
 * Create by hzh on 2020/07/20.
 */
class SideBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mCategory by lazy {
        listOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
        )
    }

    private val mPaint by lazy { Paint() }

    private var checkIndex = -1
    private val normalTextColor: Int
    private val pressTextColor: Int
    private val textSize: Float

    var textDialog: TextView? = null
    var onTouchingLetterChanged: ((String) -> Unit)? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SideBar).let {
            normalTextColor = it.getColor(
                R.styleable.SideBar_normalTextColor,
                Color.parseColor("#ff2dd0cf")
            )
            pressTextColor = it.getColor(
                R.styleable.SideBar_pressTextColor,
                Color.parseColor("#ff3399ff")
            )
            textSize = it.getDimension(R.styleable.SideBar_textSize, 12f.sp2px(context))
            it.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val realHeight = height - paddingTop - paddingBottom // item内容总的高度
        val itemHeight = realHeight / mCategory.size // 每一个item的高度

        mCategory.forEachIndexed { index, str ->
            mPaint.let {
                it.typeface = Typeface.DEFAULT
                it.isAntiAlias = true
                it.textSize = this@SideBar.textSize
                it.color = if (index == checkIndex) pressTextColor else normalTextColor
                it.isFakeBoldText = index == checkIndex // 粗体

                val x = width / 2f - it.measureText(str) / 2f
                val y = paddingTop + itemHeight * (index + 1)
                canvas.drawText(str, x, y.toFloat(), it)
                it.reset()
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.run {
            if (y < paddingTop || y > height - paddingBottom) {
                checkIndex = -1
                invalidate()
                textDialog?.isGone = true
                return false
            }

            if (action == MotionEvent.ACTION_UP) {
                checkIndex = -1
                invalidate()
                textDialog?.isGone = true
            } else {
                val currCheck =
                    ((y - paddingTop) / (height - paddingTop - paddingBottom) * mCategory.size).toInt()
                if (currCheck != checkIndex && currCheck in mCategory.indices) {
                    checkIndex = currCheck

                    onTouchingLetterChanged?.invoke(mCategory[checkIndex])

                    textDialog?.run {
                        text = mCategory[checkIndex]
                        isGone = false
                    }

                    invalidate()
                }
            }
        }
        return true
    }
}