package com.example.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.view.isGone
import com.example.common.R
import com.example.common.app.CommonApplication
import com.example.common.databinding.CommonLayoutStatusBinding
import com.example.hzh.base.util.yes
import com.example.hzh.ui.utils.filterFastClickListener

/**
 * Create by hzh on 2020/6/5.
 *
 * 状态控制布局
 */
@Suppress("InflateParams")
class StatusLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mStatusView by lazy {
        CommonLayoutStatusBinding.inflate(LayoutInflater.from(context), null, false)
    }

    private val contentId: Int
    private var mContentView: View? = null

    var retryAction: (() -> Unit)? = null

    init {
        mStatusView.root.let {
            filterFastClickListener { retryAction?.invoke() }

            it.isGone = true

            addView(it)
        }

        context.obtainStyledAttributes(attrs, R.styleable.StatusLayout).run {
            contentId = getResourceId(R.styleable.StatusLayout_contentView, -1)

            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            getChildAt(i)?.let { (it.id == contentId).yes { mContentView = it } }
        }
    }

    fun showLoading() {
        mStatusView.run {
            tvTip.setText(R.string.common_loading)

            loading.isGone = false
            loading.startAnim()

            root.isClickable = false
            root.isGone = false
        }

        mContentView?.isGone = true
    }

    fun showContent() {
        mStatusView.run {
            root.isClickable = false
            root.isGone = true

            loading.isGone = true
            loading.stopAnim()
        }

        mContentView?.isGone = false
    }

    fun showDataEmpty(@StringRes emptyTip: Int = R.string.common_no_data) {
        mStatusView.run {
            tvTip.setText(emptyTip)

            loading.isGone = true
            loading.stopAnim()

            root.isClickable = false
            root.isGone = false
        }

        mContentView?.isGone = true
    }

    fun showError() {
        mStatusView.run {
            tvTip.setText(if (CommonApplication.isNetworkAvailable) R.string.common_unknown_error_retry else R.string.common_require_network_retry)

            root.isClickable = true
            root.isGone = false
        }

        mContentView?.isGone = true
    }
}