package test.gps;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

public class Communicator implements SerialPortEventListener
{
    //passed from main GUI
  //  GUI window = null;

    //for containing the ports that will be found
    private Enumeration ports = null;
    //map the port names to CommPortIdentifiers
    private HashMap portMap = new HashMap();

    //this is the object that contains the opened port
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;

    //input and output streams for sending and receiving data
    private InputStream input = null;
    private OutputStream output = null;

    //just a boolean flag that i use for enabling
    //and disabling buttons depending on whether the program
    //is connected to a serial port or not
    private boolean bConnected = false;

    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;

    //a string for recording what goes on in the program
    //this string is written to the GUI
    String logText = "";

    public Communicator()
    {
       // this.window = window;
    }

    public static void main(String[] a){
    	Communicator communicator = new Communicator();
    	communicator.connect(communicator.getCurrentPorts());
        if (communicator.getConnected() == true)
        {
            if (communicator.initIOStream() == true)
            {
                communicator.initListener();
            }
        }
    }
    
    //search for all the serial ports
    //pre: none
    //post: adds all the found ports to a combo box on the GUI
    public void searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
              //  window.cboxPorts.addItem(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
    }
    
    public CommPortIdentifier getCurrentPorts(){
    	CommPortIdentifier curPort = null;
        ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements()) {
            CommPortIdentifier portT = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (portT.getPortType() == CommPortIdentifier.PORT_SERIAL){
              //  window.cboxPorts.addItem(curPort.getName());
            	if (portT.getName().equals("COM9")) {
            		curPort = portT;
                    System.out.println("Port:"+portT.getName());
                    break;
            	}
            }
        }
        return curPort;
    }

    //connect to the selected port in the combo box
    //pre: ports are already found by using the searchForPorts method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated
    public void connect( CommPortIdentifier portSelected)
    {
        
        CommPort commPort = null;

        try
        {
        	System.out.println("portSelected:"+portSelected);
            //the method below returns an object of type CommPort
            commPort = portSelected.open("TigerControlPanel", TIMEOUT);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;

            //for controlling GUI elements
            setConnected(true);

            //logging
           // logText = selectedPort + " opened successfully.";
           // window.txtLog.setForeground(Color.black);
           // window.txtLog.append(logText + "\n");

            //CODE ON SETTING BAUD RATE ETC OMITTED
            //XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

            //enables the controls on the GUI if a successful connection is made
           // window.keybindingController.toggleControls();
        }
        catch (PortInUseException e)
        {
            logText =  " is in use. (" + e.toString() + ")";
            
            //window.txtLog.setForeground(Color.RED);
            //window.txtLog.append(logText + "\n");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            logText = "Failed to open " + "(" + e.toString() + ")";
            e.printStackTrace();
           // window.txtLog.append(logText + "\n");
           // window.txtLog.setForeground(Color.RED);
        }
    }

    //open the input and output streams
    //pre: an open port
    //post: initialized intput and output streams for use to communicate data
    public boolean initIOStream()
    {
        //return value for whather opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            writeData(0, 0);
            
            successful = true;
            return successful;
        }
        catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
           // window.txtLog.setForeground(Color.red);
           // window.txtLog.append(logText + "\n");
            return successful;
        }
    }

    //starts the event listener that knows whenever data is available to be read
    //pre: an open serial port
    //post: an event listener for the serial port that knows when data is recieved
    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            logText = "Too many listeners. (" + e.toString() + ")";
            //window.txtLog.setForeground(Color.red);
           // window.txtLog.append(logText + "\n");
        }
    }

    //disconnect the serial port
    //pre: an open serial port
    //post: clsoed serial port
    public void disconnect()
    {
        //close the serial port
        try
        {
            writeData(0, 0);

            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);
            //window.keybindingController.toggleControls();

            logText = "Disconnected.";
           // window.txtLog.setForeground(Color.red);
           // window.txtLog.append(logText + "\n");
        }
        catch (Exception e)
        {
            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
           // window.txtLog.setForeground(Color.red);
           // window.txtLog.append(logText + "\n");
        }
    }

    final public boolean getConnected()
    {
        return bConnected;
    }

    public void setConnected(boolean bConnected)
    {
        this.bConnected = bConnected;
    }

    //what happens when data is received
    //pre: serial event is triggered
    //post: processing on the data it reads
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                byte singleData = (byte)input.read();

                if (singleData != NEW_LINE_ASCII)
                {
                    logText = new String(new byte[] {singleData});
                   // window.txtLog.append(logText);
                    System.out.println(logText+"");
                }
                else
                {
                   // window.txtLog.append("\n");
                	 System.out.println(logText+"");
                }
            }
            catch (Exception e)
            {
            	e.printStackTrace();
                logText = "Failed to read data. (" + e.toString() + ")";
               // window.txtLog.setForeground(Color.red);
               // window.txtLog.append(logText + "\n");
            }
        }
    }

    //method that can be called to send data
    //pre: open serial port
    //post: data sent to the other device
    public void writeData(int leftThrottle, int rightThrottle)
    {
        try
        {
            output.write(leftThrottle);
            output.flush();
            //this is a delimiter for the data
            output.write(DASH_ASCII);
            output.flush();
            
            output.write(rightThrottle);
            output.flush();
            //will be read as a byte so it is a space key
            output.write(SPACE_ASCII);
            output.flush();
        }
        catch (Exception e)
        {
            logText = "Failed to write data. (" + e.toString() + ")";
           // window.txtLog.setForeground(Color.red);
           // window.txtLog.append(logText + "\n");
        }
    }
}
