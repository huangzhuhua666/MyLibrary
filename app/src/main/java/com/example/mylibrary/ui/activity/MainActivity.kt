package com.example.mylibrary.ui.activity

import com.example.common.activity.UIActivity
import com.example.mylibrary.databinding.ActivityMainBinding
import com.gyf.immersionbar.ktx.immersionBar

class MainActivity : UIActivity<ActivityMainBinding>() {

    override val isStatusBarDarkFont: Boolean
        get() = true

    override fun createViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
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