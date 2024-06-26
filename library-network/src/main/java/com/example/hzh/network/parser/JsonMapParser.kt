package com.example.hzh.network.parser

import com.example.hzh.network.NetConfig
import com.example.hzh.network.bean.JsonMap
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.Converter
import java.io.IOException
import java.lang.reflect.Type

/**
 * Create by hzh on 2020/6/3.
 */
@Parser(name = "JsonMap")
class JsonMapParser : TypeParser<JsonMap> {

    constructor() : super()

    constructor(type: Type) : super(type)

    @Throws(IOException::class)
    override fun onParse(response: Response): JsonMap {
        val map = Converter.convertTo<JsonMap>(response, JsonMap::class.java, *types)

        val code = try {
            map.getInt("success")
        } catch (e: Exception) {
            map.getString("success").toInt()
        }

        if (code != NetConfig.CODE_SUCCESS && code != NetConfig.CODE_LOGIN_EXPIRED) {
            throw ParseException(code.toString(), map.getString(NetConfig.ERROR_MSG_KEY), response)
        }

        return map
    }
}