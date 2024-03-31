package com.example.mylibrary.util

import android.content.Context
import com.example.hzh.base.performance.blockcanary.BlockCanaryContext
import com.example.hzh.base.performance.blockcanary.data.BlockInfo

/**
 * Create by hzh on 2024/3/14.
 */
class AppBlockCanaryContext : BlockCanaryContext() {

    override fun onBlock(context: Context, blockInfo: BlockInfo) {

    }
}