package com.example.mylibrary.ui.activity

import com.example.common.activity.UIActivity
import com.example.hzh.base.util.vbInflate
import com.example.mylibrary.databinding.ActivityYoutubeBinding
import com.gyf.immersionbar.ktx.hideStatusBar
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Create by hzh on 2020/11/09.
 */
class YoutubeActivity : UIActivity<ActivityYoutubeBinding>() {

    override val mBinding by vbInflate<ActivityYoutubeBinding>()

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { hideStatusBar() }
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initListener() {

    }
}