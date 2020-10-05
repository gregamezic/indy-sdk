package org.hyperledger.indy.sdk;

import android.system.ErrnoException;
import android.system.Os;

import java.io.File;

import androidx.test.platform.app.InstrumentationRegistry;

public class AndroidInitHelper {

    public static void init() throws ErrnoException {
        File cacheDir = InstrumentationRegistry.getInstrumentation().getContext().getCacheDir();

        File cache = new File(cacheDir, "cache");
        File tmp = new File(cacheDir, "tmp");

        if (!cache.mkdir()) {
            throw new IllegalArgumentException("Cache dir fail.");
        }

        if (!tmp.mkdir()) {
            throw new IllegalArgumentException("Tmp dir fail.");
        }

        if (!cacheDir.equals("")) {
            Os.setenv("EXTERNAL_STORAGE", cache.getAbsolutePath(), true);
            Os.setenv("TMPDIR", tmp.getAbsolutePath(), true);

            if (!LibIndy.isInitialized()) {
                LibIndy.init();
            }
        } else {
            throw new IllegalArgumentException("External storage path must be provided.");
        }
    }

}
