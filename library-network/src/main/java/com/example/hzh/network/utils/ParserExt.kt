package com.example.hzh.network.utils

import com.example.hzh.network.bean.JsonMap
import com.example.hzh.network.parser.JsonMapParser
import rxhttp.IRxHttp
import rxhttp.toParser

/**
 * Create by hzh on 2020/6/3.
 */
fun IRxHttp.toJsonMap() = toParser(JsonMapParser(JsonMap::class.java))