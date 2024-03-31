package com.example.hzh.base.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.hzh.base.BuildConfig
import com.example.hzh.base.manager.ActivityRecordMgr
import com.example.hzh.base.util.hideKeyboard

/**
 * Create by hzh on 2020/5/8.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected val mContext by lazy { this }

    protected abstract val mBinding: VB

    protected open val isClickHideKeyboard: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        initTitleBar()

        if (BuildConfig.DEBUG) Log.d("CurrentActivity", javaClass.simpleName)

        intent?.extras?.let { onGetBundle(it) }

        AppManager.getInstance().addActivity(this)

        initView()
        initListener()
        bindViewModelObserve()
        initData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        ActivityRecordMgr.getInstance().onAttachFragment(fragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.getInstance().removeActivity(this)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (isClickHideKeyboard && ev?.actionMasked == MotionEvent.ACTION_DOWN) {
            currentFocus?.let {
                // 判断是否要隐藏键盘
                if (isShouldHideKeyboard(it, ev)) hideKeyboard(it)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 焦点在EditText：点击区域在EditText中，不隐藏键盘
     * 焦点不在EditText：不隐藏键盘
     */
    private fun isShouldHideKeyboard(view: View, ev: MotionEvent): Boolean {
        return if (view is EditText) {
            val l = IntArray(2)
            view.getLocationInWindow(l)

            val left = l[0]
            val top = l[1]
            val right = left + view.width
            val bottom = top + view.height

            val x = ev.x.toInt()
            val y = ev.y.toInt()

            !(x in left..right && y in top..bottom)
        } else false
    }

    /**
     * 初始化TitleBar的地方
     */
    protected open fun initTitleBar() {}

    /**
     * 在这里获取Intent中携带的数据
     */
    protected open fun onGetBundle(bundle: Bundle) {}

    /**
     *  ViewModel中的LiveData的观察都放到这里来
     */
    protected open fun bindViewModelObserve() {}

    protected abstract fun initView()

    protected abstract fun initListener()

    protected abstract fun initData()
}