package org.hyperledger.indy.sdk.ui.anoncreds

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.demos.Anoncreds

class AnoncredsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anoncreds)

        startDemo();
    }

    private fun startDemo() {
        Anoncreds.demo()
    }
}