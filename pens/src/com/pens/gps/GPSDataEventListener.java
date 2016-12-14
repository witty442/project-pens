package com.pens.gps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.isecinc.pens.inf.helper.Utils;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class GPSDataEventListener implements SerialPortEventListener {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmssddMMyy");
    private InputStream inputStream;
    private GPSData locationData;
	private boolean connection=true;
    
    public GPSDataEventListener(InputStream inputStream) {
        super();
        this.setInputStream(inputStream);
    }
    
    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
            if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream()));
                try {
                    boolean GPRMC_next_round=false;
                    GPSData location=new GPSData();
                    //System.out.println("Reader ready = " + reader.ready());
                    if(!reader.ready()) {
                        setConnection(false);
                    }
                    String currLine = "";
                    while (reader.ready()) {
                    	if(reader != null){
                          currLine = reader.readLine();
                    	}
                      //  if( !Utils.isNull(currLine).equals(""))
                           System.out.println("Curr line = " + currLine);
                        
                        String[] st = currLine.split(",");
                        String type = st.length != 0 ? st[0] : null;
                        
                        if (type.equals("$GPGGA")){
                        	System.out.println("setGpgga");
                            location.setGpgga(currLine);
                           //System.out.println("Curr line = " + currLine);
                            //System.out.println("lat:"+getLocationData().getLatitude()+",long:"+getLocationData().getLongitude());
                            
                        }else if (type.equals("$GPRMC")) {
                        	 System.out.println("GPRMC");
                            location.setGprmc(currLine);
                            GPRMC_next_round = true;
                        } else if (type.equals("$GPGSA")){   
                        	 System.out.println("GPGSA");
                            location.setGpgsa(currLine);
                        }
                        if(GPRMC_next_round) {
                            break;
                        }
                    }//while
                    
                    if(GPRMC_next_round) {
                        setLocationData(location);
                    }
                } catch (IOException e) {
                    setConnection(false);
                    e.printStackTrace();
                } finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }   
        } catch(Throwable e) {
            setConnection(false);
            e.printStackTrace();
        }
    }

    private void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    private void setLocationData(GPSData locationData) {
        this.locationData = locationData;
    }

    public synchronized GPSData getLocationData() {
        return locationData;
    }
    
    /**
     * Convert data from DDMM.MMMM system to DD.DDDD system
     * @param dat
     * @return
     */
    public String convertGPSdata(double dat) {
        double deg = Math.floor(dat / 100);
        double min = dat * 100 % 10000 / 6000;
        double result = (double) Math.round((deg + min) * 1000000) / 1000000;
        return Double.toString(result);
    }
    
    /**
     * 
     * @param time format hhmmss e.g. 225446 - Time of fix 22:54:46 UTC
     * @param date format ddMMyy e.g. 191194 - UTC Date of fix, 19 November 1994
     * @return
     */
    public Date convertStringToDate(String time, String date) {
        try {
            return dateFormat.parse(time + date);
        } catch (ParseException e) {
        }
        return null;
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public boolean isConnection() {
        return connection;
    }
    
}