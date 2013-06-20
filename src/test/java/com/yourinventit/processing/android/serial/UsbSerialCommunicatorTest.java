/*
 * Copyright (C) 2013 InventIt Inc.
 */
package com.yourinventit.processing.android.serial;

import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import processing.core.PApplet;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbId;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.yourinventit.dmc.api.moat.android.MoatRobolectricTestRunner;

/**
 * 
 * @author dbaba@yourinventit.com
 * 
 */
@RunWith(MoatRobolectricTestRunner.class)
public class UsbSerialCommunicatorTest {

	private UsbSerialCommunicator communicator;

	private final PApplet pApplet = mock(PApplet.class);
	private final UsbDevice usbDevice = mock(UsbDevice.class);
	private final UsbManager usbManager = mock(UsbManager.class);
	private final UsbDeviceConnection usbDeviceConnection = mock(UsbDeviceConnection.class);

	@Before
	public void setUp() throws Exception {
		when(pApplet.getApplicationContext()).thenReturn(pApplet);
		when(pApplet.getSystemService(Context.USB_SERVICE)).thenReturn(
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
