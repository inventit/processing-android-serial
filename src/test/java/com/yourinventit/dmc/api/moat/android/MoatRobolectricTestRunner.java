/*
 * Copyright (C) 2013 InventIt Inc.
 */
package com.yourinventit.dmc.api.moat.android;

import java.io.File;

import org.junit.runners.model.InitializationError;

import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;

/**
 * 
 * @author dbaba@yourinventit.com
 * 
 */
public class MoatRobolectricTestRunner extends RobolectricTestRunner {

	private static final File PROJECT_FILE;

	static {
		System.out.println(		System.getenv().get( "ANDROID_HOME" ));
		PROJECT_FILE = new File(
				"src/test/resources/robolectric/AndroidManifest.xml");
	}

	/**
	 * @param testClass
	 * @throws InitializationError
	 */
	public MoatRobolectricTestRunner(Class<?> testClass)
			throws InitializationError {
		super(testClass, new RobolectricConfig(PROJECT_FILE.getParentFile()));
	}

}