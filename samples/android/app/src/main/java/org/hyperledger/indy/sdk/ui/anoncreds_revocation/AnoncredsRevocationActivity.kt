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
        setContentView(R.layout.activity_anoncreds_revocation)

        startDemo()
    }

    /**
     * startDemo function start all functions for Anoncreds Revocation demo chronological in coroutine default thread
     */
    private fun startDemo() {

        MainScope().launch {
            Log.d(TAG, "startDemo: Anoncreds Revocation sample -> STARTED!")

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createOpenPool()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                issuerCreateOpenWallet()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                proverCreateOpenWallet()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                issuerCreateCredentialSchema()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                issuerCreateCredentialDefinition()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                issuerCreateRevocationRegistry()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                proverCreateMasterSecret()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                issuerCreateCredentialOffer()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                proverCreateCredentialRequest()
            }


            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                issuerOpenTailsReader()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                issuerCreateCredential()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                proverStoresCredential()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                proverGetCredentialsForProofRequest()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                proverCreateRevocationState()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                proverCreateProof()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                verifierVerifyProof()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closeDeleteIssuerWallet()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closeDeleteProverWallet()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closePool()
            }

            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                deletePoolLedgerConfig()
            }


            Log.d(TAG, "startDemo: Anoncreds Revocation sample -> COMPLETED!")
        }
    }


    private suspend fun createOpenPool() {
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        //1. Create and Open Pool
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private suspend fun issuerCreateOpenWallet() {
        //2. Issuer Create and Open Wallet
        issuerWalletConfig = JSONObject().put("id", "issuerWallet").toString()
        issuerWalletCredentials = JSONObject().put("key", "issuer_wallet_key").toString()
        Wallet.createWallet(issuerWalletConfig, issuerWalletCredentials).get()
        issuerWallet = Wallet.openWallet(issuerWalletConfig, issuerWalletCredentials).get()
    }

    private suspend fun proverCreateOpenWallet() {
        //3. Prover Create and Open Wallet
        proverWalletConfig = JSONObject().put("id", "trusteeWallet").toString()
        proverWalletCredentials = JSONObject().put("key", "prover_wallet_key").toString()
        Wallet.createWallet(proverWalletConfig, proverWalletCredentials).get()
        proverWallet = Wallet.openWallet(proverWalletConfig, proverWalletCredentials).get()
    }

    private suspend fun issuerCreateCredentialSchema() {
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

    private suspend fun issuerCreateCredentialDefinition() {
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

    private suspend fun issuerCreateRevocationRegistry() {
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

    private suspend fun proverCreateMasterSecret() {
        //7. Prover create Master Secret
        masterSecretId = Anoncreds.proverCreateMasterSecret(proverWallet, null).get()
    }

    private suspend fun issuerCreateCredentialOffer() {
        //8. Issuer Creates Credential Offer
        credOffer = Anoncreds.issuerCreateCredentialOffer(issuerWallet, credDefId).get()
    }

    private suspend fun proverCreateCredentialRequest() {
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

    private suspend fun issuerOpenTailsReader() {
        //10. Issuer open Tails Reader
        val blobStorageReaderCfg = BlobStorageReader.openReader("default", tailsWriterConfig).get()
        blobStorageReaderHandle = blobStorageReaderCfg.blobStorageReaderHandle
    }

    private suspend fun issuerCreateCredential() {
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

    private suspend fun proverStoresCredential() {
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

    private suspend fun proverGetCredentialsForProofRequest() {
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

    private suspend fun proverCreateRevocationState() {
        //14. Prover create RevocationState
        revStateJson = Anoncreds.createRevocationState(
                blobStorageReaderHandle,
                revRegDefJson,
                revRegDeltaJson,
                timestamp,
                credRevId
        ).get()
    }

    private suspend fun proverCreateProof() {
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
        val credentialDefs = JSONObject().put(credDefId, JSONObject(credDefJson)).toString()
        val revStates =
                JSONObject().put(revRegId, JSONObject().put("" + timestamp, JSONObject(revStateJson)))
                        .toString()

        proofJson = Anoncreds.proverCreateProof(
                proverWallet, proofRequestJson, requestedCredentialsJson, masterSecretId, schemas,
                credentialDefs, revStates
        ).get()
        proof = JSONObject(proofJson)
    }

    private suspend fun verifierVerifyProof() {
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

    private suspend fun closeDeleteIssuerWallet() {
        //17. Close and Delete issuer wallet
        issuerWallet.closeWallet().get()
        Wallet.deleteWallet(issuerWalletConfig, issuerWalletCredentials).get()
    }

    private suspend fun closeDeleteProverWallet() {
        //18. Close and Delete prover wallet
        proverWallet.closeWallet().get()
        Wallet.deleteWallet(proverWalletConfig, proverWalletCredentials).get()
    }

    private suspend fun closePool() {
        //19. Close pool
        pool.closePoolLedger().get()
    }

    private suspend fun deletePoolLedgerConfig() {
        //20. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()
    }
}