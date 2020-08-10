package com.example.common.activity

import android.view.View
import androidx.viewbinding.ViewBinding
import com.example.hzh.base.activity.BaseActivity
import com.example.hzh.base.util.yes
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar

/**
 * Create by hzh on 2020/5/13.
 */
abstract class UIActivity<VB : ViewBinding> : BaseActivity<VB>(), OnTitleBarListener {

    protected open val mTitleView: View?
        get() = null

    /**
     * 使用沉浸式状态栏
     */
    protected open val isUseImmersionBar: Boolean
        get() = true

    /**
     * 状态栏字体黑色
     */
    protected open val isStatusBarDarkFont: Boolean
        get() = false

    /**
     * 点击TitleBar左侧图标关闭Activity
     */
    protected open val isLeftFinish: Boolean
        get() = true

    override fun initTitleBar() {
        mTitleView?.let { if (it is TitleBar) it.setOnTitleBarListener(this) }

        isUseImmersionBar.yes {
            immersionBar {
                mTitleView?.let { titleBar(it) }

                statusBarDarkFont(isStatusBarDarkFont, .2f)
            }
        }
    }

    override fun onLeftClick(v: View?) {
        isLeftFinish.yes { finish() }
    }

    override fun onTitleClick(v: View?) {}

    override fun onRightClick(v: View?) {}
}