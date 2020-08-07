package com.example.hzh.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.hzh.ui.R
import com.example.hzh.ui.animator.dsl.ValueAnim
import com.example.hzh.ui.animator.dsl.valueAnim
import com.example.hzh.ui.utils.dp2px

/**
 * Create by hzh on 2020/07/23.
 */
class RWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), LifecycleObserver {

    private val mBgPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
    }

    private val mBorderPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val mTextPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val mTextPath by lazy { Path() }

    private val mWavePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val mWavePath by lazy { Path() }
    private val mCirclePath by lazy { Path() }

    private var mHorizonAnim: ValueAnim? = null
    private var mVerticalAnim: ValueAnim? = null

    private var waveWidth = 0f
    private var waveHeight = 0f
    private var horizonOffset = 0f
        set(value) {
            field = value
            postInvalidate()
        }
    private var verticalOffset = 0f
    private var progressControl = 0

    var progress: Int = 0
        set(value) {
            if (value > 100) return
            updateVerticalOffset(field, value)
            field = value
        }

    /**
     * 波浪上方字体颜色
     */
    @ColorInt
    var textColorAbove: Int = Color.parseColor("#ffff9900")
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 被波浪覆盖的字体颜色
     */
    @ColorInt
    var teColorBelow = Color.parseColor("#ffffffff")
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 波浪颜色
     */
    @ColorInt
    var waveColor = Color.parseColor("#ffff9900")
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 边框宽度
     */
    var borderWidth: Float = 1f.dp2px(context)
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 边框颜色
     */
    @ColorInt
    var borderColor: Int = Color.parseColor("#ffff9900")
        set(value) {
            field = value
            invalidate()
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RWaveView).run {
            //波浪上方字体颜色
            textColorAbove = getColor(R.styleable.RWaveView_textColorAbove, textColorAbove)

            // 被波浪覆盖的字体颜色
            teColorBelow = getColor(R.styleable.RWaveView_textColorBelow, teColorBelow)

            // 波浪颜色
            waveColor = getColor(R.styleable.RWaveView_waveColor, waveColor)

            // 边框宽度
            borderWidth = getDimension(R.styleable.RWaveView_borderWidth, borderWidth)

            // 边框颜色
            borderColor = getColor(R.styleable.RWaveView_borderColor, borderColor)

            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wrapWidth = 160f.dp2px(context).toInt()
        val wrapHeight = 160f.dp2px(context).toInt()

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        layoutParams.run {
            if (width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                height == ViewGroup.LayoutParams.WRAP_CONTENT
            ) setMeasuredDimension(wrapWidth, wrapHeight)
            else if (width == ViewGroup.LayoutParams.WRAP_CONTENT)
                setMeasuredDimension(heightSize, heightSize)
            else if (height == ViewGroup.LayoutParams.WRAP_CONTENT)
                setMeasuredDimension(widthSize, widthSize)
        }

        waveWidth = measuredWidth.toFloat()
        waveHeight = measuredWidth / 8f
        verticalOffset = measuredHeight.toFloat()
        updateHorizonOffset()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            drawCircle(width / 2f, height / 2f, width / 2f, mBgPaint)

            // 画边框
            mBorderPaint.color = borderColor
            mBorderPaint.strokeWidth = borderWidth
            drawCircle(width / 2f, height / 2f, (width - borderWidth) / 2f, mBorderPaint)

            // 画波浪上方的文字
            mTextPaint.apply {
                color = textColorAbove
                textSize = waveWidth / 8
                val txt = "$progressControl%"
                val textX = (width - measureText(txt)) / 2
                val textY = (height + descent() - ascent()) / 2
                mTextPath.reset()
                getTextPath(txt, 0, txt.length, textX, textY, mTextPath)
            }
            drawPath(mTextPath, mTextPaint)

            val itemWidth = waveWidth / 2f
            verticalOffset = height / 100f * (100 - progressControl)
            mWavePath.run {
                reset()
                moveTo(-itemWidth * 3, height / 2f)

                for (i in -3..1) {
                    val startX = i * itemWidth
                    quadTo(
                        startX + itemWidth / 2 + horizonOffset, getWaveHeight(i),
                        startX + itemWidth + horizonOffset, verticalOffset
                    )
                }

                lineTo(width.toFloat(), height.toFloat())
                lineTo(0f, height.toFloat())
                close()
            }
            mCirclePath.reset()
            mCirclePath.addCircle(width / 2f, height / 2f, (width - borderWidth) / 2f, Path.Direction.CW)
            clipPath(mCirclePath)
            mWavePaint.color = waveColor
            drawPath(mWavePath, mWavePaint)

            // 画被波浪覆盖的文字
            mTextPaint.color = teColorBelow
            clipPath(mWavePath)
            drawPath(mTextPath, mTextPaint)
        }
    }

    private fun getWaveHeight(i: Int): Float = if (i % 2 == 0) verticalOffset - waveHeight
    else verticalOffset + waveHeight

    fun updateHorizonOffset() {
        mHorizonAnim?.cancel()
        mHorizonAnim = valueAnim {
            values = floatArrayOf(0f, waveWidth)
            interpolator = LinearInterpolator()
            duration = 1000L
            repeatCount = ValueAnimator.INFINITE
            action = { horizonOffset = it as Float }
        }.apply { start() }
    }

    private fun updateVerticalOffset(oldVal: Int, newVal: Int) {
        mVerticalAnim?.cancel()
        mVerticalAnim = valueAnim {
            values = intArrayOf(oldVal, newVal)
            interpolator = DecelerateInterpolator()
            duration = 500L
            action = { progressControl = it as Int }
        }.apply { start() }
    }

    fun cancelAllAnim() {
        mHorizonAnim?.cancel()
        mHorizonAnim = null

        mVerticalAnim?.cancel()
        mVerticalAnim = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        updateHorizonOffset()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        cancelAllAnim()
    }
}