package org.hyperledger.indy.sdk.utils;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import androidx.test.platform.app.InstrumentationRegistry;


public class StorageUtils {

	private static void cleanDirectory(File path) throws IOException {
		if (path.isDirectory()) {
			FileUtils.cleanDirectory(path);
		}
	}

	public static void cleanupStorage() throws IOException {

		File tmpDir = new File(EnvironmentUtils.getTmpPath());
		File homeDir = new File(EnvironmentUtils.getIndyHomePath());

		StorageUtils.cleanDirectory(tmpDir);
		StorageUtils.cleanDirectory(homeDir);

		Context instrumentationContext = InstrumentationRegistry.getInstrumentation().getContext();
		StorageUtils.cleanDirectory(instrumentationContext.getCacheDir().getAbsoluteFile());
	}

}
