/*
 * Copyright (C) 2013 InventIt Inc.
 */
package com.yourinventit.processing.android.serial;

import processing.core.PApplet;
import processing.data.JSONObject;
import android.os.Build;

/**
 *
 * @author dbaba@yourinventit.com
 *
 */
public final class SerialCommunicatorFacory {

	/**
	 * Singleton Instance
	 */
	private static final SerialCommunicatorFacory INSTANCE = new SerialCommunicatorFacory();

	static {
		verifyVersion();
	}

	/**
	 * @return the instance
	 */
	public static SerialCommunicatorFacory getInstance() {
		return INSTANCE;
	}

	/**
	 * Suppress instantiation.
	 */
	private SerialCommunicatorFacory() {
	}

	/**
	 * Configures the serial library.
	 *
	 * @param json
	 */
	public void configure(String json) {
		configure(JSONObject.parse(json));
	}

	/**
	 * Configures the serial library.
	 *
	 * @param jsonObject
	 */
	public void configure(JSONObject jsonObject) {
		Config.setDebugEnabled(jsonObject.getBoolean("debug"));
	}

	/**
	 * Returns a {@link SerialCommunicator} instance specified by the type.
	 *
	 * @param parent
	 * @param type
	 * @return
	 */
	public SerialCommunicator create(PApplet parent, String type) {
		if ("usb".equalsIgnoreCase(type)) {
			return new UsbSerialCommunicator(parent);
		} else {
			throw new UnsupportedOperationException(type + " is not supported.");
		}
	}

	/**
	 * Unfortunately, this library uses USB Host API set introduced since
	 * Android 3.1.
	 */
	static void verifyVersion() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
			throw new UnsupportedOperationException(
					"Your device doesn't support USB Host API. Use Android 3.1 or later.");
		}
	}
}
