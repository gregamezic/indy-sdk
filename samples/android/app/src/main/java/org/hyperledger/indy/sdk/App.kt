package org.hyperledger.indy.sdk

import android.app.Application
import android.system.ErrnoException
import android.system.Os
import org.hyperledger.indy.sdk.utils.EnvironmentUtils
import pl.brightinventions.slf4android.LogLevel
import pl.brightinventions.slf4android.LoggerConfiguration


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // init android helper
        init()
    }

    @Throws(ErrnoException::class)
    fun init() {

        if (BuildConfig.DEBUG) {
            // Trace debugging for testing.
            LoggerConfiguration.configuration()
                .setRootLogLevel(LogLevel.TRACE)
        }

        Os.setenv(EXTERNAL_STORAGE, EnvironmentUtils.getIndyHomePath(baseContext), true)
        Os.setenv(TMPDIR, EnvironmentUtils.getTmpPath(baseContext), true)
        if (!LibIndy.isInitialized()) {
            LibIndy.init()
        }
    }


    /**
     * statics constants
     */
    companion object {
        private const val EXTERNAL_STORAGE = "EXTERNAL_STORAGE"
        private const val TMPDIR = "TMPDIR"
    }
}
