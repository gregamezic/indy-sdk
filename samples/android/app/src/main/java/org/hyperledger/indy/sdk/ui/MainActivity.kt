package org.hyperledger.indy.sdk.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.databinding.ActivityMainBinding
import org.hyperledger.indy.sdk.ui.anoncreds.AnoncredsActivity
import org.hyperledger.indy.sdk.ui.anoncreds_revocation.AnoncredsRevocationActivity
import org.hyperledger.indy.sdk.ui.crypto.CryptoActivity
import org.hyperledger.indy.sdk.ui.endorser.EndorserActivity
import org.hyperledger.indy.sdk.ui.ledger.LedgerActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initClickListeners()
    }

    /**
     * Click listeners opens separate activity for selected demo
     */
    private fun initClickListeners() {
        binding.btnAnoncreds.setOnClickListener {
            startActivity(AnoncredsActivity.getIntent(this))
        }

        binding.btnAnoncredsRevocation.setOnClickListener {
            startActivity(AnoncredsRevocationActivity.getIntent(this))
        }

        binding.btnCrypto.setOnClickListener {
            startActivity(CryptoActivity.getIntent(this))
        }

        binding.btnEndorser.setOnClickListener {
            startActivity(EndorserActivity.getIntent(this))
        }

        binding.btnLedger.setOnClickListener {
            startActivity(LedgerActivity.getIntent(this))
        }
    }
}
