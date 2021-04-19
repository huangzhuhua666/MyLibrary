package com.example.mylibrary.ui.activity

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseBinderAdapter
import com.example.common.activity.UIActivity
import com.example.hzh.base.util.vbInflate
import com.example.mylibrary.adapter.binder.MovieBinder
import com.example.mylibrary.app.GlideApp
import com.example.mylibrary.databinding.ActivityCatEyeBinding
import com.example.mylibrary.util.gaussianBlur
import com.example.mylibrary.widget.MovieLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create by hzh on 4/9/21.
 */
class CatEyeActivity : UIActivity<ActivityCatEyeBinding>(),
        CoroutineScope by CoroutineScope(Dispatchers.Main) {

    override val mBinding by vbInflate<ActivityCatEyeBinding>()

    override val isStatusBarDarkFont = true

    private val mAdapter by lazy { BaseBinderAdapter().addItemBinder(MovieBinder(), MovieBinder.Diff()) }
    private val mMoveLayoutManager by lazy { MovieLayoutManager() }
    private val mSnapHelper by lazy { LinearSnapHelper() }

    private var mPreBg: Drawable? = null
    private val mData by lazy {
        listOf(
            "https://img2.doubanio.com/view/photo/s_ratio_poster/public/p480747492.webp",
            "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2561716440.webp",
            "https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2372307693.webp",
            "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p511118051.webp",
            "https://img9.doubanio.com/view/photo/s_ratio_poster/public/p457760035.webp",
            "https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2578474613.webp",
            "https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2557573348.webp",
            "https://img2.doubanio.com/view/photo/s_ratio_poster/public/p492406163.webp",
            "https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2616355133.webp",
            "https://img1.doubanio.com/view/photo/s_ratio_poster/public/p524964039.webp",
        )
    }

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {
        mBinding.rvMovie.run {
            layoutManager = mMoveLayoutManager
            adapter = mAdapter
            mSnapHelper.attachToRecyclerView(this@run)
        }
    }

    override fun initData() {
        mAdapter.setDiffNewData(mData.toMutableList())

        launch { setMovieBg(0) }
    }

    private suspend fun setMovieBg(index: Int) {
        val bitmap = withContext(Dispatchers.IO) {
            GlideApp.with(mContext)
                .asBitmap()
                .load(mData[index])
                .submit()
                .get()
        }

        if (!isActive) return

        val currBg = BitmapDrawable(resources, gaussianBlur(15f, bitmap))
        mPreBg = mPreBg ?: currBg

        GlideApp.with(mContext)
            .load(bitmap)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(mPreBg)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(mBinding.ivMovieBg)

        mPreBg = currBg
    }

    override fun initListener() {
        mBinding.rvMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE) return

                val snapView = mSnapHelper.findSnapView(mMoveLayoutManager)!!
                launch { setMovieBg(mBinding.rvMovie.getChildAdapterPosition(snapView)) }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}