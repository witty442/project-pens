package util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Number Format Utility
 * 
 * @author Witty
 * @version $Id: NumberToolsUtil.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class NumberUtil {
	
	@SuppressWarnings("unused")
	private static String DECIMAL_FORMAT ="#.00000000000000000000";
	private static String CURRENCY_FORMAT ="#,##0.00";
	private static String CURRENCY_NODIGIT_FORMAT ="#,##0";
	private static String NUMBER_FORMAT ="#,##0";
	
	public static final String format_number_no_digit = "###0";
	public static final String format_current_no_disgit = "#,##0";
	public static final String format_current_2_disgit = "#,##0.00";
    public static final String format_current_5_digit = "#,##0.00000";
	public static final String format_current_6_digit = "#,##0.000000";
	

	
	public static String decimalFormat(double num){
		NumberFormat formatter = new DecimalFormat("#,##0.00");
		return formatter.format(num);
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
