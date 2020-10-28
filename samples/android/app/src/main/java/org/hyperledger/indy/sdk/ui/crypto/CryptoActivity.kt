package org.hyperledger.indy.sdk.ui.crypto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.crypto.Crypto
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.listeners.ActionFailListener
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.ui.BaseActivity
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.junit.Assert
import java.util.*

class CryptoActivity : BaseActivity(), ActionFailListener {

    // my vars
    private lateinit var myWallet: Wallet
    private lateinit var theirWallet: Wallet
    private lateinit var myVerkey: String
    private lateinit var theirVerkey: String
    private lateinit var  encryptedMessage: ByteArray
    private lateinit var  msg: String
    private lateinit var  myWalletConfig: String
    private lateinit var  myWalletCredentials: String
    private lateinit var  theirWalletConfig: String
    private lateinit var  theirWalletCredentials: String
    private lateinit var  poolName: String
    private lateinit var  pool: Pool

    private lateinit var job: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init on fail listener for demo job
        onFailListener = this

        // start demo
        startDemo()
    }


    /**
     * startDemo function start all functions for Crypto Revocation demo chronological in coroutine default thread
     */
    private fun startDemo() {

        Log.d(TAG, "startDemo: Crypto sample -> STARTED!")
        updateHeader(getString(R.string.crypto_sample_start))

        // Start
        job = MainScope().launch {

            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.create_pool),
                { createOpenPool() },
                getString(R.string.create_pool_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.crypto_create_open_my_wallet),
                { createOpenMyWallet() },
                getString(R.string.crypto_create_open_my_wallet_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.crypto_create_open_their_wallet),
                { createOpenTheirWallet() },
                getString(R.string.crypto_create_open_their_wallet_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.crypto_create_my_did),
                { createMyDID() },
                getString(R.string.crypto_create_my_did_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.crypto_create_their_did),
                { createTheirDID() },
                getString(R.string.crypto_create_their_did_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.crypto_their_auth_encrypt_message),
                { theirAuthEncryptMessage() },
                getString(R.string.crypto_their_auth_encrypt_message_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.crypto_i_decrypt_message),
                { iDecryptMessage() },
                getString(R.string.crypto_i_decrypt_message_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.crypto_close_delete_my_wallet),
                { closeDeleteMyWallet() },
                getString(R.string.crypto_close_delete_my_wallet_end)
            )


            if (job.isCancelled) return@launch
            runAction(
                getString(R.string.crypto_close_delete_their_wallet),
                { closeDeleteTheirWallet() },
                getString(R.string.crypto_close_delete_their_wallet_end)
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
            successToast(getString(R.string.success))
            updateFooter(getString(R.string.crypto_sample_completed))
            Log.d(TAG, "startDemo: Crypto sample -> COMPLETED!")
        }
    }


    // region demo steps functions
    private fun createOpenPool() {
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create and Open Pool
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private fun createOpenMyWallet() {
        // 2. Create and Open My Wallet
        myWalletConfig = JSONObject().put("id", "myWallet").toString()
        myWalletCredentials = JSONObject().put("key", "my_wallet_key").toString()
        Wallet.createWallet(myWalletConfig, myWalletCredentials).get()
        myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
    }

    private fun createOpenTheirWallet() {
        // 3. Create and Open Their Wallet
        theirWalletConfig = JSONObject().put("id", "theirWallet").toString()
        theirWalletCredentials = JSONObject().put("key", "their_wallet_key").toString()
        Wallet.createWallet(theirWalletConfig, theirWalletCredentials).get()
        theirWallet = Wallet.openWallet(theirWalletConfig, theirWalletCredentials).get()
    }

    private fun createMyDID() {
        // 4. Create My Did
        val myDid = Did.createAndStoreMyDid(myWallet, "{}").get()
        myVerkey = myDid.verkey
    }

    private fun createTheirDID() {
        // 5. Create Their Did
        val createTheirDidResult = Did.createAndStoreMyDid(theirWallet, "{}").get()
        theirVerkey = createTheirDidResult.verkey
    }

    private fun theirAuthEncryptMessage() {
        // 6. Their auth encrypt message
        msg = JSONObject()
                .put("reqId", "1495034346617224651")
                .put("identifier", "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL")
                .put(
                        "operation", JSONObject()
                        .put("type", "1")
                        .put("dest", "4efZu2SXufS556yss7W5k6Po37jt4371RM4whbPKBKdB")
                )
                .toString()

        encryptedMessage =
                Crypto.authCrypt(theirWallet, theirVerkey, myVerkey, msg.toByteArray()).get()
    }

    private fun iDecryptMessage() {
        // 7. I decrypt message
        val authDecryptResult = Crypto.authDecrypt(myWallet, myVerkey, encryptedMessage).get()

        Assert.assertTrue(Arrays.equals(msg.toByteArray(), authDecryptResult.decryptedMessage))
        Assert.assertEquals(theirVerkey, authDecryptResult.verkey)
    }

    private fun closeDeleteMyWallet() {
        // 8. Close and delete My Wallet
        myWallet.closeWallet().get()
        Wallet.deleteWallet(myWalletConfig, myWalletCredentials).get()
    }

    private fun closeDeleteTheirWallet() {
        // 9. Close and delete Their Wallet
        theirWallet.closeWallet().get()
        Wallet.deleteWallet(theirWalletConfig, theirWalletCredentials).get()
    }

    private fun closePool() {
        // 10. Close Pool
        pool.closePoolLedger().get()
    }

    private fun deletePoolLedgerConfig() {
        // 11. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()
    }
    // endregion


    override fun onFail() {
        job.cancel()
    }


    companion object {
        private val TAG = CryptoActivity::class.java.name
    }
}
