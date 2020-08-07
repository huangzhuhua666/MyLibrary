package com.example.hzh.ui.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hzh.ui.utils.ImageLoadEngine
import com.example.hzh.ui.utils.filterFastClickListener
import com.example.hzh.ui.widget.RImageView

/**
 * Create by hzh on 2020/07/28.
 */
internal class GridImageShowAdapter(
    private val context: Context,
    private val itemHeight: Int,
    private val itemMargin: Int,
    private val itemRadii: Float
) : RecyclerView.Adapter<GridImageShowAdapter.ViewHolder>() {

    var imageLoadEngine: ImageLoadEngine? = null

    var onImageClickListener: ((Int, List<String>) -> Unit)? = null

    var imageList: List<String> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(RImageView(context).apply {
            shape = RImageView.Shape.ROUND_CORNER
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                itemHeight
            ).apply {
                marginStart = itemMargin
                marginEnd = itemMargin
                topMargin = itemMargin
                bottomMargin = itemMargin
            }
            setRadii(itemRadii)
        })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.let {
            imageLoadEngine?.loadImage(context, imageList[position], it)
            it.filterFastClickListener { onImageClickListener?.invoke(position, imageList) }
        }
    }

    override fun getItemCount(): Int = imageList.size

    class ViewHolder(val imageView: RImageView) : RecyclerView.ViewHolder(imageView)
}