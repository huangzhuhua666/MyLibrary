package com.example.mylibrary.adapter.binder

import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.mylibrary.R
import com.example.mylibrary.data.bean.DragTextBean

/**
 * Create by hzh on 2020/11/12.
 */
class DragTextBinder : BaseNodeProvider() {

    override val itemViewType: Int = 2

    override val layoutId: Int = R.layout.item_drag_text

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        (item as DragTextBean).let {
            helper.setText(R.id.tv_content, it.content)
        }
    }
}