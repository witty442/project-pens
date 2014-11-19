package com.isecinc.pens.web.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Utils;

public class SAUtils {

	protected static  Logger logger = Logger.getLogger("PENS");
	
	/** 
	 * Replace ColName Case column name is too long 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public  String getShortColName(String name) throws Exception{
		 Map<String ,String> COL_LONG_NAME_MAP = new HashMap<String,String>();
		 COL_LONG_NAME_MAP.put("Customer_Category","Cust_Cate");
		 COL_LONG_NAME_MAP.put("Sales_Channel", "Sales_Ch");
		 COL_LONG_NAME_MAP.put("inventory_item_id", "inven_item");
		 COL_LONG_NAME_MAP.put("SALES_ORDER_DATE", "S_DATE");
		 COL_LONG_NAME_MAP.put("SHIP_TO_SITE_USE_ID", "SP_TO");
		 COL_LONG_NAME_MAP.put("BILL_TO_SITE_USE_ID", "BL_TO");
		 
		 //logger.debug("LongName:"+Utils.isNull(COL_LONG_NAME_MAP.get(name)));
		 if( !Utils.isNull(COL_LONG_NAME_MAP.get(name)).equals("")){
			 name = Utils.isNull(COL_LONG_NAME_MAP.get(name));
		 }
		return name;
	}
	
	
	
	
	
	public  String genColumnName(String columnName,String columnUnit) throws Exception{
		String colName ="";
		if(columnName.equalsIgnoreCase("IR")){
			colName = "(NVL(INVOICED_"+columnUnit+",0)- NVL(Returned_"+columnUnit+",0)) ";
		}
		else if(columnName.equalsIgnoreCase("NETAMT")){
			colName = "(NVL(INVOICED_"+columnUnit+",0)- NVL(Returned_"+columnUnit+",0)- NVL(Discount_"+columnUnit+",0)) ";
		}
		else{
		    colName = columnName+"_"+columnUnit+" ";
		}
		
		return colName;
	}
	
	public static boolean isColumnNumberType(String condType){
		if( "INVENTORY_ITEM_ID".equalsIgnoreCase(Utils.isNull(condType))
			|| "CUSTOMER_ID".equalsIgnoreCase(Utils.isNull(condType))
			|| "SHIP_TO_SITE_USE_ID".equalsIgnoreCase(Utils.isNull(condType))
			|| "BILL_TO_SITE_USE_ID".equalsIgnoreCase(Utils.isNull(condType))
	
				){
				return true;
			}
		return false;
	}
	
	
	public static String converToText(String columnName,String value){
		if(isColumnNumberType(columnName)){
			return value;
		}
		
		List<String> valuesText = new ArrayList<String>() ;
		String[] values = value.split("[,]");
		
		for(String text : values){
			valuesText.add("'"+text+"'");
		}
		
		return StringUtils.join(valuesText, ","); 
	}
}
