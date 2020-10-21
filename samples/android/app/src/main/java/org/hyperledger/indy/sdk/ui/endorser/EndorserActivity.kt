package org.hyperledger.indy.sdk.ui.endorser

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidJSONParameters
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert

class EndorserActivity : AppCompatActivity() {

    private val TAG = EndorserActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endorser)

        startDemo()
    }

    private fun startDemo() {

        Log.d(TAG, "startDemo: Endorser sample -> STARTED!")

        val trusteeSeed = "000000000000000000000000Trustee1"


        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create and Open Pool
        val poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        val pool = Pool.openPoolLedger(poolName, "{}").get()


        // 2. Create and Open Author Wallet
        val authorWalletConfig = JSONObject().put("id", "authorWallet").toString()
        val authorWalletCredentials = JSONObject().put("key", "author_wallet_key").toString()
        Wallet.createWallet(authorWalletConfig, authorWalletCredentials).get()
        val authorWallet = Wallet.openWallet(authorWalletConfig, authorWalletCredentials).get()


        // 3. Create and Open Endorser Wallet
        val endorserWalletConfig = JSONObject().put("id", "endorserWallet").toString()
        val endorserWalletCredentials = JSONObject().put("key", "endorser_wallet_key").toString()
        Wallet.createWallet(endorserWalletConfig, endorserWalletCredentials).get()
        val endorserWallet =
            Wallet.openWallet(endorserWalletConfig, endorserWalletCredentials).get()


        // 3. Create and Open Trustee Wallet
        val trusteeWalletConfig = JSONObject().put("id", "trusteeWallet").toString()
        val trusteeWalletCredentials = JSONObject().put("key", "trustee_wallet_key").toString()
        Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
        val trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get()


        // 4. Create Trustee DID
        val theirDidJson =
            DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null)
        val createTheirDidResult =
            Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get()
        val trusteeDid = createTheirDidResult.did


        // 5. Create Author DID
        var createMyDidResult = Did.createAndStoreMyDid(authorWallet, "{}").get()
        val authorDid = createMyDidResult.did
        val authorVerkey = createMyDidResult.verkey


        // 6. Create Endorser DID
        createMyDidResult = Did.createAndStoreMyDid(endorserWallet, "{}").get()
        val endorserDid = createMyDidResult.did
        val endorserVerkey = createMyDidResult.verkey


        // 7. Build Author Nym Request
        var nymRequest =
            Ledger.buildNymRequest(trusteeDid, authorDid, authorVerkey, null, null).get()


        // 8. Trustee Sign Author Nym Request
        Ledger.signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get()


        // 9. Build Endorser Nym Request
        nymRequest =
            Ledger.buildNymRequest(trusteeDid, endorserDid, endorserVerkey, null, "ENDORSER").get()


        // 10. Trustee Sign Endorser Nym Request
        Ledger.signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get()


        // 11. Create schema with endorser
        val schemaName = "gvt"
        val schemaVersion = "1.0"
        val schemaAttributes =
            JSONArray().put("name").put("age").put("sex").put("height").toString()
        val createSchemaResult =
            Anoncreds.issuerCreateSchema(authorDid, schemaName, schemaVersion, schemaAttributes)
                .get()
        val schemaId = createSchemaResult.schemaId
        val schemaJson = createSchemaResult.schemaJson

        //  Transaction Author builds Schema Request

        //  Transaction Author builds Schema Request
        val schemaRequest = Ledger.buildSchemaRequest(authorDid, schemaJson).get()

        //  Transaction Author appends Endorser's DID into the request

        //  Transaction Author appends Endorser's DID into the request
        val schemaRequestWithEndorser =
            Ledger.appendRequestEndorser(schemaRequest, endorserDid).get()

        //  Transaction Author signs the request with the added endorser field

        //  Transaction Author signs the request with the added endorser field
        val schemaRequestWithEndorserAuthorSigned =
            Ledger.multiSignRequest(authorWallet, authorDid, schemaRequestWithEndorser).get()

        //  Transaction Endorser signs the request

        //  Transaction Endorser signs the request
        val schemaRequestWithEndorserSigned = Ledger.multiSignRequest(
            endorserWallet,
            endorserDid,
            schemaRequestWithEndorserAuthorSigned
        ).get()

        //  Transaction Endorser sends the request

        //  Transaction Endorser sends the request
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

        Log.d(TAG, "startDemo: Endorser sample -> COMPLETED!")
    }
}