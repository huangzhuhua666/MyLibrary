package com.example.mylibrary.data.database.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

/**
 * Create by hzh on 2020/08/10.
 */
@Entity
data class Student(
    @Id var id: Long = 0,
    var name: String = ""
) {

    lateinit var teachers: ToMany<Teacher>
}