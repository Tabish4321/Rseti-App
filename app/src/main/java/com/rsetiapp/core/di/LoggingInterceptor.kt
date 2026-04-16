package com.rsetiapp.core.di

import android.util.Log
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

class LoggingInterceptor : Interceptor {

    private val TAG = "API_LOG"

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "REQUEST")
        Log.d(TAG, "URL     : ${request.url}")
        Log.d(TAG, "Method  : ${request.method}")
        Log.d(TAG, "Headers : ${request.headers}")
        Log.d(TAG, "AuthHdr : ${request.header("Authorization")}")

        request.body?.let {
            Log.d(TAG, "Body    : ${bodyToString(it)}")
        }

        val response = chain.proceed(request)

        val peekBody = response.peekBody(1024 * 1024)

        Log.d(TAG, "-------------------- RESPONSE --------------------")
        Log.d(TAG, "Code : ${response.code}")
        Log.d(TAG, "URL  : ${response.request.url}")
        Log.d(TAG, "Body : ${peekBody.string()}")

        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        return response
    }

    private fun bodyToString(body: RequestBody): String {
        return try {
            val buffer = Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            "Unable to read body"
        }
    }
}