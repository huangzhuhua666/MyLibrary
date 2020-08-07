package com.example.hzh.network.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import com.example.hzh.network.BuildConfig

/**
 * Create by hzh on 2020/6/8.
 */
@Suppress("MissingPermission")
class NetworkReceiver(
    activity: Activity,
    private val networkCallback: NetworkCallback
) : BroadcastReceiver() {

    private var callback: ConnectivityManager.NetworkCallback? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0以上，通过注册NetworkCallback接收消息
            val man = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            callback = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    if (BuildConfig.DEBUG) Log.d("Net", "网络可用")
                    networkCallback.onAvailable()
                }

                override fun onLost(network: Network) {
                    if (BuildConfig.DEBUG) Log.d("Net", "网络不可用")
                    networkCallback.onLost()
                }
            }

            man.registerDefaultNetworkCallback(callback!!)
        } else { // 7.0以下，用动态广播接收消息
            activity.registerReceiver(
                this,
                IntentFilter().apply { addAction(ConnectivityManager.CONNECTIVITY_ACTION) })
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != ConnectivityManager.CONNECTIVITY_ACTION) return

        val info = intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)

        if (info != null && info.isConnected) {
            if (BuildConfig.DEBUG) Log.d("Net", "网络可用")
            networkCallback.onAvailable()
        } else {
            if (BuildConfig.DEBUG) Log.d("Net", "网络不可用")
            networkCallback.onLost()
        }
    }

    /**
     * 注销广播或者Callback
     */
    fun unregister(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val man = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            man.unregisterNetworkCallback(callback!!)
        } else activity.unregisterReceiver(this)
    }

    interface NetworkCallback {

        /**
         * 网络可用
         */
        fun onAvailable()

        /**
         * 网络不可用
         */
        fun onLost()
    }
}