package org.hyperledger.indy.sdk.ui.endorser

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crypto.*
import kotlinx.android.synthetic.main.activity_endorser.*
import kotlinx.coroutines.*
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidJSONParameters
import org.hyperledger.indy.sdk.did.DidResults
import org.hyperledger.indy.sdk.helpers.DemoActionHelper
import org.hyperledger.indy.sdk.helpers.MessageHelper
import org.hyperledger.indy.sdk.helpers.MessageHelper.Companion.updateFooter
import org.hyperledger.indy.sdk.helpers.MessageHelper.Companion.updateHeader
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import java.lang.Exception

class EndorserActivity : AppCompatActivity() {

    private val TAG = EndorserActivity::class.java.name


    // my vars
    private val trusteeSeed = "000000000000000000000000Trustee1"
    private lateinit var trusteeWallet: Wallet
    private lateinit var authorWallet: Wallet
    private lateinit var endorserWallet: Wallet
    private lateinit var createMyDidResult: DidResults.CreateAndStoreMyDidResult
    private lateinit var trusteeDid: String
    private lateinit var authorDid: String
    private lateinit var authorVerkey: String
    private lateinit var pool: Pool
    private lateinit var nymRequest: String
    private lateinit var endorserDid: String
    private lateinit var endorserVerkey: String
    private lateinit var poolName: String
    private lateinit var trusteeWalletConfig: String
    private lateinit var trusteeWalletCredentials: String
    private lateinit var endorserWalletConfig: String
    private lateinit var endorserWalletCredentials: String
    private lateinit var authorWalletConfig: String
    private lateinit var authorWalletCredentials: String
    private lateinit var schemaJson: String
    private lateinit var schemaRequest: String
    private lateinit var schemaRequestWithEndorser: String
    private lateinit var schemaRequestWithEndorserAuthorSigned: String
    private lateinit var schemaRequestWithEndorserSigned: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demos_activity)

        startDemo()
    }



    /**
     * startDemo function start all functions fro Anoncreds chronological in coroutine default thread
     */
    private fun startDemo() {

        var result = false

        // Start
        MainScope().launch {

            Log.d(TAG, "startDemo: Endorser sample -> STARTED!")
            updateHeader(this@EndorserActivity, getString(R.string.endorser_sample_start))


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_create_pool)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createOpenPool() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_create_pool_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_create_open_author_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createOpenAuthorWallet() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_create_open_author_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_create_open_endorser_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createOpenEndorserWallet() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_create_open_endorser_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_create_open_trustee_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createOpenTrusteeWallet() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_create_open_trustee_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_create_trustee_did)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createTrusteeDID() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_create_trustee_did_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_create_author_did)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createAuthorDID() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_create_author_did_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_create_endorser_did)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createEndorserDID() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_create_endorser_did_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_build_author_nym_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { buildAuthorNymRequest() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_build_author_nym_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_trustee_sign_author_nym_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { trusteeSignAuthorNymRequest() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_trustee_sign_author_nym_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_build_endorser_nym_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { buildEndorserNymRequest() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_build_endorser_nym_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_trustee_sign_endorser_nym)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { trusteeSingEndorserNymRequest() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_trustee_sign_endorser_nym_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_create_schema_endorser)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createSchemaWithEndorser() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_create_schema_endorser_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_transaction_author_builds_schema_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { transactionAuthorBuildsSchemaRequest() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_transaction_author_builds_schema_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_transaction_author_append_DID_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { transactionAuthorSignsRequestDID() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_transaction_author_append_DID_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_transaction_author_sign_with_endorser)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { transactionAuthorSignEndorser() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_transaction_author_sign_with_endorser_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_transaction_endorser_sign_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { transactionEndorserSignRequest() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_transaction_endorser_sign_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@EndorserActivity,
                getString(R.string.endorser_transaction_endorser_send_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { transactionEndorserSendRequest() }
            }
            MessageHelper.updateUI(
                this@EndorserActivity,
                result,
                getString(R.string.endorser_transaction_endorser_send_request_end)
            )
            if (!result) return@launch



            MessageHelper.successToast(this@EndorserActivity, getString(R.string.success))
            updateFooter(this@EndorserActivity, getString(R.string.endorser_sample_completed))
            Log.d(TAG, "startDemo: Endorser sample -> COMPLETED!")
        }
    }


    private fun createOpenPool() {
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create and Open Pool
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private fun createOpenAuthorWallet() {
        // 2. Create and Open Author Wallet
        authorWalletConfig = JSONObject().put("id", "authorWallet").toString()
        authorWalletCredentials = JSONObject().put("key", "author_wallet_key").toString()
        Wallet.createWallet(authorWalletConfig, authorWalletCredentials).get()
        authorWallet = Wallet.openWallet(authorWalletConfig, authorWalletCredentials).get()
    }

    private fun createOpenEndorserWallet() {
        // 3. Create and Open Endorser Wallet
        endorserWalletConfig = JSONObject().put("id", "endorserWallet").toString()
        endorserWalletCredentials = JSONObject().put("key", "endorser_wallet_key").toString()
        Wallet.createWallet(endorserWalletConfig, endorserWalletCredentials).get()
        endorserWallet =
                Wallet.openWallet(endorserWalletConfig, endorserWalletCredentials).get()
    }

    private fun createOpenTrusteeWallet() {
        // 4. Create and Open Trustee Wallet
        trusteeWalletConfig = JSONObject().put("id", "trusteeWallet").toString()
        trusteeWalletCredentials = JSONObject().put("key", "trustee_wallet_key").toString()
        Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
        trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
    }

    private fun createTrusteeDID() {
        // 5. Create Trustee DID
        val theirDidJson =
                DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null)
        val createTheirDidResult =
                Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get()
        trusteeDid = createTheirDidResult.did
    }

    private fun createAuthorDID() {
        // 6. Create Author DID
        createMyDidResult = Did.createAndStoreMyDid(authorWallet, "{}").get()
        authorDid = createMyDidResult.did
        authorVerkey = createMyDidResult.verkey
    }

    private fun createEndorserDID() {
        // 7. Create Endorser DID
        createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get()
        endorserDid = createMyDidResult.did
        endorserVerkey = createMyDidResult.verkey
    }

    private fun buildAuthorNymRequest() {
        // 8. Build Author Nym Request
        nymRequest =
                Ledger.buildNymRequest(trusteeDid, authorDid, authorVerkey, null, null).get()
    }

    private fun trusteeSignAuthorNymRequest() {
        // 9. Trustee Sign Author Nym Request
        Ledger.signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get()
    }

    private fun buildEndorserNymRequest() {
        // 10. Build Endorser Nym Request
        nymRequest =
                Ledger.buildNymRequest(trusteeDid, endorserDid, endorserVerkey, null, "ENDORSER").get()
    }

    private fun trusteeSingEndorserNymRequest() {
        // 11. Trustee Sign Endorser Nym Request
        Ledger.signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get()
    }

    private fun createSchemaWithEndorser() {
        // 12. Create schema with endorser
        val schemaName = "gvt"
        val schemaVersion = "1.0"
        val schemaAttributes =
                JSONArray().put("name").put("age").put("sex").put("height").toString()
        val createSchemaResult =
                Anoncreds.issuerCreateSchema(authorDid, schemaName, schemaVersion, schemaAttributes)
                        .get()
        val schemaId = createSchemaResult.schemaId
        schemaJson = createSchemaResult.schemaJson
    }

    private fun transactionAuthorBuildsSchemaRequest() {
        // 13. Transaction Author builds Schema Request
        schemaRequest = Ledger.buildSchemaRequest(authorDid, schemaJson).get()
    }

    private fun transactionAuthorSignsRequestDID() {
        // 14. Transaction Author appends Endorser's DID into the request
        schemaRequestWithEndorser =
                Ledger.appendRequestEndorser(schemaRequest, endorserDid).get()
    }

    private fun transactionAuthorSignEndorser() {
        // 15. Transaction Author signs the request with the added endorser field
        schemaRequestWithEndorserAuthorSigned =
                Ledger.multiSignRequest(authorWallet, authorDid, schemaRequestWithEndorser).get()
    }

    private fun transactionEndorserSignRequest() {
        // 16. Transaction Endorser signs the request
        schemaRequestWithEndorserSigned = Ledger.multiSignRequest(
                endorserWallet,
                endorserDid,
                schemaRequestWithEndorserAuthorSigned
        ).get()
    }

    private fun transactionEndorserSendRequest() {
        // 17. Transaction Endorser sends the request
        val response = Ledger.submitRequest(pool, schemaRequestWithEndorserSigned).get()
        val responseJson = JSONObject(response)
        Assert.assertEquals("REPLY", responseJson.getString("op"))

        pool.closePoolLedger().get()
        Pool.deletePoolLedgerConfig(poolName).get()

        trusteeWallet.closeWallet().get()
        Wallet.deleteWallet(trusteeWalletConfig, trusteeWalletCredentials).get()

        authorWallet.closeWallet().get()
        Wallet.deleteWallet(authorWalletConfig, authorWalletCredentials).get()

        endorserWallet.closeWallet().get()
        Wallet.deleteWallet(endorserWalletConfig, endorserWalletCredentials).get()
    }
}
