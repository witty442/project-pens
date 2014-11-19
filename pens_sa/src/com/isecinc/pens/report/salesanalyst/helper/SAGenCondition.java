package com.isecinc.pens.report.salesanalyst.helper;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.report.salesanalyst.SAProcess;

public class SAGenCondition {
	
	protected static  Logger logger = Logger.getLogger("PENS");
	SAUtils reportU = new SAUtils();
	

	public  String genSqlWhereCondition(SABean salesBean) throws Exception{
		return genSqlWhereCondition(salesBean,"");
	}
	
	//aliasSub =SS. (select * from table)SS
	public  String genSqlWhereCondition(SABean salesBean,String aliasSub) throws Exception{
		String sql = "";
		
		//  change  "0" to "-1" for fix condition data
		if( !"-1".equals(Utils.isNull(salesBean.getCondName1())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue1()))){
			if(Utils.isNull(salesBean.getCondName1()).equalsIgnoreCase("invoice_date")|| Utils.isNull(salesBean.getCondName1()).equalsIgnoreCase("SALES_ORDER_DATE")){
				logger.debug("CondValue1:"+salesBean.getCondValue1());
				Date date = Utils.parseToBudishDate(salesBean.getCondValue1(), Utils.DD_MM_YYYY_WITH_SLASH);
				sql +=" and  "+aliasSub+salesBean.getCondName1()+" = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n";
			
			}else if(Utils.isNull(salesBean.getCondName1()).equalsIgnoreCase("BrandXX")){
				if(SAProcess.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName1()))){
					sql +="and ( ";
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName1())+" IN ("+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+") \n ";
					sql +="     or \n";
					// Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName1())+" IN ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no in ("+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+") ) \n ";
					sql +=" ) \n";
				}else{
					sql +="and ( ";
				    sql +="   "+aliasSub+ Utils.isNull(salesBean.getCondName1())+"='"+Utils.isNull(salesBean.getCondValue1())+"' \n ";
				    sql +="     or \n";
				   // Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName1())+" = ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no = '"+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+"' ) \n ";
					sql +=" ) \n";
				}
			}else{
				if(SAProcess.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName1()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName1())+" IN ("+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+") \n ";
				}else if(SAUtils.isColumnNumberType(Utils.isNull(salesBean.getCondName1()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName1())+"="+Utils.isNull(salesBean.getCondValue1())+" \n ";
				}else{
				    sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName1())+"='"+Utils.isNull(salesBean.getCondValue1())+"' \n ";
				}
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName2())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue2()))){
			if(Utils.isNull(salesBean.getCondName2()).equalsIgnoreCase("invoice_date") || Utils.isNull(salesBean.getCondName2()).equalsIgnoreCase("SALES_ORDER_DATE")){
				Date date = Utils.parseToBudishDate(salesBean.getCondValue2(), Utils.DD_MM_YYYY_WITH_SLASH);
				sql +=" and  "+aliasSub+salesBean.getCondName1()+" = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n";
			
			}else if(Utils.isNull(salesBean.getCondName2()).equalsIgnoreCase("BrandXX")){
				if(SAProcess.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName2()))){
					sql +="and ( ";
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName2())+" IN ("+ SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+") \n ";
					sql +="     or \n";
					// Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName2())+" IN ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no in ("+ SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+") ) \n ";
					sql +=" ) \n";
				}else{
					sql +="and ( ";
				    sql +="   "+aliasSub+ Utils.isNull(salesBean.getCondName2())+"='"+Utils.isNull(salesBean.getCondValue2())+"' \n ";
				    sql +="     or \n";
				   // Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName2())+" = ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no = '"+ SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+"' ) \n ";
					sql +=" ) \n";
				}
			}else{
				if(SAProcess.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName2()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName2())+" IN ("+SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+") \n ";
				}
				else if(SAUtils.isColumnNumberType(Utils.isNull(salesBean.getCondName2()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName2())+"="+Utils.isNull(salesBean.getCondValue2())+" \n ";
				}else{
				    sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName2())+"='"+Utils.isNull(salesBean.getCondValue2())+"' \n ";
				}
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName3())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue3()))){
			if(Utils.isNull(salesBean.getCondName3()).equalsIgnoreCase("invoice_date") || Utils.isNull(salesBean.getCondName3()).equalsIgnoreCase("SALES_ORDER_DATE")){
				Date date = Utils.parseToBudishDate(salesBean.getCondValue2(), Utils.DD_MM_YYYY_WITH_SLASH);
				sql +=" and  "+aliasSub+salesBean.getCondName1()+" = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n";
			
			}else if(Utils.isNull(salesBean.getCondName3()).equalsIgnoreCase("BrandXX")){
				if(SAProcess.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName3()))){
					sql +="and ( ";
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName3())+" IN ("+ SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+") \n ";
					sql +="     or \n";
					// Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName3())+" IN ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no in ("+ SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+") ) \n ";
					sql +=" ) \n";
				}else{
					sql +="and ( ";
				    sql +="   "+aliasSub+ Utils.isNull(salesBean.getCondName3())+"='"+Utils.isNull(salesBean.getCondValue3())+"' \n ";
				    sql +="     or \n";
				   // Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName3())+" = ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no = '"+ SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+"' ) \n ";
					sql +=" ) \n";
				}
			}else{
				if(SAProcess.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName3()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName3())+" IN ("+SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+") \n ";
				}
				else if(SAUtils.isColumnNumberType(Utils.isNull(salesBean.getCondName3()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName3())+"="+Utils.isNull(salesBean.getCondValue3())+" \n ";
				}else{
				   sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName3())+"='"+Utils.isNull(salesBean.getCondValue3())+"' \n ";
				}
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName4())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue4()))){
			if(Utils.isNull(salesBean.getCondName4()).equalsIgnoreCase("invoice_date")|| Utils.isNull(salesBean.getCondName4()).equalsIgnoreCase("SALES_ORDER_DATE")){
				Date date = Utils.parseToBudishDate(salesBean.getCondValue2(), Utils.DD_MM_YYYY_WITH_SLASH);
				sql +=" and  "+aliasSub+salesBean.getCondName1()+" = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n";
			
			}else if(Utils.isNull(salesBean.getCondName4()).equalsIgnoreCase("BrandXX")){
				if(SAProcess.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName4()))){
					sql +="and ( ";
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName4())+" IN ("+ SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+") \n ";
					sql +="     or \n";
					// Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName4())+" IN ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no in ("+ SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+") ) \n ";
					sql +=" ) \n";
				}else{
					sql +="and ( ";
				    sql +="   "+aliasSub+ Utils.isNull(salesBean.getCondName4())+"='"+Utils.isNull(salesBean.getCondValue4())+"' \n ";
				    sql +="     or \n";
				   // Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName4())+" = ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no = '"+ SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+"' ) \n ";
					sql +=" ) \n";
				}
				
			}else{
				if(SAProcess.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName4()))){
					sql +="and "+ Utils.isNull(salesBean.getCondName4())+" IN ("+SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+") \n ";
				}
				else if(SAUtils.isColumnNumberType(Utils.isNull(salesBean.getCondName4()))){
				   sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName4())+"="+Utils.isNull(salesBean.getCondValue4())+" \n ";
				}else{
				   sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName4())+"='"+Utils.isNull(salesBean.getCondValue4())+"' \n ";
				}
			}
		}
		return sql;
	}
	
	
	public  String genSQLGetDesc(String condType)throws Exception{
		String sql = "";
		try{
			if("inventory_item_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.INVENTORY_ITEM_DESC as DESC_ from XXPENS_BI_MST_ITEM M WHERE M.INVENTORY_ITEM_ID = S.INVENTORY_ITEM_ID )";
			
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql = "(select M.cust_cat_desc as desc_ from XXPENS_BI_MST_CUST_CAT M WHERE M.cust_cat_no = S.Customer_Category)";
			
			}else if("Division".equalsIgnoreCase(condType)){
				sql = "(select M.div_desc as desc_ from XXPENS_BI_MST_DIVISION M WHERE M.div_no = S.division)";
			
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql = "(select M.salesrep_desc desc_ from XXPENS_BI_MST_SALESREP M  WHERE M.salesrep_id= S.salesrep_id)";
			
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql = "(select M.sales_channel_desc as desc_ from XXPENS_BI_MST_SALES_CHANNEL M WHERE  M.sales_channel_no= S.sales_channel)";
			
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				sql = "(select M.cust_group_desc as desc_ from XXPENS_BI_MST_CUST_GROUP M WHERE M.cust_group_no= S.customer_group)";
			
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql = " (SELECT max(desc_) FROM (SELECT M.customer_desc desc_, M.customer_id  from XXPENS_BI_MST_CUSTOMER M ORDER BY m.create_date DESC ) WHERE customer_id = S.customer_id ) ";
			
			}else if("Brand_XX".equalsIgnoreCase(condType)){
				
				sql = "(case when (select M.brand_desc as desc_ from XXPENS_BI_MST_BRAND M WHERE M.brand_no = S.brand) is null ";
				sql += "     then (select max(M.brand_group_desc) as desc_ from XXPENS_BI_MST_BRAND_GROUP M WHERE M.brand_no = S.brand) end )";
				
			}else if("Brand".equalsIgnoreCase(condType)){
				sql = "(select M.brand_desc as desc_ from XXPENS_BI_MST_BRAND M WHERE M.brand_no = S.brand)";
				
			}else if("Brand_Group".equalsIgnoreCase(condType)){
				sql = "(select max(M.brand_group_desc) as desc_ from XXPENS_BI_MST_BRAND_GROUP M WHERE M.brand_group_no = S.brand_group)";	
				
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				sql = "(select M.Invoice_Date as desc_ from XXPENS_BI_MST_INVOICE_DATE M WHERE M.INVOICE_DATE = S.INVOICE_DATE)";
			
			}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
				sql = "(select M.ORDER_DATE as desc_ from XXPENS_BI_MST_ORDER_DATE M WHERE M.ORDER_DATE = S.SALES_ORDER_DATE)";
			
			}else if("Province".equalsIgnoreCase(condType)){
				sql = "(select M.PROVINCE as desc_ from XXPENS_BI_MST_PROVINCE M WHERE M.PROVINCE = S.PROVINCE)";
			
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				sql = "(select M.AMPHOR as desc_ from XXPENS_BI_MST_AMPHOR M WHERE M.AMPHOR =S.AMPHOR)";
			
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				sql = "(select M.TAMBOL as desc_ from XXPENS_BI_MST_TAMBOL M WHERE M.TAMBOL =S.TAMBOL)";
			
			}else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				sql = "(select  distinct M.SALES_ORDER_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.SALES_ORDER_NO =S.SALES_ORDER_NO)";
			
			}else if("INVOICE_NO".equalsIgnoreCase(condType)){
				sql = "(select  distinct M.INVOICE_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.INVOICE_NO =S.INVOICE_NO)";
			
			}else if("IR_AMT".equalsIgnoreCase(condType)){
			   sql = "(select  distinct M.INVOICED_AMT-M.RETURNED_AMT as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.INVOICE_NO =S.INVOICE_NO)";
			
			}else if("BILL_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "(SELECT M.CUSTOMER_BILL_TO_ADDRESS as DESC_ from XXPENS_BI_MST_CUST_BILL_ADDR M WHERE M.BILL_TO_SITE_USE_ID = S.BILL_TO_SITE_USE_ID )";
			
			}else if("SHIP_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "(SELECT M.CUSTOMER_SHIP_TO_ADDRESS as DESC_ from XXPENS_BI_MST_CUST_SHIP_ADDR M WHERE M.SHIP_TO_SITE_USE_ID = S.SHIP_TO_SITE_USE_ID )";
			
			}else if("Organization_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.ORGANIZATION_CODE as DESC_ from XXPENS_BI_MST_ORGANIZATION M WHERE M.Organization_id = S.Organization_id )";
			
			}else if("Order_type_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.order_type_name|| ' (' || M.order_type_cat || ')' as DESC_ from XXPENS_BI_MST_ORDER_TYPE M WHERE M.Order_type_id = S.Order_type_id )";
			}
			
			sql +=" AS "+condType+"_DESC , \n";
			
			logger.debug("SqlGetDesc:"+sql);
			return sql;
		}catch(Exception e){
		   throw e;
		}finally{
		
		}
	}
	
	public  String genSQLGetCode(String condType)throws Exception{
		String sql = "";
		try{
			if("inventory_item_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.INVENTORY_ITEM_CODE as DESC_ from XXPENS_BI_MST_ITEM M WHERE M.INVENTORY_ITEM_ID = S.INVENTORY_ITEM_ID )";
			
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql = "(select M.cust_cat_no as desc_ from XXPENS_BI_MST_CUST_CAT M WHERE M.cust_cat_no = S.Customer_Category)";
			
			}else if("Division".equalsIgnoreCase(condType)){
				sql = "(select M.div_no as desc_ from XXPENS_BI_MST_DIVISION M WHERE M.div_no = S.division)";
			
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql = "(select M.salesrep_code desc_ from XXPENS_BI_MST_SALESREP M  WHERE M.salesrep_id= S.salesrep_id)";
			
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql = "(select M.sales_channel_no as desc_ from XXPENS_BI_MST_SALES_CHANNEL M WHERE  M.sales_channel_no= S.sales_channel)";
			
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				sql = "(select M.cust_group_no as desc_ from XXPENS_BI_MST_CUST_GROUP M WHERE M.cust_group_no= S.customer_group)";
			
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql = " (SELECT max(desc_) FROM (SELECT M.customer_code desc_, M.customer_id  from XXPENS_BI_MST_CUSTOMER M ORDER BY m.create_date DESC ) WHERE customer_id = S.customer_id) ";
			
			}else if("BrandXX".equalsIgnoreCase(condType)){
				
				sql = "( case when (select M.brand_no as desc_ from XXPENS_BI_MST_BRAND M WHERE M.brand_no = S.brand) is null";
				sql += "       then (select max(M.brand_group_no) as desc_ from XXPENS_BI_MST_BRAND_GROUP M WHERE M.brand_no = S.brand) end)";
				
			}else if("Brand".equalsIgnoreCase(condType)){
				sql = "(select M.brand_no as desc_ from XXPENS_BI_MST_BRAND M WHERE M.brand_no = S.brand)";
				
			}else if("Brand_Group".equalsIgnoreCase(condType)){
				sql = "(select max(M.brand_group_no) as desc_ from XXPENS_BI_MST_BRAND_GROUP M WHERE M.brand_group_no = S.brand_group)";	
				
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				sql = "(select M.Invoice_Date as desc_ from XXPENS_BI_MST_INVOICE_DATE M WHERE M.INVOICE_DATE = S.INVOICE_DATE)";
			
			}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
				sql = "(select M.ORDER_DATE as desc_ from XXPENS_BI_MST_ORDER_DATE M WHERE M.ORDER_DATE = S.SALES_ORDER_DATE)";
			
			}else if("Province".equalsIgnoreCase(condType)){
				sql = "(select M.PROVINCE as desc_ from XXPENS_BI_MST_PROVINCE M WHERE M.PROVINCE = S.PROVINCE)";
			
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				sql = "(select M.AMPHOR as desc_ from XXPENS_BI_MST_AMPHOR M WHERE M.AMPHOR = S.AMPHOR)";
			
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				sql = "(select M.TAMBOL as desc_ from XXPENS_BI_MST_TAMBOL M WHERE M.TAMBOL = S.TAMBOL)";
			
			}else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				sql = "(select distinct M.SALES_ORDER_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.SALES_ORDER_NO =S.SALES_ORDER_NO)";
			
			}else if("INVOICE_NO".equalsIgnoreCase(condType)){
				sql = "(select distinct  M.INVOICE_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.INVOICE_NO =S.INVOICE_NO)";
			
			}else if("IR_AMT".equalsIgnoreCase(condType)){
				sql = "(select  distinct M.INVOICED_AMT-M.RETURNED_AMT as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.INVOICE_NO =S.INVOICE_NO)";
			
			}else if("BILL_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "(SELECT M.CUSTOMER_BILL_TO_ADDRESS as DESC_ from XXPENS_BI_MST_CUST_BILL_ADDR M WHERE M.BILL_TO_SITE_USE_ID = S.BILL_TO_SITE_USE_ID )";
			
			}else if("SHIP_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "(SELECT M.CUSTOMER_SHIP_TO_ADDRESS as DESC_ from XXPENS_BI_MST_CUST_SHIP_ADDR M WHERE M.SHIP_TO_SITE_USE_ID = S.SHIP_TO_SITE_USE_ID )";
			
			}else if("Organization_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.ORGANIZATION_CODE as DESC_ from XXPENS_BI_MST_ORGANIZATION M WHERE M.Organization_id = S.Organization_id )";
			
			}else if("Order_type_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.order_type_id as DESC_ from XXPENS_BI_MST_ORDER_TYPE M WHERE M.Order_type_id = S.Order_type_id )";
			}
			
			sql +=" AS "+condType+"_CODE , \n";
			
			logger.debug("SqlGetDesc:"+sql);
			return sql;
		}catch(Exception e){
		   throw e;
		}finally{
		
		}
	}
	

	/**
	 * 
	 * @param type  ORDER || INVOICE
	 * @param salesBean
	 * @return 
	 * @throws Exception
	 */
	
	public StringBuffer genSqlWhereCondByGroup(SABean salesBean,String type,String alias,String colGroupName) throws Exception{
		StringBuffer sql = new StringBuffer("");

		/** Where Condition By Group  **/
		if(SAProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
		    sql.append(" AND (1=1 AND "+alias+"invoice_year||"+alias+"invoice_month = '"+colGroupName+"' ) \n");
		    //sql.append(" AND invoice_year IN('"+salesBean.getYear()+"') \n");
		}else if(SAProcess.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
			sql.append(" AND (1=1 AND "+alias+"invoice_quarter = '"+colGroupName+"' \n");
			sql.append(" AND "+alias+"invoice_year IN('"+salesBean.getYear()+"') ) \n");
		}else if(SAProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
			sql.append(" AND (1=1 AND "+alias+"invoice_year = '"+colGroupName+"' ) \n");
		}else{
			 Date date = Utils.parseToBudishDate(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH);
			 sql.append(" AND (1=1 AND  "+alias+"INVOICE_DATE = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy') ) \n");
		}
		return sql;
	}
	
	public  String genSubSQLByType(Connection conn,User user ,SABean salesBean,String type,String groupBy,String colGroupName) throws Exception{
		StringBuffer sql = new StringBuffer("");
		
		//sql.append("/*** genSubSQLByType **/ \n");
		
		if("ORDER".equalsIgnoreCase(type)){
			sql.append(" SELECT \n");
			sql.append( genSQLSelectColumn(salesBean,type,groupBy,colGroupName));
			sql.append(" '1' AS A \n");
			sql.append(" FROM "+SAProcess.TABLE_VIEW+" V \n");
			sql.append(" WHERE 1=1 \n ");
			sql.append(" AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
			/** Condition Filter **/
			sql.append(genSqlWhereCondition(salesBean));
			
			/** Where Condition By Group  **/
			if(SAProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
			    sql.append(" AND sales_order_year||sales_order_month = '"+colGroupName+"' \n");
			}else if(SAProcess.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				sql.append(" AND sales_order_year||sales_order_quarter = '"+colGroupName+"' \n");
			}else if(SAProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				sql.append(" AND sales_order_year = '"+colGroupName+"' \n");
			}else{
				Date date = Utils.parseToBudishDate(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo() , Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("AND  SALES_ORDER_DATE = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n"); 
			}
			
			//Include Pos or not
			//sql.append(ExternalCondition.genIncludePos(salesBean));
		}else{
			sql.append(" SELECT \n");
			sql.append( genSQLSelectColumn(salesBean,type,groupBy,colGroupName));
			sql.append(" '1' AS A \n");
			sql.append(" FROM "+SAProcess.TABLE_VIEW+" V \n");
			sql.append(" WHERE 1=1 \n ");
			sql.append(" AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
			/** Condition Filter **/
			sql.append(  genSqlWhereCondition(salesBean));
			
			/** Where Condition By Group  **/
			if(SAProcess.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
			    sql.append(" AND invoice_year||invoice_month = '"+colGroupName+"' \n");
			}else if(SAProcess.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				sql.append(" AND invoice_year||invoice_quarter = '"+colGroupName+"' \n");
			}else if(SAProcess.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				sql.append(" AND invoice_year = '"+colGroupName+"' \n");
			}else{
				Date date = Utils.parseToBudishDate(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append(" AND  INVOICE_DATE = to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
			}
			
			//Include Pos or not
			//sql.append(ExternalCondition.genIncludePos(salesBean));
		}
		
		/** Filter Displsy Data By User **/
		sql.append(SecurityHelper.genWhereSqlFilterByUser(conn,user, null,"V."));
	
		return sql.toString();
	}
	
	/**
	 * 
	 * @param salesBean
	 * @param groupBy
	 * @param colGroupName
	 * @return
	 * @throws Exception
	 * Desc: gen sql count customer 
	 */
	public  StringBuffer genSqlCountCustomerNoDup(SABean salesBean,String groupBy,String colGroupName,String sqlNotInCaseCall) throws Exception{
		StringBuffer ss = new StringBuffer("");
		
		ss.append("\n NVL( ");
		ss.append("\n  ( SELECT COUNT(DISTINCT c.c_id) as c_id FROM( ");
		ss.append("\n \t  SELECT DISTINCT c.customer_id as c_id ,c."+groupBy+" from "+SAProcess.TABLE_VIEW+" c " );
		ss.append("\n \t      WHERE 1=1 " );
		ss.append("      \t "+genSqlWhereCondition(salesBean,"c."));
		ss.append("      \t "+genSqlWhereCondByGroup(salesBean,"INVOICE","c.",colGroupName));
		
		if( !Utils.isNull(sqlNotInCaseCall).equals(""))
		   ss.append("       \n "+sqlNotInCaseCall);
		ss.append("\n   ) c ");
		ss.append("\n   WHERE c."+groupBy+" = SS."+groupBy );
		ss.append("\n ) ");
		ss.append(",0) \n" );
		
		return ss;
	}
	  
	public  StringBuffer genSqlCountCustomer(SABean salesBean,String groupBy,String colGroupName) throws Exception{
		StringBuffer ss = new StringBuffer("");
		
		ss.append("\n NVL( ");
		ss.append("\n  ( SELECT COUNT(DISTINCT c.c_id) as c_id FROM( ");
		ss.append("\n \t  SELECT DISTINCT c.customer_id as c_id,c."+groupBy+" from "+SAProcess.TABLE_VIEW+" c " );
		ss.append("\n \t      WHERE 1=1 " );
		ss.append("      \t "+genSqlWhereCondition(salesBean,"c."));
		ss.append("      \t "+genSqlWhereCondByGroup(salesBean,"INVOICE","c.",colGroupName));
		ss.append("\n   ) c ");
		ss.append("\n   WHERE c."+groupBy+" = SS."+groupBy );
		ss.append("\n ) ");
		ss.append(",0) \n" );
		
		return ss;
	}
	
	public  StringBuffer genSqlMinusCustomerId(SABean salesBean,List<StringBuffer> sqlNotInCaseCallAllList) throws Exception{
		StringBuffer ss = new StringBuffer("");
		//ss.append("\n      AND (c.customer_id,"+salesBean.getGroupBy()+") NOT IN ( " );
		ss.append("\n      AND NOT EXISTS ( " );
		ss.append("\n      SELECT DISTINCT A.c_id as c_id ,A."+salesBean.getGroupBy()+" FROM( " );
		
		ss.append("\n      SELECT 0 as c_id,NULL as "+salesBean.getGroupBy()+"  FROM DUAL ");
		for(int i=0;i<sqlNotInCaseCallAllList.size();i++){
			StringBuffer condNotInAll =(StringBuffer)sqlNotInCaseCallAllList.get(i);
			ss.append("\n      UNION ALL ");
			ss.append("\n      SELECT distinct c.customer_id as c_id ,c."+salesBean.getGroupBy()+" from "+SAProcess.TABLE_VIEW+" c " );
			ss.append("\n      WHERE 1=1 " );
			ss.append("\n      "+condNotInAll );
			ss.append("\n      "+genSqlWhereCondition(salesBean,"c."));
	     }
		ss.append("\n     )A " );
		ss.append("\n     WHERE A.c_id = c.customer_id AND c."+salesBean.getGroupBy()+"= A."+salesBean.getGroupBy());
		ss.append("\n   ) " );
		return ss;
	}

	/**
	 * genTopSQLSelectColumn
	 * @param salesBean
	 * @param groupBy
	 * @param colGroupName
	 * @return String[2]  0->columnTop ,1->columnALL
	 * @throws Exception
	 * 
	 */
	
	public  String[] genTopSQLSelectColumn(SABean salesBean,String groupBy,String colGroupName) throws Exception{
		return genTopSQLSelectColumn(salesBean, groupBy, colGroupName,"");
	}
	
	/**
	 * genTopSQLSelectColumn
	 * @param salesBean
	 * @param groupBy
	 * @param colGroupName
	 * @return String[2]  0->columnTop ,1->columnALL
	 * @throws Exception
	 * 
	 */
	public  String[] genTopSQLSelectColumn(SABean salesBean,String groupBy,String colGroupName,String sqlNotIncaseCall) throws Exception{
		StringBuffer columnAll = new StringBuffer("");
		StringBuffer columnTop = new StringBuffer("");
		String result[] = new String[2];
		String columnName1="";
		String columnName2="";
		String columnName3="";
		String columnName4="";
		
		String subSelect1 ="";
		String subSelect2 ="";
		String subSelect3 ="";
		String subSelect4 ="";
		
		logger.debug("genTopSQLSelectColumn:groupBy["+groupBy+"]:colGroupName["+colGroupName+"]");
		
		colGroupName = reportU.getShortColName(colGroupName);
			
		columnTop.append( groupBy +", \n ");			
		columnAll.append(groupBy +", \n ");;
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			
			if("CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1()))){
				subSelect1 = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				columnName1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;	

				/** Gen Column Top **/
				columnTop.append( subSelect1+" as "+columnName1+", \n ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName1+", \n ");		
	
			}else if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1()))){
				subSelect1 = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				columnName1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;	
				
				String subSelectNoDup = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				String columnNameNoDup = SAProcess.NO_DUP_PREFIX+salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;
				
				/** Gen Column Top **/
				columnTop.append( subSelect1+" as "+columnName1+", \n ");
				columnTop.append( subSelectNoDup+" as "+columnNameNoDup+", \n ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName1+", \n ");
				columnAll.append( columnNameNoDup+", \n ");
				
			}else{
				subSelect1 = "NVL(sum("+salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName+"),0)" ;
				columnName1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect1+" as "+columnName1+", \n ");
				columnAll.append( columnName1+", \n ");
			}
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
					
			if("CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2()))){
				subSelect2 = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				columnName2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;
				
				/** Gen Column Top **/
				columnTop.append( subSelect2+" as "+columnName2+", \n ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName2+", \n ");
							
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
					columnTop.append("(CASE WHEN "+subSelect1+" <> 0");
					columnTop.append(" THEN ("+ subSelect2 +"/"+subSelect1 +")*100 \n ");
					columnTop.append(" ELSE 0 END )  as PER1_"+colGroupName +",  \n ");
				
					columnAll.append(" PER1_"+colGroupName+", \n");;
				}
				
			}else if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2()))){
				subSelect2 = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				columnName2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;	
				
				String subSelectNoDup = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				String columnNameNoDup = SAProcess.NO_DUP_PREFIX+salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;
					
				/** Gen Column Top **/
				columnTop.append( subSelect2+" as "+columnName2+", \n ");
				columnTop.append( subSelectNoDup+" as "+columnNameNoDup+", \n ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName2+", \n ");
				columnAll.append( columnNameNoDup+", \n ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
					columnTop.append("(CASE WHEN "+subSelect1+" <> 0");
					columnTop.append(" THEN ("+ subSelect2 +"/"+subSelect1 +")*100 \n ");
					columnTop.append(" ELSE 0 END )  as PER1_"+colGroupName +",  \n ");
				
					columnAll.append(" PER1_"+colGroupName+", \n");;
				}
				
			}else{
				subSelect2 = "NVL(sum("+salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName+"),0)" ;
				columnName2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect2+" as "+columnName2+", \n ");
				columnAll.append( columnName2+", \n ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
					columnTop.append("(CASE WHEN "+subSelect1+" <> 0");
					columnTop.append(" THEN ("+ subSelect2 +"/"+subSelect1 +")*100 \n ");
					columnTop.append(" ELSE 0 END )  as PER1_"+colGroupName +",  \n ");
					
					columnAll.append(" PER1_"+colGroupName+", \n");;
				}
			}
	   }
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			
			if("CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3()))){
				subSelect3 = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				columnName3 = salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;

				/** Gen Column Top **/
				columnTop.append( subSelect3+" as "+columnName3+", \n ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName3+", \n ");
				
			}else if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3()))){
				subSelect3 = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				columnName3 = salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;
				
				String subSelectNoDup = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
			    String columnNameNoDup = SAProcess.NO_DUP_PREFIX+salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;
					
				/** Gen Column ALL **/
				columnTop.append( subSelect3+" as "+columnName3+", \n ");
				columnTop.append( subSelectNoDup+" as "+columnNameNoDup+", \n ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName3+", \n ");
				columnAll.append( columnNameNoDup+", \n ");
				
			}else{
				subSelect3 = "NVL(sum("+salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName+"),0)" ;
				columnName3 = salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect3+" as "+columnName3+", \n ");
				columnAll.append( columnName3+", \n ");
			}
	   }
		
       if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			
			if("CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4()))){
				subSelect4 = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				columnName4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
					
				/** Gen Column Top **/
				columnTop.append( subSelect4+" as "+columnName4+", \n ");

				/** Gen Column ALL **/
				columnAll.append( columnName4+", \n ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp2()))){
					columnTop.append("(CASE WHEN "+subSelect3+" <> 0");
					columnTop.append(" THEN ("+ subSelect4 +"/"+subSelect3 +")*100 \n ");
					columnTop.append(" ELSE 0 END )  as PER2_"+colGroupName +",  \n ");
				
					columnAll.append(" PER2_"+colGroupName+", \n");;
				}
				
			}else if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4()))){
				subSelect4 = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				columnName4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
				
				String subSelectNoDup = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				String columnNameNoDup = SAProcess.NO_DUP_PREFIX+salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect4+" as "+columnName4+", \n ");
				columnTop.append( subSelectNoDup+" as "+columnNameNoDup+", \n ");
				

				/** Gen Column ALL **/
				columnAll.append( columnName4+", \n ");
				columnAll.append( columnNameNoDup+", \n ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp2()))){
					columnTop.append("(CASE WHEN "+subSelect3+" <> 0");
					columnTop.append(" THEN ("+ subSelect4 +"/"+subSelect3 +")*100 \n ");
					columnTop.append(" ELSE 0 END )  as PER2_"+colGroupName +",  \n ");
				
					columnAll.append(" PER2_"+colGroupName+", \n");;
				}	
			}else{
				subSelect4 = "NVL(sum("+salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName+"),0)" ;
				columnName4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect4+" as "+columnName4+", \n ");
				columnAll.append( columnName4+", \n ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp2()))){
					columnTop.append("(CASE WHEN "+subSelect3+" <> 0");
					columnTop.append(" THEN ("+ subSelect4 +"/"+subSelect3 +")*100 \n ");
					columnTop.append(" ELSE 0 END )  as PER2_"+colGroupName +",  \n ");
				
					columnAll.append(" PER2_"+colGroupName+", \n");;
				}
			}
	   }
     
		result[0] = columnTop.toString();
		result[1] = columnAll.toString();
		
		return result;
	}

	/**
	 * 
	 * @param salesBean
	 * @param type
	 * @param groupBy
	 * @param colGroupName
	 * @return
	 * @throws Exception
	 */
	public  String genSQLSelectColumn(SABean salesBean,String type ,String groupBy,String colGroupName) throws Exception{
		String sql ="";
		String colName1 = "";String colName2 = "";
		String colName3 = "";String colName4 = "";
		String colAlias1 = "";String colAlias2 = "";
		String colAlias3 = "";String colAlias4 = "";
		
		colGroupName = reportU.getShortColName(colGroupName);
		
		if("ORDER".equalsIgnoreCase(type)){
			if(colGroupName.equalsIgnoreCase("INVOICE_DATE")){
				sql +="SALES_ORDER_DATE as "+groupBy+", \n ";
			}else{
		        sql +=groupBy +" as "+groupBy+", \n ";
			}
		}else{
			sql +=groupBy +" as "+groupBy+", \n ";	
		}
		
		// IR_AMT = M.INVOICED_AMT-M.RETURNED_AMT
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			if( !"CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1())) && 
				!"CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1()))
					){
				colAlias1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName ;
				colName1 = reportU.genColumnName(salesBean.getColNameDisp1(),salesBean.getColNameUnit1());
				if("ORDER".equalsIgnoreCase(type) && SAProcess.getInstance().COLUMN_ORDER_MAP.get(salesBean.getColNameDisp1()) != null){
				   sql +=colName1 +" as "+colAlias1+", \n ";
				}else if("INVOICE".equalsIgnoreCase(type) && SAProcess.getInstance().COLUMN_INVOICE_MAP.get(salesBean.getColNameDisp1()) != null){
				   sql +=colName1 +" as "+colAlias1+", \n ";
				}else{
				   sql +=" 0 as "+colAlias1+", \n ";
				}
			}
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
	        if( !"CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2())) && 
	            !"CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2()))	
	        		){
				colAlias2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName  ;
				colName2 = reportU.genColumnName(salesBean.getColNameDisp2(),salesBean.getColNameUnit2());
				if("ORDER".equalsIgnoreCase(type) && SAProcess.getInstance().COLUMN_ORDER_MAP.get(salesBean.getColNameDisp2()) != null){
				   sql +=colName2 +" as "+colAlias2+", \n ";
				}else if("INVOICE".equalsIgnoreCase(type) && SAProcess.getInstance().COLUMN_INVOICE_MAP.get(salesBean.getColNameDisp2()) != null){
				   sql +=colName2 +" as "+colAlias2+", \n ";
				}else{
				   sql +=" 0 as "+colAlias2+", \n ";
				}
			}
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
	        if( !"CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3())) && 
	        	!"CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3()))	
	        		){
			
			    colAlias3 =  salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName  ;
				colName3 = reportU.genColumnName(salesBean.getColNameDisp3(),salesBean.getColNameUnit3());
				if("ORDER".equalsIgnoreCase(type) && SAProcess.getInstance().COLUMN_ORDER_MAP.get(salesBean.getColNameDisp3()) != null){
				   sql +=colName3 +" as "+colAlias3+", \n ";
				}else if("INVOICE".equalsIgnoreCase(type) && SAProcess.getInstance().COLUMN_INVOICE_MAP.get(salesBean.getColNameDisp3()) != null){
				   sql +=colName3 +" as "+colAlias3+", \n ";
				}else{
				   sql +=" 0 as "+colAlias3+", \n ";
				}
			}
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
	        if( !"CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4())) &&
	        	!"CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4()))	
	        		){
				colAlias4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName ;
				colName4 = reportU.genColumnName(salesBean.getColNameDisp4(),salesBean.getColNameUnit4());
				if("ORDER".equalsIgnoreCase(type) && SAProcess.getInstance().COLUMN_ORDER_MAP.get(salesBean.getColNameDisp4()) != null){
				   sql +=colName4 +" as "+colAlias4+", \n ";
				}else if("INVOICE".equalsIgnoreCase(type) && SAProcess.getInstance().COLUMN_INVOICE_MAP.get(salesBean.getColNameDisp4()) != null){
				   sql +=colName4 +" as "+colAlias4+", \n ";
				}else{
				   sql +=" 0 as "+colAlias4+", \n ";
				}
			}
		}
		
		return sql;
	}

}
