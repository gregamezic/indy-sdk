package org.hyperledger.indy.sdk.ui.crypto

import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.crypto.Crypto
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.ui.base.BaseActivity
import org.hyperledger.indy.sdk.ui.base.models.WalletData
import org.json.JSONObject
import org.junit.Assert
import java.util.*

class CryptoActivity : BaseActivity() {

    // my vars
    private lateinit var myWallet: WalletData
    private lateinit var theirWallet: WalletData
    private lateinit var myVerkey: String
    private lateinit var theirVerkey: String
    private lateinit var encryptedMessage: ByteArray
    private lateinit var msg: String

    /**
     * startDemo function start all functions for Crypto Revocation demo chronological in coroutine default thread
     */
    override fun onStartDemo() {

        Log.d(TAG, "startDemo: Crypto sample -> STARTED!")
        updateHeader(getString(R.string.crypto_sample_start))

        // Start
        job = MainScope().launch {

            ensureActive()
            runAction(
                getString(R.string.create_pool),
                { createAndOpenPool() },
                getString(R.string.create_pool_end)
            )

            ensureActive()
            runAction(
                getString(R.string.crypto_create_open_my_wallet),
                { createOpenMyWallet() },
                getString(R.string.crypto_create_open_my_wallet_end)
            )

            ensureActive()
            runAction(
                getString(R.string.crypto_create_open_their_wallet),
                { createOpenTheirWallet() },
                getString(R.string.crypto_create_open_their_wallet_end)
            )

            ensureActive()
            runAction(
                getString(R.string.crypto_create_my_did),
                { createMyDID() },
                getString(R.string.crypto_create_my_did_end)
            )

            ensureActive()
            runAction(
                getString(R.string.crypto_create_their_did),
                { createTheirDID() },
                getString(R.string.crypto_create_their_did_end)
            )

            ensureActive()
            runAction(
                getString(R.string.crypto_their_auth_encrypt_message),
                { theirAuthEncryptMessage() },
                getString(R.string.crypto_their_auth_encrypt_message_end)
            )

            ensureActive()
            runAction(
                getString(R.string.crypto_i_decrypt_message),
                { iDecryptMessage() },
                getString(R.string.crypto_i_decrypt_message_end)
            )

            ensureActive()
            runAction(
                getString(R.string.crypto_close_delete_my_wallet),
                { closeDeleteMyWallet() },
                getString(R.string.crypto_close_delete_my_wallet_end)
            )

            ensureActive()
            runAction(
                getString(R.string.crypto_close_delete_their_wallet),
                { closeDeleteTheirWallet() },
                getString(R.string.crypto_close_delete_their_wallet_end)
            )

            ensureActive()
            runAction(
                getString(R.string.close_pool),
                { closePool() },
                getString(R.string.close_pool_end)
            )

            ensureActive()
            runAction(
                getString(R.string.delete_pool_ledger_config),
                { deletePoolLedgerConfig() },
                getString(R.string.delete_pool_ledger_config_end)
            )

            ensureActive()
            successToast(getString(R.string.success))
            updateFooter(getString(R.string.crypto_sample_completed))
            Log.d(TAG, "startDemo: Crypto sample -> COMPLETED!")
        }
    }

    // region demo steps functions
    private fun createOpenMyWallet() {
        myWallet = createAndOpenWallet("myWallet", "my_wallet_key")
    }

    private fun createOpenTheirWallet() {
        theirWallet = createAndOpenWallet("theirWallet", "their_wallet_key")
    }

    private fun createMyDID() {
        // 4. Create My Did
        val myDid = Did.createAndStoreMyDid(myWallet.wallet, "{}").get()
        myVerkey = myDid.verkey
    }

    private fun createTheirDID() {
        // 5. Create Their Did
        val createTheirDidResult = Did.createAndStoreMyDid(theirWallet.wallet, "{}").get()
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
            Crypto.authCrypt(theirWallet.wallet, theirVerkey, myVerkey, msg.toByteArray()).get()
    }

    private fun iDecryptMessage() {
        // 7. I decrypt message
        val authDecryptResult =
            Crypto.authDecrypt(myWallet.wallet, myVerkey, encryptedMessage).get()

        Assert.assertTrue(Arrays.equals(msg.toByteArray(), authDecryptResult.decryptedMessage))
        Assert.assertEquals(theirVerkey, authDecryptResult.verkey)
    }

    private fun closeDeleteMyWallet() {
        closeAndDeleteWallet(myWallet.wallet, myWallet.walletConfig, myWallet.walletCredentials)
    }

    private fun closeDeleteTheirWallet() {
        closeAndDeleteWallet(
            theirWallet.wallet,
            theirWallet.walletConfig,
            theirWallet.walletCredentials
        )
    }
    // endregion

    private companion object {
        val TAG: String = CryptoActivity::class.java.name
    }
}
