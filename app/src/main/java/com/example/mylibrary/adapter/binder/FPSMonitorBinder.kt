package com.example.mylibrary.adapter.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.example.common.app.CommonApplication
import com.example.common.util.boolean
import com.example.hzh.base.performance.fps.FPSMonitor
import com.example.mylibrary.databinding.ItemFpsMonitorBinding

/**
 * Create by hzh on 2024/3/14.
 */
class FPSMonitorBinder : QuickViewBindingItemBinder<Boolean, ItemFpsMonitorBinding>() {

    private var mIsFPSMonitorEnabled by CommonApplication.kv.boolean("key_fps_monitor_enabled")

    override fun onCreateViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ItemFpsMonitorBinding.inflate(layoutInflater, parent, false)

    override fun convert(holder: BinderVBHolder<ItemFpsMonitorBinding>, data: Boolean) {
        holder.viewBinding.cb.isChecked = mIsFPSMonitorEnabled
    }

    override fun onClick(
        holder: BinderVBHolder<ItemFpsMonitorBinding>,
        view: View,
        data: Boolean,
        position: Int
    ) {
        if (mIsFPSMonitorEnabled) {
            FPSMonitor.getInstance().hide()
        } else {
            FPSMonitor.getInstance().show()
        }
        mIsFPSMonitorEnabled = !mIsFPSMonitorEnabled

        adapter.notifyItemChanged(position)
    }
}