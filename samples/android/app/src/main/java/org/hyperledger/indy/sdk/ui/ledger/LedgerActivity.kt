package org.hyperledger.indy.sdk.ui.ledger

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_anoncreds.*
import kotlinx.android.synthetic.main.activity_endorser.*
import kotlinx.android.synthetic.main.activity_ledger.*
import kotlinx.coroutines.*
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidJSONParameters
import org.hyperledger.indy.sdk.helpers.DemoActionHelper
import org.hyperledger.indy.sdk.helpers.MessageHelper
import org.hyperledger.indy.sdk.helpers.MessageHelper.Companion.updateFooter
import org.hyperledger.indy.sdk.helpers.MessageHelper.Companion.updateHeader
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
        setContentView(R.layout.demos_activity)

        // start Ledger demo
        startDemo()
    }



    /**
     * startDemo function start all functions for Ledger demo chronological in coroutine default thread
     */
    private fun startDemo() {

        MainScope().launch {

            var result = false


            Log.d(TAG, "startDemo: Ledger sample -> STARTED!")
            updateHeader(this@LedgerActivity, getString(R.string.ledger_sample_start))


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_create_ledger)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createLedger() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_create_ledger_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_create_open_my_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createOpenMyWallet() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_create_open_my_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_create_open_trustee_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createOpenTrusteeWallet() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_create_open_trustee_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_create_my_did)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createMyDID() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_create_my_did_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_create_did_from_trustee)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createDIDFromTrustee1Seed() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_create_did_from_trustee_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_build_nym_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { buildNymRequest() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_build_nym_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_trustee_sign_nym_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { trusteeSignNymRequest() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_trustee_sign_nym_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_close_delete_my_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { closeDeleteMyWallet() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_close_delete_my_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_close_pool)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { closePool() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_close_pool_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_close_delete_their_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { closeDeleteTheirWallet() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_close_delete_their_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@LedgerActivity,
                getString(R.string.ledger_delete_pool_ledger_config)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { deletePoolLedgerConfig() }
            }
            MessageHelper.updateUI(
                this@LedgerActivity,
                result,
                getString(R.string.ledger_delete_pool_ledger_config_end)
            )
            if (!result) return@launch



            MessageHelper.successToast(this@LedgerActivity, getString(R.string.success))
            updateFooter(this@LedgerActivity, getString(R.string.ledger_sample_completed))

            Log.d(TAG, "startDemo: Ledger sample -> COMPLETED!")
        }
    }


    private fun createLedger() {
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create ledger config from genesis txn file
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private fun createOpenMyWallet() {
        // 2. Create and Open My Wallet
        myWalletConfig = JSONObject().put("id", "myWallet").toString()
        myWalletCredentials = JSONObject().put("key", "my_wallet_key").toString()
        Wallet.createWallet(myWalletConfig, myWalletCredentials).get()
        myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
    }

    private fun createOpenTrusteeWallet() {
        // 3. Create and Open Trustee Wallet
        trusteeWalletConfig = JSONObject().put("id", "theirWallet").toString()
        trusteeWalletCredentials = JSONObject().put("key", "trustee_wallet_key").toString()
        Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
        trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
    }

    private fun createMyDID() {
        // 4. Create My Did
        val createMyDidResult = Did.createAndStoreMyDid(myWallet, "{}").get()
        myDid = createMyDidResult.did
        myVerkey = createMyDidResult.verkey
    }

    private fun createDIDFromTrustee1Seed() {
        // 5. Create Did from Trustee1 seed
        val theirDidJson =
                DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null)

        val createTheirDidResult =
                Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get()
        trusteeDid = createTheirDidResult.did
    }

    private fun buildNymRequest() {
        // 6. Build Nym Request
        nymRequest = Ledger.buildNymRequest(trusteeDid, myDid, myVerkey, null, null).get()
    }

    private fun trusteeSignNymRequest() {
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

    private fun closeDeleteMyWallet() {
        // 8. Close and delete My Wallet
        myWallet.closeWallet().get()
        Wallet.deleteWallet(myWalletConfig, myWalletCredentials).get()
    }

    private fun closeDeleteTheirWallet() {
        // 9. Close and delete Their Wallet
        trusteeWallet.closeWallet().get()
        Wallet.deleteWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
    }

    private fun closePool() {
        // 10. Close Pool
        pool.closePoolLedger().get()
    }

    private fun deletePoolLedgerConfig() {
        // 11. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()
    }
}
