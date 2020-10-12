package org.hyperledger.indy.sdk;

import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import org.apache.commons.io.FileUtils;
import org.hyperledger.indy.sdk.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import pl.brightinventions.slf4android.LogLevel;
import pl.brightinventions.slf4android.LoggerConfiguration;

import static com.sun.jna.Native.detach;

public class AndroidInitHelper {

    public static void init() throws ErrnoException {
        Logger logger = LoggerFactory.getLogger("org.hyperledger.indy.sdk.LibIndy.native");

        LoggerConfiguration.configuration()
                .setLogLevel("org.hyperledger.indy.sdk.LibIndy.native", LogLevel.TRACE);

//        File walletDir = new File(InstrumentationRegistry.getInstrumentation().getContext().getCacheDir().getAbsolutePath() + "/wallet/");
        File tmp = new File(EnvironmentUtils.getTmpPath());
//
//        cache.delete();
        tmp.delete();
        try {
            FileUtils.forceMkdirParent(tmp);
        } catch (IOException e) {
            throw new IllegalArgumentException("Tmp dir fail.");
        }

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

            LibIndy.api.indy_set_logger(null, null, Test.call, null);
//            LibIndy.api.indy_set_log_max_lvl(5);
//        } else {
//            throw new IllegalArgumentException("External storage path must be provided.");
//        }
    }

    static class Test {
        private static Callback call = new Callback() {

            public void callback(Pointer context, int level, String target, String message, String module_path, String file, int line) {
                detach(false);

                Log.v("Indy log", message);
            }
        };
    }

}
