package com.example.mylibrary.ui.activity

import com.chad.library.adapter.base.BaseBinderAdapter
import com.example.common.activity.UIActivity
import com.example.common.app.CommonApplication
import com.example.hzh.base.util.ResourcesUtils
import com.example.hzh.base.util.vbInflate
import com.example.hzh.ui.widget.SimpleItemDecoration
import com.example.hzh.ui.widget.addItemDecoration
import com.example.mylibrary.R
import com.example.mylibrary.adapter.binder.FPSMonitorBinder
import com.example.mylibrary.databinding.ActivitySettingBinding
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Create by hzh on 2024/3/14.
 */
class SettingActivity: UIActivity<ActivitySettingBinding>(){

    override val mBinding by vbInflate<ActivitySettingBinding>()

    override val isStatusBarDarkFont = true

    private val mAdapter by lazy {
        BaseBinderAdapter().also {
            it.addItemBinder(FPSMonitorBinder())
        }
    }

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {
        mBinding.root.let {
            it.adapter = mAdapter
            it.addItemDecoration {
                SimpleItemDecoration.ColorDecoration().also { decoration ->
                    decoration.color = ResourcesUtils.getColor(R.color.color_f4f4f4)
                    decoration.bottom = ResourcesUtils.dp2px(0.5f)
                }
            }
        }
    }

    override fun initListener() {

    }

    override fun initData() {
        mAdapter.setNewInstance(
            mutableListOf(
                CommonApplication.kv.getBoolean("key_fps_monitor_enabled", false),
            )
        )
    }
}