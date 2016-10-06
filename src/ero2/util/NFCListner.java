package ero2.util;

import ero2.identification.Ero2ProfileManager;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

public class NFCListner implements SerialPortEventListener {
	SerialPort serialPort;
	/** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
																				// OS
																				// X
			"/dev/ttyUSB2", // Linux
			"COM6", // Windows
	};
	/** Buffered input stream from the port */

	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 1000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 38400;

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
					.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		} else {
			System.out.println("Found your Port");
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			// input = serialPort.getInputStream();
			// output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * This Method can be called to print a single byte to the serial connection
	 */
	public void sendSingleByte(byte myByte) {
		try {
			output.write(myByte);
			output.flush();
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This Method is called when Serialdata is received
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			InputStream input = null;
			BufferedReader buffered = null;
			try {
				input = serialPort.getInputStream();
				buffered = new BufferedReader(new InputStreamReader(input,
						"UTF-8"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Event received... ");
			try {
				String nfcid = buffered.readLine();
				//byte[] raw= new byte[18];
				//input.read(raw);
				//String nfcid = new String(raw);
				System.out.println("[NFCListener]: Read from NFC: " + nfcid);
				//if (nfcid.startsWith("NFC") && nfcid.endsWith("END")) {
				if (nfcid!= null) {	
					//nfcid = nfcid.substring(3);
					//nfcid = nfcid.substring(0, nfcid.length() - 3);
					System.out
							.println("[NFCListener]: Activating the profile for:"
									+ nfcid);
					Ero2ProfileManager profileManager = new Ero2ProfileManager();
					profileManager.activateProfile(nfcid);
				}
				input.close();
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

}