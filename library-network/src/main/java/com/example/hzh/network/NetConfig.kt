package com.example.hzh.network

import rxhttp.wrapper.annotation.DefaultDomain
import kotlin.properties.Delegates

/**
 * Create by hzh on 2019/08/26.
 */
object NetConfig {

    var CODE_SUCCESS by Delegates.notNull<Int>()

    var CODE_LOGIN_EXPIRED by Delegates.notNull<Int>()

    var ERROR_MSG_KEY by Delegates.notNull<String>()

    var CONNECT_TIMEOUT = 10L

    var READ_TIMEOUT = 10L

    var WRITE_TIMEOUT = 10L

    var DOMAIN = ""

    @DefaultDomain
    lateinit var BASE_URL: String

    var CACHE_SIZE = 10 * 1024 * 1024L

    var CACHE_PATH = ""

}