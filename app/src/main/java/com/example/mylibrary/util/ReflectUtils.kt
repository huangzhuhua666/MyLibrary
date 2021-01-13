package com.example.mylibrary.util

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Create by hzh on 2021/01/07.
 */
object ReflectUtils {

    fun getField(
        clazz: Class<*>,
        filedName: String
    ): Field = clazz.getDeclaredField(filedName).apply { isAccessible = true }

    fun setField(field: Field, target: Any?, value: Any?) = field.set(target, value)

    fun getMethod(
        clazz: Class<*>,
        methodName: String,
        paramType: Array<Class<*>>
    ): Method = clazz.getDeclaredMethod(methodName, *paramType).apply { isAccessible = true }
}