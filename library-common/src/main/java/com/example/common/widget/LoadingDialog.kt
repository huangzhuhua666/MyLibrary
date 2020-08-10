package com.example.common.widget

import android.view.LayoutInflater
import android.view.Window
import com.example.common.R
import com.example.common.databinding.CommonDialogLoadingBinding
import com.example.hzh.ui.dialog.base.BaseDialog

/**
 * Create by hzh on 2020/6/8.
 */
class LoadingDialog : BaseDialog<CommonDialogLoadingBinding>() {

    override val cancelable: Boolean = false

    override val canceledOnTouchOutside: Boolean = false

    override val backgroundDrawableResource: Int = R.drawable.common_bg_dialog_loading

    override fun createViewBinding(inflater: LayoutInflater): CommonDialogLoadingBinding {
        return CommonDialogLoadingBinding.inflate(inflater, null, false)
    }

    override fun initView() {

    }

    override fun initListener() {

    }

    override fun initData() {

    }

    override fun setLayout(window: Window) {

    }
}