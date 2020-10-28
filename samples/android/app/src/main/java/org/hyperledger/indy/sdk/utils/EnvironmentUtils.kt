package org.hyperledger.indy.sdk.utils

import android.content.Context

object EnvironmentUtils {
    val testPoolIP: String
        get() {
            val testPoolIp = System.getenv("TEST_POOL_IP")
            return testPoolIp ?: "127.0.0.1"
        }

    fun getIndyHomePath(context: Context): String {
        return context.filesDir.absolutePath + "/indy_client/"
    }

    fun getIndyHomePath(filename: String, context: Context): String {
        return getIndyHomePath(context) + filename
    }

    fun getTmpPath(context: Context): String {
        return context.cacheDir.absolutePath + "/tmp/"
    }

    @JvmStatic
	fun getTmpPath(filename: String, context: Context): String {
        return getTmpPath(context) + filename
    }
}
