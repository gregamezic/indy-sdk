package org.hyperledger.indy.sdk.ui.ledger

import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidJSONParameters
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.ui.base.BaseActivity
import org.hyperledger.indy.sdk.ui.base.models.WalletData
import org.json.JSONObject
import org.junit.Assert

class LedgerActivity : BaseActivity() {

    // my vars
    private val trusteeSeed = "000000000000000000000000Trustee1"
    private lateinit var myWallet: WalletData
    private lateinit var trusteeWallet: WalletData
    private lateinit var trusteeDid: String
    private lateinit var myDid: String
    private lateinit var myVerkey: String
    private lateinit var nymRequest: String

    /**
     * startDemo function start all functions for Ledger demo chronological in coroutine default thread
     */
    override fun onStartDemo() {
        Log.d(TAG, "startDemo: Ledger sample -> STARTED!")
        updateHeader(getString(R.string.ledger_sample_start))

        job = MainScope().launch {

            // 1. Create and Open Pool
            ensureActive()
            runAction(
                getString(R.string.ledger_create_ledger),
                { createAndOpenPool() },
                getString(R.string.ledger_create_ledger_end)
            )

            // 2. Create and Open My Wallet
            ensureActive()
            runAction(
                getString(R.string.ledger_create_open_my_wallet),
                { createOpenMyWallet() },
                getString(R.string.ledger_create_open_my_wallet_end)
            )

            // 3. Create and Open Trustee Wallet
            ensureActive()
            runAction(
                getString(R.string.ledger_create_open_trustee_wallet),
                { createOpenTrusteeWallet() },
                getString(R.string.ledger_create_open_trustee_wallet_end)
            )

            // 4. Create My Did
            ensureActive()
            runAction(
                getString(R.string.ledger_create_my_did),
                { createMyDID() },
                getString(R.string.ledger_create_my_did_end)
            )

            // 5. Create Did from Trustee1 seed
            ensureActive()
            runAction(
                getString(R.string.ledger_create_did_from_trustee),
                { createDIDFromTrustee1Seed() },
                getString(R.string.ledger_create_did_from_trustee_end)
            )

            // 6. Build Nym Request
            ensureActive()
            runAction(
                getString(R.string.ledger_build_nym_request),
                { buildNymRequest() },
                getString(R.string.ledger_build_nym_request_end)
            )

            // 7. Trustee Sign Nym Request
            ensureActive()
            runAction(
                getString(R.string.ledger_trustee_sign_nym_request),
                { trusteeSignNymRequest() },
                getString(R.string.ledger_trustee_sign_nym_request_end)
            )

            // 8. Close and delete My Wallet
            ensureActive()
            runAction(
                getString(R.string.ledger_close_delete_my_wallet),
                { closeDeleteMyWallet() },
                getString(R.string.ledger_close_delete_my_wallet_end)
            )

            // 9. Close and delete Their Wallet
            ensureActive()
            runAction(
                getString(R.string.ledger_close_delete_their_wallet),
                { closeDeleteTrusteeWallet() },
                getString(R.string.ledger_close_delete_their_wallet_end)
            )

            // 10. Close Pool
            ensureActive()
            runAction(
                getString(R.string.ledger_close_pool),
                { closePool() },
                getString(R.string.ledger_close_pool_end)
            )

            // 11. Delete Pool ledger config
            ensureActive()
            runAction(
                getString(R.string.delete_pool_ledger_config),
                { deletePoolLedgerConfig() },
                getString(R.string.delete_pool_ledger_config_end)
            )

            ensureActive()
            successToast(getString(R.string.success))
            updateFooter(getString(R.string.ledger_sample_completed))
            Log.d(TAG, "startDemo: Ledger sample -> COMPLETED!")
        }
    }

    // region demo steps functions
    private fun createOpenMyWallet() {
        myWallet = createAndOpenWallet("myWallet", "my_wallet_key")
    }

    private fun createOpenTrusteeWallet() {
        trusteeWallet = createAndOpenWallet("trusteeWallet", "trustee_wallet_key")
    }

    private fun createMyDID() {
        val createMyDidResult = Did.createAndStoreMyDid(myWallet.wallet, "{}").get()
        myDid = createMyDidResult.did
        myVerkey = createMyDidResult.verkey
    }

    private fun createDIDFromTrustee1Seed() {
        val theirDidJson =
            DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null)

        val createTheirDidResult =
            Did.createAndStoreMyDid(trusteeWallet.wallet, theirDidJson.toJson()).get()
        trusteeDid = createTheirDidResult.did
    }

    private fun buildNymRequest() {
        nymRequest = Ledger.buildNymRequest(trusteeDid, myDid, myVerkey, null, null).get()
    }

    private fun trusteeSignNymRequest() {
        val nymResponseJson =
            Ledger.signAndSubmitRequest(pool, trusteeWallet.wallet, trusteeDid, nymRequest).get()

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
    }

    private fun closeDeleteMyWallet() {
        closeAndDeleteWallet(myWallet.wallet, myWallet.walletConfig, myWallet.walletCredentials)
    }

    private fun closeDeleteTrusteeWallet() {
        closeAndDeleteWallet(
            trusteeWallet.wallet,
            trusteeWallet.walletConfig,
            trusteeWallet.walletCredentials
        )
    }
    // endregion

    private companion object {
        val TAG: String = LedgerActivity::class.java.name
    }
}
