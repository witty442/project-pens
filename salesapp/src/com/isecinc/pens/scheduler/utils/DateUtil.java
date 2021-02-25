package com.isecinc.pens.scheduler.utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

public class DateUtil {
	
	private static Logger logger = Logger.getLogger(DateUtil.class);
	
	public static final String DD_MM_YYYY = "dd/MM/yyyy";
	public static final String YYYY_MM_DD = "yyyy/MM/dd";
	public static final String HH_MM_SS = "HH:mm:ss";
   // public static Calendar c = Calendar.getInstance();
	
	
	/**
	 * Parse from {@link String} to {@link Date}
	 * @param dateString the string of date
	 * @param format the format of date
	 * @return {@link Date}
	 */
	public static Date parse(String dateString, String format) throws Exception {
		Date date = null;
		SimpleDateFormat ft = new SimpleDateFormat(format, Locale.US);
		
		try {
			date = ft.parse(dateString);
		} catch (Exception e) {
			
		}
		
		return date;
	}
	
	/**
	 * 
	 * @param dateString - the string of date
	 * @param format - the format of date
	 * @param locale - the locale of date
	 * @return {@link java.util.Date}
	 * @throws Exception
	 */
	public static Date parse(String dateString, String format ,String locale) throws Exception {
		Date date = null;
		SimpleDateFormat ft = new SimpleDateFormat(format, new Locale(locale.toLowerCase()));
		
		try {
			date = ft.parse(dateString);
		} catch (Exception e) {
			
		}
		
		return date;
	}
	
	/**
	 * Convert {@link Date} to {@link String} and return date string
	 * @param date
	 * @param format the format that you want to convert
	 * @return date string
	 */
	public static String stringValue(Date date, String format) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, Locale.US);
		
		try {
			dateStr = ft.format(date);
		} catch (Exception e) {
			
		}

		return dateStr;
	}
	
	/**
	 * Convert {@link java.util.Date} to {@link java.lang.String} and return date string
	 * @param date - {@link java.util.Date} object
	 * @param format - the format that you want to convert
	 * @param locale - the locale of date
	 * @return String
	 * @throws Exception
	 */
	public static String stringValue(Date date, String format ,String locale) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, new Locale(locale.toLowerCase()));
		
		try {
			dateStr = ft.format(date);
		} catch (Exception e) {
			
		}

		return dateStr;
	}
	
	public static String getPrevDate() throws Exception{
		Calendar c = Calendar.getInstance(new Locale("TH","th"));
		c.add(Calendar.DATE, -1);
		String dateStr = stringValue(c.getTime(),DD_MM_YYYY);	
		return dateStr;
	}
	public static String getCurrentDate() throws Exception{
		Calendar c = Calendar.getInstance(new Locale("TH","th"));
		String date = stringValue(c.getTime(), DD_MM_YYYY);
		return date;
	}
	public static String getCurrentTime() throws Exception{
		Calendar c = Calendar.getInstance(new Locale("TH","th"));
		String time = stringValue(c.getTime(), HH_MM_SS);
		return time;
	}
	public static void main(String[] args){
	    try{	       
	    	
	        System.out.println("getCurrent Time : "+DateUtil.getCurrentTime());
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	
}