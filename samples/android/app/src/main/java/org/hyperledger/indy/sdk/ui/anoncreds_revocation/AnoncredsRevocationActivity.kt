package org.hyperledger.indy.sdk.ui.anoncreds_revocation

import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.blob_storage.BlobStorageReader
import org.hyperledger.indy.sdk.blob_storage.BlobStorageWriter
import org.hyperledger.indy.sdk.ui.base.BaseActivity
import org.hyperledger.indy.sdk.ui.base.models.WalletData
import org.hyperledger.indy.sdk.utils.EnvironmentUtils
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert

class AnoncredsRevocationActivity : BaseActivity() {

    // my vars
    private val issuerDid = "NcYxiDXkpYi6ov5FcYDi1e"
    private val proverDid = "VsKV7grR1BUE29mG2Fm2kX"
    private lateinit var issuerWallet: WalletData
    private lateinit var proverWallet: WalletData
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

    /**
     * startDemo function start all functions for Anoncreds Revocation demo chronological in coroutine default thread
     */
    override fun onStartDemo() {
        Log.d(TAG, "startDemo: Anoncreds Revocation sample -> STARTED!")
        updateHeader(getString(R.string.anoncreds_revocation_sample_start))

        job = MainScope().launch {

            // 1. Create and Open Pool
            ensureActive()
            runAction(
                getString(R.string.create_pool),
                { createAndOpenPool() },
                getString(R.string.create_pool_end)
            )

            // 2. Issuer Create and Open Wallet
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_create_open_wallet),
                { issuerCreateAndOpenWallet() },
                getString(R.string.anoncreds_revocation_create_open_wallet_end)
            )

            // 3. Prover Create and Open Wallet
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_prover_create_open_wallet),
                { proverCreateOpenWallet() },
                getString(R.string.anoncreds_revocation_prover_create_open_wallet_end)
            )

            // 4. Issuer Creates Credential Schema
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_issuer_create_credential_schema),
                { issuerCreateCredentialSchema() },
                getString(R.string.anoncreds_revocation_issuer_create_credential_schema_end)
            )

            // 5. Issuer create Credential Definition
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_issuer_create_credential_definition),
                { issuerCreateCredentialDefinition() },
                getString(R.string.anoncreds_revocation_issuer_create_credential_definition_end)
            )

            // 6. Issuer create Revocation Registry
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_issuer_create_revocation_registry),
                { issuerCreateRevocationRegistry() },
                getString(R.string.anoncreds_revocation_issuer_create_revocation_registry_end)
            )

            // 7. Prover create Master Secret
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_prover_create_master_secret),
                { proverCreateMasterSecret() },
                getString(R.string.anoncreds_revocation_prover_create_master_secret_end)
            )

            // 8. Issuer Creates Credential Offer
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_issuer_create_credential_offer),
                { issuerCreateCredentialOffer() },
                getString(R.string.anoncreds_revocation_issuer_create_credential_offer_end)
            )

            // 9. Prover Creates Credential Request
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_prover_create_credential_request),
                { proverCreateCredentialRequest() },
                getString(R.string.anoncreds_revocation_prover_create_credential_request_end)
            )

            // 10. Issuer open Tails Reader
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_issuer_open_tails_reader),
                { issuerOpenTailsReader() },
                getString(R.string.anoncreds_revocation_issuer_open_tails_reader_end)
            )

            // 11. Issuer create Credential
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_issuer_create_credential),
                { issuerCreateCredential() },
                getString(R.string.anoncreds_revocation_issuer_create_credential_end)
            )

            // 12. Prover Stores Credential
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_prover_stores_credential),
                { proverStoresCredential() },
                getString(R.string.anoncreds_revocation_prover_stores_credential_end)
            )

            // 13. Prover Gets Credentials for Proof Request
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_prover_get_credential_proof_request),
                { proverGetCredentialsForProofRequest() },
                getString(R.string.anoncreds_revocation_prover_get_credential_proof_request_end)
            )

            // 14. Prover create RevocationState
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_prover_create_revocation_state),
                { proverCreateRevocationState() },
                getString(R.string.anoncreds_revocation_prover_create_revocation_state_end)
            )

            // 15. Prover Creates Proof
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_prover_creates_proof),
                { proverCreateProof() },
                getString(R.string.anoncreds_revocation_prover_creates_proof_end)
            )

            // 16. Verifier verify Proof
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_verifier_verify_proof),
                { verifierVerifyProof() },
                getString(R.string.anoncreds_revocation_verifier_verify_proof_end)
            )

            // 17. Close and delete Issuer Wallet
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_close_delete_issuer_wallet),
                { closeDeleteIssuerWallet() },
                getString(R.string.anoncreds_revocation_close_delete_issuer_wallet_end)
            )

            // 18. Close and delete Prover Wallet
            ensureActive()
            runAction(
                getString(R.string.anoncreds_revocation_close_delete_prover_wallet),
                { closeDeleteProverWallet() },
                getString(R.string.anoncreds_revocation_close_delete_prover_wallet_end)
            )

            // 19. Close Pool
            ensureActive()
            runAction(
                getString(R.string.close_pool),
                { closePool() },
                getString(R.string.close_pool_end)
            )

            // 20. Delete Pool ledger configuration
            ensureActive()
            runAction(
                getString(R.string.delete_pool_ledger_config),
                { deletePoolLedgerConfig() },
                getString(R.string.delete_pool_ledger_config_end)
            )

            ensureActive()
            successToast(getString(R.string.success))
            updateFooter(getString(R.string.anoncreds_revocation_sample_completed))
            Log.d(TAG, "startDemo: Anoncreds Revocation sample -> COMPLETED!")
        }
    }

    // region demo steps functions
    private fun issuerCreateAndOpenWallet() {
        issuerWallet = createAndOpenWallet("issuerWallet", "issuer_wallet_key")
    }

    private fun proverCreateOpenWallet() {
        proverWallet = createAndOpenWallet("proverWalletId", "prover_wallet_key")
    }

    private fun issuerCreateCredentialSchema() {
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
        val credDefTag = "Tag1"
        val credDefConfigJson = JSONObject().put("support_revocation", true).toString()
        val createCredDefResult = Anoncreds.issuerCreateAndStoreCredentialDef(
            issuerWallet.wallet,
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
            issuerWallet.wallet,
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
        masterSecretId = Anoncreds.proverCreateMasterSecret(proverWallet.wallet, null).get()
    }

    private fun issuerCreateCredentialOffer() {
        credOffer = Anoncreds.issuerCreateCredentialOffer(issuerWallet.wallet, credDefId).get()
    }

    private fun proverCreateCredentialRequest() {
        val createCredReqResult = Anoncreds.proverCreateCredentialReq(
            proverWallet.wallet,
            proverDid,
            credOffer,
            credDefJson,
            masterSecretId
        ).get()
        credReqJson = createCredReqResult.credentialRequestJson
        credReqMetadataJson = createCredReqResult.credentialRequestMetadataJson
    }

    private fun issuerOpenTailsReader() {
        val blobStorageReaderCfg = BlobStorageReader.openReader("default", tailsWriterConfig).get()
        blobStorageReaderHandle = blobStorageReaderCfg.blobStorageReaderHandle
    }

    private fun issuerCreateCredential() {
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
            issuerWallet.wallet,
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
        Anoncreds.proverStoreCredential(
            proverWallet.wallet,
            null,
            credReqMetadataJson,
            credentialJson,
            credDefJson,
            revRegDefJson
        ).get()
    }

    private fun proverGetCredentialsForProofRequest() {
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
            CredentialsSearchForProofReq.open(proverWallet.wallet, proofRequestJson, null).get()

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
        revStateJson = Anoncreds.createRevocationState(
            blobStorageReaderHandle,
            revRegDefJson,
            revRegDeltaJson,
            timestamp,
            credRevId
        ).get()
    }

    private fun proverCreateProof() {
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
            JSONObject().put(revRegId, JSONObject().put("" + timestamp, JSONObject(revStateJson)))
                .toString()

        proofJson = Anoncreds.proverCreateProof(
            proverWallet.wallet,
            proofRequestJson,
            requestedCredentialsJson,
            masterSecretId,
            schemas,
            credentialDefs,
            revStates
        ).get()
        proof = JSONObject(proofJson)
    }

    private fun verifierVerifyProof() {
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
        closeAndDeleteWallet(
            issuerWallet.wallet,
            issuerWallet.walletConfig,
            issuerWallet.walletCredentials
        )
    }

    private fun closeDeleteProverWallet() {
        closeAndDeleteWallet(
            proverWallet.wallet,
            proverWallet.walletConfig,
            proverWallet.walletCredentials
        )
    }
    // endregion

    private companion object {
        val TAG: String = AnoncredsRevocationActivity::class.java.name
    }
}
