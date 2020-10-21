package org.hyperledger.indy.sdk.ui.crypto

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.crypto.Crypto
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.junit.Assert
import java.util.*

class CryptoActivity : AppCompatActivity() {

    private val TAG = CryptoActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        startDemo()
    }

    private fun startDemo() {

        Log.d(TAG, "startDemo: Crypto sample -> STARTED!")


        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create and Open Pool
        val poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        val pool = Pool.openPoolLedger(poolName, "{}").get()


        // 2. Create and Open My Wallet
        val myWalletConfig = JSONObject().put("id", "myWallet").toString()
        val myWalletCredentials = JSONObject().put("key", "my_wallet_key").toString()
        Wallet.createWallet(myWalletConfig, myWalletCredentials).get()
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()


        // 3. Create and Open Their Wallet
        val theirWalletConfig = JSONObject().put("id", "theirWallet").toString()
        val theirWalletCredentials = JSONObject().put("key", "their_wallet_key").toString()
        Wallet.createWallet(theirWalletConfig, theirWalletCredentials).get()
        val theirWallet = Wallet.openWallet(theirWalletConfig, theirWalletCredentials).get()


        // 4. Create My Did
        val myDid = Did.createAndStoreMyDid(myWallet, "{}").get()
        val myVerkey = myDid.verkey


        // 5. Create Their Did
        val createTheirDidResult = Did.createAndStoreMyDid(theirWallet, "{}").get()
        val theirVerkey = createTheirDidResult.verkey


        // 6. Their auth encrypt message
        val msg = JSONObject()
            .put("reqId", "1495034346617224651")
            .put("identifier", "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL")
            .put(
                "operation", JSONObject()
                    .put("type", "1")
                    .put("dest", "4efZu2SXufS556yss7W5k6Po37jt4371RM4whbPKBKdB")
            )
            .toString()

        val encryptedMessage =
            Crypto.authCrypt(theirWallet, theirVerkey, myVerkey, msg.toByteArray()).get()


        // 7. I decrypt message
        val authDecryptResult = Crypto.authDecrypt(myWallet, myVerkey, encryptedMessage).get()

        Assert.assertTrue(Arrays.equals(msg.toByteArray(), authDecryptResult.decryptedMessage))
        Assert.assertEquals(theirVerkey, authDecryptResult.verkey)

        // 8. Close and delete My Wallet

        // 8. Close and delete My Wallet
        myWallet.closeWallet().get()
        Wallet.deleteWallet(myWalletConfig, myWalletCredentials).get()


        // 9. Close and delete Their Wallet
        theirWallet.closeWallet().get()
        Wallet.deleteWallet(theirWalletConfig, theirWalletCredentials).get()


        // 10. Close Pool
        pool.closePoolLedger().get()


        // 11. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()

        Log.d(TAG, "startDemo: Crypto sample -> COMPLETED!")
    }
}