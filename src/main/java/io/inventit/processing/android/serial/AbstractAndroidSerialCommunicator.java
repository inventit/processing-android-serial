/*
 * Copyright (C) 2013 InventIt Inc.
 */
package io.inventit.processing.android.serial;

import android.content.Context;
import com.hoho.android.usbserial.util.HexDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author dbaba@yourinventit.com
 * 
 */
abstract class AbstractAndroidSerialCommunicator extends Serial implements
		SerialCommunicator {

	/**
	 * {@link Logger}
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractAndroidSerialCommunicator.class);

	/**
	 * {@link PApplet}
	 */
	private final PApplet parent;

	/**
	 * {@link Context}
	 */
	private final Context applicatoinContext;

	/**
	 * "serialEvent(Serial)" method in the parent.
	 */
	private final Method serialEventMethod;

	/**
	 * {@link InputStream}
	 */
	private InputStream readBufferInputStream;

	/**
	 * {@link PipedOutputStream}
	 */
	private PipedOutputStream readBufferOutputStream;

	/**
	 * Buffer
	 */
	private byte[] buffer = null;

	/**
	 * Current buffer size
	 */
	private int currentBufferCount = 0;

	/**
	 * Buffering byte data delimiter
	 */
	private int bufferDelimieter = Integer.MIN_VALUE;

	private String portIdentifier;

	private int baudrate;

	private char parity;

	private int dataBits;

	private float stopBits;

	private int last;

	/**
	 * 
	 * @param parent
	 */
	public AbstractAndroidSerialCommunicator(PApplet parent) {
		this.parent = parent;
		this.applicatoinContext = parent.getActivity().getApplicationContext();
		this.serialEventMethod = resolveSerialEventMethod(parent);
	}

	/**
	 * Finds "serialEvent(Serial)" method from the parent.
	 * 
	 * @param parent
	 * @return
	 */
	static Method resolveSerialEventMethod(PApplet parent) {
		for (Method method : parent.getClass().getMethods()) {
			if ("serialEvent".equals(method.getName())) {
				final Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes != null
						&& paramTypes.length == 1
						&& (SerialCommunicator.class
								.isAssignableFrom(paramTypes[0]) || Serial.class
								.isAssignableFrom(paramTypes[0]))) {
					return method;
				}
			}
		}
		LOGGER.info("serialEvent(Serial) is missing in the parent.");
		return null;
	}

	/**
	 * @return the serialEventMethod
	 */
	private Method getSerialEventMethod() {
		return serialEventMethod;
	}

	/**
	 * Invoked when a serial event occurs from the subclass.
	 */
	protected void serialEvent() {
		if (getSerialEventMethod() == null) {
			return;
		}
		try {
			getSerialEventMethod().invoke(parent, this);
		} catch (IllegalAccessException unexpected) {
			throw new IllegalStateException(unexpected);
		} catch (InvocationTargetException unexpected) {
			throw new IllegalStateException(unexpected);
		}
	}

	/**
	 * @return the parent
	 */
	protected PApplet getParent() {
		return parent;
	}

	/**
	 * @return the applicatoinContext
	 */
	protected Context getApplicatoinContext() {
		return applicatoinContext;
	}

	/**
	 * @return the bufferDelimieter
	 */
	protected int getBufferDelimieter() {
		return bufferDelimieter;
	}

	/**
	 * @return the portIdentifier
	 */
	protected String getPortIdentifier() {
		return portIdentifier;
	}

	/**
	 * @return the baudrate
	 */
	protected int getBaudrate() {
		return baudrate;
	}

	/**
	 * @return the parity
	 */
	protected char getParity() {
		return parity;
	}

	/**
	 * @return the dataBits
	 */
	protected int getDataBits() {
		return dataBits;
	}

	/**
	 * @return the stopBits
	 */
	protected float getStopBits() {
		return stopBits;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#buffer(int)
	 */
	@Override
	public void buffer(int count) {
		if (count < 1) {
			this.buffer = null;
		} else {
			this.buffer = new byte[count];
		}
		this.currentBufferCount = 0;
		this.bufferDelimieter = Integer.MIN_VALUE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#bufferUntil(int)
	 */
	@Override
	public void bufferUntil(int what) {
		this.bufferDelimieter = what;
		this.buffer = null;
		this.currentBufferCount = 0;
	}

	/**
	 * Sends the data into the buffer.
	 * 
	 * @param data
	 */
	protected void sendBuffer(byte[] data) {
		if (this.buffer != null) {
			int dataLength = data.length;
			int offset = this.currentBufferCount;
			while (true) {
				final int bufferLength = this.buffer.length - offset;
				if (dataLength < bufferLength) {
					System.arraycopy(data, 0, this.buffer, offset, dataLength);
					offset += dataLength;
					break;

				} else {
					System.arraycopy(data, 0, this.buffer, offset, bufferLength);
					doSendBuffer(this.buffer.clone());
					offset = 0;
					if (dataLength == bufferLength) {
						break;
					}
				}
			}
			this.currentBufferCount = offset;

		} else if (this.bufferDelimieter > 0) {
			final int delim = this.bufferDelimieter;
			try {
				for (int i = 0; i < data.length; i++) {
					this.readBufferOutputStream.write(data[i]);
					if (data[i] == delim) {
						serialEvent();
					}
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

		} else {
			doSendBuffer(data);
		}
	}

	/**
	 * 
	 * @param data
	 */
	protected void doSendBuffer(byte[] data) {
		if (readBufferOutputStream == null) {
			LOGGER.info("[doSendBuffer] Skipped data =>{}",
					HexDump.dumpHexString(data));
			return;
		}
		try {
			this.readBufferOutputStream.write(data);
			serialEvent();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#start(java.lang.String)
	 */
	@Override
	public void start(String portIdentifier) {
		start(portIdentifier, 9600);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#start(java.lang.String,
	 *      int)
	 */
	@Override
	public void start(String portIdentifier, int baudrate) {
		start(portIdentifier, baudrate, 'N', 8, 1.0f);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#start(java.lang.String,
	 *      int, char, int, float)
	 */
	@Override
	public final void start(String portIdentifier, int baudrate, char parity,
			int dataBits, float stopBits) {
		final PipedInputStream pipedInputStream = new PipedInputStream();
		this.readBufferInputStream = pipedInputStream;
		try {
			this.readBufferOutputStream = new PipedOutputStream(
					pipedInputStream);
		} catch (IOException exception) {
			throw new IllegalStateException(exception);
		}
		this.portIdentifier = portIdentifier;
		this.baudrate = baudrate;
		this.parity = parity;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		doStart(portIdentifier, baudrate, parity, dataBits, stopBits);
	}

	/**
	 * 
	 * @param portIdentifier
	 * @param baudrate
	 * @param parity
	 * @param dataBits
	 * @param stopBits
	 */
	protected abstract void doStart(String portIdentifier, int baudrate,
			char parity, int dataBits, float stopBits);

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#stop()
	 */
	@Override
	public final void stop() {
		try {
			doStop();
		} finally {
			if (readBufferOutputStream != null) {
				try {
					readBufferOutputStream.close();
				} catch (IOException ignored) {
				}
				readBufferOutputStream = null;
			}
			if (readBufferInputStream != null) {
				try {
					readBufferInputStream.close();
				} catch (IOException ignored) {
				}
				readBufferOutputStream = null;
			}
		}
	}

	/**
	 * 
	 */
	protected abstract void doStop();

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#available()
	 */
	@Override
	public int available() {
		try {
			return this.readBufferInputStream.available();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#clear()
	 */
	@Override
	public synchronized void clear() {
		try {
			this.readBufferOutputStream.flush();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		readBytes();
		if (this.buffer != null) {
			this.currentBufferCount = 0;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#read()
	 */
	@Override
	public synchronized int read() {
		try {
			final int b = this.readBufferInputStream.read();
			this.last = b;
			return b;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public char readChar() {
		return (char) this.read();
	}

	public int last() {
		return this.last;
	}

	public char lastChar() {
		return (char) this.last;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#readBytes()
	 */
	@Override
	public synchronized byte[] readBytes() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		int readByte = -1;
		final byte[] buff = new byte[1024];
		try {
			if (this.readBufferInputStream.available() > 0) {
				while ((readByte = this.readBufferInputStream.read(buff)) >= 0) {
					out.write(buff, 0, readByte);
					if (this.readBufferInputStream.available() < 1) {
						break;
					}
				}
				return out.toByteArray();
			} else {
				return null;
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#readBytes(byte[])
	 */
	@Override
	public synchronized int readBytes(byte[] byteBuffer) {
		try {
			return this.readBufferInputStream.read(byteBuffer);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#readBytesUntil(int)
	 */
	@Override
	public synchronized byte[] readBytesUntil(int interesting) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		int readByte = -1;
		if (available() > 0) {
			while ((readByte = read()) >= 0) {
				out.write(readByte);
				if (readByte == interesting) {
					return out.toByteArray();
				} else if (available() < 1) {
					break;
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#readBytesUntil(int,
	 *      byte[])
	 */
	@Override
	public synchronized int readBytesUntil(int interesting, byte[] byteBuffer) {
		int readByte = -1;
		for (int i = 0; i < byteBuffer.length; i++) {
			readByte = read();
			if (readByte < 0) {
				break;
			}
			byteBuffer[i] = (byte) readByte;
			if (readByte == interesting) {
				return i + 1;
			}
		}
		LOGGER.error("Insufficient byteBuffer size ({}).", byteBuffer.length);
		return -1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#readString()
	 */
	@Override
	public String readString() {
		return new String(readBytes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SerialCommunicator#readStringUntil(int)
	 */
	@Override
	public String readStringUntil(int interesting) {
		final byte[] readBytes = readBytesUntil(interesting);
		if (readBytes != null) {
			return new String(readBytes);
		}
		return null;
	}
}
