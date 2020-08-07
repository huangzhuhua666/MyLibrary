package com.example.hzh.ui.dialog

import android.graphics.Color
import android.view.LayoutInflater
import androidx.core.view.isGone
import com.example.hzh.ui.R
import com.example.hzh.ui.databinding.UiDialogConfirmBinding
import com.example.hzh.ui.dialog.base.BaseDialog
import com.example.hzh.ui.utils.filterFastClickListener

/**
 * Create by hzh on 2019/11/4.
 *
 * 提示、确认对话框
 */
class ConfirmDialog private constructor(private val builder: Builder) :
    BaseDialog<UiDialogConfirmBinding>() {

    override val backgroundDrawableResource: Int = R.drawable.ui_bg_dialog_white_radius_10

    override fun createViewBinding(inflater: LayoutInflater): UiDialogConfirmBinding {
        return UiDialogConfirmBinding.inflate(inflater, null, false)
    }

    override fun initView() {
        mBinding.run {
            tvTitle.text = builder.title
            tvTitle.isGone = builder.title.trim().isEmpty()
            tvTitle.setTextColor(builder.titleColor)

            tvContent.text = builder.content
            tvContent.setTextColor(builder.contentColor)

            btnLeft.text =
                if (builder.leftBtnText.trim().isEmpty()) getString(R.string.ui_cancel)
                else builder.leftBtnText
            btnLeft.isGone = builder.isSingleButton
            btnLeft.setTextColor(builder.leftBtnColor)

            line2.isGone = builder.isSingleButton

            btnRight.text =
                if (builder.rightBtnText.trim().isEmpty()) getString(R.string.ui_confirm)
                else builder.rightBtnText
            btnRight.setTextColor(builder.rightBtnColor)
        }
    }

    override fun initListener() {
        mBinding.run {
            btnLeft.filterFastClickListener {
                builder.leftClickListener?.invoke()
                dismiss()
            }

            btnRight.filterFastClickListener {
                builder.rightClickListener?.invoke()
                dismiss()
            }
        }
    }

    override fun initData() {

    }

    class Builder {

        var isSingleButton: Boolean = false

        var title: String = ""

        var titleColor: Int = Color.parseColor("#333333")

        var content: String = ""

        var contentColor: Int = Color.parseColor("#999999")

        var leftBtnText: String = ""

        var leftBtnColor: Int = Color.parseColor("#ff3b30")

        var leftClickListener: (() -> Unit)? = null

        var rightBtnText: String = ""

        var rightBtnColor: Int = Color.parseColor("#00a6ff")

        var rightClickListener: (() -> Unit)? = null

        fun build(): ConfirmDialog = ConfirmDialog(this)
    }
}