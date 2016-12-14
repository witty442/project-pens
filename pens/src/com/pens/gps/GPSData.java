package com.pens.gps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GPSData {

    private static SimpleDateFormat dateFormat;
    static {
        dateFormat = new SimpleDateFormat("HHmmssddMMyy");
    }

    private Date time;

    private void setTime(Date time) {
        this.time = time;
    }

    /**
     * true if the gps has a connection
     */
    private boolean connection = false;
    
    private String horizontal;
    
    private String vertical;
    
    /**
     * Speed in km/h
     */
    private Double speed;

    private Double latitude;
    private Double longitude;
    private String gpgga;
    private String gprmc;
    private String gpgsa;
    private Date gpsDate;

    public Date getGpsDate() {
        return gpsDate;
    }

    public void setGpsDate(Date gpsDate) {
        this.gpsDate = gpsDate;
    }

    public void setGpsDate(String hourPart, String dayPart) {
        try {
            String tmpHours = hourPart.split("\\.")[0];
            String timeStamp = tmpHours + dayPart;
            setGpsDate(dateFormat.parse(timeStamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GPSData() {
        this.time = new Date();
    }
    
    public boolean isConnection() {
        return connection;
    }

    private void setConnection(boolean connection) {
        this.connection = connection;
    }

    /**
     * E=East or W=West
     * @return
     */
    public String getHorizontal() {
        return horizontal;
    }

    private void setHorizontal(String horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * N=North or S=South
     * @return
     */
    public String getVertical() {
        return vertical;
    }


    private void setVertical(String vertical) {
        this.vertical = vertical;
    }

    public Double getSpeed() {
        return speed;
    }

    private void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getLongitude() {
        return longitude;
    }

    private void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    private void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getGpgga() {
        return gpgga;
    }

    public void setGpgga(String gpgga) {
        this.gpgga = gpgga;
    }

    public String getGprmc() {
        return gprmc;
    }

    public void setGprmc(String gprmc) {
        this.gprmc = gprmc;
        String[] data = gprmc.split(",");
        //if data[2] is A then we have a connection. If it is V then we dont.
        setConnection(data[2].equals("A"));
        //setTime(convertStringToDate(data[1], data[9]));
        //XXX: no using gps time anymore cause it was not correct when there was no signal
        setTime(new Date());
        if(isConnection()) {
            setGpsDate(data[1], data[9]);
            setLongitude(convertGPSdata(Double.valueOf(data[5])));
            setHorizontal(data[6]);
            setLatitude(convertGPSdata(Double.valueOf(data[3])));
            setVertical(data[4]);
            setSpeed(knotsToKmh(data[7]));  
        }
    }

    public String getGpgsa() {
        return gpgsa;
    }

    public void setGpgsa(String gpgsa) {
        this.gpgsa = gpgsa;
    }

    public Date getTime() {
        return time;
    }

    /**
     * Convert data from DDMM.MMMM system to DD.DDDD system
     * 
     * @param dat
     * @return
     */
    private static Double convertGPSdata(double dat) {
        double deg = Math.floor(dat / 100);
        double min = dat * 100 % 10000 / 6000;
        double result = (double) Math.round((deg + min) * 1000000) / 1000000;
        return result;
    }

    /**
     * 
     * @param time
     *            format hhmmss e.g. 225446 - Time of fix 22:54:46 UTC
     * @param date
     *            format ddMMyy e.g. 191194 - UTC Date of fix, 19 November 1994
     * @return
     */
    private static Date convertStringToDate(String time, String date) {
        Calendar cal = Calendar.getInstance();
        try {
            return dateFormat.parse(time.substring(0, time.indexOf(".")) + date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static double knotsToKmh(String knots) {
        if (knots != null && !knots.isEmpty()) {
            return Double.parseDouble(knots) * 1.852;
        } else {
            return 0d;
        }
    }
    
    
    public String toString() {
        return "Time: "+ getTime() +"; Connected: " + isConnection() + "; Longitude: " + getLongitude() + "; Latitude: " + getLatitude() + "; Speed: " + getSpeed() + ";";
    }
}
