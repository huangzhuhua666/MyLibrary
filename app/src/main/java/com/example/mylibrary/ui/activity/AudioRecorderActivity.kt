package com.example.mylibrary.ui.activity

import android.Manifest
import android.media.*
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.common.activity.UIActivity
import com.example.hzh.base.util.closeSafely
import com.example.hzh.base.util.no
import com.example.hzh.base.util.yes
import com.example.mylibrary.databinding.ActivityAudioRecorderBinding
import com.example.mylibrary.util.audio.Pcm2WavUtil
import com.gyf.immersionbar.ktx.immersionBar
import com.permissionx.guolindev.PermissionX
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.concurrent.thread

/**
 * Create by hzh on 2/20/21.
 */
@Suppress("SetTextI18n")
class AudioRecorderActivity : UIActivity<ActivityAudioRecorderBinding>() {

    companion object {

        /**
         * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
         */
        private const val SAMPLE_RATE_IN_HZ = 44100

        /**
         * 声道数。CHANNEL_IN_MONO, CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
         */
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO

        /**
         * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, ENCODING_PCM_FLOAT.
         */
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    override val isStatusBarDarkFont: Boolean = true

    private val isRecording = MutableLiveData(false)
    private var mRecorder: AudioRecord? = null

    private val isPlaying = MutableLiveData(false)
    private var mTrack: AudioTrack? = null

    override fun createViewBinding(): ActivityAudioRecorderBinding {
        return ActivityAudioRecorderBinding.inflate(layoutInflater)
    }

    override fun initTitleBar() {
        super.initTitleBar()
        immersionBar { fitsSystemWindows(true) }
    }

    override fun initView() {
        PermissionX.init(mContext)
            .permissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, _, _ -> if (!allGranted) finish() }
    }

    override fun initData() {

    }

    override fun initListener() {
        mBinding.run {
            btnRecord.setOnClickListener {
                (isRecording.value == true).yes { stopRecord() }.no { startRecord() }
            }

            btnConvert.setOnClickListener {
                val pcmFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
                val wavFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.wav")

                (!pcmFile.exists()).yes {
                    Log.d("Hzh", "origin file not exit")
                    return@setOnClickListener
                }

                wavFile.mkdirs()
                wavFile.exists().yes { wavFile.delete() }

                Pcm2WavUtil(
                    SAMPLE_RATE_IN_HZ,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT
                ).pcm2wav(pcmFile.absolutePath, wavFile.absolutePath).run {
                    Log.d("Hzh", "已保存在：${wavFile.path}")
                }
            }

            btnPlay.setOnClickListener {
                (isPlaying.value == true).yes { stopPlay() }.no { playAudioInModeStream() }
            }
        }

        isRecording.observe(mContext) { mBinding.btnRecord.text = it.yes { "StopRecord" }.no { "StartRecord" } }

        isPlaying.observe(mContext) { mBinding.btnPlay.text = it.yes { "StopPlay" }.no { "StartPlay" } }
    }

    private fun startRecord() {
        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE_IN_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
        val buffer = ByteArray(minBufferSize)

        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")

        file.mkdirs()
        file.exists().yes { file.delete() }

        mRecorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE_IN_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            minBufferSize
        ).also { it.startRecording() }

        isRecording.value = true

        thread {
            try {
                val fos = FileOutputStream(file)
                while (isRecording.value == true) {
                    val read = mRecorder!!.read(buffer, 0, minBufferSize)
                    (read != AudioRecord.ERROR_INVALID_OPERATION).yes { fos.write(buffer) }
                }
                fos.closeSafely()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isRecording.postValue(false)
                Log.d("Hzh", "已保存在：${file.path}")
            }
        }
    }

    private fun stopRecord() {
        isRecording.value = false
        mRecorder?.run {
            stop()
            release()
        }
        mRecorder = null
    }

    private fun playAudioInModeStream() {
        isPlaying.value = true

        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE_IN_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )

        mTrack = AudioTrack(
            AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_MEDIA)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            },
            AudioFormat.Builder().run {
                setSampleRate(SAMPLE_RATE_IN_HZ)
                setChannelMask(CHANNEL_CONFIG)
                setEncoding(AUDIO_FORMAT)
                build()
            },
            minBufferSize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        ).also { it.play() }

        thread {
            try {
                val pcmFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
                val fis = FileInputStream(pcmFile)
                val buffer = ByteArray(minBufferSize)

                while (fis.available() > 0) {
                    val readCount = fis.read(buffer)

                    if (readCount in listOf(AudioTrack.ERROR_INVALID_OPERATION, AudioTrack.ERROR_BAD_VALUE)) continue

                    (readCount != 0 && readCount != -1).yes {
                        mTrack!!.write(buffer, 0, readCount)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isPlaying.postValue(false)
            }
        }
    }

    private fun stopPlay() {
        isPlaying.value = false
        mTrack?.run {
            stop()
            release()
        }
        mTrack = null
    }
}