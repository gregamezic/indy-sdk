package org.hyperledger.indy.sdk.utils;

import android.content.Context;

import org.apache.commons.io.FileUtils;


public class EnvironmentUtils {


	static String getTestPoolIP() {
		String testPoolIp = System.getenv("TEST_POOL_IP");
		return testPoolIp != null ? testPoolIp : "127.0.0.1";
	}

	public static String getIndyHomePath(Context context) {
		return  context.getFilesDir().getAbsolutePath() + "/indy_client/";
		//return FileUtils.getUserDirectoryPath() + "/.indy_client/";
	}

	public static String getIndyHomePath(String filename, Context context) {
		return getIndyHomePath(context) + filename;
	}

	public static String getTmpPath(Context context) {
		return context.getCacheDir().getAbsolutePath() + "/tmp/";
		//return FileUtils.getTempDirectoryPath() + "/indy/";
	}

	public static String getTmpPath(String filename, Context context) {
		return getTmpPath(context) + filename;
	}
}
