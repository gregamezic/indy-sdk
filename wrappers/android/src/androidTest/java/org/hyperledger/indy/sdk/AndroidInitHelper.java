package org.hyperledger.indy.sdk;

import android.system.ErrnoException;
import android.system.Os;

import org.hyperledger.indy.sdk.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.brightinventions.slf4android.LogLevel;
import pl.brightinventions.slf4android.LoggerConfiguration;

public class AndroidInitHelper {

    public static void init() throws ErrnoException {
        Logger logger = LoggerFactory.getLogger("org.hyperledger.indy.sdk.LibIndy.native");

        LoggerConfiguration.configuration()
                .setLogLevel("org.hyperledger.indy.sdk.LibIndy.native", LogLevel.TRACE);

//        File walletDir = new File(InstrumentationRegistry.getInstrumentation().getContext().getCacheDir().getAbsolutePath() + "/wallet/");
//        File tmp = new File(EnvironmentUtils.getTmpPath());
//
//        cache.delete();
//        tmp.delete();
//
//        if (!cache.exists()) {
//            if (!cache.mkdirs()) {
//                throw new IllegalArgumentException("Cache dir fail.");
//            }
//        } else if (!cache.isDirectory()) {
//            throw new IllegalArgumentException("Cache not a dir.");
//        }
//
//        if (!tmp.exists()) {
//            if (!tmp.mkdirs()) {
//                throw new IllegalArgumentException("Tmp dir fail.");
//            }
//        } else if (!tmp.isDirectory()) {
//            throw new IllegalArgumentException("Tmp not a dir.");
//        }

//        if (!cacheDir.equals("")) {
            Os.setenv("EXTERNAL_STORAGE", EnvironmentUtils.getIndyHomePath(), true);
            Os.setenv("TMPDIR", EnvironmentUtils.getTmpPath(), true);

            if (!LibIndy.isInitialized()) {
                LibIndy.init();
            }
//        } else {
//            throw new IllegalArgumentException("External storage path must be provided.");
//        }
    }

}
