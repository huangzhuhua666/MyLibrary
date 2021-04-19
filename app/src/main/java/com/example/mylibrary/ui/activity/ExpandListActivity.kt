package com.example.mylibrary.ui.activity

import com.example.common.activity.UIActivity
import com.example.hzh.base.util.vbInflate
import com.example.mylibrary.adapter.ExpandListAdapter
import com.example.mylibrary.data.bean.RootNode
import com.example.mylibrary.data.bean.SecondNode
import com.example.mylibrary.databinding.ActivityExpandListBinding
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Create by hzh on 2020/08/19.
 */
class ExpandListActivity : UIActivity<ActivityExpandListBinding>() {

    override val mBinding by vbInflate<ActivityExpandListBinding>()

    override val isStatusBarDarkFont: Boolean = true

    private val mAdapter by lazy { ExpandListAdapter() }

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {
        mBinding.rvData.adapter = mAdapter
    }

    override fun initData() {
        mAdapter.setList(
            listOf(
                RootNode(
                    "安稳免调码血糖仪-单机", 50, listOf(
                        SecondNode("安稳免调码血糖仪-50支/盒-单机", 50)
                    )
                ).apply { isExpanded = false }
            )
        )
    }

    override fun initListener() {

    }
}