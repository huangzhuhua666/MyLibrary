package com.example.hzh.base.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Create by hzh on 3/30/21.
 */
inline fun <reified VB : ViewBinding> Activity.vbInflate() = lazy {
    VB::class.java.getMethod("inflate", LayoutInflater::class.java)
        .invoke(null, layoutInflater) as VB
}

inline fun <reified VB : ViewBinding> Fragment.vbBind() = FragmentViewBindingDelegate(VB::class.java)

@Suppress("UNCHECKED_CAST")
class FragmentViewBindingDelegate<VB : ViewBinding>(
    private val clazz: Class<VB>
) : ReadOnlyProperty<Fragment, VB> {

    private var isInitialized = false
    private var _binding: VB? = null
    private val mBinding: VB get() = _binding!!

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (isInitialized) return mBinding

        thisRef.viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyView() {
                _binding = null
            }
        })

        _binding = (clazz.getMethod("bind", View::class.java)
            .invoke(null, thisRef.requireView()) as VB).also {
            it.root.parent?.let { p -> (p as ViewGroup).removeView(it.root) }
        }

        isInitialized = true

        return mBinding
    }
}