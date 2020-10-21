package org.hyperledger.indy.sdk.ui.anoncreds

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert

class AnoncredsActivity : AppCompatActivity() {

    private val TAG = AnoncredsActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anoncreds)

        GlobalScope.async {
            startDemo()
        }
    }

    private suspend fun startDemo() {

        Log.d(TAG, "startDemo: Anoncreds sample -> START!")

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
        val createSchemaResult = org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateSchema(
            issuerDid,
            schemaName,
            schemaVersion,
            schemaAttributes
        ).get()
        val schemaId = createSchemaResult.schemaId
        val schemaJson = createSchemaResult.schemaJson


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
        val credDefId = createCredDefResult.credDefId
        val credDefJson = createCredDefResult.credDefJson


        //6. Prover create Master Secret
        val masterSecretId = org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverCreateMasterSecret(
            proverWallet,
            null
        ).get()


        //7. Issuer Creates Credential Offer
        val credOffer = org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateCredentialOffer(
            issuerWallet,
            credDefId
        ).get()


        //8. Prover Creates Credential Request
        val createCredReqResult =
            org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverCreateCredentialReq(
                proverWallet,
                proverDid,
                credOffer,
                credDefJson,
                masterSecretId
            ).get()
        val credReqJson = createCredReqResult.credentialRequestJson
        val credReqMetadataJson = createCredReqResult.credentialRequestMetadataJson


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
        val credential = createCredentialResult.credentialJson


        //10. Prover Stores Credential
        org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverStoreCredential(
            proverWallet,
            null,
            credReqMetadataJson,
            credential,
            credDefJson,
            null
        ).get()


        //11. Prover Gets Credentials for Proof Request
        val nonce = org.hyperledger.indy.sdk.anoncreds.Anoncreds.generateNonce().get()
        val proofRequestJson = JSONObject()
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
        val credentialIdForAttribute1 =
            credentialsForAttribute1.getJSONObject(0).getJSONObject("cred_info")
                .getString("referent")

        val credentialsForAttribute2 =
            JSONArray(credentialsSearch.fetchNextCredentials("attr2_referent", 100).get())
        val credentialIdForAttribute2 =
            credentialsForAttribute2.getJSONObject(0).getJSONObject("cred_info")
                .getString("referent")

        val credentialsForAttribute3 =
            JSONArray(credentialsSearch.fetchNextCredentials("attr3_referent", 100).get())
        Assert.assertEquals(0, credentialsForAttribute3.length().toLong())

        val credentialsForPredicate =
            JSONArray(credentialsSearch.fetchNextCredentials("predicate1_referent", 100).get())
        val credentialIdForPredicate =
            credentialsForPredicate.getJSONObject(0).getJSONObject("cred_info")
                .getString("referent")
        credentialsSearch.close()


        //12. Prover Creates Proof
        val selfAttestedValue = "8-800-300"
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

        val schemas = JSONObject().put(schemaId, JSONObject(schemaJson)).toString()
        val credentialDefs = JSONObject().put(credDefId, JSONObject(credDefJson)).toString()
        val revocStates = JSONObject().toString()

        var proofJson: String? = ""
        try {
            proofJson = org.hyperledger.indy.sdk.anoncreds.Anoncreds.proverCreateProof(
                proverWallet, proofRequestJson, requestedCredentialsJson,
                masterSecretId, schemas, credentialDefs, revocStates
            ).get()
        } catch (e: Exception) {
            println("")
        }
        val proof = JSONObject(proofJson)


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


        //14. Close and Delete issuer wallet
        issuerWallet.closeWallet().get()
        Wallet.deleteWallet(issuerWalletConfig, issuerWalletCredentials).get()


        //15. Close and Delete prover wallet
        proverWallet.closeWallet().get()
        Wallet.deleteWallet(proverWalletConfig, proverWalletCredentials).get()


        //16. Close pool
        pool.closePoolLedger().get()


        //17. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()

        Log.d(TAG, "startDemo: Anoncreds sample -> COMPLETED!")
    }
}