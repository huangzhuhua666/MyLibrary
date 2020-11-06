package com.example.mylibrary.ui.activity

import com.example.common.activity.UIActivity
import com.example.mylibrary.databinding.ActivityMotionLayoutBinding
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Create by hzh on 2020/11/06.
 */
class MotionLayoutActivity : UIActivity<ActivityMotionLayoutBinding>() {

    override val isStatusBarDarkFont: Boolean = true

    override fun createViewBinding(): ActivityMotionLayoutBinding {
        return ActivityMotionLayoutBinding.inflate(layoutInflater)
    }

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initListener() {

    }
}