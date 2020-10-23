package org.hyperledger.indy.sdk.helpers

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty

public class MessageHelper {


    companion object {

        public fun successToast(context: Context, msg: String) {
            Toasty.success(context, msg, Toast.LENGTH_SHORT).show()
        }

        public fun errorToast(context: Context, msg: String) {
            Toasty.error(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

}