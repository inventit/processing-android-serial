/*
 * Copyright (C) 2013 InventIt Inc.
 */
package io.inventit.processing.android.serial;

import processing.core.PApplet;

/**
 * This class is used for retaining the compatibility with
 * processing.serial.Serial class.
 * 
 * The difference between processing.serial.Serial is the static method
 * `list()`, where this class takes a {@link PApplet} instance as a parameter
 * whereas processing.serial.Serial doesn't.
 * 
 * @author dbaba@yourinventit.com
 * 
 */
public class Serial {

	private static final String DEFAULT_TYPE = "usb";

	/**
	 * The delegate object
	 */
	private final SerialCommunicator delegate;

	/**
	 * Non public constructor
	 */
	protected Serial() {
		this.delegate = null;
	}

	/**
	 * 
	 * @param type
	 * @param parent
	 */
	public Serial(String type, PApplet parent) {
		this.delegate = SerialCommunicatorFacory.getInstance().create(parent,
				type);
	}

	/**
	 * 
	 * @param parent
	 */
	public Serial(PApplet parent) {
		this(DEFAULT_TYPE, parent);
		this.delegate.start(null);
	}

	/**
	 * 
	 * @param parent
	 * @param portIdentifier
	 */
	public Serial(PApplet parent, String portIdentifier) {
		this(DEFAULT_TYPE, parent);
		this.delegate.start(portIdentifier);
	}

	/**
	 * 
	 * @param parent
	 * @param portIdentifier
	 * @param baudrate
	 */
	public Serial(PApplet parent, String portIdentifier, int baudrate) {
		this(DEFAULT_TYPE, parent);
		this.delegate.start(portIdentifier, baudrate);
	}

	/**
	 * 
	 * @param parent
	 * @param portIdentifier
	 * @param baudrate
	 * @param parity
	 * @param dataBits
	 * @param stopBits
	 */
	public Serial(PApplet parent, String portIdentifier, int baudrate,
			char parity, int dataBits, float stopBits) {
		this(DEFAULT_TYPE, parent);
		this.delegate.start(portIdentifier, baudrate, parity, dataBits,
				stopBits);
	}

	/**
	 * @param portIdentifier
	 * @see SerialCommunicator#start(java.lang.String)
	 */
	public void start(String portIdentifier) {
		delegate.start(portIdentifier);
	}

	/**
	 * @param portIdentifier
	 * @param baudrate
	 * @see SerialCommunicator#start(java.lang.String,
	 *      int)
	 */
	public void start(String portIdentifier, int baudrate) {
		delegate.start(portIdentifier, baudrate);
	}

	/**
	 * @param portIdentifier
	 * @param baudrate
	 * @param parity
	 * @param dataBits
	 * @param stopBits
	 * @see SerialCommunicator#start(java.lang.String,
	 *      int, char, int, float)
	 */
	public void start(String portIdentifier, int baudrate, char parity,
			int dataBits, float stopBits) {
		delegate.start(portIdentifier, baudrate, parity, dataBits, stopBits);
	}

	/**
	 * 
	 * @see SerialCommunicator#stop()
	 */
	public void stop() {
		delegate.stop();
	}

	/**
	 * @param count
	 * @see SerialCommunicator#buffer(int)
	 */
	public void buffer(int count) {
		delegate.buffer(count);
	}

	/**
	 * @param what
	 * @see SerialCommunicator#bufferUntil(int)
	 */
	public void bufferUntil(int what) {
		delegate.bufferUntil(what);
	}

	/**
	 * @return
	 * @see SerialCommunicator#available()
	 */
	public int available() {
		return delegate.available();
	}

	/**
	 * 
	 * @see SerialCommunicator#clear()
	 */
	public void clear() {
		delegate.clear();
	}

	/**
	 * @return
	 * @see SerialCommunicator#read()
	 */
	public int read() {
		return delegate.read();
	}

	/**
	 * @return
	 * @see SerialCommunicator#readBytes()
	 */
	public byte[] readBytes() {
		return delegate.readBytes();
	}

	/**
	 * @param byteBuffer
	 * @return
	 * @see SerialCommunicator#readBytes(byte[])
	 */
	public int readBytes(byte[] byteBuffer) {
		return delegate.readBytes(byteBuffer);
	}

	/**
	 * @param interesting
	 * @return
	 * @see SerialCommunicator#readBytesUntil(int)
	 */
	public byte[] readBytesUntil(int interesting) {
		return delegate.readBytesUntil(interesting);
	}

	/**
	 * @param interesting
	 * @param byteBuffer
	 * @return
	 * @see SerialCommunicator#readBytesUntil(int,
	 *      byte[])
	 */
	public int readBytesUntil(int interesting, byte[] byteBuffer) {
		return delegate.readBytesUntil(interesting, byteBuffer);
	}

	/**
	 * @return
	 * @see SerialCommunicator#readString()
	 */
	public String readString() {
		return delegate.readString();
	}

	/**
	 * @param interesting
	 * @return
	 * @see SerialCommunicator#readStringUntil(int)
	 */
	public String readStringUntil(int interesting) {
		return delegate.readStringUntil(interesting);
	}

	/**
	 * @param what
	 * @see SerialCommunicator#write(int)
	 */
	public void write(int what) {
		delegate.write(what);
	}

	/**
	 * @param what
	 * @see SerialCommunicator#write(byte[])
	 */
	public void write(byte[] what) {
		delegate.write(what);
	}

	/**
	 * @param what
	 * @see SerialCommunicator#write(java.lang.String)
	 */
	public void write(String what) {
		delegate.write(what);
	}

	/**
	 * @return
	 * @see SerialCommunicator#list()
	 */
	public String[] list() {
		return delegate.list();
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static String[] list(PApplet parent) {
		final Serial serial = new Serial(parent);
		try {
			return serial.list();
		} finally {
			serial.stop();
		}
	}

}
