package org.hyperledger.indy.sdk.ui.ledger

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_anoncreds.*
import kotlinx.android.synthetic.main.activity_ledger.*
import kotlinx.coroutines.*
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidJSONParameters
import org.hyperledger.indy.sdk.helpers.MessageHelper
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.junit.Assert
import java.lang.Exception

class LedgerActivity : AppCompatActivity() {

    private val TAG = LedgerActivity::class.java.name


    // my vars
    private val trusteeSeed = "000000000000000000000000Trustee1"
    private lateinit var myWallet: Wallet
    private lateinit var trusteeWallet: Wallet
    private lateinit var trusteeDid: String
    private lateinit var myDid: String
    private lateinit var myVerkey: String
    private lateinit var pool: Pool
    private lateinit var nymRequest: String
    private lateinit var myWalletConfig: String
    private lateinit var myWalletCredentials: String
    private lateinit var trusteeWalletConfig: String
    private lateinit var trusteeWalletCredentials: String
    private lateinit var poolName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ledger)

        // start Ledger demo
        startDemo()
    }

    private fun updateUI(text: String) {
        tvLedgerLogs.text = "${tvLedgerLogs.text}$text"
    }

    private fun updateHeader(text: String) {
        pbLedger.visibility = View.VISIBLE
        tvLedgerStart.text = text
    }

    private fun updateFooter(text: String) {
        pbLedger.visibility = View.GONE
        tvLedgerEnd.text = text
    }

    /**
     * startDemo function start all functions for Ledger demo chronological in coroutine default thread
     */
    private fun startDemo() {
        MainScope().launch {
            Log.d(TAG, "startDemo: Ledger sample -> STARTED!")

            updateHeader(getString(R.string.ledger_sample_start))


            updateUI(getString(R.string.ledger_create_ledger))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createLedger()
            }
            updateUI(getString(R.string.ledger_create_ledger_end))

            updateUI(getString(R.string.ledger_create_open_my_wallet))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createOpenMyWallet()
            }
            updateUI(getString(R.string.ledger_create_open_my_wallet_end))


            updateUI(getString(R.string.ledger_create_open_trustee_wallet))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createOpenTrusteeWallet()
            }
            updateUI(getString(R.string.ledger_create_open_trustee_wallet_end))


            updateUI(getString(R.string.ledger_create_my_did))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createMyDID()
            }
            updateUI(getString(R.string.ledger_create_my_did_end))


            updateUI(getString(R.string.ledger_create_did_from_trustee))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createDIDFromTrustee1Seed()
            }
            updateUI(getString(R.string.ledger_create_did_from_trustee_end))


            updateUI(getString(R.string.ledger_build_nym_request))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                buildNymRequest()
            }
            updateUI(getString(R.string.ledger_build_nym_request_end))


            updateUI(getString(R.string.ledger_trustee_sign_nym_request))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                trusteeSignNymRequest()
            }
            updateUI(getString(R.string.ledger_trustee_sign_nym_request_end))


            updateUI(getString(R.string.ledger_close_delete_my_wallet))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closeDeleteMyWallet()
            }
            updateUI(getString(R.string.ledger_close_delete_my_wallet_end))


            updateUI(getString(R.string.ledger_close_pool))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closePool()
            }
            updateUI(getString(R.string.ledger_close_pool_end))


            updateUI(getString(R.string.ledger_close_delete_their_wallet))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closeDeleteTheirWallet()
            }
            updateUI(getString(R.string.ledger_close_delete_their_wallet_end))


            updateUI(getString(R.string.ledger_delete_pool_ledger_config))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                deletePoolLedgerConfig()
            }
            updateUI(getString(R.string.ledger_delete_pool_ledger_config_end))


            MessageHelper.successToast(this@LedgerActivity, getString(R.string.success))
            updateFooter(getString(R.string.ledger_sample_completed))

            Log.d(TAG, "startDemo: Ledger sample -> COMPLETED!")
        }
    }


    private suspend fun createLedger() {
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create ledger config from genesis txn file
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private suspend fun createOpenMyWallet() {
        // 2. Create and Open My Wallet
        myWalletConfig = JSONObject().put("id", "myWallet").toString()
        myWalletCredentials = JSONObject().put("key", "my_wallet_key").toString()
        Wallet.createWallet(myWalletConfig, myWalletCredentials).get()
        myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
    }

    private suspend fun createOpenTrusteeWallet() {
        // 3. Create and Open Trustee Wallet
        trusteeWalletConfig = JSONObject().put("id", "theirWallet").toString()
        trusteeWalletCredentials = JSONObject().put("key", "trustee_wallet_key").toString()
        Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
        trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
    }

    private suspend fun createMyDID() {
        // 4. Create My Did
        val createMyDidResult = Did.createAndStoreMyDid(myWallet, "{}").get()
        myDid = createMyDidResult.did
        myVerkey = createMyDidResult.verkey
    }

    private suspend fun createDIDFromTrustee1Seed() {
        // 5. Create Did from Trustee1 seed
        val theirDidJson =
                DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null)

        val createTheirDidResult =
                Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get()
        trusteeDid = createTheirDidResult.did
    }

    private suspend fun buildNymRequest() {
        // 6. Build Nym Request
        nymRequest = Ledger.buildNymRequest(trusteeDid, myDid, myVerkey, null, null).get()
    }

    private suspend fun trusteeSignNymRequest() {
        // 7. Trustee Sign Nym Request
        val nymResponseJson =
                Ledger.signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get()

        val nymResponse = JSONObject(nymResponseJson)

        Assert.assertEquals(
                myDid,
                nymResponse.getJSONObject("result").getJSONObject("txn").getJSONObject("data")
                        .getString("dest")
        )
        Assert.assertEquals(
                myVerkey,
                nymResponse.getJSONObject("result").getJSONObject("txn").getJSONObject("data")
                        .getString("verkey")
        )
    }

    private suspend fun closeDeleteMyWallet() {
        // 8. Close and delete My Wallet
        myWallet.closeWallet().get()
        Wallet.deleteWallet(myWalletConfig, myWalletCredentials).get()
    }

    private suspend fun closeDeleteTheirWallet() {
        // 9. Close and delete Their Wallet
        trusteeWallet.closeWallet().get()
        Wallet.deleteWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
    }

    private suspend fun closePool() {
        // 10. Close Pool
        pool.closePoolLedger().get()
    }

    private suspend fun deletePoolLedgerConfig() {
        // 11. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()
    }
}