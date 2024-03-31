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
import rxhttp.RxHttpPlugins
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
        RxHttpPlugins.init(OkHttpClient.Builder().let {
            it.retryOnConnectionFailure(true)
            it.connectTimeout(NetConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            it.readTimeout(NetConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            it.writeTimeout(NetConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            it.cookieJar(MyCookie(applicationContext))
            it.addInterceptor(VerifyInterceptor(BuildConfig.DEBUG))
            HttpsUtils.getSSLSocketFactory()?.let { sslParams ->
                it.sslSocketFactory(sslParams.sslSocketFactory, sslParams.trustManager)
            }
            it.hostnameVerifier { _, _ -> true }
            it.build()
        }).setOnParamAssembly {
            // 添加公共请求头
            it.addAllHeader(
                mutableMapOf(
                    "Content-Type" to "application/json;charset=utf-8",
                    "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-TW;q=0.6,zh-CN;q=0.5,zh-HK;q=0.4",
                    "User-Agent" to "Mozilla/5.0 (compatible; mobile; ios;android; zzb;)"
                )
            )
        }.setConverter(JsonMapConverter.create())

        // 初始化LiveEventBus
        LiveEventBus.config().autoClear(true).enableLogger(debug)

        MultiDex.install(applicationContext)
    }
}