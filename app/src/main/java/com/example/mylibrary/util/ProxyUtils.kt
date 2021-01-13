package com.example.mylibrary.util

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 * Create by hzh on 2021/01/07.
 */
object ProxyUtils {

    fun getProxy(handler: InvocationHandler): Any = handler.javaClass.run {
        Proxy.newProxyInstance(classLoader, interfaces, handler)
    }
}