
package com.pens.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;

/**
 *	General utilities.
 *	@author Witty
 */
public class Utils {
	protected static Logger logger = Logger.getLogger("PENS");
	public static final String DD_MM_YYYY_WITH_SLASH = "dd/MM/yyyy";
	public static final String DD_MM_YYYY__HH_mm_ss_WITH_SLASH = "dd/MM/yyyy  HH:mm:ss";
	public static final String DD_MM_YYYY_HH_mm_ss_WITH_SLASH = "dd/MM/yyyy HH:mm:ss";
	public static final String YYYY_MM_DD_WITH_SLASH = "yyyy/MM/dd";
	public static final String YYYY_MM_DD_WITHOUT_SLASH = "yyyyMMdd";
	public static final String DD_MM_YYYY_WITHOUT_SLASH = "ddMMyyyy";
	public static final String DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH = "ddMMyyyy HHmmss";
	public static final String DD_MM_YYYY_HH_mm_WITHOUT_SLASH = "ddMMyyyy-HHmm";
	
	public static final String DD_MM_YYYY_HH_MM_SS_WITH_SLASH = "dd/MM/yyyy HH:mm:ss";
	
	public static final String YYYYMMDDHH_mm_ss_SSSSSS = "yyyyMMddHHmmss.SSSSSS";
	public static final String DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH = "dd/MM/yyyy  HH:mm:ss:SSSSSS";
	public static final String DD_MMM_YY = "dd-MMM-yy";
	public static final String YYYYMM = "yyyyMM";
	
	//Thai date
	public static final String MMMM_YYYY = "MMMM-yyyy";
	public static final String MMMMYYYY = "MMMMyyyy";
	public static final String MMM_YYYY = "MMM-yyyy";
	public static final String MMM_YY = "MMM-yy";
	public static final String DD_MMMM_YYYY = "dd MMMM yyyy";
	
	public static final Locale local_th= new Locale("th","TH");

	public static final String format_current_no_disgit = "#,##0";
	public static final String format_current_2_disgit = "#,##0.00";
    public static final String format_current_5_digit = "#,##0.00000";
	public static final String format_current_6_digit = "#,##0.000000";
	
	
	//private static String DECIMAL_FORMAT ="#.00000000000000000000";
	private static String CURRENCY_FORMAT ="#,##0.00";
	private static String CURRENCY_NODIGIT_FORMAT ="#,##0";
	private static String NUMBER_FORMAT ="#,##0";
	      
	public static void main(String[] args){
	    try{	
	    	String input ="020068";
	    	System.out.println(new Double(input));
	        
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	public static boolean userInRole(User user,String[] roles){
		boolean r = false;
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRole().getKey().toLowerCase().trim();
			String userRoles[] = userRoleTemp.split("\\|");

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
				//logger.debug("roleCheck:["+i+"]["+roleCheck+"]["+userRole+"]");
				
				if( roleCheck.equalsIgnoreCase(userRole)){
					//logger.debug("EQ =roleCheck["+roleCheck+"]:["+i+"]["+userRole+"]");
					r =  true;
					break;
				}
			}//for 2
			
		}//for 1
		return r;
	}
	
	public static boolean stringInStringArr(String str,String[] array){
		boolean r = false;
		for(int i=0;i<array.length;i++){
			String arrCheck = array[i].toLowerCase().trim();
			//logger.debug(str+":"+arrCheck);
			if(str.equalsIgnoreCase(arrCheck)){
				//logger.debug("equals");
				r= true;
				break;
			}

		}//for 1
		return r;
	}
	
    public static boolean isHoliday(String dateString){
	   boolean re = false;
	   try{
		   Calendar c = Calendar.getInstance();
		   c.setTime(Utils.parse(dateString, Utils.DD_MM_YYYY_WITHOUT_SLASH));
		   int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	   
		   if(dayOfWeek==Calendar.SATURDAY || dayOfWeek==Calendar.SUNDAY){
			   re = true;
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return re;
	}
    
    public static boolean isHoliday(String dateString,String dateFormat,Locale locale){
 	   boolean re = false;
 	   try{
 		   Calendar c = Calendar.getInstance();
 		   c.setTime(Utils.parse(dateString, dateFormat,locale));
 		   int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
 	   
 		   if(dayOfWeek==Calendar.SATURDAY || dayOfWeek==Calendar.SUNDAY){
 			   re = true;
 		   }
 	   }catch(Exception e){
 		   e.printStackTrace();
 	   }
 	   return re;
 	}
	  
	public static int calcTotalPage(int totalRow,int maxPerPage){
		double totalPageF = new Double(totalRow)/new Double(maxPerPage);
		//System.out.println("totalPageF:"+totalPageF);
		BigDecimal totalPage = new BigDecimal(totalPageF);
		totalPage = totalPage.setScale(0, BigDecimal.ROUND_UP);
		
		return totalPage.intValue();
	}
	
	public static int calcStartNoInPage(int currentPage,int maxPerPage){
		return (((currentPage-1)*maxPerPage))+1;
	}
	
	public static String genFileName(String prefix){
		String fileName = "";
		try{
		  String dateTimeStr = Utils.stringValue(new Date(),DD_MM_YYYY_HH_mm_WITHOUT_SLASH , local_th);
		  fileName = prefix+"-"+dateTimeStr;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return fileName;
	}
	
	public static BigDecimal isNullToZero(BigDecimal str) {
		if (str ==null){
			//return new BigDecimal("0");// comment by tutiya
			return BigDecimal.ZERO;// modify by tutiya
		}
		return str;
	}
	
	public static String convertDigitToDisplay(String columnDisyplay , Object s) {
		//logger.debug("columnDisyplay:"+columnDisyplay);
		
		if("QTY".equalsIgnoreCase(columnDisyplay) || "QTY".equalsIgnoreCase(columnDisyplay)){
			if( s instanceof BigDecimal){
				return convertToNumberStr((BigDecimal)s);
			}else {
				return convertToNumberStr((Double)s);
			}
		}else{
			if( s instanceof BigDecimal){
				return convertToCurrencyStr((BigDecimal)s);
			}else {
				return convertToCurrencyStr((Double)s);
			}
		}
	}
	
	public static String convertDigitToDisplay(Object s) {
		//logger.debug("columnDisyplay:"+columnDisyplay);
		if( s instanceof BigDecimal){
			return convertToCurrencyStr((BigDecimal)s);
		}else {
			return convertToCurrencyStr((Double)s);
		}
	}
	
	public static String convertToNumberStr(BigDecimal s) {
		String currencyStr = "0";
		try{
			 if(s == null){
				 s = new BigDecimal("0");
			 }
			 Double d = new Double(s.doubleValue());
			 DecimalFormat dc=new DecimalFormat();
		     dc.applyPattern(NUMBER_FORMAT);
		     currencyStr =dc.format(d);
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	
	public static String convertToStr(double s) {
		String str = "0";
		try{
			str = String.valueOf(s);
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	public static String convertToNumberStr(double s) {
		String currencyStr = "0";
		try{
			 Double d = new Double(s);
			 DecimalFormat dc=new DecimalFormat();
		     dc.applyPattern(NUMBER_FORMAT);
		     currencyStr =dc.format(d);
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	
	public static String convertToCurrencyStr(String s) {
		String currencyStr = "0.00";
		try{
			if( !Utils.isNull(s).equals("")){
				 Double d = new Double(Utils.isNull(s));
				 DecimalFormat dc=new DecimalFormat();
			     dc.applyPattern(CURRENCY_FORMAT);
			     currencyStr =dc.format(d);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	
	public static String convertToCurrencyNoDigitStr(String s) {
		String currencyStr = "0.00";
		try{
			if( !Utils.isNull(s).equals("")){
				if(s.indexOf(",") != -1){
					s = s.replaceAll(",", "");
				}
				 Double d = new Double(Utils.isNull(s));
				 DecimalFormat dc=new DecimalFormat();
			     dc.applyPattern(CURRENCY_NODIGIT_FORMAT);
			     currencyStr =dc.format(d);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	
	
	public static String convertToCurrencyStr(BigDecimal s) {
		String currencyStr = "0.00";
		try{
			 if(s == null){
				 s = new BigDecimal("0");
			 }
			 Double d = new Double(s.doubleValue());
			 DecimalFormat dc=new DecimalFormat();
		     dc.applyPattern(CURRENCY_FORMAT);
		     currencyStr =dc.format(d);
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	
	public static String convertToCurrencyStr(double s) {
		String currencyStr = "0.00";
		try{
			 Double d = new Double(s);
			 DecimalFormat dc=new DecimalFormat();
		     dc.applyPattern(CURRENCY_FORMAT);
		     currencyStr =dc.format(d);
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	
	public static String convertToCurrencyStr(int s) {
		String currencyStr = "0.00";
		try{
			 Double d = new Double(s);
			 DecimalFormat dc=new DecimalFormat();
		     dc.applyPattern(CURRENCY_FORMAT);
		     currencyStr =dc.format(d);
		}catch(Exception e){
			e.printStackTrace();
		}
		return currencyStr;
	}
	

	public static int convertToYearBushdish(int christYear){
		return christYear+543;
	}
	
	public static boolean isDouble(String str) {  
		  try {  
			  BigDecimal obj = new BigDecimal(str);
		  }catch(NumberFormatException nfe)  {  
		    return false;  
		  }  
		  return true;  
		}
	
	public static boolean isNumeric(String str) {  
	  try {  
	      double d = Double.parseDouble(str);  
	  }catch(NumberFormatException nfe)  {  
	    return false;  
	  }  
	  return true;  
	}
	// Round halfup  2.979 -> 2.98
	public static String decimalFormat(double num){
		NumberFormat formatter = new DecimalFormat(format_current_2_disgit);
		return formatter.format(num);
	}
	
	// Round halfup  2.979 -> 2.98
	public static String decimalFormat(double num,String format){
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
	}
	
	// Round halfup  2.979 -> 2.98
	public static String decimalFormat(double num,String format,String defaultS){
		if(num==0 || num == 0.00 || num ==0.0)
			return defaultS;
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
	}
	
	// No Round  2.979 -> 2.97
	public static String decimalFormatNoRound(double num,String format){
		NumberFormat formatter = new DecimalFormat(format);
		//No Round 
		formatter.setRoundingMode(java.math.RoundingMode.DOWN);
		return formatter.format(num);
	}
		
	public static double strToDouble(String s){
		if(s == null || "".equals(s)){
			return Double.parseDouble("0");
		}
		//logger.debug("s.indexOf(,):"+s.indexOf(","));
		if(s.indexOf(",") != -1){
			s = s.replaceAll(",", "");	
		}
		//logger.debug("s:"+s);
	   return Double.parseDouble(s);
	}
	
	// Create Utility for rounding double value
	public static double round(double d, int decimalPlace,int roundType){
	    BigDecimal bd = new BigDecimal(Double.toString(d));
	    bd = bd.setScale(decimalPlace,roundType);
	    return bd.doubleValue();
	  }
	//20081223   09 42 34.572
	//2008-12-23 09:42:34.572000
	
	
	
	
	public static BigDecimal getCurrentTimestampLong() throws Exception{
		long curr = System.currentTimeMillis();
		Timestamp ti = new Timestamp(curr);
		System.out.println("currMil:"+curr +" new Big:"+new BigDecimal(curr));
		//String dateLong = stringValue(ti, YYYYMMDDHH_mm_ss_SSSSSS);
		//System.out.println("dateLong:"+dateLong+",BigDecimal["+(new BigDecimal(dateLong))+"]");
		return new BigDecimal(curr);
	}
	/**
	 * 
	 * @param provinceName  Exception Chrecter "¨."
	 * @return
	 */
	public static String replaceProvinceNameNotMatch(String provinceName){
		String result ="";
		try{
			if(Utils.isNull(provinceName).equals("")) return "";
			result = provinceName.replaceAll("[¨][.]", "");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return result.trim();
	}
	
	/**
	 * 
	 * @param districtName Exception Chrecter "Í.", "à¢µ."
	 * @return
	 */
	public static String replaceDistrictNameNotMatch(String districtName){
		String result ="";
		try{
			if(Utils.isNull(districtName).equals("")) return "";
			result = districtName.replaceAll("[Í][.]", "");
			result = result.replaceAll("[à¢µ][.]", "");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return result.trim();
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
	public static Date parse(String dateString, String format ,String locale) throws Exception {
		Date date = null;
		SimpleDateFormat ft = new SimpleDateFormat(format, new Locale(locale.toLowerCase()));
		
		try {
			date = ft.parse(dateString);
		} catch (Exception e) {
			
		}
		
		return date;
	}
	
	
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
	
	public static String stringValue(Date date, String format ,Locale locale) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			dateStr = ft.format(date);
		} catch (Exception e) {
		}
		return dateStr;
	}
	
	public static String stringValueNull(Date date, String format ,Locale locale) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			if(date != null){
			   dateStr = ft.format(date);
			}else{
			   dateStr = "";
			}
		} catch (Exception e) {
		}
		return dateStr;
	}
	
	public static String stringValueSpecial(long dateBigdecimal, String format ,Locale locale) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			//logger.debug("date Long>>"+dateBigdecimal.doubleValue());
			//logger.debug("date Long>>"+dateBigdecimal.doubleValue());
			Timestamp ti = new Timestamp(dateBigdecimal);
			//logger.debug("date timestamp>>"+ti);
			dateStr = ft.format(ti);
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
	
	public static java.sql.Date getSqlCurrentDate() {
		return new java.sql.Date(System.currentTimeMillis());
	}
	
	public static String getCurrentMonth() {
		String mm = "";
		Calendar c = Calendar.getInstance(local_th);
		mm = (c.get(Calendar.MONTH)+1)+"";
		mm = mm.length()==1?"0"+mm:mm;
		return mm;
	}
	
	public static String format(Date date, String pattern) {
		if (date == null) {
			return "";
		}
		return new SimpleDateFormat(pattern, Locale.US).format(date);
	}

	public static String isNull(String str) {
		if (str ==null){
			return "";
		}
		return str.trim();
	}
	
	public static String isNull(int str) {
		if (str ==0){
			return "";
		}
		return String.valueOf(str);
	}
	
	public static String isNull(Object str) {
		if (str ==null){
			return "";
		}
		if(str instanceof String){
			return ((String)str).trim();
		}else if(str instanceof Double){
			return ((Double)str).toString().trim();
		}else{
			return ((String)str).trim();
		}
	}
	
	//input 111.0 
	//output 111
	public static String convertDoubleToStrNoDigit(Object str) {
		String strNoDigit = "";
		strNoDigit = isNull(str);
		if(strNoDigit.indexOf(".") != -1){
			strNoDigit = strNoDigit.substring(0,strNoDigit.indexOf("."));
		}
		
		return strNoDigit;
	}
	
	public static int convertToInt(String str) throws Exception{
		logger.debug("xx:"+str);
		if(str == null || Utils.isNull(str).equals("")){
			return 0;
		}
		return Integer.parseInt(str);
	}
	
	public static int convertCurrentcyToInt(String str) throws Exception{
		int output = 0;
		if(str == null || Utils.isNull(str).equals("")){
			return 0;
		}
		output =(new Double(str.replaceAll(",", ""))).intValue();
		logger.debug("input:"+str+",output:"+output);
		return output;
	}
	
	public static double convertToDouble(String str) throws Exception{
		if(str == null){
			return 0;
		}
		return Double.parseDouble(str);
	}
	
	public static long convertToLong(String str) throws Exception{
		if(str == null || isNull(str).equals("")){
			return 0;
		}
		return Long.parseLong(str);
	}

	//in :8.850009281274E12
	//out:8850009281274
	public static String convertStrToDoubleStr(String doubleStr){
		if(doubleStr == null){
			return "";
		}
		BigDecimal dd = new BigDecimal(doubleStr);
		doubleStr = String.valueOf(dd.longValue());
		//logger.debug("doubleStr:"+doubleStr);
		if(doubleStr.indexOf(".") != -1){
		  doubleStr = doubleStr.substring(0,doubleStr.indexOf("."));
		}
		//logger.debug("return doubleStr:"+doubleStr);
		return doubleStr;
	}
	//input  1.0 
	//output 1
	public static String convertDoubleStrToStr(String doubleStr){
		if(doubleStr == null){
			return "";
		}
		//logger.debug("doubleStr:"+doubleStr);
		if(doubleStr.indexOf(".") != -1){
		  doubleStr = doubleStr.substring(0,doubleStr.indexOf("."));
		}
		//logger.debug("return doubleStr:"+doubleStr);
		return doubleStr;
	}
	
	public static String convertDoubleToStr(Double double1){
		if(double1 == null){
			return "";
		}
		String doubleStr = double1.longValue()+"";
		//logger.debug("doubleStr:"+doubleStr);
		
		if(doubleStr.indexOf(".") != -1){
		  doubleStr = doubleStr.substring(0,doubleStr.indexOf("."));
		}
		//logger.debug("return doubleStr:"+doubleStr);
		return doubleStr;
	}
	
	/**
	 * 
	 * @param double1
	 * @return
	 * Case1 input 100.20 return 100.20
	 * case2 input 100.00 return 100
	 */
	public static String convertToNumberSpecial(BigDecimal double1){

		String doubleStr = double1.toString();
		//logger.debug("doubleStr:"+doubleStr);
		
		if(doubleStr.indexOf(".") != -1){
		   String num1 = doubleStr.substring(0,doubleStr.indexOf("."));
		   String num2 ="0";
		   if( (doubleStr.length()) > (doubleStr.indexOf(".")+3) ){
	        	num2= doubleStr.substring(doubleStr.indexOf(".")+1,(doubleStr.indexOf(".")+3));
	        }else{
	        	num2= doubleStr.substring((doubleStr.indexOf(".")+1),(doubleStr.indexOf(".")+2));
	        }
		  
		  // logger.debug("num1="+num1+",num2="+num2);
		   return num1+"."+num2;
		}
		return doubleStr;
	}
	
	
	public static double convertStrToDouble(String str){
		if(isNull(str).equals("")){
			return 0;
		}
		str = str.replaceAll(",", "");
		return new Double(str).doubleValue();
	}
	
	public static double convertStrToDouble2Digit(String str){
		if(isNull(str).equals("")){
			return 0;
		}
		str = str.replaceAll(",", "");
		return new Double(str).doubleValue();
	}
	
	public static String isNull(Double str) {
		if (str ==null){
			return "";
		}
		return str.toString();
	}
	
	public static Double isDoubleNull(Object str) {
		if (str ==null || isNull(str).equals("")){
			return new Double(0);
		}
		logger.debug("str:"+str);
		return ((Double)str);
	}
	
	public static Double isDoubleNoDigitNull(Object str) {
		if (str ==null || isNull(str).equals("")){
			return new Double(0);
		}
		//logger.debug("str:"+str);
		return ((Double)str);
	}
	
	public static Double isDoubleNull2Digit(Object str) {
		if (str ==null || isNull(str).equals("")){
			return new Double(0);
		}
		String strTemp = str.toString();
		int diff  = strTemp.length() - strTemp.indexOf(".");
		strTemp = strTemp.substring(0,strTemp.indexOf(".")+diff);
		//logger.debug("isDoubleNull:"+(new Double(strTemp)));
		return new Double(strTemp);
	}
	
	public static Double isDoubleNull(String str) {
		if (str ==null || isNull(str).equals("")){
			return new Double(0);
		}
		str = str.replaceAll(",", "");
		//logger.debug("isDoubleNull:"+(new Double(str)));
		return (new Double(str));
	}
	
	public static int convertStrToInt(String str) {
		if (str ==null || "".equals(str)){
			return 0;
		}
		str= str.replaceAll("\\,", "");
		return Integer.parseInt(str);
	}
	
	public static BigDecimal convertStrToBigDecimal(String str) {
		if (str ==null || "".equals(str)){
			return new BigDecimal("0");
		}
		str= str.replaceAll("\\,", "");
		return new BigDecimal(str.trim());
	}
	
	public static int convertStrToInt(String str,int defaultInt) {
		if (str.equalsIgnoreCase("null") || str ==null || "".equals(str)){
			return defaultInt;
		}
		str= str.replaceAll("\\,", "");
	
		return Integer.parseInt(str);
	}
	
	public static boolean isBlank(Object o) {
		boolean r = false;
		if (o == null) {
			return true;
		}

		if (o instanceof String) {
			String s= (String)o;
			if ("".equals(s.trim())) {
				return true;
			}
		} 
		return r;
	}
	
	public static String toUnicodeChar(String s){
        try{
            StringBuffer strx = new StringBuffer("");
            for(int i = 0; i < s.length(); i++){
                char c = s.charAt(i);
                int cc = (int)c;
                String uc = "";
                if(c >= '\u0E00' && c <= '\u0E5F'){
                    //logger.debug("is unicode");
                    cc = (int)c;     
                    String hx = "0"+Integer.toHexString(cc).toUpperCase(); 
                    uc = "\\u" + hx;
                }else  if((int)c >= 160 && (int)c <= 255){ 
                    //logger.debug("is ASCII");
                    c += 3424;
                    cc = (int)c;     
                    String hx = "0"+Integer.toHexString(cc).toUpperCase(); 
                    uc = "\\u" + hx;
                }else{
                    uc = String.valueOf(c); //uc + Integer.toHexString(cc);
                }
                
               
            	strx.append(uc);
            }
            
            return strx.toString();
        }catch(Exception e){
        	 logger.error(e.getMessage(),e);
            return "";
        }
    }
    
    
	public static String UnicodeToASCII(String unicode) {
	//		initial temporary space of ascii.
			StringBuffer ascii = new StringBuffer(unicode);
			int code; 
	
	//		continue loop based on number of character.
			for (int i = 0; i < unicode.length(); i++) {
	//		reading a value of each character in the unicode (as String).
			code = (int) unicode.charAt(i); 
	
	//		check the value is Thai language in Unicode scope or not.
			if ((0xE01 <= code) && (code <= 0xE5B)) {
	//		if yes, it will be converted to Thai language in ASCII scope.
			 ascii.setCharAt(i, (char) (code - 0xD60));
			}
		  }
			return ascii.toString();
		} 

		public static String ASCIIToUnicode(String ascii) {
	//		initial temporary space of unicode
			StringBuffer unicode = new StringBuffer(ascii);
			int code; 
	
	//		continue loop based on number of character.
			for (int i = 0; i < ascii.length(); i++) {
			code = (int) ascii.charAt(i); 
	
	//		check the value is Thai language in ASCII scope or not.
			if ((0xA1 <= code) && (code <= 0xFB)) {
	//		if yes, it will be converted to Thai language in Unicode scope.
			unicode.setCharAt(i, (char) (code + 0xD60));
			}
			} 
	
	//		convert unicode to be as String type to use continue.
			return unicode.toString();
		}
		
    public static boolean isAscii(String str){
    	int code; 
    	for (int i = 0; i < str.length(); i++) {
			code = (int) str.charAt(i); 
	
	//		check the value is Thai language in ASCII scope or not.
			if ((0xA1 <= code) && (code <= 0xFB)) {
	//		if yes, it will be converted to Thai language in Unicode scope.
			   return true;
			}
		} 
    	return false;
    }
    
    public static boolean isUnicode(String str){
    	int code; 
    	for (int i = 0; i < str.length(); i++) {
			code = (int) str.charAt(i); 
	//		check the value is Thai language in Unicode scope or not.
			if ((0xE01 <= code) && (code <= 0xE5B)) {

			   return true;
			}
		} 
    	return false;
    }
  
	public static String getStringInStrArray(String[] arr ,int position) {
		String str = "";
		try{
			str =arr[position];
		}catch(Exception e){
		}
		return str;
	}
	
	public static String removeStringEnter(String str){
		if(str ==null){
			return "";
		}
		return str.replaceAll("\\n", "").trim();
	}
	
	public static String getNumberOnly(String str){
		if(str ==null){
			return "";
		}
		return str.replaceAll("\\D", "").trim();
	}
	
	
	public static String excQuery(String sql) {
	    PreparedStatement ps =null;
		ResultSet rs = null;
        Connection conn = null;
        ResultSetMetaData rsm = null;
        int columnCount = 0;
        StringBuffer str = new StringBuffer("");
		try{
			System.out.println("sql:"+sql);   
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			rsm = rs.getMetaData();
			columnCount = rsm.getColumnCount();
			
			//getColumnHeader 
			 str.append("<table align='center' border='1' cellpadding='3' cellspacing='1' class='result'> \n");
			 str.append("<tr>");  
			 for(int i=1;i<=columnCount;i++){
				    //System.out.println("["+i+"]"+rsm.getColumnName(i));
				    str.append("<th> \n");
	            	str.append(rsm.getColumnName(i));
	            	str.append("</th>");
			   }
			 str.append("</tr> \n"); 
			 
			 //Gen Detail
			 while(rs.next()){
				 str.append("<tr> \n");  
				 for(int i=1;i<=columnCount;i++){
					    str.append("<td class='lineE'>");
		            	str.append(isNull(rs.getString(rsm.getColumnName(i))));
		            	str.append("</td>");
				   }
				 str.append("</tr> ");  
			 }
			
			str.append("</table> \n");
			
		}catch(Exception e){
	      e.printStackTrace();
	      str.append("ERROR: \n"+e.getMessage());
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
				if(rs != null){
				   rs.close();rs = null;
				}
				
				if(conn != null){
					conn.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str.toString();
  }
	
	
	public static String excUpdate(String sql) {
	    PreparedStatement ps =null;
        Connection conn = null;
        StringBuffer str = new StringBuffer("");
		try{  
			conn = DBConnection.getInstance().getConnection();
			String[] sqlArr = sql.split("\\;");
			if(sqlArr != null && sqlArr.length>0){
			   for(int i=0;i<sqlArr.length;i++){
				 
				 if( !isNull(sqlArr[i]).equals("")){
				     ps = conn.prepareStatement(sqlArr[i]);
				     int recordUpdate = ps.executeUpdate();
				     str.append("<br>["+i+"] SQL Execute  :"+sqlArr[i]);
				     str.append("<br>- Result Effect:"+recordUpdate+"");
			     }
			   }
			}
		}catch(Exception e){
	      e.printStackTrace();
	      str.append("ERROR: \n"+e.getMessage());
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
				if(conn != null){
					conn.close();
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return str.toString();
  }
	
	public static int excUpdateOneSql(Connection conn,String sqlOne) {
	    PreparedStatement ps =null;
	    int recordUpdate = 0;
		try{  
		    ps = conn.prepareStatement(sqlOne);
			recordUpdate = ps.executeUpdate();
		}catch(Exception e){
	      logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return recordUpdate;
  }
		
	/**
	 * 
	 * @param value
	 * @param fixLength
	 * @return
	 * @throws Exception
	 * Ex: value:100 ,FixLength :10
	 * Result:[XXXXXXX100]
	 */
	public static String appendLeft(String value,String cAppend ,int fixLength) throws Exception{
		int i = 0;
		String newValue  ="";
		int loopAppend = 0;
		if(value.length() < fixLength){
			loopAppend = fixLength - value.length();
		}
		if(value.length()> fixLength){
			value = value.substring(0,fixLength);
		}else{
			for(i=0;i<loopAppend;i++){
				newValue +=cAppend;
			}
		}
		newValue +=value;
		return newValue;
	}
	/**
	 * 
	 * @param value
	 * @param fixLength
	 * @return
	 * @throws Exception
	 * Ex: value:100 ,FixLength :10
	 * Result:[100XXXXXXX]
	 */
	public static String appendRight(String value,String cAppend ,int fixLength) throws Exception{
		int i = 0;
		int loopAppend = 0;
		if(value.length() < fixLength){
			loopAppend = fixLength - value.length();
		}
		if(value.length()> fixLength){
			value = value.substring(0,fixLength);
		}else{
			for(i=0;i<loopAppend;i++){
				value +=cAppend;
			}
		}
		return value;
	}
	
	public static String converToTextSqlIn(String value){
		
		List<String> valuesText = new ArrayList<String>() ;
		String[] values = value.split("[,]");
		
		for(String text : values){
			valuesText.add("'"+text+"'");
		}
		
		return StringUtils.join(valuesText, ","); 
	}
	
   public static String converToTextSqlIn(List<StoreBean> value){
		List<String> valuesText = new ArrayList<String>() ;
		
		if(value != null && value.size() >0){
			for(int i=0;i<value.size();i++){
				StoreBean s= value.get(i);
				valuesText.add("'"+s.getStoreCode()+"'");
			}
		}
		return StringUtils.join(valuesText, ","); 
	}
   
 //Case null retun ""
 	public static String stringValueSpecial2(long dateBigdecimal, String format ,Locale locale) throws Exception {
 		String dateStr = "";		
 		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
 		try {
 			logger.debug("dateBigdecimal:"+dateBigdecimal);
 			if(dateBigdecimal != 0.0){
 			   Timestamp ti = new Timestamp(dateBigdecimal);
 			   logger.debug("date timestamp>>"+ti);
 			   dateStr = ft.format(ti);
 			}
 		} catch (Exception e) {
 		}
 		return dateStr;
 	}
 	public static String[] isNullArray(Object obj){
		if(obj == null)
		  return null;
		String[] str = (String[]) obj;
		return str;
	}
	public static Date getDate(String strDateTime, String pattern) {
		try {
		if (strDateTime != null && strDateTime.length() > 0) {
		SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
		Date dtDate = format.parse(strDateTime);

		return dtDate;

		} else {
		return null;
		}
		} catch (Exception ex) {
		ex.printStackTrace();
		}
		return null;
		}
	
	
	public static String isNullDefaultZero(Object obj){
		if(obj == null)
		  return "0";
		String str = (String) obj;
		return str.trim();
	}
}