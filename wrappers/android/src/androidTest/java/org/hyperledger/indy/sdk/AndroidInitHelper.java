package org.hyperledger.indy.sdk;

import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import org.hyperledger.indy.sdk.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.brightinventions.slf4android.LogLevel;
import pl.brightinventions.slf4android.LoggerConfiguration;

import static com.sun.jna.Native.detach;

public class AndroidInitHelper {

    public static void init() throws ErrnoException {
        Logger logger = LoggerFactory.getLogger("org.hyperledger.indy.sdk.LibIndy.native");

        LoggerConfiguration.configuration()
                .setLogLevel("org.hyperledger.indy.sdk.LibIndy.native", LogLevel.TRACE);

        // TODO should this be set as part of init?
        Os.setenv("EXTERNAL_STORAGE", EnvironmentUtils.getIndyHomePath(), true);
        Os.setenv("TMPDIR", EnvironmentUtils.getTmpPath(), true);

        if (!LibIndy.isInitialized()) {
            LibIndy.init();
        }

        // TODO remove
        LibIndy.api.indy_set_logger(null, null, Test.call, null);
    }

    // TODO remove
    static class Test {
        private static Callback call = new Callback() {

            public void callback(Pointer context, int level, String target, String message,
                    String module_path, String file, int line) {
                detach(false);

                Log.v("Indy log", message);
            }
        };
    }

}
