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
import com.example.hzh.base.manager.ActivityRecordMgr

/**
 * Create by hzh on 2020/5/13.
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    protected val mContext by lazy { requireActivity() as BaseActivity<*> }
    protected val mBinding get() = _binding!!

    private val onVisibilityChangeListeners by lazy { LinkedHashSet<OnVisibilityChangeListener>() }

    private var _binding: VB? = null
    private var isFirstIn = true
    private var isVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isFirstIn = true

        _binding = createViewBinding(inflater, container).also {
            it.root.parent?.let { p -> (p as ViewGroup).removeView(it.root) }
        }

        arguments?.let { onGetBundle(it) }

        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initTitleBar()
        initView()
        initListener()
        bindViewModelObserve()
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        ActivityRecordMgr.getInstance().onAttachFragment(childFragment)
    }

    override fun onResume() {
        super.onResume()
        if (BuildConfig.DEBUG) {
            Log.d("CurrentFragment", javaClass.simpleName)
        }

        // onResume 并不代表 fragment 可见
        // 如果是在 viewpager 里，就需要判断 getUserVisibleHint，不在 viewpager 时，getUserVisibleHint 默认为 true
        // 如果是其它情况，就通过 isHidden 判断，因为 show/hide 时会改变 isHidden 的状态
        // 所以，只有当 fragment 原来是可见状态时，进入 onResume 就回调 onVisible
        if (userVisibleHint && !isHidden) {
            notifyVisible()
        }

        if (isFirstIn) {
            initData()
            isFirstIn = false
        }
    }

    private fun notifyVisible() {
        if (isVisible) {
            return
        }

        if (parentFragment?.userVisibleHint != true) {
            return
        }

        isVisible = true
        onVisible()
    }

    open fun onVisible() {
        onVisibilityChanged(true)
    }

    private fun onVisibilityChanged(isVisible: Boolean) {
        onVisibilityChangeListeners.toList().forEach {
            it.onVisibilityChanged(isVisible)
        }
    }

    override fun onPause() {
        super.onPause()
        // onPause 时也需要判断，如果当前 fragment 在 viewpager 中不可见，就已经回调过了，onPause 时也就不需要再次回调 onInvisible 了
        // 所以，只有当 fragment 是可见状态时进入 onPause 才加调 onInvisible
        if (userVisibleHint && !isHidden) {
            notifyInvisible()
        }
    }

    private fun notifyInvisible() {
        if (!isVisible) {
            return
        }

        isVisible = false
        onInvisible()
    }

    open fun onInvisible() {
        onVisibilityChanged(false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        val change = isVisibleToUser != userVisibleHint
        super.setUserVisibleHint(isVisibleToUser)
        // 在 viewpager 中，创建 fragment 时就会调用这个方法，但这时还没有 resume，为了避免重复调用 visible 和 invisible，
        // 只有当 fragment 状态是 resumed 并且初始化完毕后才进行 visible 和 invisible 的回调
        if (isResumed && change) {
            if (userVisibleHint) {
                notifyVisible()
            } else {
                notifyInvisible()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            notifyInvisible()
        } else {
            notifyVisible()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun addOnVisibilityChangeListener(onVisibilityChangeListener: OnVisibilityChangeListener) {
        onVisibilityChangeListeners.add(onVisibilityChangeListener)
    }

    fun removeOnVisibilityChangeListener(onVisibilityChangeListener: OnVisibilityChangeListener) {
        onVisibilityChangeListeners.remove(onVisibilityChangeListener)
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