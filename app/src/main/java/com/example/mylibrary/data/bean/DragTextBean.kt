package com.example.mylibrary.data.bean

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode

/**
 * Create by hzh on 2020/11/11.
 */
data class DragTextIconBean1(val content: String): BaseExpandNode() {

    override val childNode: MutableList<BaseNode>? = mutableListOf()
}

data class DragTextIconBean2(val content: String): BaseExpandNode() {

    override val childNode: MutableList<BaseNode>? = mutableListOf()
}

data class DragTextBean(val content: String) : BaseNode() {

    override val childNode: MutableList<BaseNode>? = null
}