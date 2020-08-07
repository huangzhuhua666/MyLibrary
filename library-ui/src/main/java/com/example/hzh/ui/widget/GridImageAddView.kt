package com.example.hzh.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hzh.ui.R
import com.example.hzh.ui.adapter.GridImageAddAdapter
import com.example.hzh.ui.utils.ImageLoadEngine
import com.example.hzh.ui.utils.dp2px
import kotlin.math.min

/**
 * Create by hzh on 2020/07/30.
 */
class GridImageAddView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val mRecyclerView by lazy {
        RecyclerView(context).apply {
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            layoutManager = GridLayoutManager(context, spanCount)
        }
    }

    private val mAdapter by lazy {
        GridImageAddAdapter(
            context,
            defaultAddImage,
            defaultDeleteIcon,
            maxCount,
            itemHeight.toInt(),
            itemMargin.toInt(),
            itemRadii
        )
    }

    /**
     * 添加图片占位图
     */
    @DrawableRes
    private val defaultAddImage: Int

    /**
     * 删除按钮图标
     */
    @DrawableRes
    private val defaultDeleteIcon: Int

    /**
     * 一行展示的数量，默认3张
     */
    private val spanCount: Int

    /**
     * 最大添加图片数量，默认9张
     */
    private val maxCount: Int

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
        context.obtainStyledAttributes(attrs, R.styleable.GridImageAddView).run {
            defaultAddImage =
                getResourceId(R.styleable.GridImageAddView_defaultAddImage, R.drawable.ui_ic_add_image)

            defaultDeleteIcon =
                getResourceId(R.styleable.GridImageAddView_defaultDeleteIcon, R.drawable.ui_ic_close_round_gray)

            spanCount = getInteger(R.styleable.GridImageAddView_spanCount, 3)

            maxCount = getInteger(R.styleable.GridImageAddView_maxCount, 9)

            itemHeight = getDimension(R.styleable.GridImageAddView_itemHeight, 100f.dp2px(context))

            itemMargin = getDimension(R.styleable.GridImageAddView_itemMargin, 0f)

            itemRadii = getDimension(R.styleable.GridImageAddView_itemRadii, 0f)

            recycle()
        }

        addView(mRecyclerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mRecyclerView.adapter = mAdapter
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val layoutHeight = getChildAt(0).measuredHeight + paddingTop + paddingBottom
        setMeasuredDimension(widthSize, min(heightSize, layoutHeight))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        getChildAt(0).let {
            it.layout(paddingLeft, paddingTop, paddingLeft + it.measuredWidth, paddingTop + it.measuredHeight)
        }
    }

    /**
     * 设置图片加载方式
     */
    fun setImageLoadEngine(loadEngine: ImageLoadEngine) {
        mAdapter.imageLoadEngine = loadEngine
    }

    /**
     * 设置点击添加图片监听
     */
    fun setAddImageListener(listener: (Int) -> Unit) {
        mAdapter.onAddImage = listener
    }

    /**
     * 预览图片
     */
    fun setOnPreviewImage(listener: (Int, List<String>) -> Unit) {
        mAdapter.onPreviewImage = listener
    }

    /**
     * 添加图片用于显示
     */
    fun addImage(position: Int, path: String) {
        mAdapter.addImage(position, path)
    }

    /**
     * 获取图片url列表
     */
    fun getImageList(): List<String> = mAdapter.getImageList()
}