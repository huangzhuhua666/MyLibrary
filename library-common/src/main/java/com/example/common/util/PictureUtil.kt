package com.example.common.util

import android.app.Activity
import android.os.Build
import com.example.common.R
import com.example.hzh.base.util.no
import com.example.hzh.base.util.yes
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 * Create by hzh on 2020/07/22.
 */
fun Activity.openGalley(
    maxNum: Int = 1,
    onResult: (List<String>) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())
        .theme(R.style.common_PictureViewStyle)
        .imageEngine(GlideEngine.getInstance())
        .imageSpanCount(4)
        .maxSelectNum(maxNum)
        .isCamera(false)
        .isEnableCrop(false)
        .isCompress(true)
        .minimumCompressSize(1024)
        .compressQuality(90)
        .forResult(object : OnResultCallbackListener<LocalMedia> {

            override fun onResult(result: MutableList<LocalMedia>?) {
                result?.let { list ->
                    onResult.invoke(list.map {
                        it.isCompressed.yes { it.compressPath } // 压缩，使用压缩后的路径
                            .no {
                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q).yes {
                                    it.androidQToPath // Android Q之后，用这个获取真实路径
                                }.no { it.path } // Android Q之前，用这个获取路径
                            }
                    })
                }
            }

            override fun onCancel() {
                onCancel?.invoke()
            }
        })
}

fun Activity.openCamera(
    onResult: (List<String>) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    PictureSelector.create(this)
        .openCamera(PictureMimeType.ofImage())
        .theme(R.style.common_PictureViewStyle)
        .imageEngine(GlideEngine.getInstance())
        .isEnableCrop(false)
        .isCompress(true)
        .minimumCompressSize(1024)
        .compressQuality(90)
        .forResult(object : OnResultCallbackListener<LocalMedia> {

            override fun onResult(result: MutableList<LocalMedia>?) {
                result?.let { list ->
                    onResult.invoke(list.map {
                        it.isCompressed.yes { it.compressPath } // 压缩，使用压缩后的路径
                            .no {
                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q).yes {
                                    it.androidQToPath // Android Q之后，用这个获取真实路径
                                }.no { it.path } // Android Q之前，用这个获取路径
                            }
                    })
                }
            }

            override fun onCancel() {
                onCancel?.invoke()
            }
        })
}

fun Activity.previewImage(medias: List<LocalMedia>, position: Int) {
    PictureSelector.create(this)
        .themeStyle(R.style.common_PictureViewStyle)
        .imageEngine(GlideEngine.getInstance())
        .openExternalPreview(position, medias)
}