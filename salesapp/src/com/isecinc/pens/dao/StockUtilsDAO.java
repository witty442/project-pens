package com.isecinc.pens.dao;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.model.MUOMConversion;
import com.pens.util.NumberToolsUtil;
import com.pens.util.Utils;

public class StockUtilsDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * 
	 * @param conn
	 * @param productId
	 * @param uomId
	 * @param qty
	 * @return  priQty
	 * @throws Exception
	 */
	 public static double calcPriQty(Connection conn,String productId,String uomId,int qty) throws Exception{
		double priQty = 0;
		if("CTN".equalsIgnoreCase(uomId)){
			priQty = qty;
		}else{
			UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), "CTN");//default to CTN
		    UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), uomId);
	        logger.debug("("+uc1.getConversionRate()+"/"+uc2.getConversionRate()+")");
	        
	        if(uc2.getConversionRate() > 0){
	        	double qty2Temp = qty/ (uc1.getConversionRate()/uc2.getConversionRate()) ;
	        	//convert to Str 1.666667
				String qty2Str5Digit = NumberToolsUtil.decimalFormat(qty2Temp, NumberToolsUtil.format_current_6_digit);
				//substr remove digit no 6 :"7" -> 1.66666
				qty2Str5Digit = qty2Str5Digit.substring(0,qty2Str5Digit.length()-1);
				
				double pcsQty = Double.parseDouble((qty2Str5Digit));
	        	priQty = pcsQty;
	        }
	    }
	    logger.debug("result calc qty["+priQty+"]");
	    return priQty;
	}
	 
	 public static double calcPriQtyToSubQty(Connection conn,String productId,String uom2,double subQty) throws Exception{
		double calcSubQty = 0;
		UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), "CTN");//default to CTN
		UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(productId), uom2);
	    logger.debug("uom2:"+uom2);
		logger.debug("("+uc1.getConversionRate()+"/"+uc2.getConversionRate()+")");
	        
	     if(uc2.getConversionRate() > 0){
        	double qty2Temp = subQty * uc1.getConversionRate() ;
        	calcSubQty = qty2Temp/uc2.getConversionRate();
	      }
	     logger.debug("result calcSubQty["+calcSubQty+"]");
	    return calcSubQty;
	}
}
