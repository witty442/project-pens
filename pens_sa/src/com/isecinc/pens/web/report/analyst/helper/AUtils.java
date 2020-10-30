package com.isecinc.pens.web.report.analyst.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.report.salesanalyst.SAInitial;
import com.isecinc.pens.web.report.analyst.bean.ABean;
import com.pens.util.Utils;

public class AUtils {

	protected static  Logger logger = Logger.getLogger("PENS");
	
	public static boolean isParentCodeRelate(String currCondType,String parentCondType){
		boolean relate = false;
		
		//brand ,brand_group-->inventory_item_id
		if("inventory_item_id".equalsIgnoreCase(currCondType)){
			if(Utils.isNull(parentCondType).equalsIgnoreCase("brand_group") 
				|| Utils.isNull(parentCondType).equalsIgnoreCase("brand") ){
				relate = true;
			}
		}
		//brand_group -->brand
		if("brand".equalsIgnoreCase(currCondType)){
			if(Utils.isNull(parentCondType).equalsIgnoreCase("brand_group") ){
				relate = true;
			}
		}
	
		/*
		ดิวิชั่น   Division               ==>  พนักงานขาย Salesrep_id	
		ประเภทขาย Customer_Category    ==>  พนักงานขาย Salesrep_id
		ภาคตามพนักงานขาย Sales_ZONE     ==>  พนักงานขาย Salesrep_id
		*/
		
		/*
		ประเภทขาย Customer_Category   + ภาคตามพนักงานขายย Sales_Channel ==>  พนักงานขาย Salesrep_id
		ดิวิชั่น   Division              + ภาคตามพนักงานขาย Sales_Channel  ==>  พนักงานขาย Salesrep_id	
		ภาคตามพนักงานขาย Sales_Channel  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
		ภาคตามพนักงานขาย Sales_Channel  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_id
		xx
		ประเภทขาย Customer_Category   + ภาคตามการดูแล SALES_ZONE ==>  พนักงานขายย Salesrep_id
		ดิวิชั่น   Division              + ภาคตามการดูแล  SALES_ZONE  ==>  พนักงานขาย Salesrep_id	
		ภาคตามการดูแล SALES_ZONE  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
		ภาคตามการดูแล SALES_ZONE  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_id
		
		*/
		if("Salesrep_id".equalsIgnoreCase(currCondType)){
			if(Utils.isNull(parentCondType).equalsIgnoreCase("Division") 
			  || Utils.isNull(parentCondType).equalsIgnoreCase("Customer_Category") 
			  || Utils.isNull(parentCondType).equalsIgnoreCase("Sales_ZONE") 
			  || Utils.isNull(parentCondType).equalsIgnoreCase("Sales_Channel") 
			  ){
				relate = true;
			}
		}
		
		//ภาคตามการดูแล SALES_ZONE  ==> กลุ่มร้านค้า(TT) CUSTOMER_GROUP_TT 
		if("CUSTOMER_GROUP_TT".equalsIgnoreCase(currCondType)){
			if(Utils.isNull(parentCondType).equalsIgnoreCase("SALES_ZONE") 
			  ){
				relate = true;
			}
		}
		
		//กลุ่มร้านค้า(Customer_Group) --> ร้านค้า(Customer_id)
		if("Customer_id".equalsIgnoreCase(currCondType)){
			if(Utils.isNull(parentCondType).equalsIgnoreCase("Customer_Group") 
			  || Utils.isNull(parentCondType).equalsIgnoreCase("CUSTOMER_GROUP_TT")
			  ){
				relate = true;
			}
		}
		
		//จังหวัด(Province) --> อำเภอ(AMPHOR)
		if("AMPHOR".equalsIgnoreCase(currCondType)){
			if(Utils.isNull(parentCondType).equalsIgnoreCase("Province") 
			){
				relate = true;
			}
		}
		return relate;
	}
	
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
	
	
	public  boolean isColumnDispOrder(ABean salesBean) throws Exception{
		boolean isOrder = false;
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			if(isColumnOrder(Utils.isNull(salesBean.getColNameDisp1()))){
				isOrder = true;
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
			if(isColumnOrder(Utils.isNull(salesBean.getColNameDisp2()))){
				isOrder = true;
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			if(isColumnOrder(Utils.isNull(salesBean.getColNameDisp3()))){
				isOrder = true;
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			if(isColumnOrder(Utils.isNull(salesBean.getColNameDisp4()))){
				isOrder = true;
			}
		}
		
		return isOrder;
	}
	
	public  boolean isColumnDispInvoice(ABean salesBean) throws Exception{
		boolean isInv = false;
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			if(isColumnInvoice(Utils.isNull(salesBean.getColNameDisp1()))){
				isInv = true;
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
			if(isColumnInvoice(Utils.isNull(salesBean.getColNameDisp2()))){
				isInv = true;
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			if(isColumnInvoice(Utils.isNull(salesBean.getColNameDisp3()))){
				isInv = true;
			}
		}
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			if(isColumnInvoice(Utils.isNull(salesBean.getColNameDisp4()))){
				isInv = true;
			}
		}
		
		return isInv;
	}
	
	public  boolean isColumnOrder(String columnName) throws Exception{
		boolean isColumnOrder = false;
		if(SAInitial.getInstance().COLUMN_ORDER_MAP.get(columnName) != null){
			isColumnOrder = true;
		}
		return isColumnOrder;
	}
	
	public  boolean isColumnInvoice(String columnName) throws Exception{
		boolean isColumnInvoice = false;
		if(SAInitial.getInstance().COLUMN_INVOICE_MAP.get(columnName) != null){
			isColumnInvoice = true;
		}
		return isColumnInvoice;
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
	
	/**
	 * ORDER - BME	B'me
	ORDER - CREDIT SALES	Credit Sales
	ORDER - DIRECT DELIVERY	Direct Delivery
	ORDER - EXPORT	Export
	ORDER - MODERN TRADE	Modern Trade
	ORDER - VAN SALES	Van Sales
	 * @return
	 */
	public static String getCustomerCatagoryFlag(String cusFlag){
		String flag = "";
		if(cusFlag.equalsIgnoreCase("ORDER - CREDIT SALES")){
			flag ="S";	
		}else if(cusFlag.equalsIgnoreCase("ORDER - DIRECT DELIVERY")){
			flag ="";
		}else if(cusFlag.equalsIgnoreCase("ORDER - EXPORT	Export")){
			flag ="";
		}else if(cusFlag.equalsIgnoreCase("ORDER - MODERN TRADE")){
			flag ="MT";
		}else if(cusFlag.equalsIgnoreCase("ORDER - VAN SALES")){
			flag ="V";
		}
			
		return flag;
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
