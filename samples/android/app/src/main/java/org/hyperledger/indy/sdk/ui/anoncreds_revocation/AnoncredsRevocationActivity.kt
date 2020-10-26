package org.hyperledger.indy.sdk.ui.anoncreds_revocation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.blob_storage.BlobStorageReader
import org.hyperledger.indy.sdk.blob_storage.BlobStorageWriter
import org.hyperledger.indy.sdk.helpers.DemoActionHelper
import org.hyperledger.indy.sdk.helpers.MessageHelper
import org.hyperledger.indy.sdk.helpers.MessageHelper.Companion.updateFooter
import org.hyperledger.indy.sdk.helpers.MessageHelper.Companion.updateHeader
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.utils.EnvironmentUtils
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert

class AnoncredsRevocationActivity : AppCompatActivity() {

    private val TAG = AnoncredsRevocationActivity::class.java.name


    // my vars
    private val issuerDid = "NcYxiDXkpYi6ov5FcYDi1e"
    private val proverDid = "VsKV7grR1BUE29mG2Fm2kX"

    private lateinit var issuerWallet: Wallet
    private lateinit var proverWallet: Wallet
    private lateinit var schemaJson: String
    private lateinit var credDefId: String
    private lateinit var credOffer: String
    private lateinit var credDefJson: String
    private lateinit var masterSecretId: String
    private lateinit var tailsWriterConfig: String
    private lateinit var credReqJson: String
    private lateinit var revRegId: String
    private var blobStorageReaderHandle = -1
    private lateinit var credReqMetadataJson: String
    private lateinit var credentialJson: String
    private lateinit var revRegDefJson: String
    private lateinit var revRegDeltaJson: String
    private var timestamp: Long = 0
    private lateinit var credRevId: String
    private lateinit var credIdForAttr1: String
    private lateinit var credIdForPred1: String
    private lateinit var schemaId: String
    private lateinit var revStateJson: String
    private lateinit var proofRequestJson: String
    private lateinit var proof: JSONObject
    private lateinit var proofJson: String
    private lateinit var schemas: String
    private lateinit var credentialDefs: String
    private lateinit var issuerWalletConfig: String
    private lateinit var issuerWalletCredentials: String
    private lateinit var proverWalletConfig: String
    private lateinit var proverWalletCredentials: String
    private lateinit var pool: Pool
    private lateinit var poolName: String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demos_activity)

        startDemo()
    }



    /**
     * startDemo function start all functions for Anoncreds Revocation demo chronological in coroutine default thread
     */
    private fun startDemo() {

        MainScope().launch {

            var result = false

            Log.d(TAG, "startDemo: Anoncreds Revocation sample -> STARTED!")
            updateHeader(this@AnoncredsRevocationActivity, getString(R.string.anoncreds_revocation_sample_start))


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_create_pool)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { createOpenPool() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_create_pool_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_create_open_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { issuerCreateOpenWallet() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_create_open_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_prover_create_open_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { proverCreateOpenWallet() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_prover_create_open_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_issuer_create_credential_schema)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { issuerCreateCredentialSchema() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_issuer_create_credential_schema_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_issuer_create_credential_definition)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { issuerCreateCredentialDefinition() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_issuer_create_credential_definition_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_issuer_create_revocation_registry)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { issuerCreateRevocationRegistry() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_issuer_create_revocation_registry_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_prover_create_master_secret)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { proverCreateMasterSecret() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_prover_create_master_secret_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_issuer_create_credential_offer)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { issuerCreateCredentialOffer() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_issuer_create_credential_offer_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_prover_create_credential_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { proverCreateCredentialRequest() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_prover_create_credential_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_issuer_open_tails_reader)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { issuerOpenTailsReader() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_issuer_open_tails_reader_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_issuer_create_credential)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { issuerCreateCredential() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_issuer_create_credential_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_prover_stores_credential)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { proverStoresCredential() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_prover_stores_credential_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_prover_get_credential_proof_request)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { proverGetCredentialsForProofRequest() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_prover_get_credential_proof_request_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_prover_create_revocation_state)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { proverCreateRevocationState() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_prover_create_revocation_state_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_prover_creates_proof)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { proverCreateProof() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_prover_creates_proof_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_verifier_verify_proof)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { verifierVerifyProof() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_verifier_verify_proof_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_close_delete_issuer_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { closeDeleteIssuerWallet() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_close_delete_issuer_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_close_delete_prover_wallet)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { closeDeleteProverWallet() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_close_delete_prover_wallet_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_close_pool)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { closePool() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_close_pool_end)
            )
            if (!result) return@launch


            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                getString(R.string.anoncreds_revocation_delete_pool_ledger_config)
            )
            result = withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                DemoActionHelper.runDemoStep { deletePoolLedgerConfig() }
            }
            MessageHelper.updateUI(
                this@AnoncredsRevocationActivity,
                result,
                getString(R.string.anoncreds_revocation_delete_pool_ledger_config_end)
            )
            if (!result) return@launch


            MessageHelper.successToast(this@AnoncredsRevocationActivity, getString(R.string.success))
            updateFooter(this@AnoncredsRevocationActivity, getString(R.string.anoncreds_revocation_sample_completed))
            Log.d(TAG, "startDemo: Anoncreds Revocation sample -> COMPLETED!")
        }
    }


    private fun createOpenPool() {

        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        //1. Create and Open Pool
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private fun issuerCreateOpenWallet() {
        //2. Issuer Create and Open Wallet
        issuerWalletConfig = JSONObject().put("id", "issuerWallet").toString()
        issuerWalletCredentials = JSONObject().put("key", "issuer_wallet_key").toString()
        Wallet.createWallet(issuerWalletConfig, issuerWalletCredentials).get()
        issuerWallet = Wallet.openWallet(issuerWalletConfig, issuerWalletCredentials).get()
    }

    private fun proverCreateOpenWallet() {
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
        val createSchemaResult =
                Anoncreds.issuerCreateSchema(issuerDid, schemaName, schemaVersion, schemaAttributes)
                        .get()
        schemaId = createSchemaResult.schemaId
        schemaJson = createSchemaResult.schemaJson
    }

    private fun issuerCreateCredentialDefinition() {
        //5. Issuer create Credential Definition
        val credDefTag = "Tag1"
        val credDefConfigJson = JSONObject().put("support_revocation", true).toString()
        val createCredDefResult = Anoncreds.issuerCreateAndStoreCredentialDef(
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

    private fun issuerCreateRevocationRegistry() {
        //6. Issuer create Revocation Registry
        val revRegDefConfig = JSONObject()
                .put("issuance_type", "ISSUANCE_ON_DEMAND")
                .put("max_cred_num", 5)
                .toString()
        tailsWriterConfig = JSONObject()
                .put(
                        "base_dir", EnvironmentUtils.getIndyHomePath("tails", baseContext).replace(
                        '\\',
                        '/'
                )
                )
                .put("uri_pattern", "")
                .toString()
        val tailsWriter = BlobStorageWriter.openWriter("default", tailsWriterConfig).get()

        val revRegDefTag = "Tag2"
        val createRevRegResult = Anoncreds.issuerCreateAndStoreRevocReg(
                issuerWallet,
                issuerDid,
                null,
                revRegDefTag,
                credDefId,
                revRegDefConfig,
                tailsWriter
        ).get()
        revRegId = createRevRegResult.revRegId
        revRegDefJson = createRevRegResult.revRegDefJson
    }

    private fun proverCreateMasterSecret() {
        //7. Prover create Master Secret
        masterSecretId = Anoncreds.proverCreateMasterSecret(proverWallet, null).get()
    }

    private fun issuerCreateCredentialOffer() {
        //8. Issuer Creates Credential Offer
        credOffer = Anoncreds.issuerCreateCredentialOffer(issuerWallet, credDefId).get()
    }

    private fun proverCreateCredentialRequest() {
        //9. Prover Creates Credential Request
        val createCredReqResult = Anoncreds.proverCreateCredentialReq(
                proverWallet,
                proverDid,
                credOffer,
                credDefJson,
                masterSecretId
        ).get()
        credReqJson = createCredReqResult.credentialRequestJson
        credReqMetadataJson = createCredReqResult.credentialRequestMetadataJson
    }

    private fun issuerOpenTailsReader() {
        //10. Issuer open Tails Reader
        val blobStorageReaderCfg = BlobStorageReader.openReader("default", tailsWriterConfig).get()
        blobStorageReaderHandle = blobStorageReaderCfg.blobStorageReaderHandle
    }

    private fun issuerCreateCredential() {
        //11. Issuer create Credential
        //    note that encoding is not standardized by Indy except that 32-bit integers are encoded as themselves. IS-786
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

        val createCredentialResult = Anoncreds.issuerCreateCredential(
                issuerWallet,
                credOffer,
                credReqJson,
                credValuesJson,
                revRegId,
                blobStorageReaderHandle
        ).get()
        credentialJson = createCredentialResult.credentialJson
        revRegDeltaJson = createCredentialResult.revocRegDeltaJson
        credRevId = createCredentialResult.revocId
    }

    private fun proverStoresCredential() {
        //12. Prover Stores Credential
        Anoncreds.proverStoreCredential(
                proverWallet,
                null,
                credReqMetadataJson,
                credentialJson,
                credDefJson,
                revRegDefJson
        ).get()
    }

    private fun proverGetCredentialsForProofRequest() {
        //13. Prover Gets Credentials for Proof Request
        timestamp = System.currentTimeMillis() / 1000
        val nonce = Anoncreds.generateNonce().get()
        proofRequestJson = JSONObject()
                .put("nonce", nonce)
                .put("name", "proof_req_1")
                .put("version", "0.1")
                .put(
                        "requested_attributes", JSONObject()
                        .put("attr1_referent", JSONObject().put("name", "name"))
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
                .put(
                        "non_revoked", JSONObject()
                        .put("to", timestamp)
                )
                .toString()

        val credentialsSearch =
                CredentialsSearchForProofReq.open(proverWallet, proofRequestJson, null).get()

        val credentialsForAttribute1 =
                JSONArray(credentialsSearch.fetchNextCredentials("attr1_referent", 100).get())
        credIdForAttr1 = credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info")
                .getString("referent")

        val credentialsForAttribute2 =
                JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", 100).get())
        credIdForPred1 = credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info")
                .getString("referent")
        credentialsSearch.close()
    }

    private fun proverCreateRevocationState() {
        //14. Prover create RevocationState
        revStateJson = Anoncreds.createRevocationState(
                blobStorageReaderHandle,
                revRegDefJson,
                revRegDeltaJson,
                timestamp,
                credRevId
        ).get()
    }

    private fun proverCreateProof() {
        //15. Prover Creates Proof
        val requestedCredentialsJson = JSONObject()
                .put("self_attested_attributes", JSONObject())
                .put(
                        "requested_attributes", JSONObject()
                        .put(
                                "attr1_referent", JSONObject()
                                .put("cred_id", credIdForAttr1)
                                .put("revealed", true)
                                .put("timestamp", timestamp)
                        )
                )
                .put(
                        "requested_predicates", JSONObject()
                        .put(
                                "predicate1_referent", JSONObject()
                                .put("cred_id", credIdForPred1)
                                .put("timestamp", timestamp)
                        )
                )
                .toString()

        schemas = JSONObject().put(schemaId, JSONObject(schemaJson)).toString()
        credentialDefs = JSONObject().put(credDefId, JSONObject(credDefJson)).toString()
        val revStates =
                JSONObject().put(revRegId, JSONObject().put("" + timestamp, JSONObject(revStateJson))   )
                        .toString()

        proofJson = Anoncreds.proverCreateProof(
                proverWallet, proofRequestJson, requestedCredentialsJson, masterSecretId, schemas,
                credentialDefs, revStates
        ).get()
        proof = JSONObject(proofJson)
    }

    private fun verifierVerifyProof() {
        //16. Verifier verify Proof
        val revealedAttr1 = proof.getJSONObject("requested_proof").getJSONObject("revealed_attrs")
                .getJSONObject("attr1_referent")
        Assert.assertEquals("Alex", revealedAttr1.getString("raw"))

        val revRegDefs = JSONObject().put(revRegId, JSONObject(revRegDefJson)).toString()
        val revRegs = JSONObject().put(
                revRegId,
                JSONObject().put("" + timestamp, JSONObject(revRegDeltaJson))
        ).toString()

        val valid = Anoncreds.verifierVerifyProof(
                proofRequestJson,
                proofJson,
                schemas,
                credentialDefs,
                revRegDefs,
                revRegs
        ).get()
        Assert.assertTrue(valid)
    }

    private fun closeDeleteIssuerWallet() {
        //17. Close and Delete issuer wallet
        issuerWallet.closeWallet().get()
        Wallet.deleteWallet(issuerWalletConfig, issuerWalletCredentials).get()
    }

    private fun closeDeleteProverWallet() {
        //18. Close and Delete prover wallet
        proverWallet.closeWallet().get()
        Wallet.deleteWallet(proverWalletConfig, proverWalletCredentials).get()
    }

    private fun closePool() {
        //19. Close pool
        pool.closePoolLedger().get()
    }

    private fun deletePoolLedgerConfig() {
        //20. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()
    }
}
