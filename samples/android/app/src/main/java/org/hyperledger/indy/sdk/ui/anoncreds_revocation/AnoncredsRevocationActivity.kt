package org.hyperledger.indy.sdk.ui.anoncreds_revocation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.demos.AnoncredsRevocation

class AnoncredsRevocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anoncreds_revocation)

        startDemo()
    }

    private fun startDemo() {
        AnoncredsRevocation.demo()
    }
}