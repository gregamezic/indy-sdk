package org.hyperledger.indy.sdk.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.ui.anoncreds.AnoncredsActivity
import org.hyperledger.indy.sdk.ui.anoncreds_revocation.AnoncredsRevocationActivity
import org.hyperledger.indy.sdk.ui.crypto.CryptoActivity
import org.hyperledger.indy.sdk.ui.endorser.EndorserActivity
import org.hyperledger.indy.sdk.ui.ledger.LedgerActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initClickListeners()
    }

    /**
     * Click listeners opens separate activity for selected demo
     */
    private fun initClickListeners() {
        btnAnoncreds.setOnClickListener {
            val intent = Intent(this, AnoncredsActivity::class.java)
            startActivity(intent)
        }

        btnAnoncredsRevocation.setOnClickListener {
            val intent = Intent(this, AnoncredsRevocationActivity::class.java)
            startActivity(intent)
        }

        btnCrypto.setOnClickListener {
            val intent = Intent(this, CryptoActivity::class.java)
            startActivity(intent)
        }

        btnEndorser.setOnClickListener {
            val intent = Intent(this, EndorserActivity::class.java)
            startActivity(intent)
        }

        btnLedger.setOnClickListener {
            val intent = Intent(this, LedgerActivity::class.java)
            startActivity(intent)
        }
    }
}