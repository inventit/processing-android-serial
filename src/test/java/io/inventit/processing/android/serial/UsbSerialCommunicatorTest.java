/*
 * Copyright (C) 2013 InventIt Inc.
 */
package io.inventit.processing.android.serial;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import io.inventit.processing.android.serial.UsbSerialCommunicator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import processing.core.PApplet;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbId;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

/**
 * 
 * @author dbaba@yourinventit.com
 * 
 */
@Config(manifest = "src/test/resources/robolectric/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class UsbSerialCommunicatorTest {

	private UsbSerialCommunicator communicator;

	private final PApplet pApplet = mock(PApplet.class);
	private final Activity activity = mock(Activity.class);
	private final Context context = mock(Context.class);
	private final UsbDevice usbDevice = mock(UsbDevice.class);
	private final UsbManager usbManager = mock(UsbManager.class);
	private final UsbDeviceConnection usbDeviceConnection = mock(UsbDeviceConnection.class);

	@Before
	public void setUp() throws Exception {
		when(pApplet.getActivity()).thenReturn(activity);
		when(activity.getApplicationContext()).thenReturn(context);
		when(context.getSystemService(Context.USB_SERVICE)).thenReturn(
				usbManager);
		communicator = new UsbSerialCommunicator(pApplet);
	}

	/**
	 * Regression Test
	 */
	@Test
	public void test_getDevice_ok1() {
		when(usbDevice.getVendorId()).thenReturn(UsbId.VENDOR_ARDUINO);
		when(usbDevice.getProductId()).thenReturn(UsbId.ARDUINO_LEONARDO);
		when(usbManager.openDevice(usbDevice)).thenReturn(usbDeviceConnection);
		final UsbSerialDriver driver = communicator.getDevice(
				UsbSerialProber.CDC_ACM_SERIAL, usbDevice);
		assertNotNull(driver);
	}

	/**
	 * Arduino Due Test
	 */
	@Test
	public void test_getDevice_ok2() {
		when(usbDevice.getVendorId()).thenReturn(UsbId.VENDOR_ARDUINO);
		// http://www.devtal.de/wiki/Benutzer:Rdiez/ArduinoDue
		// 0x003e for Arduino Due
		when(usbDevice.getProductId()).thenReturn(0x003e);
		when(usbManager.openDevice(usbDevice)).thenReturn(usbDeviceConnection);
		final UsbSerialDriver driver = communicator.getDevice(
				UsbSerialProber.CDC_ACM_SERIAL, usbDevice);
		assertNotNull(driver);
	}

	/**
	 * Ensure not working with other vendor ID than Arduino's.
	 */
	@Test
	public void test_getDevice_null() {
		when(usbDevice.getVendorId()).thenReturn(UsbId.VENDOR_FTDI);
		when(usbManager.openDevice(usbDevice)).thenReturn(usbDeviceConnection);
		final UsbSerialDriver driver = communicator.getDevice(
				UsbSerialProber.CDC_ACM_SERIAL, usbDevice);
		assertNull(driver);
	}
}
