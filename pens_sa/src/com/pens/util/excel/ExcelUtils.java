package com.pens.util.excel;

import org.apache.log4j.Logger;

import com.pens.util.Utils;

public class ExcelUtils {
	public static Logger logger = Logger.getLogger("PENS");
	
	/** Validate Excel number or text (get digit or not by format decimal) **/
	public static String isCellNumberOrText(Object cellValue,String format) {
		if((cellValue instanceof Double || cellValue instanceof Integer || cellValue instanceof Float)){
			  //logger.debug("is_number : is Number:"+cellValue+">:"+isCellDouble(cellValue,format));
			  return isCellDouble(cellValue,format);
		}else{
			  //logger.debug("no number : No Number:"+cellValue+">:"+Utils.isNull(cellValue));
			 return Utils.isNull(cellValue);
		}
	}

	public static String isCellDouble(Object cellValue,String format) {
		return Utils.decimalFormat(isCellDoubleNull(cellValue), format);
	}
	
	public static Double isCellDoubleNull(Object str) {
		if (str ==null){
			return new Double(0);
		}
		//logger.debug("str:"+str);
		return ((Double)str);
	}
	
}
