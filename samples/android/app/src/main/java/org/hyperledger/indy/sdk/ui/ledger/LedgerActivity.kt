package org.hyperledger.indy.sdk.ui.ledger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.demos.Ledger

class LedgerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ledger)

        startDemo()
    }

    private fun startDemo() {
        Ledger.demo()
    }
}