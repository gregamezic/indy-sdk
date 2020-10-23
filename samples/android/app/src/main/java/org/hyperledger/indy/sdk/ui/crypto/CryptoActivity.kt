package org.hyperledger.indy.sdk.ui.crypto

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_anoncreds.*
import kotlinx.android.synthetic.main.activity_crypto.*
import kotlinx.coroutines.*
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.crypto.Crypto
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.helpers.MessageHelper
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.junit.Assert
import java.util.*

class CryptoActivity : AppCompatActivity() {

    private val TAG = CryptoActivity::class.java.name



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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        startDemo()
    }


    private fun updateUI(text: String) {
        tvCryptoLogs.text = "${tvCryptoLogs.text}$text"
    }

    private fun updateHeader(text: String) {
        pbCrypto.visibility = View.VISIBLE
        tvCryptoStart.text = text
    }

    private fun updateFooter(text: String) {
        pbCrypto.visibility = View.GONE
        tvCryptoEnd.text = text
    }


    /**
     * startDemo function start all functions for Crypto Revocation demo chronological in coroutine default thread
     */
    private fun startDemo() {
        MainScope().launch {
            Log.d(TAG, "startDemo: Crypto sample -> STARTED!")
            updateHeader(getString(R.string.crypto_sample_start))

            updateUI(getString(R.string.crypto_create_pool))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createOpenPool()
            }
            updateUI(getString(R.string.crypto_create_pool_end))


            updateUI(getString(R.string.crypto_create_open_my_wallet))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createOpenMyWallet()
            }
            updateUI(getString(R.string.crypto_create_open_my_wallet_end))


            updateUI(getString(R.string.crypto_create_open_their_wallet))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createOpenTheirWallet()
            }
            updateUI(getString(R.string.crypto_create_open_their_wallet_end))


            updateUI(getString(R.string.crypto_create_my_did))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createMyDID()
            }
            updateUI(getString(R.string.crypto_create_my_did_end))


            updateUI(getString(R.string.crypto_create_their_did))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                createTheirDID()
            }
            updateUI(getString(R.string.crypto_create_their_did_end))


            updateUI(getString(R.string.crypto_their_auth_encrypt_message))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                theirAuthEncryptMessage()
            }
            updateUI(getString(R.string.crypto_their_auth_encrypt_message_end))


            updateUI(getString(R.string.crypto_i_decrypt_message))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                iDecryptMessage()
            }
            updateUI(getString(R.string.crypto_i_decrypt_message_end))


            updateUI(getString(R.string.crypto_close_delete_my_wallet))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closeDeleteMyWallet()
            }
            updateUI(getString(R.string.crypto_close_delete_my_wallet))

            updateUI(getString(R.string.crypto_close_delete_their_wallet))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closeDeleteTheirWallet()
            }
            updateUI(getString(R.string.crypto_close_delete_their_wallet_end))


            updateUI(getString(R.string.crypto_close_pool))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                closePool()
            }
            updateUI(getString(R.string.crypto_close_pool_end))


            updateUI(getString(R.string.crypto_delete_pool_ledger_config))
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                deletePoolLedgerConfig()
            }
            updateUI(getString(R.string.crypto_delete_pool_ledger_config_end))


            MessageHelper.successToast(this@CryptoActivity, getString(R.string.success))
            updateFooter(getString(R.string.crypto_sample_completed))
            Log.d(TAG, "startDemo: Crypto sample -> COMPLETED!")
        }
    }


    private suspend fun createOpenPool() {
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()


        // 1. Create and Open Pool
        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    private suspend fun createOpenMyWallet() {
        // 2. Create and Open My Wallet
        myWalletConfig = JSONObject().put("id", "myWallet").toString()
        myWalletCredentials = JSONObject().put("key", "my_wallet_key").toString()
        Wallet.createWallet(myWalletConfig, myWalletCredentials).get()
        myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
    }

    private suspend fun createOpenTheirWallet() {
        // 3. Create and Open Their Wallet
        theirWalletConfig = JSONObject().put("id", "theirWallet").toString()
        theirWalletCredentials = JSONObject().put("key", "their_wallet_key").toString()
        Wallet.createWallet(theirWalletConfig, theirWalletCredentials).get()
        theirWallet = Wallet.openWallet(theirWalletConfig, theirWalletCredentials).get()
    }

    private suspend fun createMyDID() {
        // 4. Create My Did
        val myDid = Did.createAndStoreMyDid(myWallet, "{}").get()
        myVerkey = myDid.verkey
    }

    private suspend fun createTheirDID() {
        // 5. Create Their Did
        val createTheirDidResult = Did.createAndStoreMyDid(theirWallet, "{}").get()
        theirVerkey = createTheirDidResult.verkey
    }

    private suspend fun theirAuthEncryptMessage() {
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

    private suspend fun iDecryptMessage() {
        // 7. I decrypt message
        val authDecryptResult = Crypto.authDecrypt(myWallet, myVerkey, encryptedMessage).get()

        Assert.assertTrue(Arrays.equals(msg.toByteArray(), authDecryptResult.decryptedMessage))
        Assert.assertEquals(theirVerkey, authDecryptResult.verkey)
    }

    private suspend fun closeDeleteMyWallet() {
        // 8. Close and delete My Wallet
        myWallet.closeWallet().get()
        Wallet.deleteWallet(myWalletConfig, myWalletCredentials).get()
    }

    private suspend fun closeDeleteTheirWallet() {
        // 9. Close and delete Their Wallet
        theirWallet.closeWallet().get()
        Wallet.deleteWallet(theirWalletConfig, theirWalletCredentials).get()
    }

    private suspend fun closePool() {
        // 10. Close Pool
        pool.closePoolLedger().get()
    }

    private suspend fun deletePoolLedgerConfig() {
        // 11. Delete Pool ledger config
        Pool.deletePoolLedgerConfig(poolName).get()
    }
}