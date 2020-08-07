package com.example.hzh.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.hzh.ui.databinding.UiItemAddImageBinding
import com.example.hzh.ui.utils.ImageLoadEngine
import com.example.hzh.ui.utils.filterFastClickListener

/**
 * Create by hzh on 2020/07/31.
 */
internal class GridImageAddAdapter(
    private val context: Context,
    @DrawableRes private val defaultAddImage: Int,
    @DrawableRes private val defaultDeleteIcon: Int,
    private val maxCount: Int,
    private val itemHeight: Int,
    private val itemMargin: Int,
    private val itemRadii: Float
) : RecyclerView.Adapter<GridImageAddAdapter.ViewHolder>() {

    var imageLoadEngine: ImageLoadEngine? = null

    var onAddImage: ((Int) -> Unit)? = null

    var onPreviewImage: ((Int, List<String>) -> Unit)? = null

    private val imageList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            UiItemAddImageBinding.inflate(LayoutInflater.from(context), parent, false).apply {
                root.layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    itemHeight
                ).apply {
                    marginStart = itemMargin
                    marginEnd = itemMargin
                    topMargin = itemMargin
                    bottomMargin = itemMargin
                }

                cvImage.radius = itemRadii
            }
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewBinding.run {
            if (position == imageList.size) ivImage.setImageResource(defaultAddImage)
            else imageLoadEngine?.loadImage(context, imageList[position], ivImage)
            ivImage.filterFastClickListener { // 添加、预览
                if (position == imageList.size) onAddImage?.invoke(position) // 添加
                else onPreviewImage?.invoke(position, imageList) // 预览
            }

            btnDelete.setImageResource(defaultDeleteIcon)
            btnDelete.isGone = position == imageList.size
            btnDelete.filterFastClickListener { // 删除
                imageList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount - position)
            }
        }
    }

    override fun getItemCount(): Int = if (imageList.size == maxCount) maxCount else imageList.size + 1

    fun addImage(position: Int, path: String) {
        imageList.add(position, path)
        notifyItemChanged(position)
    }

    fun getImageList(): List<String> = imageList

    class ViewHolder(val viewBinding: UiItemAddImageBinding) : RecyclerView.ViewHolder(viewBinding.root)
}