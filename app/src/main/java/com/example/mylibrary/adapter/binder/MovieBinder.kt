package com.example.mylibrary.adapter.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.example.mylibrary.databinding.ItemMovieBinding

/**
 * Create by hzh on 4/9/21.
 */
class MovieBinder : QuickViewBindingItemBinder<String, ItemMovieBinding>() {

    override fun onCreateViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemMovieBinding = ItemMovieBinding.inflate(layoutInflater, parent, false)

    override fun convert(holder: BinderVBHolder<ItemMovieBinding>, data: String) {
        Glide.with(context)
            .load(data)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .format(DecodeFormat.PREFER_RGB_565)
            .skipMemoryCache(false)
            .into(holder.viewBinding.root)
    }

    override fun onClick(
        holder: BinderVBHolder<ItemMovieBinding>,
        view: View,
        data: String,
        position: Int
    ) {
        (view.parent as RecyclerView).smoothScrollToPosition(position)
    }

    class Diff : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}