package org.hyperledger.indy.sdk.helpers

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_anoncreds.*
import org.hyperledger.indy.sdk.R

public class MessageHelper {


    companion object {

        // toasts
        fun successToast(context: Context, msg: String) {
            Toasty.success(context, msg, Toast.LENGTH_SHORT).show()
        }
        fun errorToast(context: Context, msg: String) {
            Toasty.error(context, msg, Toast.LENGTH_SHORT).show()
        }


        // update ui
        fun updateUI(context: Context, text: String) {
            val tvText = (context as Activity).findViewById<TextView>(R.id.tvDemoLogs)
            tvText.setTextColor(ContextCompat.getColor(context, R.color.normalColor))
            tvText.text = "${tvText.text}$text"
        }
        fun updateUI(context: Context, success: Boolean, text: String) {

            val tvText = (context as Activity).findViewById<TextView>(R.id.tvDemoLogs)

            when {
                success -> {
                    tvText.setTextColor(ContextCompat.getColor(context, R.color.normalColor))
                }
                else -> {
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
    }

}
