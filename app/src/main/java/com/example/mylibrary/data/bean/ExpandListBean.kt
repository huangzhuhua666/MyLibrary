package com.example.mylibrary.data.bean

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode

/**
 * Create by hzh on 2020/08/19.
 */
data class RootNode(val title: String, val quantity: Int, val childList: List<BaseNode>) : BaseExpandNode() {

    override val childNode: MutableList<BaseNode>?
        get() = childList.toMutableList()
}

data class SecondNode(val title: String, val quantity: Int) : BaseNode() {

    override val childNode: MutableList<BaseNode>? = null
}