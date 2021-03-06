package util;

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
	public static final String format_current_2_disgit = "#,##0.00";
	public static final String format_current_5_digit = "#,##0.000000";
	
	public static String decimalFormat(double num){
		NumberFormat formatter = new DecimalFormat(format_current_2_disgit);
		return formatter.format(num);
	}
	
	public static String decimalFormat(double num,String format){
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(num);
	}
	
	public static double strToDouble(String s){
		if(s == null || "".equals(s)){
			return Double.parseDouble("0");
		}
	   return Double.parseDouble(s);
	}
	
	
}
