package org.hyperledger.indy.sdk.ui.anoncreds_revocation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anoncreds_revocation)

        startDemo()
    }

    private fun startDemo() {

        Log.d(TAG, "startDemo: Anoncreds Revocation sample -> STARTED!")

        val issuerDid = "NcYxiDXkpYi6ov5FcYDi1e"
        val proverDid = "VsKV7grR1BUE29mG2Fm2kX"


        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        //1. Create and Open Pool
        val poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        val pool = Pool.openPoolLedger(poolName, "{}").get()


        //2. Issuer Create and Open Wallet
        val issuerWalletConfig = JSONObject().put("id", "issuerWallet").toString()
        val issuerWalletCredentials = JSONObject().put("key", "issuer_wallet_key").toString()
        Wallet.createWallet(issuerWalletConfig, issuerWalletCredentials).get()
        val issuerWallet = Wallet.openWallet(issuerWalletConfig, issuerWalletCredentials).get()


        //3. Prover Create and Open Wallet
        val proverWalletConfig = JSONObject().put("id", "trusteeWallet").toString()
        val proverWalletCredentials = JSONObject().put("key", "prover_wallet_key").toString()
        Wallet.createWallet(proverWalletConfig, proverWalletCredentials).get()
        val proverWallet = Wallet.openWallet(proverWalletConfig, proverWalletCredentials).get()


        //4. Issuer Creates Credential Schema
        val schemaName = "gvt"
        val schemaVersion = "1.0"
        val schemaAttributes =
            JSONArray().put("name").put("age").put("sex").put("height").toString()
        val createSchemaResult =
            Anoncreds.issuerCreateSchema(issuerDid, schemaName, schemaVersion, schemaAttributes)
                .get()
        val schemaId = createSchemaResult.schemaId
        val schemaJson = createSchemaResult.schemaJson


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
        val credDefId = createCredDefResult.credDefId
        val credDefJson = createCredDefResult.credDefJson


        //6. Issuer create Revocation Registry
        val revRegDefConfig = JSONObject()
            .put("issuance_type", "ISSUANCE_ON_DEMAND")
            .put("max_cred_num", 5)
            .toString()
        val tailsWriterConfig = JSONObject()
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
        val revRegId = createRevRegResult.revRegId
        val revRegDefJson = createRevRegResult.revRegDefJson


        //7. Prover create Master Secret
        val masterSecretId = Anoncreds.proverCreateMasterSecret(proverWallet, null).get()


        //8. Issuer Creates Credential Offer
        val credOffer = Anoncreds.issuerCreateCredentialOffer(issuerWallet, credDefId).get()


        //9. Prover Creates Credential Request
        val createCredReqResult = Anoncreds.proverCreateCredentialReq(
            proverWallet,
            proverDid,
            credOffer,
            credDefJson,
            masterSecretId
        ).get()
        val credReqJson = createCredReqResult.credentialRequestJson
        val credReqMetadataJson = createCredReqResult.credentialRequestMetadataJson


        //10. Issuer open Tails Reader
        val blobStorageReaderCfg = BlobStorageReader.openReader("default", tailsWriterConfig).get()
        val blobStorageReaderHandle = blobStorageReaderCfg.blobStorageReaderHandle


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
        val credentialJson = createCredentialResult.credentialJson
        val revRegDeltaJson = createCredentialResult.revocRegDeltaJson
        val credRevId = createCredentialResult.revocId


        //12. Prover Stores Credential
        Anoncreds.proverStoreCredential(
            proverWallet,
            null,
            credReqMetadataJson,
            credentialJson,
            credDefJson,
            revRegDefJson
        ).get()


        //13. Prover Gets Credentials for Proof Request
        val timestamp = System.currentTimeMillis() / 1000
        val nonce = Anoncreds.generateNonce().get()
        val proofRequestJson = JSONObject()
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
        val credIdForAttr1 = credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info")
            .getString("referent")

        val credentialsForAttribute2 =
            JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", 100).get())
        val credIdForPred1 = credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info")
            .getString("referent")
        credentialsSearch.close()


        //14. Prover create RevocationState
        val revStateJson = Anoncreds.createRevocationState(
            blobStorageReaderHandle,
            revRegDefJson,
            revRegDeltaJson,
            timestamp,
            credRevId
        ).get()


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

        val schemas = JSONObject().put(schemaId, JSONObject(schemaJson)).toString()
        val credentialDefs = JSONObject().put(credDefId, JSONObject(credDefJson)).toString()
        val revStates =
            JSONObject().put(revRegId, JSONObject().put("" + timestamp, JSONObject(revStateJson)))
                .toString()

        val proofJson = Anoncreds.proverCreateProof(
            proverWallet, proofRequestJson, requestedCredentialsJson, masterSecretId, schemas,
            credentialDefs, revStates
        ).get()
        val proof = JSONObject(proofJson)


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


        //17. Close and Delete issuer wallet
        issuerWallet.closeWallet().get()
        Wallet.deleteWallet(issuerWalletConfig, issuerWalletCredentials).get()


        //18. Close and Delete prover wallet
        proverWallet.closeWallet().get()
        Wallet.deleteWallet(proverWalletConfig, proverWalletCredentials).get()


        //19. Close pool
        pool.closePoolLedger().get()


        //20. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()

        Log.d(TAG, "startDemo: Anoncreds Revocation sample -> COMPLETED!")
    }
}