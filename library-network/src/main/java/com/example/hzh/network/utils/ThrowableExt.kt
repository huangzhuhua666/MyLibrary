package com.example.hzh.network.utils

import com.example.hzh.network.NetConfig
import kotlinx.coroutines.TimeoutCancellationException
import rxhttp.wrapper.exception.HttpStatusCodeException
import rxhttp.wrapper.exception.ParseException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Create by hzh on 2020/6/3.
 */
val Throwable.code: Int
    get() {
        val errorCode = when (this) {
            is HttpStatusCodeException -> statusCode // Http状态异常码
            is ParseException -> errorCode // 业务code异常
            else -> "-1"
        }

        return try {
            errorCode.toInt()
        } catch (e: Exception) {
            -1
        }
    }

val Throwable.msg: String
    get() = when (this) {
        is UnknownHostException -> "当前无网络，请检查你的网络设置" // 网络异常
        is SocketTimeoutException, // okhttp全局设置超时
        is TimeoutException, // rxjava中的timeout方法超时
        is TimeoutCancellationException -> // 协程超时
            "连接超时,请稍后再试"
        is ConnectException -> "网络不给力，请稍候再试！"
        is HttpStatusCodeException -> {
            if (code == NetConfig.CODE_LOGIN_EXPIRED) "需要登录" // 登录失效
            else message ?: "请求失败，请稍后再试" // 请求失败异常
        }
        is ParseException -> message ?: "未知错误，请稍后再试"
        else -> "未知错误，请稍后再试"
    }