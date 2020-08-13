package com.example.hzh.base.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.hzh.base.BuildConfig
import com.example.hzh.base.activity.BaseActivity

/**
 * Create by hzh on 2020/5/13.
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var isFirstIn = true

    protected val mContext by lazy { requireActivity() as BaseActivity<*> }

    private var _binding: VB? = null
    protected val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isFirstIn = true

        _binding = createViewBinding(inflater, container).also {
            it.root.parent?.let { p -> (p as ViewGroup).removeView(it.root) }
        }

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initTitleBar()
        initView()
        initListener()
        bindViewModelObserve()
    }

    override fun onResume() {
        super.onResume()
        if (BuildConfig.DEBUG) Log.d("CurrentFragment", javaClass.simpleName)

        arguments?.let { onGetBundle(it) }

        if (isFirstIn) {
            initData()
            isFirstIn = false
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    /**
     * 初始化TitleBar的地方
     */
    protected open fun initTitleBar() {}

    /**
     * 在这里获取Intent中携带的数据
     */
    protected open fun onGetBundle(bundle: Bundle) {}

    /**
     *  ViewModel中的LiveData的观察都放到这里来
     */
    protected open fun bindViewModelObserve() {}

    /**
     * 创建ViewBinding
     */
    protected abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected abstract fun initView()

    protected abstract fun initListener()

    /**
     * 这个方法只会执行一次
     */
    protected open fun initData() {}
}