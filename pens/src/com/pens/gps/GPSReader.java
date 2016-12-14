package com.pens.gps;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;


public class GPSReader extends Thread implements SerialPortEventListener {

    private GPSData currentPosition;
    private long portScanTime=2000;
    private long updateTime=500;
    private boolean found=false; 
    private InputStream inputStream;
    private GPSDataEventListener dataListener;
    private SerialPort gpsPort = null;
    private boolean running=true;
    
    private static final String regExp = "((\\$GPGGA)|(\\$GPRMC)|(\\$GPGSA)|(\\$GPGLL)|(\\$GPGSV)|(\\$GPVTG)).*";
   // private static final String regExp = "((\\$GPGGA)).*";
    
    public static void main(String[] args) {
		GPSReader reader = new GPSReader();
		reader.start();
	}
    public static void startProcess() {
		GPSReader reader = new GPSReader();
		reader.start();
	}
    
    public void boforeStartClearData(){
    	Connection conn = null;
    	PreparedStatement ps = null;
    	try{
    		conn = DBConnection.getInstance().getConnection();
    		ps =conn.prepareStatement("delete from c_temp_location");
    		ps.executeUpdate();
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
    		  conn.close();
    		  ps.close();
    		}catch(Exception e){}
    	}
    }
    
    public void updateLocationDB(GPSData g){
    	Connection conn = null;
    	PreparedStatement ps = null;
    	try{
    	    System.out.println("updatedb lat:"+g.getLatitude()+",long:"+g.getLongitude());
    		String sql = "insert into c_temp_location(lat,lng,create_date,error)values('"+g.getLatitude()+"' ,'"+g.getLongitude()+"' ,sysdate(),'')";
    	    System.out.println("sql:"+sql);
    		conn = DBConnection.getInstance().getConnection();
    		ps =conn.prepareStatement(sql);
    		ps.executeUpdate();
   
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
    		  conn.close();
    		  ps.close();
    		}catch(Exception e){}
    	}
    }
    
    public void updateErrorLocationDB(String error){
    	Connection conn = null;
    	PreparedStatement ps = null;
    	try{
    		String sql = "insert into c_temp_location(lat,lng,create_date,error)values('' ,'' ,sysdate(),'"+error+"')";
    	  
    		conn = DBConnection.getInstance().getConnection();
    		ps =conn.prepareStatement(sql);
    		ps.executeUpdate();
   
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
    		  conn.close();
    		  ps.close();
    		}catch(Exception e){}
    	}
    }
    
    /**
     * Return lat|long
     */
    public String getLocationDB(){
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	String location = "";
    	try{
    		conn = DBConnection.getInstance().getConnection();
    		ps = conn.prepareStatement("select lat,lng ,error from c_temp_location ");
    		rs = ps.executeQuery();
    		if(rs.next()){
    			location =Utils.isNull(rs.getString("lat"))+"|"+Utils.isNull(rs.getString("lng"))+"|"+Utils.isNull(rs.getString("error"));
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
    		  conn.close();
    		  ps.close();
    		}catch(Exception e){}
    	}
    	return location;
    }
    
    
    public boolean findPortAll() {
        Enumeration enumer = CommPortIdentifier.getPortIdentifiers();
        while(enumer.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier)enumer.nextElement();
            if(port.getPortType() == CommPortIdentifier.PORT_SERIAL && !port.isCurrentlyOwned()) {
                SerialPort serialPort=null;
                try {
                    serialPort = (SerialPort)port.open("MY_PORT_NAME", 2000);
                    System.out.println("Trying port: " + port.getName());
                    
                    inputStream = null;
                    inputStream = serialPort.getInputStream();
                    if (inputStream == null) {
                        return false;
                    }

                    serialPort.addEventListener(this);
                    
                    serialPort.notifyOnDataAvailable(true);
                    
                    serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8,SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    
                    Thread.sleep(getPortScanTime());
                } catch (PortInUseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TooManyListenersException e) {
                    e.printStackTrace();
                } catch (UnsupportedCommOperationException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
 
                if(this.isFound()) {
                    System.out.println("Found Stream on com port " + serialPort.getName());
                    serialPort.removeEventListener();
                    setDataListener(new GPSDataEventListener(inputStream));
                    this.gpsPort = serialPort; 
                    try {
                        serialPort.addEventListener(getDataListener());
                        serialPort.notifyOnDataAvailable(true);
                        System.out.println("Start");
                    } catch (TooManyListenersException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    try {
                        if(inputStream != null) {
                          inputStream.close();    
                        }
                        if(serialPort != null) {
                          serialPort.close();  
                        } 
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("No Stream found!");
        return false;
    }
    
    public boolean findPort() {
    	CommPortIdentifier port = null;
    	try{
            port = CommPortIdentifier.getPortIdentifier("COM99");
    	}catch(Exception e){
    		e.printStackTrace();
    		 System.out.println("Update ERROR Not found Usb device");
             updateErrorLocationDB("ErrorNotFoundGPSDevice");
    	}
        if(port.getPortType() == CommPortIdentifier.PORT_SERIAL && !port.isCurrentlyOwned()) {
            SerialPort serialPort=null;
            try {
                serialPort = (SerialPort)port.open("MY_PORT_NAME", 2000);
                System.out.println("Trying port: " + port.getName());
                    
                    inputStream = null;
                    inputStream = serialPort.getInputStream();
                    if (inputStream == null) {
                        return false;
                    }

                    serialPort.addEventListener(this);
                    
                    serialPort.notifyOnDataAvailable(true);
                    
                    serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8,SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    
                    Thread.sleep(getPortScanTime());
                } catch (PortInUseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TooManyListenersException e) {
                    e.printStackTrace();
                } catch (UnsupportedCommOperationException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
 
                if(this.isFound()) {
                    System.out.println("Found Stream on com port " + serialPort.getName());
	                serialPort.removeEventListener();
	                setDataListener(new GPSDataEventListener(inputStream));
	                this.gpsPort = serialPort; 
	                try {
	                    serialPort.addEventListener(getDataListener());
	                    serialPort.notifyOnDataAvailable(true);
	                    System.out.println("Start");
	                } catch (TooManyListenersException e) {
	                    e.printStackTrace();
	                }
	                return true;
            } else {
                try {
                    if(inputStream != null) {
                      inputStream.close();    
                    }
                    if(serialPort != null) {
                      serialPort.close();  
                    } 
                } catch (IOException e) {
                    System.out.println("error find port:"+e.getMessage());
                }
            }
        }
        
        System.out.println("No Stream found!");
        return false;
    }
	
    @Override
    public void run() {
        try {
            setRunning(true);// Set the reader thread flag to running
            while(isRunning()) { // run while the running flag is set to true
            	 // Read data if the GPS device is found, else try to find the GPS reader port
                if(isFound() && getDataListener() != null && getDataListener().isConnection()) {
                    GPSData data = getDataListener().getLocationData();
                    setCurrentPosition(data);
                    
                    System.out.println("lat:"+getCurrentPosition().getLatitude()+",long:"+getCurrentPosition().getLongitude());
                    
                    System.out.println("Update Location DB");
                    updateLocationDB(getCurrentPosition());
                    
                    setRunning(false);
                    closeConnection();
                    System.out.println("Stop find Location");

                } else { 
                	System.out.println("Not Found");
                    if(getDataListener() != null) {
                    	 System.out.println("Update ERROR Location not found");
                         updateErrorLocationDB("ErrorNotFoundLocation");
                         
                    	  // Close all ports and streams if not already done
                        setDataListener(null);
                        gpsPort.close();
                        gpsPort.removeEventListener();
                        gpsPort = null;
                        setFound(false);
                    }else{
                       
                    }
                    findPort();
                }
                Thread.sleep(getUpdateTime());
            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
        	System.out.println("e1:"+e.getMessage());
        } catch (Throwable t ) {
            t.printStackTrace();
            /** Cannot Load Lib **/
   		    System.out.println("Cannot Load libraly Update ERROR Not found Usb device");
            updateErrorLocationDB("ErrorNotLoadGPSDevice");
        }
    }
    
    public synchronized void closeConnection() {
	    if (gpsPort != null) {
	    	gpsPort.removeEventListener();
	    	gpsPort.close();
	    }
	}
    
    @Override
    public void serialEvent(SerialPortEvent event) {
        //System.out.println("Event:" + event.getEventType());
        if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE && !found) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream()));
            String line=null;
            try {
                if (reader.ready()) {
                    line = reader.readLine();
                    if(line != null){
                    	//System.out.println("line:"+line);
                    }
                    if(line != null  && line.matches(regExp)) {
                    	System.out.println("Found:"+line);
                        this.setFound(true);  
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	public long getUpdateTime() {
		return updateTime;
	}

    private void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    private void setFound(boolean found) {
        this.found = found;
    }

    public boolean isFound() {
        return found;
    }

    synchronized private void setCurrentPosition(GPSData currentPosition) {
        this.currentPosition = currentPosition;
    }

    synchronized public GPSData getCurrentPosition() {
        return currentPosition;
    }

    public void setPortScanTime(long portScanTime) {
        this.portScanTime = portScanTime;
    }

    public long getPortScanTime() {
        return portScanTime;
    }

    private void setDataListener(GPSDataEventListener dataListener) {
        this.dataListener = dataListener;
    }

    private GPSDataEventListener getDataListener() {
        return dataListener;
    }

    public void setRunning(boolean run) {
        this.running = run;
    }

    public boolean isRunning() {
        return running;
    }
    
    public boolean isConnection() {
        return isFound() && getCurrentPosition() != null && getCurrentPosition().isConnection();
    }


}