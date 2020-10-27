package com.example.common.activity

import androidx.viewbinding.ViewBinding
import com.example.common.R
import com.example.common.viewmodel.BaseVM
import com.example.common.widget.LoadingDialog
import com.example.common.widget.StatusLayout
import com.example.hzh.base.util.toast
import com.example.hzh.network.utils.msg
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * Create by hzh on 2020/5/18.
 */
abstract class YYActivity<VB : ViewBinding, VM : BaseVM> : UIActivity<VB>() {

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
            vm.isShowLoading.observe(mContext) {
                if (it) mLoadingDialog.show(mContext)
                else if (!it && mLoadingDialog.isShowing()) mLoadingDialog.dismiss()
            }

            vm.isFinish.observe(mContext) {
                if (it) mRefresh?.run {
                    finishRefresh()
                    finishLoadMore()
                }
            }

            vm.isNoMoreData.observe(mContext) { mRefresh?.setNoMoreData(it) }

            vm.isDataEmpty.observe(mContext) { onDataEmpty(it) }

            vm.toastTip.observe(mContext) { toast(it) }

            vm.exception.observe(mContext) { onError(it) }
        }
    }

    protected open fun onDataEmpty(isEmpty: Boolean) {
        if (isEmpty) mStatusView?.showDataEmpty(mDataEmptyTip)
    }

    protected open fun onError(e: Throwable) {
        toast(e.msg)
        mStatusView?.showError()
    }
}