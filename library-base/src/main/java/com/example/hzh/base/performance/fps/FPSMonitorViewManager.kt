package com.example.hzh.base.performance.fps

import android.app.Activity
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.hzh.base.Global
import com.example.hzh.base.manager.ActivityRecordMgr
import com.example.hzh.base.util.ActivityLifeCycleCallbackAdapter
import com.example.hzh.base.util.ResourcesUtils
import com.example.hzh.base.util.ThreadPoolUtils
import java.lang.ref.WeakReference
import java.util.*

/**
 * Create by hzh on 2024/3/13.
 */
internal class FPSMonitorViewManager {

    private var mIsEnable: Boolean = false
    private var mActivityRecordMap = WeakHashMap<Activity, WeakReference<IFPSMonitorView>>()

    init {
        Global.getApplication().registerActivityLifecycleCallbacks(object : ActivityLifeCycleCallbackAdapter() {

            override fun onActivityStarted(activity: Activity) {
                if (!mIsEnable || mActivityRecordMap[activity]?.get() != null) {
                    return
                }

                addViewToRootView(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                val iFpsView = mActivityRecordMap[activity]?.get()
                if (!mIsEnable || iFpsView == null) {
                    return
                }

                iFpsView.getView().let {
                    it.alpha = FPSConfig.curAlpha
                    (it.layoutParams as? ViewGroup.MarginLayoutParams)?.let { params ->
                        params.leftMargin = FPSConfig.curXPosition
                        params.topMargin = FPSConfig.curYPosition
                    }
                }
            }
        })

        ThreadPoolUtils.executeOnNewThread {
            val curSystemTime = System.currentTimeMillis()
            var passTime = (System.currentTimeMillis() - curSystemTime).toInt()
            var lastPassTime = 0
            while (passTime >= 0) {
                if (lastPassTime != passTime && passTime % 15 == 0) {
                    mActivityRecordMap.values.toList().forEach {
                        it.get()?.getView()?.postInvalidate()
                    }
                }

                lastPassTime = passTime
                passTime = (System.currentTimeMillis() - curSystemTime).toInt()
            }
        }
    }

    fun notifyFPSDataChanged(slice: FPSSlice) {
        mActivityRecordMap.values.forEach {
            it.get()?.update(slice)
        }
    }

    fun show() {
        mIsEnable = true
        mActivityRecordMap.forEach {
            val view = it.value.get()
            val activity = it.key
            if (view == null) {
                addViewToRootView(activity)
            } else {
                if (ActivityRecordMgr.getInstance().getTopActivity() == activity) {
                    view.show(true)
                } else {
                    view.show(false)
                }
            }
        }
    }

    fun hide() {
        mIsEnable = false
        mActivityRecordMap.forEach {
            val view = it.value.get()
            val activity = it.key
            if (ActivityRecordMgr.getInstance().getTopActivity() == activity) {
                view?.hide(true)
            } else {
                view?.hide(false)
            }
        }
    }

    private fun addViewToRootView(activity: Activity?) {
        activity ?: return

        val params = FrameLayout.LayoutParams(ResourcesUtils.dp2px(40), ResourcesUtils.dp2px(40)).also {
            it.leftMargin = FPSConfig.curXPosition
            it.topMargin = FPSConfig.curXPosition
        }
        val fpsMonitorView = generateFPSMonitorView(activity).also {
            it.alpha = FPSConfig.curAlpha
            it.isHapticFeedbackEnabled = false

            val gestureDetector = GestureDetector(
                it.context,
                FPSMonitorViewGestureDetectorListener(it)
            )
            it.setOnTouchListener(FPSMonitorViewTouchListener(params, gestureDetector))
        }

        getRootView(activity).let {
            it.post {
                it.addView(fpsMonitorView, params)
                fpsMonitorView.show(true)
            }
        }

        (fpsMonitorView as? IFPSMonitorView)?.let {
            mActivityRecordMap[activity] = WeakReference(it)
        }
    }

    private fun getRootView(activity: Activity) = activity.findViewById<FrameLayout>(android.R.id.content)

    private fun generateFPSMonitorView(context: Context) = FPSMonitorView(context)

    private class FPSMonitorViewGestureDetectorListener(val view: View) : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent): Boolean {
            view.alpha = if (view.alpha != 1f) {
                1f
            } else {
                0.3f
            }
            FPSConfig.curAlpha = view.alpha
            return super.onDoubleTap(e)
        }
    }

    private class FPSMonitorViewTouchListener(
        private val layoutParams: ViewGroup.MarginLayoutParams,
        private val gestureDetector: GestureDetector
    ) : View.OnTouchListener {

        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f

        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.leftMargin
                    initialY = layoutParams.topMargin
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams.leftMargin = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams.topMargin = initialY + (event.rawY - initialTouchY).toInt()
                    v?.layoutParams = layoutParams
                }
                MotionEvent.ACTION_UP -> {
                    FPSConfig.curXPosition = layoutParams.leftMargin
                    FPSConfig.curYPosition = layoutParams.topMargin
                }
            }
            return true
        }
    }

    interface IFPSMonitorView {

        fun show(animated: Boolean)

        fun hide(animated: Boolean)

        fun update(slice: FPSSlice)

        fun getView(): View
    }
}