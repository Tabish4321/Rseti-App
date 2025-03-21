package com.rsetiapp.core.uidai.crypto

import com.rsetiapp.core.util.AppConstant.Constants.CRYPT_ID
import com.rsetiapp.core.util.AppConstant.Constants.CRYPT_IV
import kotlin.Throws
import java.lang.Exception
import javax.inject.Inject


class EncryptionImpl @Inject constructor(private val cryptLib: CryptLib) : CryptoStrategy {

    @Throws(Exception::class)
    override fun encrypt(body: String): String {
        return cryptLib.encrypt(body, CRYPT_ID, CRYPT_IV)
    }

    override fun decrypt(data: String): String? {
        return null
    }
}