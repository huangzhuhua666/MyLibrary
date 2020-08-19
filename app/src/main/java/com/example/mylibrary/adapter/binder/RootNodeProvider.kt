package com.example.mylibrary.adapter.binder

import android.view.View
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.hzh.base.util.no
import com.example.hzh.base.util.yes
import com.example.mylibrary.R
import com.example.mylibrary.data.bean.RootNode

/**
 * Create by hzh on 2020/08/19.
 */
class RootNodeProvider : BaseNodeProvider() {

    override val itemViewType: Int = 0

    override val layoutId: Int = R.layout.item_expand

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        (item as RootNode).run {
            helper.setText(R.id.tv_title, title)
                .setText(R.id.tv_right, isExpanded.yes { "x${quantity}" }.no { "展开" })
        }
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        getAdapter()?.expandOrCollapse(position)
    }
}