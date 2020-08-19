package com.example.mylibrary.adapter.binder

import android.view.View
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.mylibrary.R
import com.example.mylibrary.data.bean.SecondNode

/**
 * Create by hzh on 2020/08/19.
 */
class SecondNodeProvider : BaseNodeProvider() {

    override val itemViewType: Int = 1

    override val layoutId: Int = R.layout.item_expand

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        (item as SecondNode).run {
            helper.setText(R.id.tv_title, title)
                .setText(R.id.tv_right, "x${quantity}")
        }
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        getAdapter()?.run { collapse(findParentNode(data)) }
    }
}