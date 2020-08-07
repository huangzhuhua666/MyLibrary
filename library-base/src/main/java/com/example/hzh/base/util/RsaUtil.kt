package com.example.hzh.base.util

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Create by hzh on 2020/6/17.
 */
object RsaUtil {

    private val CHARSET = Charsets.UTF_8
    private const val RSA_ALGORITHM = "RSA"

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(publicKey: String): RSAPublicKey = KeyFactory.getInstance(RSA_ALGORITHM)
        .generatePublic(
            X509EncodedKeySpec(Base64.decode(publicKey.toByteArray(), 0))
        ) as RSAPublicKey

    fun encrypt(data: String, publicKey: RSAPublicKey): String = try {
        Cipher.getInstance("RSA/ECB/PKCS1Padding").run {
            init(1, publicKey)
            Base64.encodeToString(
                rsaSplitCode(
                    this,
                    1,
                    data.toByteArray(CHARSET),
                    publicKey.modulus.bitLength()
                ), 0
            )
        }
    } catch (e: Exception) {
        throw java.lang.RuntimeException("加密字符串[$data]时遇到异常", e)
    }

    private fun rsaSplitCode(
        cipher: Cipher,
        opMode: Int,
        data: ByteArray,
        keySize: Int
    ): ByteArray {
        val maxBlock = (opMode == 2).yes { keySize / 8 }.no { keySize / 8 - 11 }

        var offset = 0
        var i = 0
        var result: ByteArray
        ByteArrayOutputStream().run {
            try {
                while (data.size > offset) {
                    result = (data.size - offset > maxBlock)
                        .yes { cipher.doFinal(data, offset, maxBlock) }
                        .no { cipher.doFinal(data, offset, data.size - offset) }

                    write(result, 0, result.size)
                    ++i
                    offset = i * maxBlock
                }
            } catch (e: Exception) {
                throw RuntimeException("加解密阀值为[$maxBlock]的数据时发生异常", e)
            }

            result = toByteArray()

            closeSafely()
        }

        return result
    }
}