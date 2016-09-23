/*
 * Copyright (C) 2013 InventIt Inc.
 */
package io.inventit.processing.android.serial;

/**
 * 
 * @author dbaba@yourinventit.com
 * 
 */
final class Config {

	private static boolean debugEnabled = false;

	private static long waitOnException = 5000;

	/**
	 * @return the debugEnabled
	 */
	public static boolean isDebugEnabled() {
		return debugEnabled;
	}

	/**
	 * @param debugEnabled
	 *            the debugEnabled to set
	 */
	public static void setDebugEnabled(boolean debugEnabled) {
		Config.debugEnabled = debugEnabled;
	}

	/**
	 * @return the waitOnException
	 */
	public static long getWaitOnException() {
		return waitOnException;
	}

	/**
	 * @param waitOnException
	 *            the waitOnException to set
	 */
	public static void setWaitOnException(long waitOnException) {
		Config.waitOnException = waitOnException;
	}

	private Config() {
	}

}
