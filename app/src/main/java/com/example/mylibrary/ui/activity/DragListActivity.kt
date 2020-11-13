package com.example.mylibrary.ui.activity

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.example.common.activity.UIActivity
import com.example.hzh.base.util.no
import com.example.hzh.base.util.yes
import com.example.mylibrary.adapter.DragAndSwipeAdapter
import com.example.mylibrary.data.bean.DragTextBean
import com.example.mylibrary.data.bean.DragTextIconBean1
import com.example.mylibrary.data.bean.DragTextIconBean2
import com.example.mylibrary.databinding.ActivityDragListBinding
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Create by hzh on 2020/11/11.
 */
class DragListActivity : UIActivity<ActivityDragListBinding>() {

    override val isStatusBarDarkFont: Boolean = true

    private val mAdapter by lazy { DragAndSwipeAdapter() }

    override fun createViewBinding(): ActivityDragListBinding {
        return ActivityDragListBinding.inflate(layoutInflater)
    }

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {
        mBinding.rvData.adapter = mAdapter
    }

    override fun initData() {
        val list = mutableListOf<BaseNode>()
        for (i in 0 until 50) {
            when (i % 5) {
                0 -> {
                    (i % 2 == 0).yes {
                        list.add(DragTextIconBean1("This is text icon item${i} (type 1)").apply {
                            isExpanded = true
                        })
                    }.no {
                        list.add(DragTextIconBean2("This is text icon item${i} (type 2)").apply {
                            isExpanded = true
                        })
                    }
                }
                else -> list[i / 5].childNode?.add(DragTextBean("This is text item${i}"))
            }
        }
        mAdapter.setNewInstance(list)
    }

    override fun initListener() {
        mAdapter.draggableModule.run {
            isDragEnabled = true

            setOnItemDragListener(object : OnItemDragListener {

                override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                }

                override fun onItemDragMoving(
                    source: RecyclerView.ViewHolder?,
                    from: Int,
                    target: RecyclerView.ViewHolder?,
                    to: Int
                ) {

                }

                override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                }
            })
        }
    }
}