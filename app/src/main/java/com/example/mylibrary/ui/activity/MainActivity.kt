package com.example.mylibrary.ui.activity

import com.chad.library.adapter.base.BaseBinderAdapter
import com.example.common.activity.UIActivity
import com.example.common.app.CommonApplication
import com.example.hzh.base.performance.fps.FPSMonitor
import com.example.hzh.base.util.vbInflate
import com.example.mylibrary.adapter.binder.MenuBean
import com.example.mylibrary.adapter.binder.MenuBinder
import com.example.mylibrary.databinding.ActivityMainBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.gyf.immersionbar.ktx.immersionBar

class MainActivity : UIActivity<ActivityMainBinding>() {

    override val mBinding by vbInflate<ActivityMainBinding>()

    override val isStatusBarDarkFont: Boolean
        get() = true

    private val mAdapter by lazy { BaseBinderAdapter().addItemBinder(MenuBinder()) }

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {
        mBinding.rvMenu.run {
            layoutManager = FlexboxLayoutManager(mContext)
            adapter = mAdapter
        }
    }

    override fun initData() {
        mAdapter.setNewInstance(
            mutableListOf(
                MenuBean("Setting", SettingActivity::class.java),
                MenuBean("ObjectBox", ObjectBoxActivity::class.java),
                MenuBean("ExpandList", ExpandListActivity::class.java),
                MenuBean("GoodsDetail", GoodsDetailActivity::class.java),
                MenuBean("Video", VideoActivity::class.java),
                MenuBean("MotionLayout1", MotionLayoutActivity::class.java),
                MenuBean("MotionLayout2", WzryActivity::class.java),
                MenuBean("MotionLayout3", YoutubeActivity::class.java),
                MenuBean("DragAndSwipeList", DragListActivity::class.java),
                MenuBean("NoRegisterActivity", NoRegisterActivity::class.java),
                MenuBean("DrawImage", DrawImageActivity::class.java),
                MenuBean("AudioRecorder", AudioRecorderActivity::class.java),
                MenuBean("CatEye", CatEyeActivity::class.java),
            )
        )

        if (CommonApplication.kv.getBoolean("key_fps_monitor_enabled", false)) {
            FPSMonitor.getInstance().show()
        }
    }

    override fun initListener() {

    }
}