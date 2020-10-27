package com.example.mylibrary.adapter.binder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.example.mylibrary.databinding.ItemTextBinding

/**
 * Create by hzh on 2020/10/05.
 */
class TextBinder : QuickViewBindingItemBinder<String, ItemTextBinding>() {

    override fun onCreateViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTextBinding = ItemTextBinding.inflate(layoutInflater, parent, false)

    override fun convert(holder: BinderVBHolder<ItemTextBinding>, data: String) {
        holder.viewBinding.root.text = data
    }
}