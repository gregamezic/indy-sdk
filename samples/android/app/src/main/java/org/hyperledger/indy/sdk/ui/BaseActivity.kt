package org.hyperledger.indy.sdk.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_base.view.*
import kotlinx.android.synthetic.main.base_main_content.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hyperledger.indy.sdk.R
import org.hyperledger.indy.sdk.databinding.ActivityBaseBinding
import org.hyperledger.indy.sdk.databinding.BaseMainContentBinding
import org.hyperledger.indy.sdk.listeners.ActionFailListener
import java.lang.Exception

open class BaseActivity : AppCompatActivity() {

    // view bindings
    private lateinit var baseBinding: ActivityBaseBinding
    private lateinit var mainContentView: View

    // listener
    lateinit var onFailListener: ActionFailListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init binder
        baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        mainContentView = baseBinding.coordinatorMain.main_content

        // set content view
        setContentView(baseBinding.root)
    }


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
    private fun updateUI(text: String) {
        mainContentView.tvDemoLogs.append(text)
    }

    private fun showError(text: String) {

        // show error text
        mainContentView.tvDemoLogs.apply {
            setTextColor(ContextCompat.getColor(baseContext, R.color.errorColor))
            // TODO: 28/10/2020 should this be here??
            //append(text)
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
        updateUI(startText)

        // do action in coroutine background
        try {
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                action()
            }
        } catch (e: Exception) {
            showError(endText)
            onFailListener.onFail()
            return false
        }

        // end text
        updateUI(endText)
        return true
    }
    //endregion
}
