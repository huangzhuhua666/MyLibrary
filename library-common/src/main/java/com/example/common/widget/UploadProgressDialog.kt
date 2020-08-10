package com.example.common.widget

import android.view.LayoutInflater
import android.view.Window
import com.example.common.R
import com.example.common.databinding.CommonDialogUploadProgressBinding
import com.example.hzh.ui.dialog.base.BaseDialog

/**
 * Create by hzh on 2020/07/23.
 */
class UploadProgressDialog : BaseDialog<CommonDialogUploadProgressBinding>() {

    override val cancelable: Boolean = false

    override val canceledOnTouchOutside: Boolean = false

    override val backgroundDrawableResource: Int = R.drawable.common_bg_dialog_upload

    override fun createViewBinding(inflater: LayoutInflater): CommonDialogUploadProgressBinding {
        return CommonDialogUploadProgressBinding.inflate(inflater, null, false)
    }

    override fun initView() {
        lifecycle.addObserver(mBinding.progress)
    }

    override fun initListener() {

    }

    override fun initData() {

    }

    override fun setLayout(window: Window) {

    }

    fun updateProgress(progress: Int) {
        mBinding.progress.progress = progress
    }
}