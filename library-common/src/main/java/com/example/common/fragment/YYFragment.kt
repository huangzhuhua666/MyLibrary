package com.example.common.fragment

import androidx.viewbinding.ViewBinding
import com.example.common.R
import com.example.hzh.base.util.toast
import com.example.common.viewmodel.BaseVM
import com.example.common.widget.LoadingDialog
import com.example.common.widget.StatusLayout
import com.example.hzh.network.utils.msg
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * Create by hzh on 2020/5/18.
 */
abstract class YYFragment<VB : ViewBinding, VM : BaseVM> : UIFragment<VB>() {

    abstract val mViewModel: VM

    private val mLoadingDialog by lazy { LoadingDialog() }

    protected open val mStatusView: StatusLayout?
        get() = null

    protected open val mRefresh: SmartRefreshLayout?
        get() = null

    protected open val mDataEmptyTip: Int
        get() = R.string.common_no_data

    override fun bindViewModelObserve() {
        mViewModel.let { vm ->
            vm.isShowLoading.observe(this) {
                if (it) mLoadingDialog.show(this)
                else if (!it && mLoadingDialog.isShowing()) mLoadingDialog.dismiss()
            }

            vm.isFinish.observe(this) {
                if (it) mRefresh?.run {
                    finishRefresh()
                    finishLoadMore()
                }
            }

            vm.isNoMoreData.observe(this) { mRefresh?.setNoMoreData(it) }

            vm.isDataEmpty.observe(this) { onDataEmpty(it) }

            vm.toastTip.observe(this) { mContext.toast(it) }

            vm.exception.observe(this) { onError(it) }
        }
    }

    protected open fun onDataEmpty(isEmpty: Boolean) {
        if (isEmpty) mStatusView?.showDataEmpty(mDataEmptyTip)
    }

    protected open fun onError(e: Throwable) {
        mContext.toast(e.msg)
        mStatusView?.showError()
    }
}