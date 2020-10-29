package org.hyperledger.indy.sdk.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_base.view.*
import kotlinx.android.synthetic.main.base_main_content.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.databinding.ActivityBaseBinding
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.ui.base.models.WalletData
import org.hyperledger.indy.sdk.utils.PoolUtils
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject

abstract class BaseActivity : AppCompatActivity() {

    // view bindings
    private lateinit var baseBinding: ActivityBaseBinding
    private lateinit var mainContentView: View
    lateinit var job: Job

    // pool
    lateinit var pool: Pool
    private lateinit var poolName: String

    // on start demo function
    abstract fun onStartDemo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init binder
        baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        mainContentView = baseBinding.coordinatorMain.main_content

        // set content view
        setContentView(baseBinding.root)

        onStartDemo()
    }

    // region show toasts
    fun successToast(msg: String) {
        Snackbar.make(baseBinding.coordinatorMain, msg, Snackbar.LENGTH_INDEFINITE).show()
    }

    private fun errorToast(msg: String) {

        Snackbar.make(baseBinding.coordinatorMain, msg, Snackbar.LENGTH_INDEFINITE).apply {
            // To change background color to red
            view.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.errorColor))
            show()
        }
    }
    //endregion

    //region update ui
    private fun appendLogStart(text: String) {
        mainContentView.tvDemoLogs.append("\n\n\n$text")
    }

    private fun appendLogEnd(text: String) {
        mainContentView.tvDemoLogs.append("\n$text")
    }

    private fun showError() {
        // show error text
        mainContentView.tvDemoLogs.apply {
            setTextColor(ContextCompat.getColor(baseContext, R.color.errorColor))
        }

        // hide progress bar
        mainContentView.pbDemo.visibility = View.INVISIBLE

        // show error toast
        errorToast(getString(R.string.error))
    }

    // update header/footer
    fun updateHeader(text: String) {
        mainContentView.tvDemoStart.text = text
        mainContentView.pbDemo.visibility = View.VISIBLE
    }

    fun updateFooter(text: String) {
        mainContentView.tvDemoEnd.text = text
        mainContentView.pbDemo.visibility = View.INVISIBLE
    }
    // endregion

    //region run action
    suspend fun runAction(
        startText: String,
        action: () -> Unit,
        endText: String
    ): Boolean {
        // start text
        appendLogStart(startText)

        // do action in coroutine background
        try {
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                action()
            }
        } catch (e: Exception) {
            // show error and cancel job
            showError()
            job.cancel()
            return false
        }

        // end text
        appendLogEnd(endText)
        return true
    }
    //endregion

    // region pool helper
    fun createAndOpenPool() {
        // Set protocol version 2 to work with Indy Node 1.4
        Pool.setProtocolVersion(PoolUtils.PROTOCOL_VERSION).get()

        poolName = PoolUtils.createPoolLedgerConfig(baseContext)
        pool = Pool.openPoolLedger(poolName, "{}").get()
    }

    fun closePool() {
        pool.closePoolLedger().get()
    }

    fun deletePoolLedgerConfig() {
        Pool.deletePoolLedgerConfig(poolName).get()
    }
    // endregion

    // region wallet helper
    fun createAndOpenWallet(walletId: String, walletKey: String): WalletData {
        // Issuer Create and Open Wallet
        val walletConfig = JSONObject().put("id", walletId).toString()
        val walletCredentials = JSONObject().put("key", walletKey).toString()
        Wallet.createWallet(walletConfig, walletCredentials).get()
        val wallet = Wallet.openWallet(walletConfig, walletCredentials).get()
        return WalletData(wallet, walletConfig, walletCredentials)
    }

    fun closeAndDeleteWallet(wallet: Wallet, walletId: String, walletKey: String) {
        wallet.closeWallet().get()
        Wallet.deleteWallet(walletId, walletKey).get()
    }
    // endregion
}
