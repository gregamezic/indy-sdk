package org.hyperledger.indy.sdk.utils;

import org.apache.commons.io.FileUtils;

import androidx.test.platform.app.InstrumentationRegistry;

public class EnvironmentUtils {
    static String getTestPoolIP() {
        String testPoolIp = System.getenv("TEST_POOL_IP");
        return testPoolIp != null ? testPoolIp : "127.0.0.1";
    }

    public static String getIndyHomePath() {
        return FileUtils.getUserDirectoryPath() + "/.indy_client/";
    }

    public static String getIndyHomePath(String filename) {
        return getIndyHomePath() + filename;
    }

    public static String getTmpPath() {
        return InstrumentationRegistry.getInstrumentation().getContext().getCacheDir().getAbsolutePath() + "/indy_client/";
        //        return FileUtils.getTempDirectoryPath() + "/indy_client/";
    }

    public static String getTmpPath(String filename) {
        return getTmpPath() + filename;
    }
}
