package com.rsetiapp.core.util

import android.util.Base64

import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets;



object AESCryptography {

    private var enableEncryption = true

    @Throws(
        UnsupportedEncodingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )

    fun encryptIntoBase64String(inputText: String, secretKey: String, ivKey: String): String {
        if (enableEncryption) {
            val keySpec = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(ivKey.toByteArray(StandardCharsets.UTF_8))
            val cipher = Cipher.getInstance(AppConstant.Constants.CRYPLIBAES)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            val encryptedBytes: ByteArray = cipher.doFinal(inputText.toByteArray(StandardCharsets.UTF_8))

            // ✅ Encode as Base64 instead of hex
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT).trim()
        }
        return inputText
    }


    fun decryptIntoString(inputText: String, secretKey: String, ivKey: String): String {
        return try {
            // Ensure key and IV are exactly 16 bytes (AES-128)
            val keyBytes = secretKey.toByteArray(StandardCharsets.UTF_8)
            val ivBytes = ivKey.toByteArray(StandardCharsets.UTF_8)

            if (keyBytes.size != 16) {
                throw IllegalArgumentException("Key must be 16 bytes long (AES-128)")
            }
            if (ivBytes.size != 16) {
                throw IllegalArgumentException("IV must be 16 bytes long")
            }

            val keySpec = SecretKeySpec(keyBytes, "AES")
            val ivSpec = IvParameterSpec(ivBytes)
            val cipher = Cipher.getInstance(AppConstant.Constants.CRYPLIBAES)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            // ✅ Decode Base64 (check input)
            val decodedBytes = Base64.decode(inputText, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)

            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            log("Decryption failed: ", e.message.toString())
            ""
        }
    }


    private fun convertByteArrayToHexString(byteArray: ByteArray): String {
        val sb = StringBuffer()
        for (b in byteArray) {
            val hexString = String.format("%02X", b)
            sb.append(hexString)
        }
        return sb.toString()
    }

    fun aesDecrypt(encryptedText: String, secretKey: String, ivKey: String): String {
        return try {
            // Define AES settings
            val keySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(ivKey.toByteArray(Charsets.UTF_8))

            // Create AES cipher instance
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            // Decode hex string to byte array
            val encryptedBytes = hexStringToByteArray(encryptedText)

            // Perform decryption
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    // Helper function to convert hex string to byte array
    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }

}