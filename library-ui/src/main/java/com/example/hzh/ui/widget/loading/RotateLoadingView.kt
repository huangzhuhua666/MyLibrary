package com.example.hzh.ui.widget.loading

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.hzh.ui.R
import com.example.hzh.ui.utils.dp2px

/**
 * Create by hzh on 2020/6/8.
 */
class RotateLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    private lateinit var mLoadingRect: RectF
    private lateinit var mShadowRect: RectF

    private var mLineWidth: Float = 3f.dp2px(context)
    private var mLineColor: Int = Color.WHITE
    private var mShadowOffset: Float = 1f.dp2px(context)
    private var mShadowColor: Int = Color.parseColor("#1a000000")

    private var mChangeBigger: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    private var mArc: Float = 10f
        set(value) {
            field = value
            invalidate()
        }

    private var mTopDegree: Float = 10f
        set(value) {
            field = value
            if (field > 360) field -= 360
        }

    private var mBotDegree: Float = 190f
        set(value) {
            field = value
            if (field > 360) field -= 360
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RotateLoadingView).run {
            mLineWidth = getDimension(R.styleable.RotateLoadingView_lineWidth, mLineWidth)
            mLineColor = getColor(R.styleable.RotateLoadingView_lineColor, mLineColor)

            mShadowOffset = getDimension(R.styleable.RotateLoadingView_shadowOffset, mShadowOffset)
            mShadowColor = getColor(R.styleable.RotateLoadingView_shadowColor, mShadowColor)

            recycle()
        }

        mPaint.strokeWidth = mLineWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wrapWidth = 60f.dp2px(context).toInt()
        val wrapHeight = 60f.dp2px(context).toInt()

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
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mLoadingRect = RectF(
            2 * mLineWidth, 2 * mLineWidth,
            w - 2 * mLineWidth, h - 2 * mLineWidth
        )

        mShadowRect = RectF(
            2 * mLineWidth + mShadowOffset, 2 * mLineWidth + mShadowOffset,
            w - 2 * mLineWidth + mShadowOffset, h - 2 * mLineWidth + mShadowOffset
        )

        mArc = 10f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            mPaint.color = mShadowColor
            drawArc(mShadowRect, mTopDegree, mArc, false, mPaint)
            drawArc(mShadowRect, mBotDegree, mArc, false, mPaint)

            mPaint.color = mLineColor
            drawArc(mLoadingRect, mTopDegree, mArc, false, mPaint)
            drawArc(mLoadingRect, mBotDegree, mArc, false, mPaint)

            try {
                Thread.sleep(2L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            mTopDegree += 10
            mBotDegree += 10

            if (mChangeBigger) {
                if (mArc < 160f) mArc += 2.5f
            } else {
                if (mArc > 10) mArc -= 5f
            }

            if (mArc == 160f || mArc == 10f) mChangeBigger = !mChangeBigger
        }
    }
}