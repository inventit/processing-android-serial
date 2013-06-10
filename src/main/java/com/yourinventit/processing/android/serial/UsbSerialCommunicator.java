/*
 * Copyright (C) 2013 InventIt Inc.
 */
package com.yourinventit.processing.android.serial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.yourinventit.processing.android.serial.SerialInputOutputManager.Listener;

/**
 * Serial class implementation with USB serial.
 * 
 * @author dbaba@yourinventit.com
 * 
 */
class UsbSerialCommunicator extends AbstractAndroidSerialCommunicator implements Listener {

	/**
	 * {@link Logger}
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(UsbSerialCommunicator.class);

	/**
	 * {@link UsbManager}
	 */
	private final UsbManager usbManager;

	/**
	 * {@link UsbSerialDriver}
	 */
	private UsbSerialDriver usbSerialDriver;

	/**
	 * {@link ExecutorService}
	 */
	private final ExecutorService serialInputOutputExecutor = Executors
			.newSingleThreadExecutor();

	/**
	 * {@link SerialInputOutputManager}
	 */
	private SerialInputOutputManager serialInputOutputManager;

	/**
	 * @param parent
	 */
	public UsbSerialCommunicator(PApplet parent) {
		super(parent);
		this.usbManager = (UsbManager) getApplicatoinContext()
				.getSystemService(Context.USB_SERVICE);
	}

	/**
	 * 
	 * @param deviceName
	 * @return
	 */
	protected UsbSerialDriver findUsbSerialDriver(String deviceName) {
		if (deviceName == null || deviceName.length() == 0) {
			return UsbSerialProber.acquire(usbManager);
		}
		for (final UsbSerialProber prober : UsbSerialProber.values()) {
			for (final UsbDevice usbDevice : usbManager.getDeviceList()
					.values()) {
				final UsbSerialDriver driver = prober.getDevice(usbManager,
						usbDevice);
				if (driver != null
						&& deviceName
								.equals(driver.getDevice().getDeviceName())) {
					return driver;
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.processing.android.serial.AbstractAndroidSerialCommunicator#doStart(java.lang.String,
	 *      int, char, int, float)
	 */
	@Override
	protected void doStart(String portIdentifier, int baudrate, char parity,
			int dataBits, float stopBits) {
		// TODO Supporting parity, dataBits and stopBits parameters. v101 doesn't support them.
		LOGGER.info("parity, dataBits and stopBits are not supported yet.");
		if (inquireUsbSerialDriver(portIdentifier, baudrate) == false) {
			// failed to start
			throw new IllegalStateException("Cannot setup USB Serial.");
		}
	}

	/**
	 * Inquires a USB serial driver and returns if any driver is detected.
	 * 
	 * @param deviceName
	 * @return true if a USB serial driver is found and is ready.
	 */
	protected boolean inquireUsbSerialDriver(String deviceName, int baudRate) {
		if (this.usbSerialDriver != null) {
			return true;
		}
		UsbSerialDriver usbSerialDriver = findUsbSerialDriver(deviceName);
		try {
			stopSerialInputOutputManager();
			if (usbSerialDriver != null) {
				usbSerialDriver.open();
				usbSerialDriver.setBaudRate(baudRate);
			}

		} catch (IOException exception) {
			usbSerialDriver = null;

		} finally {
			this.usbSerialDriver = usbSerialDriver;
			startSerialInputOutputManager();
		}
		return usbSerialDriver != null;
	}

	/**
	 * setup {@link SerialInputOutputManager}
	 */
	private void startSerialInputOutputManager() {
		if (usbSerialDriver != null) {
			serialInputOutputManager = new SerialInputOutputManager(
					usbSerialDriver);
			serialInputOutputManager.setListener(this);
			// Serial I/O is performed in another thread.
			serialInputOutputExecutor.submit(serialInputOutputManager);
		}
	}

	/**
	 * {@link SerialInputOutputManager#stop()}
	 */
	private void stopSerialInputOutputManager() {
		if (serialInputOutputManager != null) {
			serialInputOutputManager.stop();
			serialInputOutputManager = null;
		}
	}

	/**
	 * @return the serialInputOutputManager
	 */
	protected SerialInputOutputManager getSerialInputOutputManager() {
		if (serialInputOutputManager == null) {
			throw new IllegalStateException(
					"The serial connection is already closed.");
		}
		return serialInputOutputManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.processing.android.serial.AbstractAndroidSerialCommunicator#doStop()
	 */
	@Override
	protected void doStop() {
		stopSerialInputOutputManager();
		if (usbSerialDriver != null) {
			try {
				usbSerialDriver.close();
			} catch (IOException ignored) {
			} finally {
				usbSerialDriver = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.processing.android.serial.SerialInputOutputManager.Listener#onNewData(byte[])
	 */
	@Override
	public void onNewData(byte[] data) {
		sendBuffer(data);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.processing.android.serial.SerialInputOutputManager.Listener#onRunError(java.lang.Exception)
	 */
	@Override
	public void onRunError(Exception e) {
		LOGGER.warn("Exception detected. Restart SerialInputOutputManager.", e);
		doStop();
		try {
			Thread.sleep(Config.getWaitOnException());
		} catch (InterruptedException ignored) {
		}
		doStart(getPortIdentifier(), getBaudrate(), getParity(), getDataBits(),
				getStopBits());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.processing.android.serial.SerialCommunicator#list()
	 */
	@Override
	public String[] list() {
		final List<String> names = new ArrayList<String>();
		for (final UsbSerialProber prober : UsbSerialProber.values()) {
			for (final UsbDevice usbDevice : usbManager.getDeviceList()
					.values()) {
				final UsbSerialDriver driver = prober.getDevice(usbManager,
						usbDevice);
				if (driver != null) {
					names.add(driver.getDevice().getDeviceName());
				}
			}
		}
		return names.toArray(new String[names.size()]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.processing.android.serial.SerialCommunicator#write(byte[])
	 */
	@Override
	public void write(byte[] what) {
		getSerialInputOutputManager().writeAsync(what);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.processing.android.serial.SerialCommunicator#write(int)
	 */
	@Override
	public void write(int what) {
		write(new byte[] { (byte) what });
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.yourinventit.processing.android.serial.SerialCommunicator#write(java.lang.String)
	 */
	@Override
	public void write(String what) {
		write(what.getBytes());
	}
}
