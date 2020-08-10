package com.example.common.fragment

import android.view.View
import androidx.viewbinding.ViewBinding
import com.example.hzh.base.fragment.BaseFragment
import com.example.hzh.base.util.yes
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar

/**
 * Create by hzh on 2020/5/13.
 */
abstract class UIFragment<VB : ViewBinding> : BaseFragment<VB>(), OnTitleBarListener {

    protected open val mTitleView: View?
        get() = null

    /**
     * 使用沉浸式状态栏
     */
    protected open val isUseImmersionBar: Boolean
        get() = false

    /**
     * 状态栏字体黑色
     */
    protected open val isStatusBarDarkFont: Boolean
        get() = false

    override fun initTitleBar() {
        mTitleView?.let { if (it is TitleBar) it.setOnTitleBarListener(this) }

        isUseImmersionBar.yes {
            immersionBar {
                mTitleView?.let { titleBar(it) }

                statusBarDarkFont(isStatusBarDarkFont, .2f)
            }
        }
    }

    override fun onLeftClick(v: View?) {}

    override fun onTitleClick(v: View?) {}

    override fun onRightClick(v: View?) {}
}