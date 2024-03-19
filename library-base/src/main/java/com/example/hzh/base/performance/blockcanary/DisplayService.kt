package com.example.hzh.base.performance.blockcanary

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.example.hzh.base.Global
import com.example.hzh.base.R
import com.example.hzh.base.performance.blockcanary.data.BlockInfo
import com.example.hzh.base.performance.blockcanary.ui.DisplayActivity
import com.example.hzh.base.util.ResourcesUtils

/**
 * Create by hzh on 2024/3/15.
 */
internal class DisplayService : BlockInterceptor {

    override fun onBlock(context: Context, blockInfo: BlockInfo) {
        val intent = Intent(context, DisplayActivity::class.java).also {
            it.putExtras(bundleOf("show_latest" to blockInfo.timeStart))
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        show(
            context,
            ResourcesUtils.getString(R.string.base_block_canary_class_has_blocked, blockInfo.timeStart),
            PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        )
    }

    private fun show(context: Context, contentTitle: String, pendingIntent: PendingIntent) {
        Global.getSystemService<NotificationManager>(Context.NOTIFICATION_SERVICE)?.let {
            val notification = Notification.Builder(context)
                .setSmallIcon(R.drawable.base_block_canary_notification)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(contentTitle)
                .setContentText(ResourcesUtils.getString(R.string.base_block_canary_notification_message))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build()

            it.notify(0xDEAFBEEF.toInt(), notification)
        }
    }
}