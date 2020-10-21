package org.hyperledger.indy.sdk.ui.ledger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidJSONParameters
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.junit.Assert

class LedgerActivity : AppCompatActivity() {

    private val TAG = LedgerActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ledger)

        startDemo()
    }

    private fun startDemo() {

        Log.d(TAG, "startDemo: Ledger sample -> STARTED!")

        val trusteeSeed = "000000000000000000000000Trustee1"


        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create ledger config from genesis txn file
        val poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        val pool = Pool.openPoolLedger(poolName, "{}").get()


        // 2. Create and Open My Wallet
        val myWalletConfig = JSONObject().put("id", "myWallet").toString()
        val myWalletCredentials = JSONObject().put("key", "my_wallet_key").toString()
        Wallet.createWallet(myWalletConfig, myWalletCredentials).get()
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()


        // 3. Create and Open Trustee Wallet
        val trusteeWalletConfig = JSONObject().put("id", "theirWallet").toString()
        val trusteeWalletCredentials = JSONObject().put("key", "trustee_wallet_key").toString()
        Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).get()
        val trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get()


        // 4. Create My Did
        val createMyDidResult = Did.createAndStoreMyDid(myWallet, "{}").get()
        val myDid = createMyDidResult.did
        val myVerkey = createMyDidResult.verkey


        // 5. Create Did from Trustee1 seed
        val theirDidJson =
            DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null)

        val createTheirDidResult =
            Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get()
        val trusteeDid = createTheirDidResult.did


        // 6. Build Nym Request
        val nymRequest = Ledger.buildNymRequest(trusteeDid, myDid, myVerkey, null, null).get()


        // 7. Trustee Sign Nym Request
        val nymResponseJson =
            Ledger.signAndSubmitRequest(pool, trusteeWallet, trusteeDid, nymRequest).get()

        val nymResponse = JSONObject(nymResponseJson)

        Assert.assertEquals(
            myDid,
            nymResponse.getJSONObject("result").getJSONObject("txn").getJSONObject("data")
                .getString("dest")
        )
        Assert.assertEquals(
            myVerkey,
            nymResponse.getJSONObject("result").getJSONObject("txn").getJSONObject("data")
                .getString("verkey")
        )


        // 8. Close and delete My Wallet
        myWallet.closeWallet().get()
        Wallet.deleteWallet(myWalletConfig, myWalletCredentials).get()


        // 9. Close and delete Their Wallet
        trusteeWallet.closeWallet().get()
        Wallet.deleteWallet(trusteeWalletConfig, trusteeWalletCredentials).get()


        // 10. Close Pool
        pool.closePoolLedger().get()


        // 11. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()

        Log.d(TAG, "startDemo: Ledger sample -> COMPLETED!")

    }
}