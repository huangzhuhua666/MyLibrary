package com.example.mylibrary.adapter.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.example.mylibrary.R
import com.example.mylibrary.data.database.entity.Teacher
import com.example.mylibrary.databinding.ItemTeacherBinding

/**
 * Create by hzh on 2020/08/11.
 */
class TeacherBinder : QuickViewBindingItemBinder<Teacher, ItemTeacherBinding>() {

    init {
        addChildClickViewIds(R.id.btn_delete)
    }

    override fun onCreateViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemTeacherBinding = ItemTeacherBinding.inflate(layoutInflater, parent, false)

    override fun convert(holder: BinderVBHolder<ItemTeacherBinding>, data: Teacher) {
        holder.viewBinding.tvName.text = data.name
    }

    override fun onClick(
        holder: BinderVBHolder<ItemTeacherBinding>,
        view: View,
        data: Teacher,
        position: Int
    ) {

    }

    class Diff : DiffUtil.ItemCallback<Teacher>() {

        override fun areItemsTheSame(oldItem: Teacher, newItem: Teacher): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Teacher, newItem: Teacher): Boolean =
            oldItem.name == newItem.name
    }
}