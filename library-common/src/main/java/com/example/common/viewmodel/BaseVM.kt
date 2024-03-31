package com.example.common.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.BuildConfig
import com.example.hzh.base.util.yes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create by hzh on 2020/5/18.
 */
open class BaseVM : ViewModel() {

    /**
     * 是否需要显示loading动画
     */
    protected val _isShowLoading = MutableLiveData(false)

    /**
     * @see _isShowLoading
     */
    val isShowLoading: LiveData<Boolean> = _isShowLoading

    /**
     * 是否已加载完数据，用来控制SmartRefreshLayout的刷新加载动画
     */
    protected val _isFinish = MutableLiveData(false)

    /**
     * @see _isFinish
     */
    val isFinish: LiveData<Boolean> = _isFinish

    /**
     * 是否已没有更多数据，用来控制SmartRefreshLayout是否可以继续加载
     */
    protected val _isNoMoreData = MutableLiveData(false)

    /**
     * @see _isNoMoreData
     */
    val isNoMoreData: LiveData<Boolean> = _isNoMoreData

    /**
     * 是否没有数据
     */
    protected val _isDataEmpty = MutableLiveData(false)

    /**
     * @see _isDataEmpty
     */
    val isDataEmpty: LiveData<Boolean> = _isDataEmpty

    /**
     * toast信息
     */
    protected val _toastTip = MutableLiveData<@receiver:StringRes Int>()

    /**
     * @see _toastTip
     */
    val toastTip: LiveData<Int> = _toastTip

    /**
     * 捕获的异常
     */
    protected val _exception = MutableLiveData<Throwable>()

    /**
     * @see _exception
     */
    val exception: LiveData<Throwable> = _exception

    protected fun action(
        block: suspend CoroutineScope.() -> Unit,
        onError: ((Throwable) -> Unit)? = null,
        onStart: (() -> Unit)? = null,
        onFinally: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            // do on start
            onStart?.invoke()

            try {
                withContext(Dispatchers.IO) {
                    block()
                }
            } catch (e: Exception) {
                onError?.invoke(e)

                BuildConfig.DEBUG.yes {
                    e.printStackTrace()
                }

                _exception.value = e
            } finally {
                _isShowLoading.value = false
                _isFinish.value = true
                onFinally?.invoke()
            }
        }
    }
}