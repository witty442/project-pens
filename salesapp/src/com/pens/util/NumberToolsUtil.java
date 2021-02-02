package com.pens.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Number Format Utility
 * 
 * @author Aneak.t
 * @version $Id: NumberToolsUtil.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class NumberToolsUtil {
	public static final String format_current_no_disgit = "#,##0";
	public static final String format_current_2_disgit = "#,##0.00";
    public static final String format_current_5_digit = "#,##0.00000";
	public static final String format_current_6_digit = "#,##0.000000";
	
	public static final String format_current_five_digit = "00000";
	
	public static void main(String[] a){
		try{
			double creditAmt  = 10.;
			System.out.println(NumberToolsUtil.getDoubleDigit(creditAmt, 2));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static String decimalFormat(double num){
		NumberFormat formatter = new DecimalFormat(format_current_2_disgit);
		return formatter.format(num);
	}
	
	public static String decimalFormat(double num,String format){
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
	}
	
	public static String decimalFormat(BigDecimal num,String format){
		if(num==null) return "";
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
	}
	
	public static double strToDouble(String s){
		if(s == null || "".equals(s)){
			return Double.parseDouble("0");
		}
	   return Double.parseDouble(s);
	}
	
	// Create Utility for rounding double value
	public static double round(double d, int decimalPlace,int roundType){
	    BigDecimal bd = new BigDecimal(Double.toString(d));
	    bd = bd.setScale(decimalPlace,roundType);
	    return bd.doubleValue();
	  }
	
	public static double getDoubleDigit(double d, int decimalPlace){
	    String doubleStr = "";
	    String doubleStrTemp = Double.toString(d);
	    System.out.println("doubleStrTemp:"+doubleStrTemp);
	    if(doubleStrTemp.indexOf(".") != -1){
	    	doubleStr = doubleStrTemp.substring(0,doubleStrTemp.indexOf("."))+".";
	    	if(doubleStrTemp.indexOf(".")+3 < doubleStrTemp.length()){
	    		doubleStr +=doubleStrTemp.substring(doubleStrTemp.indexOf(".")+1,doubleStrTemp.indexOf(".")+3);
	    	}else{
	    		doubleStr +=doubleStrTemp.substring(doubleStrTemp.indexOf(".")+1,doubleStrTemp.length());
	    	}
	    }
	    System.out.println("doubleStr:"+doubleStr);
	    return new Double(doubleStr);
	  }
}
