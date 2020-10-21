package org.hyperledger.indy.sdk.ui.crypto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.demos.Crypto

class CryptoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        startDemo()
    }

    private fun startDemo() {
        Crypto.demo()
    }
}