package org.hyperledger.indy.sdk.ui.endorser

import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidJSONParameters
import org.hyperledger.indy.sdk.did.DidResults
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.ui.base.BaseActivity
import org.hyperledger.indy.sdk.ui.base.models.WalletData
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert

class EndorserActivity : BaseActivity() {

    // my vars
    private val trusteeSeed = "000000000000000000000000Trustee1"
    private lateinit var trusteeWallet: WalletData
    private lateinit var authorWallet: WalletData
    private lateinit var endorserWallet: WalletData
    private lateinit var createMyDidResult: DidResults.CreateAndStoreMyDidResult
    private lateinit var trusteeDid: String
    private lateinit var authorDid: String
    private lateinit var authorVerkey: String
    private lateinit var nymRequest: String
    private lateinit var endorserDid: String
    private lateinit var endorserVerkey: String
    private lateinit var schemaJson: String
    private lateinit var schemaRequest: String
    private lateinit var schemaRequestWithEndorser: String
    private lateinit var schemaRequestWithEndorserAuthorSigned: String
    private lateinit var schemaRequestWithEndorserSigned: String

    /**
     * startDemo function start all functions fro Anoncreds chronological in coroutine default thread
     */
    override fun onStartDemo() {

        Log.d(TAG, "startDemo: Endorser sample -> STARTED!")
        updateHeader(getString(R.string.endorser_sample_start))

        // Start
        job = MainScope().launch {

            // 1. Create and Open Pool
            ensureActive()
            runAction(
                getString(R.string.create_pool),
                { createAndOpenPool() },
                getString(R.string.create_pool_end)
            )

            // 2. Create and Open Author Wallet
            ensureActive()
            runAction(
                getString(R.string.endorser_create_open_author_wallet),
                { createOpenAuthorWallet() },
                getString(R.string.endorser_create_open_author_wallet_end)
            )

            // 3. Create and Open Endorser Wallet
            ensureActive()
            runAction(
                getString(R.string.endorser_create_open_endorser_wallet),
                { createOpenEndorserWallet() },
                getString(R.string.endorser_create_open_endorser_wallet_end)
            )

            // 4. Create and Open Trustee Wallet
            ensureActive()
            runAction(
                getString(R.string.endorser_create_open_trustee_wallet),
                { createOpenTrusteeWallet() },
                getString(R.string.endorser_create_open_trustee_wallet_end)
            )

            // 5. Create Trustee DID
            ensureActive()
            runAction(
                getString(R.string.endorser_create_trustee_did),
                { createTrusteeDID() },
                getString(R.string.endorser_create_trustee_did_end)
            )

            // 6. Create Author DID
            ensureActive()
            runAction(
                getString(R.string.endorser_create_author_did),
                { createAuthorDID() },
                getString(R.string.endorser_create_author_did_end)
            )

            // 7. Create Endorser DID
            ensureActive()
            runAction(
                getString(R.string.endorser_create_endorser_did),
                { createEndorserDID() },
                getString(R.string.endorser_create_endorser_did_end)
            )

            // 8. Build Author Nym Request
            ensureActive()
            runAction(
                getString(R.string.endorser_build_author_nym_request),
                { buildAuthorNymRequest() },
                getString(R.string.endorser_build_author_nym_request_end)
            )

            // 9. Trustee Sign Author Nym Request
            ensureActive()
            runAction(
                getString(R.string.endorser_trustee_sign_author_nym_request),
                { trusteeSignAuthorNymRequest() },
                getString(R.string.endorser_trustee_sign_author_nym_request_end)
            )

            // 10. Build Endorser Nym Request
            ensureActive()
            runAction(
                getString(R.string.endorser_build_endorser_nym_request),
                { buildEndorserNymRequest() },
                getString(R.string.endorser_build_endorser_nym_request_end)
            )

            // 11. Trustee Sign Endorser Nym Request
            ensureActive()
            runAction(
                getString(R.string.endorser_trustee_sign_endorser_nym),
                { trusteeSingEndorserNymRequest() },
                getString(R.string.endorser_trustee_sign_endorser_nym_end)
            )

            // 12. Create schema with endorser
            ensureActive()
            runAction(
                getString(R.string.endorser_create_schema_endorser),
                { createSchemaWithEndorser() },
                getString(R.string.endorser_create_schema_endorser_end)
            )

            // 13. Transaction Author builds Schema Request
            ensureActive()
            runAction(
                getString(R.string.endorser_transaction_author_builds_schema_request),
                { transactionAuthorBuildsSchemaRequest() },
                getString(R.string.endorser_transaction_author_builds_schema_request_end)
            )

            // 14. Transaction Author appends Endorser's DID into the request
            ensureActive()
            runAction(
                getString(R.string.endorser_transaction_author_append_DID_request),
                { transactionAuthorSignsRequestDID() },
                getString(R.string.endorser_transaction_author_append_DID_request_end)
            )

            // 15. Transaction Author signs the request with the added endorser field
            ensureActive()
            runAction(
                getString(R.string.endorser_transaction_author_sign_with_endorser),
                { transactionAuthorSignEndorser() },
                getString(R.string.endorser_transaction_author_sign_with_endorser_end)
            )

            // 16. Transaction Endorser signs the request
            ensureActive()
            runAction(
                getString(R.string.endorser_transaction_endorser_sign_request),
                { transactionEndorserSignRequest() },
                getString(R.string.endorser_transaction_endorser_sign_request_end)
            )

            // 17. Transaction Endorser sends the request
            ensureActive()
            runAction(
                getString(R.string.endorser_transaction_endorser_send_request),
                { transactionEndorserSendRequest() },
                getString(R.string.endorser_transaction_endorser_send_request_end)
            )

            // 18. Close author Wallet
            ensureActive()
            runAction(
                getString(R.string.endorser_close_delete_author_wallet),
                { closeAuthorWallet() },
                getString(R.string.endorser_close_delete_author_wallet_end)
            )

            // 19. Close endorser Wallet
            ensureActive()
            runAction(
                getString(R.string.endorser_close_delete_endorser_wallet),
                { closeEndorserWallet() },
                getString(R.string.endorser_close_delete_endorser_wallet_end)
            )

            // 20. Close trustee Wallet
            ensureActive()
            runAction(
                getString(R.string.endorser_close_delete_trustee_wallet),
                { closeTrusteeWallet() },
                getString(R.string.endorser_close_delete_trustee_wallet_end)
            )

            // 21. Close Pool
            ensureActive()
            runAction(
                getString(R.string.close_pool),
                { closePool() },
                getString(R.string.close_pool_end)
            )

            // 22. Delete Pool ledger configuration
            ensureActive()
            runAction(
                getString(R.string.delete_pool_ledger_config),
                { deletePoolLedgerConfig() },
                getString(R.string.delete_pool_ledger_config_end)
            )

            ensureActive()
            successToast(getString(R.string.success))
            updateFooter(getString(R.string.endorser_sample_completed))
            Log.d(TAG, "startDemo: Endorser sample -> COMPLETED!")
        }
    }

    // region demo steps functions
    private fun createOpenAuthorWallet() {
        authorWallet = createAndOpenWallet("authorWallet", "author_wallet_key")
    }

    private fun createOpenEndorserWallet() {
        createAndOpenWallet("endorserWallet", "endorser_wallet_key")
    }

    private fun createOpenTrusteeWallet() {
        createAndOpenWallet("trusteeWallet", "trustee_wallet_key")
    }

    private fun createTrusteeDID() {
        val theirDidJson =
            DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null)
        val createTheirDidResult =
            Did.createAndStoreMyDid(trusteeWallet.wallet, theirDidJson.toJson()).get()
        trusteeDid = createTheirDidResult.did
    }

    private fun createAuthorDID() {
        createMyDidResult = Did.createAndStoreMyDid(authorWallet.wallet, "{}").get()
        authorDid = createMyDidResult.did
        authorVerkey = createMyDidResult.verkey
    }

    private fun createEndorserDID() {
        createMyDidResult = Did.createAndStoreMyDid(endorserWallet.wallet, "{}").get()
        endorserDid = createMyDidResult.did
        endorserVerkey = createMyDidResult.verkey
    }

    private fun buildAuthorNymRequest() {
        nymRequest =
            Ledger.buildNymRequest(trusteeDid, authorDid, authorVerkey, null, null).get()
    }

    private fun trusteeSignAuthorNymRequest() {
        Ledger.signAndSubmitRequest(pool, trusteeWallet.wallet, trusteeDid, nymRequest).get()
    }

    private fun buildEndorserNymRequest() {
        nymRequest =
            Ledger.buildNymRequest(trusteeDid, endorserDid, endorserVerkey, null, "ENDORSER").get()
    }

    private fun trusteeSingEndorserNymRequest() {
        Ledger.signAndSubmitRequest(pool, trusteeWallet.wallet, trusteeDid, nymRequest).get()
    }

    private fun createSchemaWithEndorser() {
        val schemaName = "gvt"
        val schemaVersion = "1.0"
        val schemaAttributes =
            JSONArray().put("name").put("age").put("sex").put("height").toString()
        val createSchemaResult =
            Anoncreds.issuerCreateSchema(authorDid, schemaName, schemaVersion, schemaAttributes)
                .get()
        createSchemaResult.schemaId
        schemaJson = createSchemaResult.schemaJson
    }

    private fun transactionAuthorBuildsSchemaRequest() {
        schemaRequest = Ledger.buildSchemaRequest(authorDid, schemaJson).get()
    }

    private fun transactionAuthorSignsRequestDID() {
        schemaRequestWithEndorser =
            Ledger.appendRequestEndorser(schemaRequest, endorserDid).get()
    }

    private fun transactionAuthorSignEndorser() {
        schemaRequestWithEndorserAuthorSigned =
            Ledger.multiSignRequest(authorWallet.wallet, authorDid, schemaRequestWithEndorser).get()
    }

    private fun transactionEndorserSignRequest() {
        schemaRequestWithEndorserSigned = Ledger.multiSignRequest(
            endorserWallet.wallet,
            endorserDid,
            schemaRequestWithEndorserAuthorSigned
        ).get()
    }

    private fun transactionEndorserSendRequest() {
        val response = Ledger.submitRequest(pool, schemaRequestWithEndorserSigned).get()
        val responseJson = JSONObject(response)
        Assert.assertEquals("REPLY", responseJson.getString("op"))
    }

    private fun closeAuthorWallet() {
        closeAndDeleteWallet(
            authorWallet.wallet,
            authorWallet.walletConfig,
            authorWallet.walletCredentials
        )
    }

    private fun closeEndorserWallet() {
        closeAndDeleteWallet(
            endorserWallet.wallet,
            endorserWallet.walletConfig,
            endorserWallet.walletCredentials
        )
    }

    private fun closeTrusteeWallet() {
        closeAndDeleteWallet(
            trusteeWallet.wallet,
            trusteeWallet.walletConfig,
            trusteeWallet.walletCredentials
        )
    }
    // endregion

    private companion object {
        val TAG: String = EndorserActivity::class.java.name
    }
}
