package org.hyperledger.indy.sdk.helpers

import android.util.Log
import java.lang.Exception

class DemoActionHelper {


    companion object {
        private val TAG = DemoActionHelper::class.java.name

        // run in
        fun runDemoStep(action: () -> Unit): Boolean {
            return try {
                action()
                true
            } catch (e: Exception) {
                Log.e(TAG, "runDemoStep: ", e)
                false
            }
        }
    }
}
