package com.example.mylibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.example.mylibrary.data.database.entity.Student
import com.example.mylibrary.databinding.ItemStudentBinding

/**
 * Create by hzh on 2020/08/11.
 */
class StudentBinder : QuickViewBindingItemBinder<Student, ItemStudentBinding>() {

    override fun onCreateViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemStudentBinding = ItemStudentBinding.inflate(layoutInflater, parent, false)

    override fun convert(holder: BinderVBHolder<ItemStudentBinding>, data: Student) {
        holder.viewBinding.run {
            tvName.text = data.name
//            tvScore.text = "${data.score}"
        }
    }

    override fun onClick(
        holder: BinderVBHolder<ItemStudentBinding>,
        view: View,
        data: Student,
        position: Int
    ) {

    }

    class Diff : DiffUtil.ItemCallback<Student>() {

        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem.name == newItem.name
    }
}