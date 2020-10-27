package org.hyperledger.indy.sdk.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hyperledger.indy.sdk.R
import java.lang.Exception

open class BaseActivity: AppCompatActivity() {

    lateinit var coordinatorView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demos_activity)
        coordinatorView = findViewById(R.id.coordinator_main)

    }

    // toasts
    fun successToast(activity: Activity, msg: String) {
        val snackbar = Snackbar.make(coordinatorView, msg, Snackbar.LENGTH_INDEFINITE)
        snackbar.show()
    }
    private fun errorToast(activity: Activity, msg: String) {
        val snackbar = Snackbar.make(coordinatorView, msg, Snackbar.LENGTH_INDEFINITE)

        // To change background color to red
        snackbar.view.setBackgroundColor(ContextCompat.getColor(activity, R.color.errorColor))
        snackbar.show()
    }


    // update ui
    private fun updateUI(context: Context, text: String) {
        val tvText = (context as Activity).findViewById<TextView>(R.id.tvDemoLogs)
        tvText.text = "${tvText.text}$text"
    }

    private fun updateUI(context: Context, success: Boolean, text: String) {

        val tvText = (context as Activity).findViewById<TextView>(R.id.tvDemoLogs)

        when {
            !success -> {
                tvText.setTextColor(ContextCompat.getColor(context, R.color.errorColor))
                val pbDemo = context.findViewById<ProgressBar>(R.id.pbDemo)
                pbDemo.visibility = View.INVISIBLE
                errorToast(context, context.getString(R.string.error))
            }
        }

        tvText.text = "${tvText.text}${text}"
    }


    // update header/footer
    fun updateHeader(context: Context, text: String) {
        (context as Activity)
        val tvText = context.findViewById<TextView>(R.id.tvDemoStart)
        val pbDemo = context.findViewById<ProgressBar>(R.id.pbDemo)

        tvText.text = text
        pbDemo.visibility = View.VISIBLE
    }

    fun updateFooter(context: Context, text: String) {
        (context as Activity)
        val tvText = context.findViewById<TextView>(R.id.tvDemoEnd)
        val pbDemo = context.findViewById<ProgressBar>(R.id.pbDemo)

        tvText.text = text
        pbDemo.visibility = View.INVISIBLE
    }


    // run action
    suspend fun runAction(
        activity: AppCompatActivity,
        startText: String,
        action: () -> Unit,
        endText: String
    ): Boolean {

        // start text
        updateUI(activity, startText)

        // do action in coroutine background
        try {
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                action()
            }
        } catch (e: Exception) {
            updateUI(activity, false, endText)
            return false
        }
        updateUI(activity, endText)
        return true

    }
}
