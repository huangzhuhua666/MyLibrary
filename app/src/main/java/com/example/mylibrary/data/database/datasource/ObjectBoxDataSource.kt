package com.example.mylibrary.data.database.datasource

import com.example.common.util.OperateCallback
import com.example.mylibrary.app.App
import com.example.mylibrary.data.database.entity.Teacher
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.boxFor
import io.objectbox.reactive.DataSubscription

/**
 * Create by hzh on 2020/08/11.
 */
class ObjectBoxDataSource private constructor() {

    companion object {

        fun getInstance(): ObjectBoxDataSource = HOLDER.instance
    }

    private object HOLDER {

        val instance = ObjectBoxDataSource()
    }

    fun initData(teachers: List<Teacher>) {
        App.boxStore.boxFor<Teacher>().put(teachers)
    }

    fun getTeacherAll(callback: OperateCallback<List<Teacher>>): DataSubscription = App.boxStore.boxFor<Teacher>()
        .query()
        .build()
        .subscribe()
        .on(AndroidScheduler.mainThread())
        .observer { callback.onCallback(it) }
}