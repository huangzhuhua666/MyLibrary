package com.example.hzh.ui.widget

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.annotation.ColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.hzh.ui.R
import com.example.hzh.ui.utils.dp2px
import com.example.hzh.ui.utils.filterFastClickListener
import com.example.hzh.ui.utils.sp2px

/**
 * Create by hzh on 2020/07/01.
 */
class RunTextViewVertical @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextSwitcher(context, attrs), ViewSwitcher.ViewFactory, LifecycleObserver {

    companion object {

        private const val FLAG_START_AUTO_SCROLL = 0

        private const val FLAG_STOP_AUTO_SCROLL = 1
    }

    private var handler: MyHandler? = null

    /**
     * 字体大小
     */
    var textSize: Float = 16f.sp2px(context)
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 内间距
     */
    var padding: Float = 5f.dp2px(context)
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 字体颜色
     */
    @ColorInt
    var textColor: Int = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 文字进入和退出的时间间隔
     */
    var animDuration: Int = 800
        set(value) {
            field = value
            setFactory { makeView() }
            inAnimation = TranslateAnimation(0f, 0f, value.toFloat(), 0f).apply {
                duration = value.toLong()
                interpolator = AccelerateInterpolator()
            }

            outAnimation = TranslateAnimation(0f, 0f, 0f, -value.toFloat()).apply {
                duration = value.toLong()
                interpolator = AccelerateInterpolator()
            }
        }

    /**
     * 文字停留时长
     */
    var keepTime: Int = 3000

    private var currentIndex = -1

    var textList: List<String> = listOf()
        set(value) {
            handler?.removeMessages(FLAG_START_AUTO_SCROLL)
            field = value
            currentIndex = -1
            handler?.sendEmptyMessage(FLAG_START_AUTO_SCROLL)
        }

    var onClickListener: ((Int) -> Unit)? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.RunTextViewVertical).run {
            textSize = getDimension(R.styleable.RunTextViewVertical_textSize, textSize)

            padding = getDimension(R.styleable.RunTextViewVertical_padding, padding)

            textColor = getColor(R.styleable.RunTextViewVertical_textColor, textColor)

            animDuration = getInteger(R.styleable.RunTextViewVertical_animDuration, animDuration)

            keepTime = getInteger(R.styleable.RunTextViewVertical_keepTime, keepTime)

            recycle()
        }
    }

    override fun makeView(): View = TextView(context).apply {
        gravity = Gravity.CENTER_VERTICAL or Gravity.START
        maxLines = 1
        padding.toInt().let { setPadding(it, it, it, it) }
        setTextColor(textColor)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, this@RunTextViewVertical.textSize)
        isClickable = true

        filterFastClickListener {
            if (textList.isNotEmpty() && currentIndex > -1)
                onClickListener?.invoke(currentIndex % textList.size)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        handler = MyHandler()
        handler?.sendEmptyMessage(FLAG_START_AUTO_SCROLL)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        handler?.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL)
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    @Suppress("HandlerLeak")
    private inner class MyHandler : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                FLAG_START_AUTO_SCROLL -> {
                    if (textList.isNotEmpty()) {
                        ++currentIndex
                        setText(textList[currentIndex % textList.size])
                    }
                    handler?.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, keepTime.toLong())
                }
                FLAG_STOP_AUTO_SCROLL -> {
                    handler?.removeMessages(FLAG_START_AUTO_SCROLL)
                }
            }
        }
    }
}