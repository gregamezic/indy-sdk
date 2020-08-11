package org.hyperledger.indy.sdk;

import android.content.Context;
import android.system.ErrnoException;
import android.system.Os;

public class AndroidInitHelper {

    public static void init(Context context) throws ErrnoException {
        String cacheDir = context.getCacheDir().getAbsolutePath();

        if (!cacheDir.equals("")) {
            Os.setenv("EXTERNAL_STORAGE", cacheDir, true);

            if (!LibIndy.isInitialized()) {
                LibIndy.init();
            }
        } else {
            throw new IllegalArgumentException("External storage path must be provided.");
        }
    }

}
