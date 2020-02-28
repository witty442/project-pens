package com.pens.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.isecinc.pens.SystemElements;

public class DateUtil {
	protected static  Logger logger = Logger.getLogger("PENS");
	public static final String SHORT_TIME = "HH:mm";

	public static final int DAY = 1;
	public static final int MONTH = 2;
	public static final int YEAR = 3;
	private static final long ONE_HOUR = 60 * 60 * 1000L;
	private static ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
	
	public static final String DD_MM_YYYY_WITH_SLASH = "dd/MM/yyyy";
	public static final String YYYY_MM_DD_WITH_SLASH = "yyyy/MM/dd";
	public static final String YYYY_MM_DD_WITHOUT_SLASH = "yyyyMMdd";
	public static final String DD_MM_YYYY_WITHOUT_SLASH = "ddMMyyyy";
	public static final String DD_MM_YYYY_HH_MM_SS_WITH_SLASH = "dd/MM/yyyy HH:mm:ss";
	public static final String DD_MM_YYYY__HH_mm_ss_WITH_SLASH = "dd/MM/yyyy  HH:mm:ss";
	public static final String DD_MM_YYYY_HH_MM_WITH_SLASH = "dd/MM/yyyy HH:mm";
	
	public static final Locale local_th= new Locale("th","TH");
	
	public static final String DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH = "ddMMyyyy HHmmss";
	public static final String DD_MM_YYYY_HH_mm_WITHOUT_SLASH = "ddMMyyyy-HHmm";
	public static final String DD_MMM_YYYY = "dd-MMM-yyyy";
	public static final String MMM_YY = "MMM-yy";
	public static final String MMMM_YYYY = "MMMM-yyyy";
	
	public static void main(String[] args) {
		//System.out.println(isFromToDateCorrect("15/11/2553", "15/11/2553"));
		try{
		    Date backDate = getBackDate(new Date(), -30);
			logger.debug("backDate:"+backDate);
			//calcDiffMonthYear(date1,date2);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static int calcDiffMonthYear(Date date1,Date date2){
	 int diffMonth = 0;
	 int mm1 = 0;
	 int yyyy1 = 0;
	 int mm2 = 0;
	 int yyyy2 = 0;
		try{
		   String date1Str = stringValue(date1,"MM-yyyy");
		   String date2Str = stringValue(date2,"MM-yyyy");
		   
		   mm1 = Integer.parseInt(date1Str.substring(0,2));
		   yyyy1 = Integer.parseInt(date1Str.substring(3,7));
		   mm2 = Integer.parseInt(date2Str.substring(0,2));
		   yyyy2 = Integer.parseInt(date2Str.substring(3,7));
		   System.out.println("mm1:"+mm1);
		   System.out.println("yyyy1:"+yyyy1);
		   
		   int diffMonthOfYear = (yyyy2-yyyy1)*12;
		    diffMonth = (mm2+diffMonthOfYear)-mm1;
		   
		}catch(Exception e){
			e.printStackTrace();
		}
		return  diffMonth;
	}
	
	public static String getToday(Connection con, int format) throws Exception {
		String stroutput = "";

		Statement stmt = con.createStatement();
		ResultSet rst = stmt.executeQuery("select convert(varchar,getdate()," + format + ") as output ");
		while (rst.next()) {
			stroutput = rst.getString("output");
		}
		stmt.close();

		return stroutput;
	}

	public static String getToday(Connection con, int len, int format) throws Exception {
		String stroutput = "";

		Statement stmt = con.createStatement();
		ResultSet rst = stmt.executeQuery("select convert(varchar(" + len + "),getdate()," + format + ") as output ");
		while (rst.next()) {
			stroutput = rst.getString("output");
		}
		stmt.close();

		return stroutput;
	}

	public static int getNumOfDay(String day) {
		int numOfDay = 0;

		if (day.equalsIgnoreCase(SystemElements.SUN)) {
			numOfDay = 0;
		} else if (day.equalsIgnoreCase(SystemElements.MON)) {
			numOfDay = 1;
		} else if (day.equalsIgnoreCase(SystemElements.TUE)) {
			numOfDay = 2;
		} else if (day.equalsIgnoreCase(SystemElements.WED)) {
			numOfDay = 3;
		} else if (day.equalsIgnoreCase(SystemElements.THU)) {
			numOfDay = 4;
		} else if (day.equalsIgnoreCase(SystemElements.FRI)) {
			numOfDay = 5;
		} else if (day.equalsIgnoreCase(SystemElements.SAT)) {
			numOfDay = 6;
		}

		return numOfDay;
	}

	public static String getDayOfNum(int num) {
		String dayOfNum = "";

		switch (num) {
		case 0:
			dayOfNum = bundle.getString("Sunday");
			break;
		case 1:
			dayOfNum = bundle.getString("Monday");
			break;
		case 2:
			dayOfNum = bundle.getString("Tueday");
			break;
		case 3:
			dayOfNum = bundle.getString("Wednesday");
			break;
		case 4:
			dayOfNum = bundle.getString("Thursday");
			break;
		case 5:
			dayOfNum = bundle.getString("Friday");
			break;
		case 6:
			dayOfNum = bundle.getString("Saturday");
			break;
		default:
			break;
		}

		return dayOfNum;
	}

	public static String getMonthOfNum(int num) {
		String monthOfNum = "";
		switch (num) {
		case 0:
			monthOfNum = bundle.getString(SystemElements.JAN);
			break;
		case 1:
			monthOfNum = bundle.getString(SystemElements.FEB);
			break;
		case 2:
			monthOfNum = bundle.getString(SystemElements.MAR);
			break;
		case 3:
			monthOfNum = bundle.getString(SystemElements.APR);
			break;
		case 4:
			monthOfNum = bundle.getString(SystemElements.MAY);
			break;
		case 5:
			monthOfNum = bundle.getString(SystemElements.JUN);
			break;
		case 6:
			monthOfNum = bundle.getString(SystemElements.JUL);
			break;
		case 7:
			monthOfNum = bundle.getString(SystemElements.AUG);
			break;
		case 8:
			monthOfNum = bundle.getString(SystemElements.SEP);
			break;
		case 9:
			monthOfNum = bundle.getString(SystemElements.OCT);
			break;
		case 10:
			monthOfNum = bundle.getString(SystemElements.NOV);
			break;
		case 11:
			monthOfNum = bundle.getString(SystemElements.DEC);
			break;
		default:
			break;
		}
		return monthOfNum;
	}
	
	/** Return last day of month 
	 * 
	 * @param Date;
	 * @return
	 */
	public static String getMaxDayOfMonth(Date date) {
	   String maxDayOfMonth = "";
	   Calendar cal = Calendar.getInstance();
	   cal.setTime(date);
	   logger.debug("InputDate:"+cal.getTime());
	   maxDayOfMonth = String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	   return maxDayOfMonth;
	}

	/**
	 * Convert string to date (format dd/MM/yyyy)
	 * 
	 * @param date
	 * @return
	 */
	public static Date convertStringToDate(String date) {
		DateFormat df = null;
		Date newDate = null;

		try {
			df = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
			newDate = df.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newDate;
	}

	/**
	 * Calculate number of differences date.
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long calDiffDate(int type, String startDate, String endDate) {
		long diff = 0;
		Date d1 = convertStringToDate(startDate);
		Date d2 = convertStringToDate(endDate);
		Calendar c1 = new GregorianCalendar(new Locale("th", "TH"));
		Calendar c2 = new GregorianCalendar(new Locale("th", "TH"));
		c1.setTime(d1);
		c2.setTime(d2);

		switch (type) {
		case 1:
			diff = daysBetween(c1.getTime(), c2.getTime());
			break;
		case 2:
			diff = monthsBetween(c1.getTime(), c2.getTime());
			break;
		case 3:
			diff = yearsBetween(c1.getTime(), c2.getTime());
			break;
		default:
			break;
		}

		return diff;
	}

	private static long daysBetween(Date d1, Date d2) {
		return ((d2.getTime() - d1.getTime() + ONE_HOUR) / (ONE_HOUR * 24));
	}

	private static long monthsBetween(Date d1, Date d2) {
		return ((d2.getTime() - d1.getTime() + ONE_HOUR) / (ONE_HOUR * 24 * 30));
	}

	private static long yearsBetween(Date d1, Date d2) {
		return ((d2.getTime() - d1.getTime() + ONE_HOUR) / (ONE_HOUR * 24 * 30 * 12));
	}

	/**
	 * Convert Date type to String type (format : dd/MM/yyyy)
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentDateTime(String pattern) {
		String toDay = "";
		SimpleDateFormat format = new SimpleDateFormat(pattern, new Locale("th", "TH"));
		toDay = format.format(new Date());
		return toDay;
	}

	/**
	 * Convert to String
	 * 
	 * @param ts
	 */
	public static String convertToString(Timestamp ts) {
		return convertToString(new Date(ts.getTime()));
	}

	/**
	 * Convert to String
	 * 
	 * @param d
	 */
	public static String convertToString(Date d) {
		if (d != null) {
			return convertToString(d,"dd/MM/yyyy");
		} else {
			return "";
		}
	}
	
	public static String convertToString(Date d,String fmt) {
		if (d != null && fmt != null) {
			return new SimpleDateFormat(fmt, new Locale("th", "TH")).format(d);
		} else {
			return "";
		}
	}

	/**
	 * Convert to TimeStamp
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static Timestamp convertToTimeStamp(String date) throws Exception {
		Timestamp ts = null;
		if (date.length() > 0) {
			Date d = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).parse(date);
			ts = new Timestamp(d.getTime());
		}
		return ts;
	}

	/**
	 * Convert from Timestamp
	 * 
	 * @param ts
	 */
	public static String convertFromTimestamp(Timestamp ts) {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("th", "TH")).format(ts.getTime());
	}

	/**
	 * Compare with Today
	 * 
	 * 1 if today more date compare <br>
	 * 0 if equal <br>
	 * -1 if today less date compare
	 * 
	 * @param dateToCompare
	 * @return
	 * @throws Exception
	 */
	public static int compareWithToday(String dateToCompare) throws Exception {
		String toDay = getCurrentDateTime("dd/MM/yyyy");
		Date cdate = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).parse(dateToCompare);
		Date now = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).parse(toDay);
		return now.compareTo(cdate);
	}

	/**
	 * check Start End
	 * 
	 * @param startDate
	 * @param endDate
	 * @return true if infinite promotion(no start-end)<br>
	 *         true if current date between equal start-end<br>
	 *         false if current date out of bound
	 * 
	 * @throws Exception
	 */
	public static boolean checkStartEnd(String startDate, String endDate) throws Exception {
		boolean useDate = false;
		if (startDate.length() == 0 && endDate.length() == 0) {
			// infinite promotion
			useDate = true;
		} else {
			if (startDate.length() > 0) {
				// have start
				if (compareWithToday(startDate) != -1) {
					// start date <= today
					useDate = true;
				} else {
					// start date > today
					useDate = false;
				}
			}
			if (endDate.length() > 0) {
				// have end
				if (compareWithToday(endDate) != 1) {
					// end date >= today
					useDate = true;
				} else {
					// end date < today
					useDate = false;
				}
			}
		}
		return useDate;
	}

	/**
	 * Convert date format dd/MM/yyyy (Sample : 17/11/2553 --> ¾Ø¸·Õè 17 ¾ÄÈ¨Ô¡ÒÂ¹ ¾.È.2553)
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String dateNumToWord(String dateWord) {
		Calendar cDate = Calendar.getInstance();
		cDate.setTime(convertStringToDate(dateWord));
		String day = getDayOfNum(cDate.getTime().getDay());
		String date = dateWord.split("/")[0];
		String month = getMonthOfNum(cDate.getTime().getMonth());
		String year = dateWord.split("/")[2];

		dateWord = day + " " + bundle.getString("On") + " " + date + " " + month + " " + year;
		return dateWord;
	}



	/**
	 * Check dateTo not over dateFrom.
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	public static boolean isFromToDateCorrect(String dateFrom, String dateTo) {
		Date dFrom = convertStringToDate(dateFrom);
		Date dTo = convertStringToDate(dateTo);
		long dateDiff = dTo.getTime() - dFrom.getTime();
		if (dateDiff < 0) { return false; }
		return true;
	}

	/**
	 * To Calendar
	 * 
	 * @param date
	 * @return
	 */
	public static Calendar toCalendar(String date) {
		Calendar c = Calendar.getInstance(new Locale("th", "TH"));
		String[] d = date.split("/");
		if (d.length != 3) return null;
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d[0]));
		c.set(Calendar.MONTH, Integer.parseInt(d[1]) - 1);
		c.set(Calendar.YEAR, Integer.parseInt(d[2]));
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}
	
	public static int getDayOfDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int convertToYearBushdish(int christYear){
		return christYear+543;
	}
	
	/**
	 * Check Status Active  By Compare CurrentDate ,startDate vs endDate
	 * @param startDateStr
	 * @param endDateStr
	 * @return
	 */
	public static  boolean isStatusActiveByDate(String startDateStr,String endDateStr){
		Date startDate = null;
		Date endDate = null;
		Date curDate = null;
		try{
			if( !Utils.isNull(startDateStr).equals("")){
			   startDate = parse(startDateStr, DD_MM_YYYY_WITH_SLASH, local_th);
			}
			if( !Utils.isNull(endDateStr).equals("")){
			   endDate = parse(endDateStr, DD_MM_YYYY_WITH_SLASH, local_th);
			}
			
			curDate = parse(stringValue(new Date(),DD_MM_YYYY_WITH_SLASH,local_th), DD_MM_YYYY_WITH_SLASH, local_th);
            
			logger.debug("curDate:"+curDate);
			logger.debug("startDate:"+startDate);
			logger.debug("endDate:"+endDate);
			
			if(startDate != null && endDate != null){
				logger.debug("case 1");
				if(  ( startDate.before(curDate) || startDate.equals(curDate))   //startDate <= curDate
				  && ( endDate.after(curDate) || endDate.equals(curDate) )  ){ //endDate >= curDate
					return true;
				}
			}else{
				logger.debug("case 2");
				if( startDate.before(curDate) || startDate.equals(curDate)){   //startDate <= curDate
					return true;
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return false;
	}
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
	 * @param dateString
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static Date parseToBudishDate(String dateString, String format) throws Exception {
		Date date = null;
		SimpleDateFormat ft = new SimpleDateFormat(format, new Locale("TH","th"));
		
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
	public static Date parse(String dateString, String format ,Locale locale) throws Exception {
		Date date = null;
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		
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
	
	
	public static String stringValue(Timestamp date, String format) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, Locale.US);
		
		try {
			dateStr = ft.format(date);
		} catch (Exception e) {
			
		}

		return dateStr;
	}
	
	public static String stringValue(Timestamp date, String format ,Locale locale) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		
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
	public static String stringValue(Date date, String format ,Locale locale) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			dateStr = ft.format(date);
		} catch (Exception e) {
		}
		return dateStr;
	}
	
	public static String stringValueChkNull(Date date, String format ,Locale locale) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			if(date ==null)
				return "";
			dateStr = ft.format(date);
		} catch (Exception e) {
		}
		return dateStr;
	}
	
	public static Timestamp getCurrentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}
	
	public static String format(Date date, String pattern) {
		if (date == null) {
			return "";
		}
		return new SimpleDateFormat(pattern, Locale.US).format(date);
	}
	
	//BackDay negative (-1)
	public static Date getBackDate(Date date, int backDay) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, backDay);
		
		return c.getTime();
	}
}
