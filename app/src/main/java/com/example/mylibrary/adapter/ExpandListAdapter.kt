package com.example.mylibrary.adapter

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.example.mylibrary.adapter.binder.RootNodeProvider
import com.example.mylibrary.adapter.binder.SecondNodeProvider
import com.example.mylibrary.data.bean.RootNode
import com.example.mylibrary.data.bean.SecondNode

/**
 * Create by hzh on 2020/08/19.
 */
class ExpandListAdapter : BaseNodeAdapter() {

    init {
        addFullSpanNodeProvider(RootNodeProvider())
        addFullSpanNodeProvider(SecondNodeProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int = when (data[position]) {
        is RootNode -> 0
        is SecondNode -> 1
        else -> -1
    }
}