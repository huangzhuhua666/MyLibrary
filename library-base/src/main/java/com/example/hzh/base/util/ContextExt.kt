package com.example.hzh.base.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.hzh.base.R
import java.io.File
import java.io.IOException

/**
 * Create by hzh on 2019/09/12.
 */
fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getString(resId), duration)
}

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Context.inflate(@LayoutRes resource: Int, root: ViewGroup? = null): View =
    LayoutInflater.from(this).inflate(resource, root)

/**
 * 创建一个File
 */
fun Context.createFile(name: String): File {
    (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED).yes {
        throw IOException(getString(R.string.base_create_file_failed))
    }

    val type = when (name.substringAfterLast('.')) {
        "apk" -> Environment.DIRECTORY_DOWNLOADS
        "jpg", "jpeg", "png", "gif" -> Environment.DIRECTORY_PICTURES
        "mp3" -> Environment.DIRECTORY_MUSIC
        "mp4", "rmvb", "mkv" -> Environment.DIRECTORY_MOVIES
        else -> Environment.DIRECTORY_DOCUMENTS
    }

    getExternalFilesDir(type)?.let {
        if (!it.exists()) it.mkdirs()
        return File(it, name)
    }
    throw IOException(getString(R.string.base_create_file_failed))
}

/**
 * 安装apk
 * @param path apk文件路径
 */
fun Context.installApp(path: String) = getInstallIntent(path)?.let { startActivity(it) }

/**
 * 获取安装activity的意图
 * @param path apk文件路径
 */
fun Context.getInstallIntent(path: String): Intent? = File(path).let {
    (!it.exists()).yes { return@let null }

    Intent(Intent.ACTION_VIEW).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK

        targetVersion(
            Build.VERSION_CODES.N,
            after = {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            })

        setDataAndType(getUriForFile(it), "application/vnd.android.package-archive")
    }
}

/**
 * 获取文件uri
 * @param file
 */
fun Context.getUriForFile(file: File): Uri? {
    var uri: Uri? = null
    targetVersion(
        Build.VERSION_CODES.N,
        after = {
            uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        },
        before = {
            uri = Uri.fromFile(file)
        })
    return uri
}

/**
 * 隐藏软键盘
 */
fun Activity.hideKeyboard(view: View) {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        view.windowToken, 0
    )
}

/**
 * 启动一个Activity
 *
 * @param bundle 参数传递
 * @param requestCode 不传值默认-1，表示不带返回结果的启动
 * @param finishCurrent 是否关闭当前Activity，只有requestCode为-1，即不带返回结果的启动Activity时才生效
 */
inline fun <reified A : Activity> Activity.startActivity(
    bundle: Bundle? = null,
    requestCode: Int = -1,
    finishCurrent: Boolean = false
) {
    if (bundle == null) {
        if (requestCode == -1) startActivity(Intent(this, A::class.java))
        else startActivityForResult(Intent(this, A::class.java), requestCode)
    } else {
        val intent = Intent(this, A::class.java).apply { putExtras(bundle) }
        if (requestCode == -1) startActivity(intent)
        else startActivityForResult(intent, requestCode)
    }

    if (requestCode == -1 && finishCurrent) finish()
}

/**
 * 新建一个Fragment
 */
inline fun <reified F : Fragment> newFragment(bundle: Bundle? = null) =
    if (bundle == null) F::class.java.newInstance()
    else F::class.java.newInstance().apply { arguments = bundle }

/**
 * 获取进程名
 */
fun Context.getCurrentProcessName(): String? =
    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).run {
        val runningApps = runningAppProcesses ?: return null

        runningApps.forEach { proInfo ->
            if (proInfo.pid == android.os.Process.myPid()) return proInfo.processName ?: null
        }

        return null
    }

/**
 * 获取屏幕宽度
 */
fun Context.getScreenWidth(): Int = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).run {
    val point = Point()
    defaultDisplay.getSize(point)
    point.x
}

/**
 * 获取屏幕高度
 */
fun Context.getScreenHeight(): Int = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).run {
    val point = Point()
    defaultDisplay.getSize(point)
    point.y
}

/**
 * 获取App缓存大小
 */
fun Context.getCacheSize(): String {
    var size = 0L
    try {
        size = cacheDir.getFolderSize()
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            size += externalCacheDir?.getFolderSize() ?: 0
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return size.formatSize()
}

/**
 * 清除缓存
 */
fun Context.clearCache() {
    cacheDir.deleteFile()
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) externalCacheDir?.deleteFile()
}

fun Context.easyGetColor(
    @ColorRes id: Int,
    theme: Resources.Theme? = null
) = ResourcesCompat.getColor(resources, id, theme)

fun Context.easyGetDrawable(
    @DrawableRes id: Int,
    theme: Resources.Theme? = null
) = ResourcesCompat.getDrawable(resources, id, theme)

/**
 * 是否为Debug的APP
 */
fun Context.isDebugApp() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0