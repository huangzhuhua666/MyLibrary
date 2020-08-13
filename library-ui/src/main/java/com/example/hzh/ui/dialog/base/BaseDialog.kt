package com.example.hzh.ui.dialog.base

import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.*
import androidx.viewbinding.ViewBinding
import com.example.hzh.ui.R

/**
 * Create by hzh on 2019/11/4.
 */
abstract class BaseDialog<VB : ViewBinding> : DialogFragment() {

    private var _binding: VB? = null
    protected val mBinding get() = _binding!!

    protected open val cancelable: Boolean
        get() = true

    protected open val canceledOnTouchOutside: Boolean
        get() = true

    protected open val backgroundDrawableResource: Int = android.R.color.transparent

    protected open val windowAnimations: Int
        get() = R.style.ui_AnimDialogIOS

    protected open val gravity: Int
        get() = Gravity.CENTER

    private var mShowTag = ""
    private val mills = LongArray(2)
    protected val dm by lazy { DisplayMetrics() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createViewBinding(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    override fun onStart() {
        super.onStart()
        dialog?.run {
            activity?.windowManager?.defaultDisplay?.getMetrics(dm)

            window?.let {
                setLayout(it)
                it.setGravity(gravity)
                it.setBackgroundDrawableResource(backgroundDrawableResource)
                it.setWindowAnimations(windowAnimations)
                it
            }

            setCancelable(cancelable)
            setCanceledOnTouchOutside(canceledOnTouchOutside)
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun show(fragment: Fragment) {
        show(fragment.fragmentManager!!, fragment.javaClass.name)
    }

    fun show(activity: FragmentActivity) {
        show(activity.supportFragmentManager, activity.javaClass.name)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!isShowing() && !isRepeatedShow(tag ?: "")) {
            DialogFragment::class.java.run {
                val dismissed = getDeclaredField("mDismissed")
                dismissed.isAccessible = true
                dismissed.set(this@BaseDialog, false)

                val showByMe = getDeclaredField("mShownByMe")
                showByMe.isAccessible = true
                showByMe.set(this@BaseDialog, true)
            }

            manager.beginTransaction().run {
                add(this@BaseDialog, tag)
                commitAllowingStateLoss()
            }
        }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return if (!isShowing() && !isRepeatedShow(tag ?: "")) {
            DialogFragment::class.java.run {
                val dismissed = getDeclaredField("mDismissed")
                dismissed.isAccessible = true
                dismissed.set(this@BaseDialog, false)

                val showByMe = getDeclaredField("mShownByMe")
                showByMe.isAccessible = true
                showByMe.set(this@BaseDialog, true)

                transaction.add(this@BaseDialog, tag)

                val viewDestroyed = getDeclaredField("mViewDestroyed")
                viewDestroyed.isAccessible = true
                viewDestroyed.set(this@BaseDialog, true)

                val backStackId = getDeclaredField("mBackStackId")
                backStackId.isAccessible = true
                val id = transaction.commitAllowingStateLoss()
                backStackId.set(this@BaseDialog, id)

                id
            }
        } else -1
    }

    private fun isRepeatedShow(tag: String): Boolean {
        System.arraycopy(mills, 1, mills, 0, 1)
        mills[1] = SystemClock.uptimeMillis()

        val result = tag == mShowTag && mills[1] - mills[0] < 500
        mShowTag = tag
        return result
    }

    fun isShowing(): Boolean = dialog?.isShowing ?: false

    /**
     * 创建ViewBinding
     */
    protected abstract fun createViewBinding(inflater: LayoutInflater): VB

    protected abstract fun initView()

    protected abstract fun initListener()

    protected abstract fun initData()

    protected open fun setLayout(window: Window) {
        window.setLayout((dm.widthPixels * 0.8f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}