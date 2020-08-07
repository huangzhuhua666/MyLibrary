package com.example.hzh.ui.widget.loading

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.hzh.ui.R
import com.example.hzh.ui.animator.dsl.ValueAnim
import com.example.hzh.ui.animator.dsl.valueAnim
import com.example.hzh.ui.utils.dp2px
import kotlin.properties.Delegates

/**
 * Create by hzh on 2020/6/5.
 */
class BarLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private var mLineWidth by Delegates.notNull<Float>()
    private var mLineMargin by Delegates.notNull<Float>()
    private val mLineRatioList by lazy { MutableList(4) { Math.random().toFloat() } }

    private var mAnim: ValueAnim? = null

    init {
        var lineColor: Int = Color.parseColor("#ff0000")

        context.obtainStyledAttributes(attrs, R.styleable.BarLoadingView).run {
            lineColor = getColor(R.styleable.BarLoadingView_lineColor, lineColor)

            recycle()
        }

        mPaint.color = lineColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wrapWidth = 16f.dp2px(context).toInt()
        val wrapHeight = 16f.dp2px(context).toInt()

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        layoutParams.run {
            if (width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                height == ViewGroup.LayoutParams.WRAP_CONTENT
            ) setMeasuredDimension(wrapWidth, wrapHeight)
            else if (width == ViewGroup.LayoutParams.WRAP_CONTENT)
                setMeasuredDimension(wrapWidth, heightSize)
            else if (height == ViewGroup.LayoutParams.WRAP_CONTENT)
                setMeasuredDimension(widthSize, wrapHeight)
        }

        mLineWidth = measuredWidth.toFloat() / 12
        mLineMargin = (measuredWidth - 4 * mLineWidth) / 4
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            mPaint.strokeWidth = mLineWidth

            for (i in 0..3) {
                val x = (2 * i + 1) * (mLineMargin + mLineWidth) / 2
                drawLine(x, height.toFloat(), x, height * mLineRatioList[i], mPaint)
            }
        }
    }

    fun startAnim() {
        mAnim?.cancel()

        if (visibility != VISIBLE) return

        mAnim = valueAnim {
            values = arrayOf(0f, 1f)
            duration = 180
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE

            onRepeat = {
                for (i in 0..3) {
                    mLineRatioList[i] = Math.random().toFloat()

                    invalidate()
                }
            }
        }.apply { start() }
    }

    fun stopAnim() {
        mAnim?.cancel()
        mAnim = null
    }
}