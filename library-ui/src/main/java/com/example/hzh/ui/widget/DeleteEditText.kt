package com.example.hzh.ui.widget

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import com.example.hzh.ui.R

/**
 * Create by hzh on 2020/6/16.
 */
class DeleteEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private val mIconStart: Drawable?
    private val mIconEnd: Drawable?

    init {
        context.obtainStyledAttributes(attrs, R.styleable.DeleteEditText).run {
            mIconStart = getDrawable(R.styleable.DeleteEditText_iconStart)
            mIconEnd = getDrawable(R.styleable.DeleteEditText_iconEnd)

            recycle()
        }

        setDrawable()

        doAfterTextChanged { setDrawable() }

        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
    }

    private fun setDrawable() {
        if (length() == 0)
            setCompoundDrawablesWithIntrinsicBounds(mIconStart, null, null, null)
        else
            setCompoundDrawablesWithIntrinsicBounds(mIconStart, null, mIconEnd, null)
    }

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mIconEnd?.run {
            event?.run {
                if (action == MotionEvent.ACTION_UP) {
                    val rect = Rect()
                    getGlobalVisibleRect(rect)
                    rect.left = rect.right - paddingRight - intrinsicWidth
                    if (rect.contains(rawX.toInt(), rawY.toInt())) setText("")
                }
            }
        }
        return super.onTouchEvent(event)
    }
}