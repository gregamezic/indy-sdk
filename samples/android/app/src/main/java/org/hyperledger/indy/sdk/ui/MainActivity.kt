package org.hyperledger.indy.sdk.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.hyperledger.indy.sdk.R
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
            startActivity(Intent(this, AnoncredsActivity::class.java))
        }

        binding.btnAnoncredsRevocation.setOnClickListener {
            startActivity(Intent(this, AnoncredsRevocationActivity::class.java))
        }

        binding.btnCrypto.setOnClickListener {
            startActivity(Intent(this, CryptoActivity::class.java))
        }

        binding.btnEndorser.setOnClickListener {
            startActivity(Intent(this, EndorserActivity::class.java))
        }

        binding.btnLedger.setOnClickListener {
            startActivity(Intent(this, LedgerActivity::class.java))
        }
    }
}
