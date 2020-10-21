package org.hyperledger.indy.sdk.ui.endorser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.demos.Endorser

class EndorserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endorser)

        startDemo()
    }

    private fun startDemo() {
        Endorser.demo()
    }
}