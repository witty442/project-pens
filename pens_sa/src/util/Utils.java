
package util;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;

/**
 *	General utilities.
 *	@author Witty
 */
public class Utils {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static final String DD_MM_YYYY_WITH_SLASH = "dd/MM/yyyy";
	public static final String YYYY_MM_DD_WITH_SLASH = "yyyy/MM/dd";
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
	
	@SuppressWarnings("unused")
	private static String DECIMAL_FORMAT ="#.00000000000000000000";
	private static String CURRENCY_FORMAT ="#,##0.00";
	private static String NUMBER_FORMAT ="#,##0";
	
	public static final String format_number_no_digit = "###0";
	public static final String format_current_no_disgit = "#,##0";
	public static final String format_current_2_disgit = "#,##0.00";
    public static final String format_current_5_digit = "#,##0.00000";
	public static final String format_current_6_digit = "#,##0.000000";
	
	public static void main(String[] args){
	    try{	       
	        String date = "ดูข้อมูลได้ทั้งหมด";
	        System.out.println(toUnicodeChar(date));
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	public static int convertStrToInt(String str) {
		if (str ==null || "".equals(str)){
			return 0;
		}
		str= str.replaceAll("\\,", "");
		return Integer.parseInt(str);
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
   public static String converToTextSqlIn(String value){
		
		List<String> valuesText = new ArrayList<String>() ;
		String[] values = value.split("[,]");
		
		for(String text : values){
			valuesText.add("'"+text+"'");
		}
		
		return StringUtils.join(valuesText, ","); 
	}
	public static double convertStrToDouble(String str){
		if(isNull(str).equals("")){
			return 0;
		}else{
			//logger.debug("doubleStr:"+str);
		}
		str = str.replaceAll(",", "");
		return new Double(str).doubleValue();
	}
	public static long convertStrToLong(String str,long defaults){
		if(isNull(str).equals("")){
			return defaults;
		}
		str = str.replaceAll(",", "");
		return new Long(str).longValue();
	}
	
	public static double convertStrToDouble2Digit(String str){
		if(isNull(str).equals("")){
			return 0;
		}
		str = str.replaceAll(",", "");
		return new Double(str).doubleValue();
	}
	
	public static String convertStrDoubleToStr(String str,String format){
		if(isNull(str).equals("")){
			return "";
		}
		str = str.replaceAll(",", "");
		return  decimalFormat(new Double(str).doubleValue(),format);
	}
	
	public static boolean statusInCheck(String status,String[] statusCheckArr){
		boolean r = false;
		for(int i=0;i<statusCheckArr.length;i++){
			String statusCheck = statusCheckArr[i].toLowerCase().trim();
			String statusArr[] = status.split("\\/");

			for(int j =0;j<statusArr.length;j++){
				String statusArrTemp = statusArr[j];
				//logger.debug("roleCheck:["+i+"]["+roleCheck+"]["+userRole+"]");
				
				if( statusCheck.equalsIgnoreCase(statusArrTemp)){
					//logger.debug("EQ =roleCheck["+roleCheck+"]:["+i+"]["+userRole+"]");
					r =  true;
					break;
				}
			}//for 2
			
		}//for 1
		return r;
	}
	
	public static String decimalFormat(double num,String format){
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
	}
	public static String decimalFormat(double num,String format,String defaultS){
		if(num==0 || num == 0.00 || num ==0.0)
			return defaultS;
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
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
	
	
	
	public static String isNull(String str) {
		if (str ==null){
			return "";
		}
		return str.trim();
	}
	
	public static String isNullDoubleStrToZero(String doubleStr) {
		if (doubleStr ==null || isNull(doubleStr).equals("")){
			return "0.00";
		}
		return doubleStr;
	}
	public static String isNullDoubleStrToBlank(String doubleStr) {
		if (doubleStr ==null || isNull(doubleStr).equals("")
			|| isNull(doubleStr).equals("0") || isNull(doubleStr).equals("0.00")){
			return "";
		}
		return doubleStr;
	}
	public static String isNull(Object str) {
		if (str ==null){
			return "";
		}
		return ((String)str);
	}
	
	public static BigDecimal isNullToZero(BigDecimal str) {
		if (str ==null){
			//return new BigDecimal("0");// comment by tutiya
			return BigDecimal.ZERO;// modify by tutiya
		}
		return str;
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
	public static String excQueryApps(String sql) {
	    PreparedStatement ps =null;
		ResultSet rs = null;
        Connection conn = null;
        ResultSetMetaData rsm = null;
        int columnCount = 0;
        StringBuffer str = new StringBuffer("");
		try{
			System.out.println("sql:"+sql);   
			conn = DBConnection.getInstance().getConnectionApps();
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
				e.printStackTrace();
			}
		}
		return str.toString();
  }
	
	
	public static String convertDigitToDisplay(String columnDisyplay , Object s) {
		//logger.debug("columnDisyplay:"+columnDisyplay);
		
		if("CALL".equalsIgnoreCase(columnDisyplay) || "CALL NEW".equalsIgnoreCase(columnDisyplay)){
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
			   startDate = Utils.parse(startDateStr, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
			}
			if( !Utils.isNull(endDateStr).equals("")){
			   endDate = Utils.parse(endDateStr, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
			}
			
			curDate = Utils.parse( Utils.stringValue(new Date(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
            
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
		
	public static int convertToInt(String s){
		int r = 0;
		try{
			if( !"".equalsIgnoreCase(isNull(s))){
				r = Integer.parseInt(s);
			}
		}catch(Exception e){
		  logger.error(e.getMessage(),e);
		}
		return r;
	}
	
	
	public static String protectSpecialCharacters(String originalUnprotectedString) {
		if (originalUnprotectedString == null) {
			return null;
		}
		boolean anyCharactersProtected = false;

		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < originalUnprotectedString.length(); i++) {
			char ch = originalUnprotectedString.charAt(i);

			boolean controlCharacter = ch < 32;
			boolean unicodeButNotAscii = ch > 126;
			boolean characterWithSpecialMeaningInXML = ch == '<' || ch == '&' || ch == '>';

			if (characterWithSpecialMeaningInXML || unicodeButNotAscii || controlCharacter) {
				stringBuffer.append("&#" + (int) ch + ";");
				anyCharactersProtected = true;
			} else {
				stringBuffer.append(ch);
			}
		}
		if (anyCharactersProtected == false) {
			return originalUnprotectedString;
		}

		String r = stringBuffer.toString();
		// replace \n
		r = r.replace("&#10;", "<br>");

		return r;
	}
	
 public static double convMeterToKilometer(String meter, int scale){
	  BigDecimal temp = new BigDecimal(Utils.convertStrToDouble(meter)/1000);
	 return temp.setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();
  }
}