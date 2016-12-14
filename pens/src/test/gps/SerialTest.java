package test.gps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;


public class SerialTest implements SerialPortEventListener {
SerialPort serialPort;

    /** The port we're normally going to use. */
private static final String PORT_NAMES[] = {"/dev/tty.usbserial-A9007UX1", // Mac OS X
        "/dev/ttyUSB0", // Linux
        "COM9", // Windows
};

private BufferedReader input;
private OutputStream output;
private static final int TIME_OUT = 2000;
private static final int DATA_RATE = 9600;

private static StringBuffer dataBuffApp = new StringBuffer();

	public void initialize() {
	    CommPortIdentifier portId = null;
	    Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
	
	    //First, Find an instance of serial port as set in PORT_NAMES.
	    while (portEnum.hasMoreElements()) {
	        CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
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
	    }
	
	    try {
	        serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);
	        serialPort.setSerialPortParams(DATA_RATE,
	                SerialPort.DATABITS_8,
	                SerialPort.STOPBITS_1,
	                SerialPort.PARITY_NONE);
	
	        // open the streams
	        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
	        output = serialPort.getOutputStream();
	
	        FileOutputStream fos = new FileOutputStream(new File("d:/temp/log_gps.txt"));
	        
	        serialPort.addEventListener(this);
	        serialPort.notifyOnDataAvailable(true);
	    } catch (Exception e) {
	        System.err.println("E1:"+e.toString());
	    }
	}

	public synchronized void close() {
	    if (serialPort != null) {
	        serialPort.removeEventListener();
	        serialPort.close();
	    }
	}
	
	public synchronized void serialEvent(SerialPortEvent oEvent) {
	    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
	        try {
	            String inputLine=null;
	            System.out.println("input.ready():"+input.ready());
	            if (input.ready()) {
	                inputLine = input.readLine();
	                //System.out.println(inputLine);
	                dataBuffApp.append(inputLine);
	            }
	
	        } catch (Exception e) {
	            System.err.println("E2:"+e.toString());
	        }
	    }
	    // Ignore all the other eventTypes, but you should consider the other ones.
	}
	
	

	public static void main(String[] args) throws Exception {
	    SerialTest main = new SerialTest();
	    main.initialize();
	    Thread t=new Thread() {
	        public void run() {
	            //the following line will keep this app alive for 1000    seconds,
	            //waiting for events to occur and responding to them    (printing incoming messages to console).
	            try {Thread.sleep(1000000);} catch (InterruptedException    ie) {}
	        }
	    };
	    t.start();
	    System.out.println("Started");
	}

	public static void Test() throws Exception {
	    SerialTest main = new SerialTest();
	    main.initialize();
	    Thread t=new Thread() {
	        public void run() {
	            //the following line will keep this app alive for 1000    seconds,
	            //waiting for events to occur and responding to them    (printing incoming messages to console).
	            try {Thread.sleep(1000000);} catch (InterruptedException    ie) {}
	        }
	    };
	    t.start();
	    System.out.println("Started");
	}

}
