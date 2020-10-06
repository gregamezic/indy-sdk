package org.hyperledger.indy.sdk;

import android.system.ErrnoException;
import android.system.Os;

import org.hyperledger.indy.sdk.utils.EnvironmentUtils;

import java.io.File;

public class AndroidInitHelper {

    public static void init() throws ErrnoException {
        File cache = new File(EnvironmentUtils.getIndyHomePath());
        File tmp = new File(EnvironmentUtils.getTmpPath());

        if (!cache.exists()) {
            if (!cache.mkdirs()) {
                throw new IllegalArgumentException("Cache dir fail.");
            }
        } else if (!cache.isDirectory()) {
            throw new IllegalArgumentException("Cache not a dirr.");
        }

        if (!tmp.exists()) {
            if (!tmp.mkdirs()) {
                throw new IllegalArgumentException("Tmp dir fail.");
            }
        } else if (!tmp.isDirectory()) {
            throw new IllegalArgumentException("Tmp not a dir.");
        }

//        if (!cacheDir.equals("")) {
            Os.setenv("EXTERNAL_STORAGE", cache.getAbsolutePath(), true);
            Os.setenv("TMPDIR", tmp.getAbsolutePath(), true);

            if (!LibIndy.isInitialized()) {
                LibIndy.init();
            }
//        } else {
//            throw new IllegalArgumentException("External storage path must be provided.");
//        }
    }

}
