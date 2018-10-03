
package com.isecinc.pens.inf.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import org.apache.log4j.Logger;

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
	public static final String YYYY_MM_DD_WITH_LINE = "yyyy-MM-dd";
	public static final String YYYY_MM_DD_WITHOUT_SLASH = "yyyyMMdd";
	public static final String DD_MM_YYYY_WITHOUT_SLASH = "ddMMyyyy";
	public static final String DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH = "ddMMyyyy HHmmss";
	public static final String DD_MM_YYYY_HH_mm_WITHOUT_SLASH = "ddMMyyyyHHmm";
	public static final String DD_MM_YYYY_HHmmss_WITHOUT_SLASH = "ddMMyyyyHHmmss";
	
	public static final String YYYYMMDDHH_mm_ss_SSSSSS = "yyyyMMddHHmmss.SSSSSS";
	public static final String DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH = "dd/MM/yyyy  HH:mm:ss:SSSSSS";
	public static final String YY_MM = "yyMM";
	
	public static final Locale local_th = new Locale("th","TH");
	public static final String format_num_no_disgit = "#";
	public static final String format_current_no_disgit = "#,##0";
	public static final String format_current_2_disgit = "#,##0.00";
    public static final String format_current_5_digit = "#,##0.00000";
	public static final String format_current_6_digit = "#,##0.000000";
	
	//20081223   09 42 34.572
	//2008-12-23 09:42:34.572000

	public static void main(String[] args){
	    try{	   
	    	double r = 0.001;
	    	if(r > 0){
	    		System.out.println("more than 0");
	    	}
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
   public static boolean isInternetConnect(String urlTest){
        boolean r = true;
        try{
            URL url = new URL(urlTest);
            url.openConnection();
            url.openStream();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return r;
    }
	  
	public static int calcTotalPage(int totalRow,int maxPerPage) throws Exception{
		try{
			double totalPageF = new Double(totalRow)/new Double(maxPerPage);
			//System.out.println("totalPageF:"+totalPageF);
			BigDecimal totalPage = new BigDecimal(totalPageF);
			totalPage = totalPage.setScale(0, BigDecimal.ROUND_UP);
			
			/// Find Total Page
			logger.info("totalRecord["+totalRow+"]/["+(maxPerPage)+"]totalPage["+totalPage+"]");
			return totalPage.intValue();
		}catch(Exception e){
			throw e;
		}
	}
	
	public static void stopTomcat(){
	      String line;
	      OutputStream stdin = null;
	      InputStream stderr = null;
	      InputStream stdout = null;
	      try{
	          //Runtime.getRuntime().exec("taskkill /IM "+ );
	          System.out.println("stopTomcat");
	          String command = "taskkill /f /IM tomcat6.exe";
	          Process process = Runtime.getRuntime().exec(command);

	          String command2 = "taskkill /f /IM cmd.exe";
	          process = Runtime.getRuntime().exec(command2);
	          
	            stdin = process.getOutputStream ();
	            stderr = process.getErrorStream ();
	            stdout = process.getInputStream ();

	            // "write" the parms into stdin
	            line = "param1" + "\n";
	            stdin.write(line.getBytes() );
	            stdin.flush();

	            line = "param2" + "\n";
	            stdin.write(line.getBytes() );
	            stdin.flush();

	            line = "param3" + "\n";
	            stdin.write(line.getBytes() );
	            stdin.flush();

	           // System.out.println("stdin:"+stdin.toString());
	            stdin.close();

	            // clean up if any output in stdout
	            BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stdout));
	            while ((line = brCleanUp.readLine ()) != null) {
	              System.out.println ("[Stdout] " + line);
	            }
	            brCleanUp.close();

	            // clean up if any output in stderr
	            brCleanUp =new BufferedReader (new InputStreamReader (stderr));
	            while ((line = brCleanUp.readLine ()) != null) {
	              System.out.println ("[Stderr] " + line);
	            }
	            brCleanUp.close();
	            
	       }catch(Exception e){
	          e.printStackTrace();
	       }  
	    }
	   
	    public static void startTomcat(){
	        String line;
	        OutputStream stdin = null;
	        InputStream stderr = null;
	        InputStream stdout = null;
	        String command = "";
	        String pathTomcat = "C:/Program Files/Apache Software Foundation/Tomcat 6.0/webapps/";
	        String pathTomcatX64 = "C:/Program Files (x86)/Apache Software Foundation/Tomcat 6.0/webapps/";
	        String windowType ="x86";
	        try{
	        	//Validate Path Tomcat Case Windwo 64 bit
	            if( !FileUtil.isFolderExist(pathTomcat)){
	               pathTomcat = pathTomcatX64; 
	               windowType = "x64";
	            }
	            
	            System.out.println("startTomcat");
	            //Runtime.getRuntime().exec("cmd ");
	            System.out.println("windowType:"+windowType);
	            if("x64".equals(windowType)){
	              command = "cmd /c start d:/salesApp/SalesAppUpdater/startTomcatX64.bat_sh.lnk";
	              //command = "cmd /c start C:\\\\Program Files (x86)\\\\Apache Software Foundation\\\\Tomcat 6.0\\\\bin\\\\tomcat6.exe";
	            }else{
	              command = "cmd /c start d:/salesApp/SalesAppUpdater/startTomcat.bat_sh.lnk";
	              // command = "start C:\\\\Program Files\\\\Apache Software Foundation\\\\Tomcat 6.0\\\\bin\\\\tomcat6.exe";
	            }
	            Process process = Runtime.getRuntime().exec(command);
	            
	           // System.out.println("startTomcat:"+child.exitValue());
	            stdin = process.getOutputStream ();
	            stderr = process.getErrorStream ();
	            stdout = process.getInputStream ();

	            // "write" the parms into stdin
	            line = "param1" + "\n";
	            stdin.write(line.getBytes() );
	            stdin.flush();

	            line = "param2" + "\n";
	            stdin.write(line.getBytes() );
	            stdin.flush();

	            line = "param3" + "\n";
	            stdin.write(line.getBytes() );
	            stdin.flush();

	           // System.out.println("stdin:"+stdin.toString());
	            stdin.close();

	            // clean up if any output in stdout
	            BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stdout));
	            while ((line = brCleanUp.readLine ()) != null) {
	              System.out.println ("[Stdout] " + line);
	            }
	            brCleanUp.close();

	            // clean up if any output in stderr
	            brCleanUp =new BufferedReader (new InputStreamReader (stderr));
	            while ((line = brCleanUp.readLine ()) != null) {
	              System.out.println ("[Stderr] " + line);
	            }
	            brCleanUp.close();
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	   }
	public static boolean isInternetConnect(){
		boolean r = true;
		try{
		   InetAddress addr = InetAddress.getByName("www.google.co.th");
		   //System.out.println(addr.getHostName());
		}catch(Exception e){
			//e.printStackTrace();
			r = false;
		}
		return r;
	}
	public static boolean isInternetConnect_bk(){
		boolean r = true;
		try{
			 int timeout = 2000;
			 InetAddress[] addresses = InetAddress.getAllByName("www.google.com");
			 for (InetAddress address : addresses) {
			    if (address.isReachable(timeout)){
			      System.out.printf("%s is reachable%n", address);
			      r = true;
			      break;
			    }else{
			      System.out.printf("%s could not be contacted%n", address);
			      r = false;
			      break;
			    }
			 }
		}catch(Exception e){
			//e.printStackTrace();
			r = false;
		}
		return r;
	}
	
	public static String decimalFormat(double num){
		NumberFormat formatter = new DecimalFormat(format_current_2_disgit);
		return formatter.format(num);
	}
	
	public static String decimalFormat(double num,String format){
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
	}
	
	public static <T extends Appendable> T escapeNonLatin(CharSequence sequence,
	      T out) throws java.io.IOException {
	    for (int i = 0; i < sequence.length(); i++) {
	      char ch = sequence.charAt(i);
	      if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.BASIC_LATIN) {
	        out.append(ch);
	      } else {
	        int codepoint = Character.codePointAt(sequence, i);
	        // handle supplementary range chars
	        i += Character.charCount(codepoint) - 1;
	        // emit entity
	        out.append("&#x");
	        out.append(Integer.toHexString(codepoint));
	        out.append(";");
	      }
	    }
	    return out;
	  }
	
	public static String[] getCurrentDatebuddhistSplitDDMMYYYY(){
		String[] d = new String[3];
		Calendar c = Calendar.getInstance(Locale.US);
		logger.debug("currentYear:"+String.valueOf(c.get(Calendar.YEAR)));
		try{
			d[0] = String.valueOf(c.get(Calendar.DATE)).length()==1?"0"+String.valueOf(c.get(Calendar.DATE)):String.valueOf(c.get(Calendar.DATE));
			d[1] = String.valueOf(c.get(Calendar.MONTH)).length()==1?"0"+String.valueOf(c.get(Calendar.MONTH)+1):String.valueOf(c.get(Calendar.MONTH)+1);
			d[2] = String.valueOf(c.get(Calendar.YEAR)).length()==1?"0"+String.valueOf(c.get(Calendar.YEAR)+543):String.valueOf(c.get(Calendar.YEAR)+543);
			
		}catch(Exception e){
			
		}
		return d;
	}
	
	public static void reconveryReport(String[] args){
	    try{	       
	    	String sourcePath = "c:/move_order_req_report.jasper";

	    	String outputPath = "c:/move_order_req_report.jrxml";

	    	JasperReport report = (JasperReport) JRLoader.loadObject(sourcePath);

	    	JRXmlWriter.writeReport(report, outputPath, "UTF-8");

	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	public static BigDecimal getCurrentTimestampLong() throws Exception{
		long curr = System.currentTimeMillis();
		Timestamp ti = new Timestamp(curr);
		System.out.println("currMil:"+curr +" new Big:"+new BigDecimal(curr));
		//String dateLong = stringValue(ti, YYYYMMDDHH_mm_ss_SSSSSS);
		//System.out.println("dateLong:"+dateLong+",BigDecimal["+(new BigDecimal(dateLong))+"]");
		return new BigDecimal(curr);
	}
	
	public static java.sql.Date getCurrentSqlDate() throws Exception{
		return new java.sql.Date(new Date().getTime());
	}
	/**
	 * 
	 * @param provinceName  Exception Character "¨."
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
	 * @param districtName Exception Character "Í.", "à¢µ."
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
	
	public static Date parseCheckNull(String dateString, String format) throws Exception {
		Date date = null;
		SimpleDateFormat ft = new SimpleDateFormat(format, Locale.US);
		try {
			if( !isNull(dateString).equals(""))
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
			logger.error(e.getMessage(),e);
		}
		return date;
	}
	public static Date parseCheckNull(String dateString, String format ,Locale locale) throws Exception {
		Date date = null;
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			if( !isNull(dateString).equals(""))
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
	
	public static String stringValueDefault(Date date, String format ,Locale locale,String defaultS) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			if(date==null){
				return defaultS;
			}
			dateStr = ft.format(date);
		} catch (Exception e) {
		}
		return dateStr;
	}
	
	public static String stringValueSpecial(long dateBigdecimal, String format ,Locale locale) throws Exception {
		String dateStr = null;		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			Timestamp ti = new Timestamp(dateBigdecimal);
			//logger.debug("date timestamp>>"+ti);
			dateStr = ft.format(ti);
		} catch (Exception e) {
		}
		return dateStr;
	}
	
	//Case null retun ""
	public static String stringValueSpecial2(long dateBigdecimal, String format ,Locale locale) throws Exception {
		String dateStr = "";		
		SimpleDateFormat ft = new SimpleDateFormat(format, locale);
		try {
			//logger.debug("dateBigdecimal:"+dateBigdecimal);
			if(dateBigdecimal != 0.0){
			   Timestamp ti = new Timestamp(dateBigdecimal);
			   //logger.debug("date timestamp>>"+ti);
			   dateStr = ft.format(ti);
			}
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
	
	public static boolean isLocationValid(String str) {
		boolean r= true;
		if (str ==null || Utils.isNull(str).equals("")){
			return false;
		}
		//logger.debug("before 1str:"+str);
		str = str.replaceAll("\\,", "");
		//logger.debug("before 2str:"+str);
		str = str.replaceAll("\\.", "");
		//logger.debug("before 3str:"+str);
		str = str.replaceAll(" ", "");
		//logger.debug("after str:"+str.trim());
		try{
			BigDecimal c = new BigDecimal(str);
		}catch(Exception e){
			//e.printStackTrace();
			r= false;
		}
		return r;
	}
	
	public static String isNull(String str) {
		if (str ==null){
			return "";
		}
		return str.trim();
	}
	
	
	public static String isNull(Object str) {
		if (str ==null){
			return "";
		}
		return ((String)str);
	}
	
	public static String isNullInt(int str) {
		if (str ==0){
			return "";
		}
		return String.valueOf(str);
	}
	
	public static String isNullBig(BigDecimal str) {
		if (str == null){
			return "";
		}
		return String.valueOf(str);
	}
	
	public static int convertStrToInt(String str) {
		if (str ==null || "".equals(str)){
			return 0;
		}
		str = str.replaceAll(",", "");
		return Integer.parseInt(str);
	}
	
	public static String convertDoubleToStrDefault(double d,String defaultStr) {
		//logger.debug("d["+d+"]");
		if (d ==0 || 0.0==d){
			return defaultStr;
		}
		return decimalFormat(d,format_current_no_disgit);
	}
	public static String convertIntToStrDefault(int d,String defaultStr) {
		//logger.debug("d["+d+"]");
		if (d ==0 ){
			return defaultStr;
		}
		return decimalFormat(d,format_current_no_disgit);
	}
	
	public static double convertStrToDouble(String str) {
		if (str ==null || "".equals(str)){
			return 0;
		}
		str = str.replaceAll(",", "");
		return new Double(str).doubleValue();
	}
	
	public static BigDecimal convertStrToBig(String str) {
		if (str ==null || "0".equals(str) || "0.00".equals(str) || isNull(str).equals("")){
			return null;
		}
		str = str.replaceAll(",", "");
		return new BigDecimal(str);
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
			 str.append("<table align='center' border='1' cellpadding='3' cellspacing='1' class='result'>");
			 str.append("<tr>");  
			 for(int i=1;i<=columnCount;i++){
				    //System.out.println("["+i+"]"+rsm.getColumnName(i));
				    str.append("<th>");
	            	str.append(rsm.getColumnName(i));
	            	str.append("</th>");
			   }
			 str.append("</tr>"); 
			 
			 //Gen Detail
			 while(rs.next()){
				 str.append("<tr>");  
				 for(int i=1;i<=columnCount;i++){
					    str.append("<td class='lineE'>");
		            	str.append(isNull(rs.getString(rsm.getColumnName(i))));
		            	str.append("</td>");
				   }
				 str.append("</tr>");  
			 }
			
			str.append("</table>");
			
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
	public static int excUpdateReInt(Connection conn,String sql) {
		  return excUpdateModelReInt(conn,sql);
	}
	
   public static String excUpdate(String sql) {
	   Connection conn = null;
	   try{
			conn = DBConnection.getInstance().getConnection();
			  return excUpdateModel(conn,sql);
	   }catch(Exception e){
		   logger.error(e.getMessage(),e);
	   }finally{
		   try{
			  if(conn != null){
				  conn.close();
			   }
		   }catch(Exception e){}
	   }
	  return "";
	}
   
	public static String excUpdateModel(Connection conn,String sql) {
	    PreparedStatement ps =null;
        StringBuffer str = new StringBuffer("");
		try{  
		
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
				
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return str.toString();
  }
	
	public static int excUpdateModelReInt(Connection conn,String sql) {
	    PreparedStatement ps =null;
	    int recordUpdate = 0;
		try{  
			String[] sqlArr = sql.split("\\;");
			if(sqlArr != null && sqlArr.length>0){
			   for(int i=0;i<sqlArr.length;i++){
				 if( !isNull(sqlArr[i]).equals("")){
				     ps = conn.prepareStatement(sqlArr[i]);
				      recordUpdate = ps.executeUpdate();
			     }
			   }
			}
		}catch(Exception e){
	      e.printStackTrace();
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
		
}