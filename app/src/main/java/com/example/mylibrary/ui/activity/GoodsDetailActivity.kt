package com.example.mylibrary.ui.activity

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.common.activity.YYActivity
import com.example.hzh.ui.utils.filterFastClickListener
import com.example.mylibrary.databinding.ActivityGoodsDetailBinding
import com.example.mylibrary.model.GoodsDetailModel
import com.example.mylibrary.viewmodel.GoodsDetailVM

/**
 * Create by hzh on 2020/10/13.
 */
@Suppress("UNCHECKED_CAST")
class GoodsDetailActivity : YYActivity<ActivityGoodsDetailBinding, GoodsDetailVM>() {

    override val mTitleView: View
        get() = mBinding.rlTitle

    override val mViewModel by viewModels<GoodsDetailVM> {
        object : ViewModelProvider.Factory {

            override fun <T : ViewModel?> create(modelClass: Class<T>): T = GoodsDetailVM(GoodsDetailModel()) as T
        }
    }

    override fun createViewBinding(): ActivityGoodsDetailBinding {
        return ActivityGoodsDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initListener() {
        mBinding.run {
            btnBack.filterFastClickListener { finish() }
        }
    }

    override fun bindViewModelObserve() {
        super.bindViewModelObserve()
    }
}