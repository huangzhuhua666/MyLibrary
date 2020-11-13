package com.example.mylibrary.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.dragswipe.DragAndSwipeCallback
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.module.BaseDraggableModule
import com.chad.library.adapter.base.module.DraggableModule
import com.example.hzh.base.util.yes
import com.example.mylibrary.adapter.binder.DragTextBinder
import com.example.mylibrary.adapter.binder.DragTextIconBinder1
import com.example.mylibrary.adapter.binder.DragTextIconBinder2
import com.example.mylibrary.data.bean.DragTextBean
import com.example.mylibrary.data.bean.DragTextIconBean1
import com.example.mylibrary.data.bean.DragTextIconBean2

/**
 * Create by hzh on 2020/11/11.
 */
class DragAndSwipeAdapter : BaseNodeAdapter(), DraggableModule {

    init {
        addFullSpanNodeProvider(DragTextIconBinder1())
        addFullSpanNodeProvider(DragTextIconBinder2())
        addFullSpanNodeProvider(DragTextBinder())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int = when (data[position]) {
        is DragTextIconBean1 -> 0
        is DragTextIconBean2 -> 1
        is DragTextBean -> 2
        else -> -1
    }

    override fun addDraggableModule(baseQuickAdapter: BaseQuickAdapter<*, *>): BaseDraggableModule =
        BaseDraggableModule(baseQuickAdapter).apply {
            itemTouchHelperCallback = object : DragAndSwipeCallback(this) {

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    (viewHolder.itemViewType in listOf(0, 1)).yes { return makeMovementFlags(0, 0) }

                    return super.getMovementFlags(recyclerView, viewHolder)
                }

//                override fun onMove(
//                    recyclerView: RecyclerView,
//                    source: RecyclerView.ViewHolder,
//                    target: RecyclerView.ViewHolder
//                ): Boolean = true
            }

            itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        }
}