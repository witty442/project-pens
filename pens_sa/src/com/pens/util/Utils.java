
package com.pens.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
	public static final Locale local_th= new Locale("th","TH");
	
	@SuppressWarnings("unused")
	private static String DECIMAL_FORMAT ="#.00000000000000000000";
	private static String CURRENCY_FORMAT ="#,##0.00";
	private static String NUMBER_FORMAT ="#,##0";
	
	public static final String format_number_no_disgit = "###0";
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
	public static long convertStrToLong(String str){
		if(isNull(str).equals("")){
			return 0;
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
	
	// Round Up  2.11 -> 3
	public static String decimalFormatRoundUp(double num,String format){
		NumberFormat formatter = new DecimalFormat(format);
		formatter.setRoundingMode(RoundingMode.UP);
		return formatter.format(num);
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
  public static boolean isNumeric(String str) {  
	  try {  
	      double d = Double.parseDouble(str);  
	  }catch(NumberFormatException nfe)  {  
	    return false;  
	  }  
	  return true;  
	}
	public static Double isDoubleNull(Object str) {
		if (str ==null || isNull(str).equals("")){
			return new Double(0);
		}
		logger.debug("str:"+str);
		return ((Double)str);
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
	public static String convertSciToDecimal(String scientificNotation){
		//String scientificNotation = "8.854922341299E12";
		Double scientificDouble = Double.parseDouble(scientificNotation);
		NumberFormat nf = new DecimalFormat("################################################.###########################################");
		String decimalString = nf.format(scientificDouble);
		
		//System.out.println(decimalString);
		return decimalString;
	}
}