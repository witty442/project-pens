package com.pens.util;

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
