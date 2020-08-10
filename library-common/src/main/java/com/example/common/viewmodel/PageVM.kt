package com.example.common.viewmodel

import com.example.hzh.base.util.no
import com.example.hzh.base.util.yes

/**
 * Create by hzh on 2020/6/12.
 */
open class PageVM : BaseVM() {

    /**
     * 页码
     */
    private var page = 1

    protected val isFirstPage: Boolean get() = page == 1

    /**
     * 获取加载页码
     * @param firstPage true -> 第一页; false -> 下一页
     */
    protected fun getPage(firstPage: Boolean): Int {
        page = firstPage.yes { 1 }.no { ++page }
        return page
    }

    protected fun loadFailed() {
        page = (page > 1).yes { --page }.no { 1 }
    }
}