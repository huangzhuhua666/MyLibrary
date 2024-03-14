package com.example.hzh.base.performance.blockcanary

import android.content.Context
import com.example.hzh.base.performance.blockcanary.data.BlockInfo

/**
 * Create by hzh on 2024/3/14.
 */
internal interface BlockInterceptor {

    fun onBlock(
        context: Context,
        blockInfo: BlockInfo,
    )
}