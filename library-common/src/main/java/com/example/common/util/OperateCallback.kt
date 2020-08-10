package com.example.common.util

import androidx.annotation.StringRes

/**
 * Create by hzh on 2020/6/16.
 */
interface OperateCallback<T> {

    /**
     * 非法输入回调
     * @param tip 提示信息
     */
    fun onInputIllegal(@StringRes tip: Int) {}

    /**
     * 网络请求前的操作回调
     */
    fun onPreOperate() {}

    /**
     * 操作成功回调
     */
    fun onCallback(data: T) {}

    /**
     * 返回数据为空
     */
    fun onDataEmpty() {}
}