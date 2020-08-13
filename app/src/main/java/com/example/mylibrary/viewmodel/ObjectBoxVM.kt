package com.example.mylibrary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.common.app.BaseApplication
import com.example.common.util.OperateCallback
import com.example.common.util.boolean
import com.example.common.viewmodel.BaseVM
import com.example.hzh.base.util.yes
import com.example.mylibrary.data.database.entity.Teacher
import com.example.mylibrary.model.ObjectBoxModel
import io.objectbox.reactive.DataSubscriptionList

/**
 * Create by hzh on 2020/08/10.
 */
class ObjectBoxVM(private val model: ObjectBoxModel) : BaseVM() {

    private var hasData by BaseApplication.kv.boolean("has_data")

    private val subscriptions by lazy { DataSubscriptionList() }

    private val _teachers = MutableLiveData<List<Teacher>>()
    val teachers: LiveData<List<Teacher>> = _teachers

    fun initData() {
        hasData.yes { return }

        model.initData().run { hasData = true }
    }

    fun getTeacherAll() {
        subscriptions.add(model.getTeacherAll(object : OperateCallback<List<Teacher>> {

            override fun onCallback(data: List<Teacher>) {
                _teachers.value = data
            }
        }))
    }

    override fun onCleared() {
        subscriptions.cancel()
        super.onCleared()
    }
}