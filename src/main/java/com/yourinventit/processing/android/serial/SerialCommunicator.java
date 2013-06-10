/*
 * Copyright (C) 2013 InventIt Inc.
 */
package com.yourinventit.processing.android.serial;

/**
 * This interface represents a serial port and encapsulates its implementation.
 * 
 * The declared methods are almost same as <code>Serial</code> class from
 * Processing API.
 * 
 * In order to instantiate this class, you must invoke
 * {@link SerialCommunicatorFacory#create(processing.core.PApplet, String)}.
 * 
 * Use {@link SerialCommunicator#start(String, int, String, String, String, String)} to set
 * a port idenfier, baurdrate, etc.
 * 
 * You can get a list of avaiable port identifiers by {@link SerialCommunicator#list()}.
 * 
 * @author dbaba@yourinventit.com
 * 
 */
public interface SerialCommunicator {

	/**
	 * Starts the serial communication.
	 * 
	 * From <a
	 * href="http://processing.org/reference/libraries/serial/Serial.html"
	 * >Serial</a>
	 * 
	 * @param portIdentifier
	 *            name of the port
	 */
	void start(String portIdentifier);

	/**
	 * Starts the serial communication.
	 * 
	 * From <a
	 * href="http://processing.org/reference/libraries/serial/Serial.html"
	 * >Serial</a>
	 * 
	 * @param portIdentifier
	 *            name of the port
	 * @param baudrate
	 *            9600 is the default
	 */
	void start(String portIdentifier, int baudrate);

	/**
	 * Starts the serial communication.
	 * 
	 * From <a
	 * href="http://processing.org/reference/libraries/serial/Serial.html"
	 * >Serial</a>
	 * 
	 * @param portIdentifier
	 *            name of the port
	 * @param baudrate
	 *            9600 is the default
	 * @param parity
	 *            'N' for none, 'E' for even, 'O' for odd ('N' is the default)
	 * @param dataBits
	 *            8 is the default
	 * @param stopBits
	 *            1.0, 1.5, or 2.0 (1.0 is the default)
	 */
	void start(String portIdentifier, int baudrate, char parity, int dataBits,
			float stopBits);

	/**
	 * Stops data communication on this port. Use to shut the connection when
	 * you're finished with the Serial.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_stop_.html"
	 * >Serial.stop()</a>
	 */
	void stop();

	/**
	 * Sets the number of bytes to buffer before calling serialEvent().
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_buffer_.html"
	 * >Serial.buffer()</a>
	 * 
	 * @param count
	 *            number of bytes to buffer
	 */
	void buffer(int count);

	/**
	 * Sets a specific byte to buffer until before calling serialEvent().
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_bufferUntil_.html"
	 * >Serial.bufferUntil()</a>
	 * 
	 * @param what
	 *            the value to buffer until
	 */
	void bufferUntil(int what);

	/**
	 * Returns the number of bytes available.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_available_.html"
	 * >Serial.available()</a>
	 * 
	 * @return
	 */
	int available();

	/**
	 * Empty the buffer, removes all the data stored there.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_clear_.html"
	 * >Serial.clear()</a>
	 */
	void clear();

	/**
	 * Returns a numeric value between 0 and 255 for the next byte that's
	 * waiting in the buffer.
	 * 
	 * Returns -1 if there is no byte, although this should be avoided by first
	 * checking available() to see if data is available.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_read_.html"
	 * >Serial.read()</a>
	 * 
	 * @return
	 */
	int read();

	/**
	 * Reads a group of bytes from the buffer. The version with no parameters
	 * returns a byte array of all data in the buffer. This is not efficient,
	 * but is easy to use.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_readBytes_.html"
	 * >Serial.readBytes()</a>
	 * 
	 * @return
	 */
	byte[] readBytes();

	/**
	 * The version with the byteBuffer parameter is more memory and time
	 * efficient. It grabs the data in the buffer and puts it into the byte
	 * array passed in and returns an int value for the number of bytes read. If
	 * more bytes are available than can fit into the byteBuffer, only those
	 * that fit are read.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_readBytes_.html"
	 * >Serial.readBytes()</a>
	 * 
	 * @param byteBuffer
	 * @return
	 */
	int readBytes(byte[] byteBuffer);

	/**
	 * Reads from the port into a buffer of bytes up to and including a
	 * particular character. If the character isn't in the buffer, 'null' is
	 * returned. The version with without the byteBuffer parameter returns a
	 * byte array of all data up to and including the interesting byte. This is
	 * not efficient, but is easy to use.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_readBytesUntil_.html"
	 * >Serial.readBytesUntil()</a>
	 * 
	 * @param interesting
	 *            character designated to mark the end of the data
	 * @return
	 */
	byte[] readBytesUntil(int interesting);

	/**
	 * The version with the byteBuffer parameter is more memory and time
	 * efficient. It grabs the data in the buffer and puts it into the byte
	 * array passed in and returns an int value for the number of bytes read. If
	 * the byte buffer is not large enough, -1 is returned and an error is
	 * printed to the message area. If nothing is in the buffer, 0 is returned.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_readBytesUntil_.html"
	 * >Serial.readBytesUntil()</a>
	 * 
	 * @param interesting
	 *            character designated to mark the end of the data
	 * @param byteBuffer
	 *            passed in byte array to be altered
	 * @return
	 */
	int readBytesUntil(int interesting, byte[] byteBuffer);

	/**
	 * Returns all the data from the buffer as a String. This method assumes the
	 * incoming characters are ASCII. If you want to transfer Unicode data,
	 * first convert the String to a byte stream in the representation of your
	 * choice (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_readString_.html"
	 * >Serial.readString()</a>
	 * 
	 * @return
	 */
	String readString();

	/**
	 * Combination of readBytesUntil() and readString(). Returns null if it
	 * doesn't find what you're looking for.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_readStringUntil_.html"
	 * >Serial.readStringUntil()</a>
	 * 
	 * @param interesting
	 *            character designated to mark the end of the data
	 * @return
	 */
	String readStringUntil(int interesting);

	/**
	 * Writes bytes, chars, ints, bytes[], Strings to the serial port
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_write_.html"
	 * >Serial.write()</a>
	 * 
	 * @param what
	 *            data to write
	 */
	void write(int what);

	/**
	 * Writes bytes, chars, ints, bytes[], Strings to the serial port
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_write_.html"
	 * >Serial.write()</a>
	 * 
	 * @param what
	 *            data to write
	 */
	void write(byte[] what);

	/**
	 * Writes bytes, chars, ints, bytes[], Strings to the serial port
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_write_.html"
	 * >Serial.write()</a>
	 * 
	 * @param what
	 *            data to write
	 */
	void write(String what);

	/**
	 * Gets a list of all available serial ports. Use println() to write the
	 * information to the text window.
	 * 
	 * From <a href=
	 * "http://processing.org/reference/libraries/serial/Serial_list_.html"
	 * >Serial.list()</a>
	 * 
	 * @return
	 */
	String[] list();
}
