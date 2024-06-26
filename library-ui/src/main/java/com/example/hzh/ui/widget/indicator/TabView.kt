package com.example.hzh.ui.widget.indicator

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.createBitmap
import android.graphics.Paint.*
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.toBitmap
import com.example.hzh.ui.R
import com.example.hzh.ui.utils.dp2px
import kotlin.math.ceil
import kotlin.math.min

/**
 * Create by hzh on 2019/9/24.
 */
class TabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var mAlpha = 0.0f // 当前的透明度
        set(value) {
            if (value in 0.0f..1.0f) {
                field = value
                invalidateView()
            } else throw IllegalArgumentException("透明度必须是 0.0 - 1.0")
        }

    private var mPadding = 5 //文字和图片之间的距离5dp

    private var mText: String? = null // 描述文本
    private var mTextSize = 12 // 描述文本的默认字体大小12sp

    @ColorInt
    private var mTextColorNormal = Color.parseColor("#999999") // 描述文本的默认显示颜色
    @ColorInt
    private var mTextColorSelect = Color.parseColor("#46c01b") // 述文本的默认选中显示颜色

    private var mIconNormal: Bitmap? = null // 默认图标
    private var mIconSelect: Bitmap? = null // 选中的图标

    private val mSelectPaint by lazy { Paint() } // 背景的画笔
    private val mIconAvailableRect by lazy { Rect() } // 图标可用的绘制区域
    private val mIconDrawRect by lazy { Rect() } // 图标真正的绘制区域

    private val mTextPaint by lazy { Paint(ANTI_ALIAS_FLAG) } // 描述文本的画笔
    private val mTextBound by lazy { Rect() } // 描述文本矩形测量大小
    private var mFmi: FontMetricsInt? = null // 用于获取字体的各种属性

    private var isShowRemove = false // 是否移除当前角标

    var isShowDot = false // 是否显示圆点
        private set

    var badgeNum = 0 // 角标数
        set(value) {
            field = value
            isShowRemove = field == 0
            isShowDot = false
            invalidate()
        }

    @ColorInt
    private var mBadgeBackgroundColor = Color.parseColor("#ff0000") // 默认红颜色

    init {
        mPadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            mPadding.toFloat(),
            resources.displayMetrics
        ).toInt()

        mTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            mTextSize.toFloat(),
            resources.displayMetrics
        ).toInt()

        context.obtainStyledAttributes(attrs, R.styleable.TabView).let {
            mText = it.getString(R.styleable.TabView_tabText)
            mTextSize = it.getDimensionPixelSize(R.styleable.TabView_tabTextSize, mTextSize)
            mTextColorNormal = it.getColor(R.styleable.TabView_tabTextColorNormal, mTextColorNormal)
            mTextColorSelect = it.getColor(R.styleable.TabView_tabTextColorSelect, mTextColorSelect)

            it.getDrawable(R.styleable.TabView_tabIconNormal)?.let { drawable ->
                mIconNormal = drawable.toBitmap()
            }

            it.getDrawable(R.styleable.TabView_tabIconSelect)?.let { drawable ->
                mIconSelect = drawable.toBitmap()
            }

            mBadgeBackgroundColor = it.getColor(
                R.styleable.TabView_badgeBackgroundColor,
                mBadgeBackgroundColor
            )

            mPadding = it.getDimensionPixelSize(R.styleable.TabView_paddingTextWithIcon, mPadding)

            it.recycle()
        }

        initText()
    }

    /**
     * 如果有设置文字就获取文字的区域大小
     */
    private fun initText() {
        mText?.run {
            mTextPaint.textSize = mTextSize.toFloat()
            mTextPaint.isDither = true
            mTextPaint.getTextBounds(mText, 0, mText!!.length, mTextBound)
            mFmi = mTextPaint.fontMetricsInt
        }
    }

    fun showDot() {
        isShowRemove = false
        badgeNum = -1
        isShowDot = true

        invalidate()
    }

    fun removeShow() {
        badgeNum = 0
        isShowDot = false
        isShowRemove = true

        invalidate()
    }

    /**
     * 根据当前所在线程更新界面
     */
    private fun invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) invalidate()
        else postInvalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mText != null || (mIconNormal != null && mIconSelect != null)) {
            // 计算出可用绘图的区域
            val availableWidth = measuredWidth - paddingLeft - paddingRight
            var availableHeight = measuredHeight - paddingTop - paddingBottom

            if (mText != null && mIconNormal != null) {
                availableHeight -= mTextBound.height() + mPadding
                // 计算出图标可以绘制的画布大小
                mIconAvailableRect.set(
                    paddingLeft,
                    paddingTop,
                    paddingLeft + availableWidth,
                    paddingTop + availableHeight
                )

                // 计算文字的绘图区域
                val textLeft = paddingLeft + (availableWidth - mTextBound.width()) / 2
                val textTop = mIconAvailableRect.bottom + mPadding
                mTextBound.set(
                    textLeft,
                    textTop,
                    textLeft + mTextBound.width(),
                    textTop + mTextBound.height()
                )
            } else if (mText == null) {
                // 计算出图标可以绘制的画布大小
                mIconAvailableRect.set(
                    paddingLeft,
                    paddingTop,
                    paddingLeft + availableWidth,
                    paddingTop + availableHeight
                )
            } else if (mIconNormal == null) {
                // 计算文字的绘图区域
                val textLeft = paddingLeft + (availableWidth - mTextBound.width()) / 2
                val textTop = paddingTop + (availableHeight - mTextBound.height()) / 2
                mTextBound.set(
                    textLeft,
                    textTop,
                    textLeft + mTextBound.width(),
                    textTop + mTextBound.height()
                )
            }
        } else throw IllegalArgumentException("必须设置tabText或者tabIconSelected、tabIconNormal两个")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val alpha = ceil(mAlpha * 255.toDouble()).toInt()

        if (mIconNormal != null && mIconSelect != null) {
            availableToDrawRect(mIconAvailableRect, mIconNormal)

            mSelectPaint.let {
                it.reset()
                it.isAntiAlias = true // 设置抗锯齿
                it.isFilterBitmap = true // 抗锯齿
                it.alpha = 255 - alpha
                canvas.drawBitmap(mIconNormal!!, null, mIconDrawRect, mSelectPaint)

                it.reset()
                it.isAntiAlias = true // 设置抗锯齿
                it.isFilterBitmap = true // 抗锯齿
                it.alpha = alpha
                canvas.drawBitmap(mIconSelect!!, null, mIconDrawRect, mSelectPaint)
            }
        }

        mText?.let { text ->
            mTextPaint.let {
                // 绘制原始文字
                it.color = mTextColorNormal
                it.alpha = 255 - alpha
                // 由于在该方法中，y轴坐标代表的是baseLine的值，mTextBound.height() + mFmi.bottom就是字体的高
                // 所以在最后绘制前，修正偏移量，将文字向上修正mFmi.bottom / 2即可实现垂直居中
                canvas.drawText(
                    text,
                    mTextBound.left.toFloat(),
                    mTextBound.bottom - mFmi!!.bottom / 2.toFloat(),
                    mTextPaint
                )

                // 绘制变色文字
                it.color = mTextColorSelect
                it.alpha = alpha
                canvas.drawText(
                    text,
                    mTextBound.left.toFloat(),
                    mTextBound.bottom - mFmi!!.bottom / 2.toFloat(),
                    mTextPaint
                )
            }
        }

        // 绘制角标
        if (!isShowRemove) {
            drawBadge(canvas)
        }
    }

    private fun availableToDrawRect(availableRect: Rect, bitmap: Bitmap?) {
        bitmap ?: return

        var dx = 0f
        var dy = 0f
        val wRatio = availableRect.width() * 1.0f / bitmap.width
        val hRatio = availableRect.height() * 1.0f / bitmap.height

        if (wRatio > hRatio) dx = (availableRect.width() - hRatio * bitmap.width) / 2
        else dy = (availableRect.height() - wRatio * bitmap.height) / 2

        val left = (availableRect.left + dx + .5f).toInt()
        val top = (availableRect.top + dy + .5f).toInt()
        val right = (availableRect.right - dx + .5f).toInt()
        val bottom = (availableRect.bottom - dy + .5f).toInt()
        mIconDrawRect.set(left, top, right, bottom)
    }

    private fun drawBadge(c: Canvas) {
        var i = min(measuredWidth / 14, measuredHeight / 9)

        if (badgeNum > 0) {
            val bgPaint = Paint(ANTI_ALIAS_FLAG).apply { color = mBadgeBackgroundColor }
            val number = if (badgeNum > 99) "99+" else badgeNum.toString()
            val textSize = if (i / 1.5f == 0f) 5.0f else i / 1.5f

            val width: Int
            val height = i.toFloat().dp2px(context).toInt()

            val bitmap: Bitmap = when (number.length) {
                1 -> {
                    width = i.toFloat().dp2px(context).toInt()
                    createBitmap(width, height, Config.ARGB_8888)
                }
                2 -> {
                    width = (i + 5f).dp2px(context).toInt()
                    createBitmap(width, height, Config.ARGB_8888)
                }
                else -> {
                    width = (i + 8f).dp2px(context).toInt()
                    createBitmap(width, height, Config.ARGB_8888)
                }
            }

            val canvasMessages = Canvas(bitmap)
            val messageRectF = RectF(0.0f, 0.0f, width.toFloat(), height.toFloat())
            canvasMessages.drawRoundRect(messageRectF, 50f, 50f, bgPaint) // 画椭圆

            val numberPaint = Paint(ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                this.textSize = textSize.dp2px(context)
                textAlign = Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }
            val fontMetrics = numberPaint.fontMetrics
            val x = width / 2.0f
            val y = fontMetrics.let { height / 2.0f - it.descent + (it.descent - it.ascent) / 2 }
            canvasMessages.drawText(number, x, y, numberPaint)

            val left = measuredWidth / 10 * 6.0f
            val top = 5f.dp2px(context)
            c.drawBitmap(bitmap, left, top, null)
            bitmap.recycle()
        } else {
            if (isShowDot) {
                val paint = Paint(ANTI_ALIAS_FLAG).apply { color = mBadgeBackgroundColor }
                val left = measuredWidth / 10 * 6.0f
                val top = 5f.dp2px(context)
                i = if (i > 10) 10 else i

                val width = i.toFloat().dp2px(context)
                val messageRectF = RectF(left, top, left + width, top + width)
                c.drawOval(messageRectF, paint)
            }
        }
    }
}