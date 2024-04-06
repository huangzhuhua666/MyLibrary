package com.example.web

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.web.model.HybridParam

/**
 * Create by hzh on 2024/4/7.
 */
object WebAgentManager {

    fun init() {

    }

    /**
     * 获取带有WebView页面
     * @param param HybridParam
     * @return Fragment
     */
    fun getHybridFragment(param: HybridParam): Fragment {
        return Fragment()
    }

    /**
     * 跳转带有WebView页面
     * @param param HybridParam
     */
    fun openWebPage(context: Context?, param: HybridParam) {
        context ?: return
    }

    /**
     * 通过统一API获取的WebViewController，不用手动init()
     * @param context Context?
     * @param param HybridParam
     * @return WebViewController?
     */
    fun getWebViewController(context: Context?, param: HybridParam) {

    }
}