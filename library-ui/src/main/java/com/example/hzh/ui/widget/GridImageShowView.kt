package com.example.hzh.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hzh.ui.R
import com.example.hzh.ui.adapter.GridImageShowAdapter
import com.example.hzh.ui.utils.ImageLoadEngine
import com.example.hzh.ui.utils.dp2px

/**
 * Create by hzh on 2020/07/28.
 */
class GridImageShowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val mAdapter by lazy {
        GridImageShowAdapter(context, itemHeight.toInt(), itemMargin.toInt(), itemRadii)
    }

    /**
     * 一行展示的数量
     */
    private val spanCount: Int

    /**
     * 每张图片的高度，默认100dp
     */
    private val itemHeight: Float

    /**
     * 每张图片的间隔，默认0dp
     */
    private val itemMargin: Float

    /**
     * 每张图片的圆角弧度，默认0dp
     */
    private val itemRadii: Float

    init {
        context.obtainStyledAttributes(attrs, R.styleable.GridImageShowView).run {
            spanCount = getInteger(R.styleable.GridImageShowView_spanCount, 3)

            itemHeight = getDimension(R.styleable.GridImageShowView_itemHeight, 100f.dp2px(context))

            itemMargin = getDimension(R.styleable.GridImageShowView_itemMargin, 0f)

            itemRadii = getDimension(R.styleable.GridImageShowView_itemRadii, 0f)

            recycle()
        }

        overScrollMode = OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        layoutManager = GridLayoutManager(context, spanCount)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        adapter = mAdapter
    }

    /**
     * 设置点击添加图片监听
     */
    fun setImageLoadEngine(loadEngine: ImageLoadEngine) {
        mAdapter.imageLoadEngine = loadEngine
    }

    /**
     * 设置图片列表用于显示
     */
    fun setImageList(imageList: List<String>) {
        mAdapter.imageList = imageList
    }

    /**
     * 预览图片
     */
    fun setOnImageClickListener(listener: (Int, List<String>) -> Unit) {
        mAdapter.onImageClickListener = listener
    }
}