package org.hyperledger.indy.sdk;

import android.system.ErrnoException;
import android.system.Os;

import androidx.test.platform.app.InstrumentationRegistry;

public class AndroidInitHelper {

    public static void init() throws ErrnoException {
        String cacheDir = InstrumentationRegistry.getInstrumentation().getContext().getCacheDir().getAbsolutePath();

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
