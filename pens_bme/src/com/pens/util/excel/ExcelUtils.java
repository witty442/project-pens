package com.pens.util.excel;

import com.pens.util.Utils;

public class ExcelUtils {

	public static String isCellNumberOrText(Object cellValue) {
		if(Utils.isNumeric(Utils.isNull(cellValue)) && !(cellValue instanceof String)){
			  //logger.debug("account_number : is Number:"+cellValue);
			 return Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
		}else{
			   //logger.debug("account_number : No Number:"+cellValue);
			 return Utils.isNull(cellValue);
		}
	}
	public static String isCellDouble(Object cellValue) {
		return String.valueOf(isCellDoubleNull(cellValue));
	}
	
	public static Double isCellDoubleNull(Object str) {
		if (str ==null || Utils.isNull(str).equals("")){
			return new Double(0);
		}
		//logger.debug("str:"+str);
		return ((Double)str);
	}
}
