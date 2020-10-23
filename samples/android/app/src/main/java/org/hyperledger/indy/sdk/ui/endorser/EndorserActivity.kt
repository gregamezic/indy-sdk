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
import org.hyperledger.indy.sdk.helpers.MessageHelper
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
        setContentView(R.layout.activity_endorser)

        startDemo()
    }


    private fun updateUI(text: String) {
        tvEndorserLogs.text = "${tvEndorserLogs.text}$text"
    }

    private fun updateHeader(text: String) {
        pbEndorser.visibility = View.VISIBLE
        tvEndorserStart.text = text
    }

    private fun updateFooter(text: String) {
        pbEndorser.visibility = View.GONE
        tvEndorserStart.text = text
    }


    /**
     * startDemo function start all functions fro Anoncreds chronological in coroutine default thread
     */
    private fun startDemo() {
        // Start
        MainScope().launch {
            Log.d(TAG, "startDemo: Endorser sample -> STARTED!")

            updateHeader(getString(R.string.endorser_sample_start))


            updateUI(getString(R.string.endorser_create_pool))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    createOpenPool()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_create_pool_end))


            updateUI(getString(R.string.endorser_create_open_author_wallet))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    createOpenAuthorWallet()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_create_open_author_wallet_end))


            updateUI(getString(R.string.endorser_create_open_endorser_wallet))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    createOpenEndorserWallet()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_create_open_endorser_wallet_end))


            updateUI(getString(R.string.endorser_create_open_trustee_wallet))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    createOpenTrusteeWallet()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_create_open_trustee_wallet_end))


            updateUI(getString(R.string.endorser_create_trustee_did))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    createTrusteeDID()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_create_trustee_did_end))


            updateUI(getString(R.string.endorser_create_author_did))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    createAuthorDID()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_create_author_did_end))


            updateUI(getString(R.string.endorser_create_endorser_did))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    createEndorserDID()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_create_endorser_did_end))


            updateUI(getString(R.string.endorser_build_author_nym_request))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    buildAuthorNymRequest()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_build_author_nym_request_end))


            updateUI(getString(R.string.endorser_trustee_sign_author_nym_request))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    trusteeSignAuthorNymRequest()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_trustee_sign_author_nym_request_end))


            updateUI(getString(R.string.endorser_build_endorser_nym_request))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    buildEndorserNymRequest()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_build_endorser_nym_request_end))


            updateUI(getString(R.string.endorser_trustee_sign_endorser_nym))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    trusteeSingEndorserNymRequest()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_trustee_sign_endorser_nym_end))


            updateUI(getString(R.string.endorser_create_schema_endorser))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    createSchemaWithEndorser()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_create_schema_endorser_end))


            updateUI(getString(R.string.endorser_transaction_author_builds_schema_request))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    transactionAuthorBuildsSchemaRequest()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_transaction_author_builds_schema_request_end))


            updateUI(getString(R.string.endorser_transaction_author_append_DID_request))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    transactionAuthorSignsRequestDID()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_transaction_author_append_DID_request_end))


            updateUI(getString(R.string.endorser_transaction_author_sign_with_endorser))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    transactionAuthorSignEndorser()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_transaction_author_sign_with_endorser_end))


            updateUI(getString(R.string.endorser_transaction_endorser_sign_request))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    transactionEndorserSignRequest()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_transaction_endorser_sign_request_end))


            updateUI(getString(R.string.endorser_transaction_endorser_send_request))
            try {
                withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                    transactionEndorserSendRequest()
                }
            } catch (e: Exception) {
                MessageHelper.errorToast(this@EndorserActivity, getString(R.string.error))
                pbEndorser.visibility = View.GONE
                return@launch
            }
            updateUI(getString(R.string.endorser_transaction_endorser_send_request_end))


            MessageHelper.successToast(this@EndorserActivity, getString(R.string.success))
            updateFooter(getString(R.string.endorser_sample_completed))

            Log.d(TAG, "startDemo: Endorser sample -> COMPLETED!")
        }
    }


    private suspend fun createOpenPool() {
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create and Open Pool
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private suspend fun createOpenAuthorWallet() {
        // 2. Create and Open Author Wallet
        authorWalletConfig = JSONObject().put("id", "authorWallet").toString()
        authorWalletCredentials = JSONObject().put("key", "author_wallet_key").toString()
        Wallet.createWallet(authorWalletConfig, authorWalletCredentials).get()
        authorWallet = Wallet.openWallet(authorWalletConfig, authorWalletCredentials).get()
    }

    private suspend fun createOpenEndorserWallet() {
        // 3. Create and Open Endorser Wallet
        endorserWalletConfig = JSONObject().put("id", "endorserWallet").toString()
        endorserWalletCredentials = JSONObject().put("key", "endorser_wallet_key").toString()
        Wallet.createWallet(endorserWalletConfig, endorserWalletCredentials).get()
        endorserWallet =
                Wallet.openWallet(endorserWalletConfig, endorserWalletCredentials).get()
    }

    private suspend fun createOpenTrusteeWallet() {
        // 4. Create and Open Trustee Wallet
        trusteeWalletConfig = JSONObject().put("id", "trusteeWallet").toString()
        trusteeWalletCredentials = JSONObject().put("key", "trustee_wallet_key").toString()
        Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
        trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
    }

    private suspend fun createTrusteeDID() {
        // 5. Create Trustee DID
        val theirDidJson =
                DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null)
        val createTheirDidResult =
                Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get()
        trusteeDid = createTheirDidResult.did
    }

    private suspend fun createAuthorDID() {
        // 6. Create Author DID
        createMyDidResult = Did.createAndStoreMyDid(authorWallet, "{}").get()
        authorDid = createMyDidResult.did
        authorVerkey = createMyDidResult.verkey
    }

    private suspend fun createEndorserDID() {
        // 7. Create Endorser DID
        createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get()
        endorserDid = createMyDidResult.did
        endorserVerkey = createMyDidResult.verkey
    }

    private suspend fun buildAuthorNymRequest() {
        // 8. Build Author Nym Request
        nymRequest =
                Ledger.buildNymRequest(trusteeDid, authorDid, authorVerkey, null, null).get()
    }

    private suspend fun trusteeSignAuthorNymRequest() {
        // 9. Trustee Sign Author Nym Request
        Ledger.signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get()
    }

    private suspend fun buildEndorserNymRequest() {
        // 10. Build Endorser Nym Request
        nymRequest =
                Ledger.buildNymRequest(trusteeDid, endorserDid, endorserVerkey, null, "ENDORSER").get()
    }

    private suspend fun trusteeSingEndorserNymRequest() {
        // 11. Trustee Sign Endorser Nym Request
        Ledger.signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get()
    }

    private suspend fun createSchemaWithEndorser() {
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

    private suspend fun transactionAuthorBuildsSchemaRequest() {
        // 13. Transaction Author builds Schema Request
        schemaRequest = Ledger.buildSchemaRequest(authorDid, schemaJson).get()
    }

    private suspend fun transactionAuthorSignsRequestDID() {
        // 14. Transaction Author appends Endorser's DID into the request
        schemaRequestWithEndorser =
                Ledger.appendRequestEndorser(schemaRequest, endorserDid).get()
    }

    private suspend fun transactionAuthorSignEndorser() {
        // 15. Transaction Author signs the request with the added endorser field
        schemaRequestWithEndorserAuthorSigned =
                Ledger.multiSignRequest(authorWallet, authorDid, schemaRequestWithEndorser).get()
    }

    private suspend fun transactionEndorserSignRequest() {
        // 16. Transaction Endorser signs the request
        schemaRequestWithEndorserSigned = Ledger.multiSignRequest(
                endorserWallet,
                endorserDid,
                schemaRequestWithEndorserAuthorSigned
        ).get()
    }

    private suspend fun transactionEndorserSendRequest() {
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