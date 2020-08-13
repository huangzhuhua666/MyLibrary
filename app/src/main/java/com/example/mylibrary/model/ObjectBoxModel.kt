package com.example.mylibrary.model

import com.example.common.util.OperateCallback
import com.example.mylibrary.data.database.datasource.ObjectBoxDataSource
import com.example.mylibrary.data.database.entity.Student
import com.example.mylibrary.data.database.entity.Teacher
import io.objectbox.reactive.DataSubscription

/**
 * Create by hzh on 2020/08/10.
 */
class ObjectBoxModel {

    private val mObjectBoxDataSource by lazy { ObjectBoxDataSource.getInstance() }

    fun initData() {
        val student1 = Student(name = "学生1")
        val student2 = Student(name = "学生2")
        val student3 = Student(name = "学生3")
        val student4 = Student(name = "学生4")
        val student5 = Student(name = "学生5")

        mObjectBoxDataSource.initData(listOf(
            Teacher(name = "语文老师").apply {
                students.run {
                    add(student1)
                    add(student2)
                    add(student3)
                    add(student4)
                    add(student5)
                }
            },
            Teacher(name = "数学老师").apply {
                students.run {
                    add(student1)
                    add(student2)
                    add(student3)
                    add(student4)
                    add(student5)
                }
            },
            Teacher(name = "英语老师").apply {
                students.run {
                    add(student1)
                    add(student2)
                    add(student3)
                    add(student4)
                    add(student5)
                }
            }
        ))
    }

    fun getTeacherAll(callback: OperateCallback<List<Teacher>>): DataSubscription =
        mObjectBoxDataSource.getTeacherAll(callback)
}