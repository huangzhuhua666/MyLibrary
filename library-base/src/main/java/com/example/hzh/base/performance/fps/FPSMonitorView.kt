package com.example.hzh.base.performance.fps

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.hzh.base.R

/**
 * Create by hzh on 2024/3/13.
 */
internal class FPSMonitorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), FPSMonitorViewManager.IFPSMonitorView {

    companion object {

        private const val SHORT_ANIM_DURATION = 200L
        private const val LONG_ANIM_DURATION = 700L
    }

    private val mTvFPS by lazy {
        TextView(context).also {
            it.gravity = Gravity.CENTER
            it.setTextColor(Color.BLACK)
            it.textSize = 19f
        }
    }

    init {
        addView(mTvFPS, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun show(animated: Boolean) {
        if (animated) {
            alpha = 0f
            isVisible = true

            animate().alpha(FPSConfig.curAlpha)
                .setDuration(LONG_ANIM_DURATION)
                .setListener(null)
        } else {
            alpha = FPSConfig.curAlpha
            isVisible = true
        }
    }

    override fun hide(animated: Boolean) {
        if (animated) {
            animate().alpha(0f)
                .setDuration(SHORT_ANIM_DURATION)
                .setListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        isGone = true
                    }
                })
        } else {
            isGone = true
        }
    }

    override fun update(slice: FPSSlice) {
        val backgroundRes = when (slice.metric) {
            FPSSlice.Metric.Good -> {
                R.drawable.base_bg_fps_metric_good
            }
            FPSSlice.Metric.Medium -> {
                R.drawable.base_bg_fps_metric_medium
            }
            FPSSlice.Metric.Bad -> {
                R.drawable.base_bg_fps_metric_bad
            }
        }

        setBackgroundResource(backgroundRes)
        mTvFPS.text = slice.fps.toString()
    }

    override fun getView(): View = this
}