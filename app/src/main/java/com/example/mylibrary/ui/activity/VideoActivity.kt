@file:Suppress("DEPRECATION")

package com.example.mylibrary.ui.activity

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.view.Surface
import android.view.TextureView
import com.example.common.activity.UIActivity
import com.example.hzh.base.util.createFile
import com.example.hzh.base.util.no
import com.example.hzh.base.util.vbInflate
import com.example.hzh.base.util.yes
import com.example.hzh.ui.utils.filterFastClickListener
import com.example.mylibrary.R
import com.example.mylibrary.databinding.ActivityVideoBinding
import java.io.FileOutputStream

/**
 * Create by hzh on 2020/10/27.
 */
class VideoActivity : UIActivity<ActivityVideoBinding>(),
        TextureView.SurfaceTextureListener,
        MediaRecorder.OnInfoListener {

    override val mBinding by vbInflate<ActivityVideoBinding>()

    private var isRecording = false
    private var mCamera: Camera? = null
    private var mRecorder: MediaRecorder? = null
    private val mTempPicFile by lazy { createFile("temp_pic.png") }
    private val mTempVideoFile by lazy { createFile("temp_video.mp4") }

    override fun initView() {
        mTempPicFile.exists().yes { mTempPicFile.delete() }
        mTempVideoFile.exists().yes { mTempVideoFile.delete() }

        mRecorder = MediaRecorder()

        mBinding.textureView.surfaceTextureListener = this
    }

    override fun initData() {

    }

    override fun initListener() {
        mBinding.run {
            btnControl.filterFastClickListener {
                isRecording.yes {
                    releaseRes()
                    getVideoFirstFrame()
                    finish()
                }.no {
                    mRecorder?.start()
                }

                isRecording = !isRecording

                btnControl.text = getString(isRecording.yes { R.string.end }.no { R.string.start })
            }
        }
    }

    private fun prepareMediaRecorder(surface: Surface) {
        mRecorder?.run {
//            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)

            // 输出格式
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            // 视频帧率
//            setVideoFrameRate(30)
            // 视频大小
//            setVideoSize(720, 1280)
            // 视频比特率
            setVideoEncodingBitRate(100 * 1024 * 1024)
            // 视频编码器
            setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP)

            // 音频编码率
//            setAudioEncodingBitRate()
            // 音频声道
//            setAudioChannels()
            // 音频采样率
//            setAudioSamplingRate()
            // 音频编码器
//            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            // 输出路径
            setOutputFile(mTempVideoFile.absolutePath)

            // 设置视频输出的最大尺寸
//            setMaxFileSize()

            // 设置视频输出的最大时长
            setMaxDuration(10 * 1000)

            setOnInfoListener(this@VideoActivity)

            setOrientationHint(90)

            setPreviewDisplay(surface)

            prepare()
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        try {
            mCamera = Camera.open().apply {
                setDisplayOrientation(90)
                setPreviewTexture(surface)
                startPreview()

                unlock()
                mRecorder?.setCamera(this)
                prepareMediaRecorder(Surface(surface))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        releaseRes()
        getVideoFirstFrame()
        return true
    }

    override fun onInfo(mr: MediaRecorder?, what: Int, extra: Int) {
        when (what) {
            MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED -> {
                releaseRes()
                getVideoFirstFrame()
                finish()
            }
        }
    }

    private fun releaseRes() {
        mRecorder?.run {
            release()
            mCamera?.lock()
        }
        mRecorder = null

        mCamera?.run {
            stopPreview()
            release()
        }
        mCamera = null
    }

    private fun getVideoFirstFrame() {
        try {
            MediaMetadataRetriever().run {
                setDataSource(mTempVideoFile.absolutePath)
                frameAtTime?.let { bitmap ->
                    val fos = FileOutputStream(mTempPicFile)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.flush()
                    fos.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}