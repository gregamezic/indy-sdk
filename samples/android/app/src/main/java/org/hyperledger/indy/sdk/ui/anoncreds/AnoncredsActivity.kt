package org.hyperledger.indy.sdk.ui.anoncreds

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.listeners.ActionFailListener
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.ui.BaseActivity
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert

class AnoncredsActivity : BaseActivity(), ActionFailListener {

    // my vars
    private val issuerDid = "NcYxiDXkpYi6ov5FcYDi1e"
    private val proverDid = "VsKV7grR1BUE29mG2Fm2kX"
    private lateinit var issuerWallet: Wallet
    private lateinit var schemaJson: String
    private lateinit var proverWallet: Wallet
    private lateinit var credDefId: String
    private lateinit var credOffer: String
    private lateinit var credDefJson: String
    private lateinit var masterSecretId: String
    private lateinit var credReqJson: String
    private lateinit var credReqMetadataJson: String
    private lateinit var credential: String
    private lateinit var credentialIdForAttribute1: String
    private lateinit var credentialIdForAttribute2: String
    private lateinit var credentialIdForPredicate: String
    private lateinit var schemaId: String
    private lateinit var proofRequestJson: String
    private lateinit var proof: JSONObject
    private lateinit var selfAttestedValue: String
    private lateinit var proofJson: String
    private lateinit var schemas: String
    private lateinit var credentialDefs: String
    private lateinit var issuerWalletConfig: String
    private lateinit var issuerWalletCredentials: String
    private lateinit var proverWalletConfig: String
    private lateinit var proverWalletCredentials: String
    private lateinit var pool: Pool
    private lateinit var poolName: String

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init on fail listener for demo job
        onFailListener = this

        // start demo
        startDemo()
    }


    /**
     * startDemo function start all functions for Anoncreds demo chronological in coroutine default thread
     */
    private fun startDemo() {
        Log.d(TAG, "startDemo: Anoncreds sample -> START!")
        updateHeader(getString(R.string.anoncreds_sample_start))

        // Start
        job = MainScope().launch {


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.create_pool),
                { createPool() },
                getString(R.string.create_pool_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_create_open_wallet),
                { issuerCreate() },
                getString(R.string.anoncreds_create_open_wallet_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_prover_create_open_wallet),
                { proverCreate() },
                getString(R.string.anoncreds_prover_create_open_wallet_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_issuer_create_credential_schema),
                { issuerCreateCredentialSchema() },
                getString(R.string.anoncreds_issuer_create_credential_schema_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_issuer_create_credential_definition),
                { issuerCreateCredentialDefinition() },
                getString(R.string.anoncreds_issuer_create_credential_definition_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_prover_master_secret),
                { proverCreateMasterSecret() },
                getString(R.string.anoncreds_prover_master_secret_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_issuer_create_credential_offer),
                { issuerCreateCredentialOffer() },
                getString(R.string.anoncreds_issuer_create_credential_offer_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_prover_create_credential_request),
                { proverCreateCredentialRequest() },
                getString(R.string.anoncreds_prover_create_credential_request_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_issuer_create_credential),
                { issuerCreateCredential() },
                getString(R.string.anoncreds_issuer_create_credential_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_prover_stores_credential),
                { proverStoresCredential() },
                getString(R.string.anoncreds_prover_stores_credential_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_prover_get_credential_proof_request),
                { proverCredentialsForProofRequest() },
                getString(R.string.anoncreds_prover_get_credential_proof_request_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_prover_creates_proof),
                { proverCreateProof() },
                getString(R.string.anoncreds_prover_creates_proof_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_verifier_verify_proof),
                { verifierVerifyProof() },
                getString(R.string.anoncreds_verifier_verify_proof_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_close_delete_issuer_wallet),
                { closeDeleteIssuerWallet() },
                getString(R.string.anoncreds_close_delete_issuer_wallet_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.anoncreds_close_delete_prover_wallet),
                { closeDeleteProverWallet() },
                getString(R.string.anoncreds_close_delete_prover_wallet_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.close_pool),
                { closePool() },
                getString(R.string.close_pool_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.delete_pool_ledger_config),
                { deletePoolLedgerConfig() },
                getString(R.string.delete_pool_ledger_config_end)
            )


            if (job.isCancelled) return@launch
            // show success logs
            updateFooter(getString(R.string.anoncreds_sample_completed))
            successToast(getString(R.string.success))
            Log.d(TAG, "startDemo: Anoncreds sample -> COMPLETED!")
        }
    }


    // region demo steps functions
    private fun createPool() {

        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()

        //1. Create and Open Pool
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private fun issuerCreate() {

        //2. Issuer Create and Open Wallet
        issuerWalletConfig = JSONObject().put("id", "issuerWallet").toString()
        issuerWalletCredentials = JSONObject().put("key", "issuer_wallet_key").toString()
        Wallet.createWallet(issuerWalletConfig, issuerWalletCredentials).get()
        issuerWallet = Wallet.openWallet(issuerWalletConfig, issuerWalletCredentials).get()
    }

    private fun proverCreate() {

        //3. Prover Create and Open Wallet
        proverWalletConfig = JSONObject().put("id", "trusteeWallet").toString()
        proverWalletCredentials = JSONObject().put("key", "prover_wallet_key").toString()
        Wallet.createWallet(proverWalletConfig, proverWalletCredentials).get()
        proverWallet = Wallet.openWallet(proverWalletConfig, proverWalletCredentials).get()
    }

    private fun issuerCreateCredentialSchema() {

        //4. Issuer Creates Credential Schema
        val schemaName = "gvt"
        val schemaVersion = "1.0"
        val schemaAttributes =
            JSONArray().put("name").put("age").put("sex").put("height").toString()
        val createSchemaResult = org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateSchema(
            issuerDid,
            schemaName,
            schemaVersion,
            schemaAttributes
        ).get()
        schemaId = createSchemaResult.schemaId
        schemaJson = createSchemaResult.schemaJson
    }

    private fun issuerCreateCredentialDefinition() {

        //5. Issuer create Credential Definition
        val credDefTag = "Tag1"
        val credDefConfigJson = JSONObject().put("support_revocation", false).toString()
        val createCredDefResult =
            org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateAndStoreCredentialDef(
                issuerWallet,
                issuerDid,
                schemaJson,
                credDefTag,
                null,
                credDefConfigJson
            ).get()
        credDefId = createCredDefResult.credDefId
        credDefJson = createCredDefResult.credDefJson
    }

    private fun proverCreateMasterSecret() {

        //6. Prover create Master Secret
        masterSecretId = org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverCreateMasterSecret(
            proverWallet,
            null
        ).get()
    }

    private fun issuerCreateCredentialOffer() {

        //7. Issuer Creates Credential Offer
        credOffer = org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateCredentialOffer(
            issuerWallet,
            credDefId
        ).get()
    }

    private fun proverCreateCredentialRequest() {

        //8. Prover Creates Credential Request
        val createCredReqResult =
            org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverCreateCredentialReq(
                proverWallet,
                proverDid,
                credOffer,
                credDefJson,
                masterSecretId
            ).get()
        credReqJson = createCredReqResult.credentialRequestJson
        credReqMetadataJson = createCredReqResult.credentialRequestMetadataJson
    }

    private fun issuerCreateCredential() {

        //9. Issuer create Credential
        //   note that encoding is not standardized by Indy except that 32-bit integers are encoded as themselves. IS-786
        val credValuesJson = JSONObject()
            .put(
                "sex",
                JSONObject().put("raw", "male").put(
                    "encoded",
                    "594465709955896723921094925839488742869205008160769251991705001"
                )
            )
            .put(
                "name",
                JSONObject().put("raw", "Alex")
                    .put("encoded", "1139481716457488690172217916278103335")
            )
            .put("height", JSONObject().put("raw", "175").put("encoded", "175"))
            .put("age", JSONObject().put("raw", "28").put("encoded", "28"))
            .toString()

        val createCredentialResult =
            org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateCredential(
                issuerWallet,
                credOffer,
                credReqJson,
                credValuesJson,
                null,
                -1
            ).get()
        credential = createCredentialResult.credentialJson
    }

    private fun proverStoresCredential() {

        //10. Prover Stores Credential
        org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverStoreCredential(
            proverWallet,
            null,
            credReqMetadataJson,
            credential,
            credDefJson,
            null
        ).get()
    }

    private fun proverCredentialsForProofRequest() {

        //11. Prover Gets Credentials for Proof Request
        val nonce = org.hyperledger.indy.sdk.anoncreds.Anoncreds.generateNonce().get()
        proofRequestJson = JSONObject()
            .put("nonce", nonce)
            .put("name", "proof_req_1")
            .put("version", "0.1")
            .put(
                "requested_attributes", JSONObject()
                    .put("attr1_referent", JSONObject().put("name", "name"))
                    .put("attr2_referent", JSONObject().put("name", "sex"))
                    .put("attr3_referent", JSONObject().put("name", "phone"))
            )
            .put(
                "requested_predicates", JSONObject()
                    .put(
                        "predicate1_referent", JSONObject()
                            .put("name", "age")
                            .put("p_type", ">=")
                            .put("p_value", 18)
                    )
            )
            .toString()

        val credentialsSearch =
            CredentialsSearchForProofReq.open(proverWallet, proofRequestJson, null).get()

        val credentialsForAttribute1 =
            JSONArray(credentialsSearch.fetchNextCredentials("attr1_referent", 100).get())
        credentialIdForAttribute1 =
            credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info")
                .getString("referent")

        val credentialsForAttribute2 =
            JSONArray(credentialsSearch.fetchNextCredentials("attr2_referent", 100).get())
        credentialIdForAttribute2 =
            credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info")
                .getString("referent")

        val credentialsForAttribute3 =
            JSONArray(credentialsSearch.fetchNextCredentials("attr3_referent", 100).get())
        Assert.assertEquals(0, credentialsForAttribute3.length().toLong())

        val credentialsForPredicate =
            JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", 100).get())
        credentialIdForPredicate =
            credentialsForPredicate.getJSONObject(0).getJSONObject("cred_info")
                .getString("referent")
        credentialsSearch.close()
    }

    private fun proverCreateProof() {

        //12. Prover Creates Proof
        selfAttestedValue = "8-800-300"
        val requestedCredentialsJson = JSONObject()
            .put("self_attested_attributes", JSONObject().put("attr3_referent", selfAttestedValue))
            .put(
                "requested_attributes", JSONObject()
                    .put(
                        "attr1_referent", JSONObject()
                            .put("cred_id", credentialIdForAttribute1)
                            .put("revealed", true)
                    )
                    .put(
                        "attr2_referent", JSONObject()
                            .put("cred_id", credentialIdForAttribute2)
                            .put("revealed", false)
                    )
            )
            .put(
                "requested_predicates", JSONObject()
                    .put(
                        "predicate1_referent", JSONObject()
                            .put("cred_id", credentialIdForPredicate)
                    )
            )
            .toString()

        schemas = JSONObject().put(schemaId, JSONObject(schemaJson)).toString()
        credentialDefs = JSONObject().put(credDefId, JSONObject(credDefJson)).toString()
        val revocStates = JSONObject().toString()

        proofJson = ""
        try {
            proofJson = org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverCreateProof(
                proverWallet, proofRequestJson, requestedCredentialsJson,
                masterSecretId, schemas, credentialDefs, revocStates
            ).get()
        } catch (e: Exception) {
            println("")
        }
        proof = JSONObject(proofJson)
    }

    private fun verifierVerifyProof() {

        //13. Verifier verify Proof
        val revealedAttr1 = proof.getJSONObject("requested_proof").getJSONObject("revealed_attrs")
            .getJSONObject("attr1_referent")
        Assert.assertEquals("Alex", revealedAttr1.getString("raw"))

        Assert.assertNotNull(
            proof.getJSONObject("requested_proof").getJSONObject("unrevealed_attrs")
                .getJSONObject("attr2_referent").getInt("sub_proof_index")
        )

        Assert.assertEquals(
            selfAttestedValue,
            proof.getJSONObject("requested_proof").getJSONObject("self_attested_attrs")
                .getString("attr3_referent")
        )

        val revocRegDefs = JSONObject().toString()
        val revocRegs = JSONObject().toString()

        val valid = org.hyperledger.indy.sdk.anoncreds.Anoncreds.verifierVerifyProof(
            proofRequestJson,
            proofJson,
            schemas,
            credentialDefs,
            revocRegDefs,
            revocRegs
        ).get()
        Assert.assertTrue(valid)
    }

    private fun closeDeleteIssuerWallet() {

        //14. Close and Delete issuer wallet
        issuerWallet.closeWallet().get()
        Wallet.deleteWallet(issuerWalletConfig, issuerWalletCredentials).get()
    }

    private fun closeDeleteProverWallet() {

        //15. Close and Delete prover wallet
        proverWallet.closeWallet().get()
        Wallet.deleteWallet(proverWalletConfig, proverWalletCredentials).get()
    }

    private fun closePool() {

        //16. Close pool
        pool.closePoolLedger().get()
    }

    private fun deletePoolLedgerConfig() {

        //17. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()
    }
    // endregion  function


    override fun onFail() {
        job.cancel()
    }

    companion object {
        private val TAG = AnoncredsActivity::class.java.name
    }
}
