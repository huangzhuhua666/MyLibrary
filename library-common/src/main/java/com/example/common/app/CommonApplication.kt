package com.example.common.app

import androidx.multidex.MultiDex
import com.example.common.BuildConfig
import com.example.hzh.base.application.BaseApplication
import com.example.hzh.network.NetConfig
import com.example.hzh.network.converter.JsonMapConverter
import com.example.hzh.network.cookie.MyCookie
import com.example.hzh.network.https.HttpsUtils
import com.example.hzh.network.interceptor.VerifyInterceptor
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import rxhttp.wrapper.param.RxHttp
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * Create by hzh on 2020/6/15.
 */
open class CommonApplication : BaseApplication() {

    companion object {

        val kv by lazy { MMKV.defaultMMKV()!! }

        /**
         * 网络是否可用
         */
        var isNetworkAvailable = false
    }

    override fun onCreate() {
        super.onCreate()
        // mmkv
        MMKV.initialize(applicationContext)

        // logger
        Logger.addLogAdapter(object : AndroidLogAdapter(PrettyFormatStrategy.newBuilder().run {
            tag("Demo")
            build()
        }) {
            override fun isLoggable(priority: Int, tag: String?): Boolean = debug
        })

        // 一些基本的网络配置：url...
//        NetConfig.run {
//            CODE_SUCCESS
//
//            BASE_URL
//        }

        // RxHttp
        RxHttp.init(OkHttpClient.Builder().run {
            retryOnConnectionFailure(true)
            connectTimeout(NetConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(NetConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(NetConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            cookieJar(MyCookie(applicationContext))
            addInterceptor(VerifyInterceptor(BuildConfig.DEBUG))
            HttpsUtils.getSSLSocketFactory()?.run {
                sslSocketFactory(sslSocketFactory, trustManager)
            }
            HostnameVerifier { _, _ -> true }
            build()
        }, false)

        // 公共请求头
        RxHttp.setOnParamAssembly { param ->
            param.run {
                addAllHeader(
                    mutableMapOf(
                        "Content-Type" to "application/json;charset=utf-8",
                        "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-TW;q=0.6,zh-CN;q=0.5,zh-HK;q=0.4",
                        "User-Agent" to "Mozilla/5.0 (compatible; mobile; ios;android; zzb;)"
                    )
                ) // 添加公共请求头
            }
        }
        RxHttp.setConverter(JsonMapConverter.create())

        // 初始化LiveEventBus
        LiveEventBus.config().autoClear(true).enableLogger(debug)

        MultiDex.install(applicationContext)
    }
}