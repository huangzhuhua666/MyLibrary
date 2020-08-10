package com.example.mylibrary.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.example.hzh.base.activity.BaseActivity
import com.example.hzh.base.util.yes
import com.example.mylibrary.databinding.ItemMenuBinding
import java.util.*

/**
 * Create by hzh on 2020/08/10.
 */
class MenuBinder : QuickViewBindingItemBinder<MenuBean, ItemMenuBinding>() {

    override fun onCreateViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemMenuBinding = ItemMenuBinding.inflate(layoutInflater, parent, false)

    override fun convert(holder: BinderVBHolder<ItemMenuBinding>, data: MenuBean) {
        holder.viewBinding.root.run {
            normalTextColor = Color.rgb(
                Random().nextInt(235),
                Random().nextInt(235),
                Random().nextInt(235)
            )

            text = data.name
        }
    }

    override fun onClick(
        holder: BinderVBHolder<ItemMenuBinding>,
        view: View,
        data: MenuBean,
        position: Int
    ) {
        context.let { it.startActivity(Intent(it, data.clazz)) }
    }
}

data class MenuBean(val name: String, val clazz: Class<*>)