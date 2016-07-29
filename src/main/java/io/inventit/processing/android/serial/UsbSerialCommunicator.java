/*
 * Copyright (C) 2013 InventIt Inc.
 */
package io.inventit.processing.android.serial;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import io.inventit.processing.android.serial.SerialInputOutputManager.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Serial class implementation with USB serial.
 * 
 * @author dbaba@yourinventit.com
 * 
 */
class UsbSerialCommunicator extends AbstractAndroidSerialCommunicator implements
		Listener {

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
	 * {@link UsbSerialPort}
	 */
	private UsbSerialPort usbSerialDriver;

	/**
	 * {@link UsbDeviceConnection}
     */
	private UsbDeviceConnection usbDeviceConnection;

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
			final List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
			if (drivers == null || drivers.isEmpty()) {
				return null;
			}
			return drivers.get(0);
		}
		for (final UsbDevice usbDevice : usbManager.getDeviceList()
				.values()) {
			final UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
			if (driver != null
					&& deviceName
							.equals(driver.getDevice().getDeviceName())) {
				return driver;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see AbstractAndroidSerialCommunicator#doStart(java.lang.String,
	 *      int, char, int, float)
	 */
	@Override
	protected void doStart(final String portIdentifier, int baudrate, char parity,
			int dataBits, float stopBits) {
		// TODO Supporting parity, dataBits and stopBits parameters. v101 doesn't support them.
		LOGGER.info("parity, dataBits and stopBits are not supported yet.");
		if (inquireUsbSerialDriver(portIdentifier, baudrate) == false) {
			// failed to start
            LOGGER.error("Failed to start as this app failed to retrieve a serial driver: port={}", portIdentifier);
            final boolean[] waiting = { true };
            getParent().getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(getParent().getActivity())
                            .setTitle("Processing-Android USB Serial ERROR")
                            .setMessage("Port: " + portIdentifier + "\n" +
                                    "Check the following items:\n" +
                                    "1. Make sure AndroidManifest.xml contains <use-feature> tag for android.hardware.usb.host\n" +
                                    "2. -----------\n")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    waiting[0] = false;
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
            while (waiting[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            getParent().getActivity().finish();
		}
	}

	/**
	 * Inquires a USB serial driver and returns if any driver is detected.
	 * 
	 * @param deviceName null for wildcard
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
				this.usbDeviceConnection = this.usbManager.openDevice(usbSerialDriver.getDevice());
				final UsbSerialPort port = usbSerialDriver.getPorts().get(0);
				port.open(this.usbDeviceConnection);
				port.setParameters(baudRate, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
			}

		} catch (IOException exception) {
			usbSerialDriver = null;

		} finally {
			this.usbSerialDriver = usbSerialDriver.getPorts().get(0);
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
	 * @see AbstractAndroidSerialCommunicator#doStop()
	 */
	@Override
	protected void doStop() {
		stopSerialInputOutputManager();
		if (usbSerialDriver != null) {
			try {
				usbDeviceConnection.close();
			} finally {
				usbSerialDriver = null;
				usbDeviceConnection = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialInputOutputManager.Listener#onNewData(byte[])
	 */
	public void onNewData(byte[] data) {
		sendBuffer(data);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialInputOutputManager.Listener#onRunError(java.lang.Exception)
	 */
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
	 * @see SerialCommunicator#list()
	 */
	@Override
	public String[] list() {
		final List<String> names = new ArrayList<String>();
		for (final UsbDevice usbDevice : usbManager.getDeviceList()
				.values()) {
			final UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
			if (driver != null) {
				names.add(driver.getDevice().getDeviceName());
			}
		}
		return names.toArray(new String[names.size()]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#write(byte[])
	 */
	@Override
	public void write(byte[] what) {
		getSerialInputOutputManager().writeAsync(what);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#write(int)
	 */
	@Override
	public void write(int what) {
		write(new byte[] { (byte) what });
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#write(java.lang.String)
	 */
	@Override
	public void write(String what) {
		write(what.getBytes());
	}
}
