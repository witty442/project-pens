package com.isecinc.pens.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class ReportOnhandMTTSQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuilder genSQL(Connection conn,OnhandSummary c,User user,Date initDate,String summaryType) throws Exception{
		StringBuilder sql = new StringBuilder();
		try {
			//prepare parameter
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getSalesDate()).equals("")){
				Date d = DateUtil.parse(c.getSalesDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = DateUtil.stringValue(d, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			String initDateStr ="";
			if( initDate != null){
				initDateStr = DateUtil.stringValue(initDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			if("GroupCode".equalsIgnoreCase(summaryType)){
				sql.append("\n SELECT A.customer_code,A.customer_desc , A.cust_no ,A.group_type ");
				sql.append("\n ,SUM(A.INIT_SALE_QTY) as INIT_SALE_QTY");
				sql.append("\n ,SUM(A.SALE_IN_QTY) AS SALE_IN_QTY");
				sql.append("\n ,SUM(A.SALE_OUT_QTY) AS SALE_OUT_QTY");
				sql.append("\n ,SUM(A.SALE_RETURN_QTY) AS SALE_RETURN_QTY");
				sql.append("\n ,SUM(A.ONHAND_QTY) AS ONHAND_QTY");
				sql.append("\n FROM(");
			}else{
			  sql.append("\n SELECT A.* FROM(");
			}
			sql.append("\n SELECT M.*");
			
			sql.append("\n , NVL(INIT_MTT.INIT_SALE_QTY,0) AS INIT_SALE_QTY");
			sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
			sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , (NVL(INIT_MTT.INIT_SALE_QTY,0) + NVL(SALE_IN.SALE_IN_QTY,0)) - (NVL(SALE_OUT.SALE_OUT_QTY,0)+NVL(SALE_RETURN.SALE_RETURN_QTY,0)) ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			   sql.append("\n SELECT DISTINCT AA.* FROM(");
					sql.append("\n SELECT DISTINCT ");
					sql.append("\n M.customer_code,M.customer_desc,M.cust_no,P.inventory_item_code as pens_item,   ");
					sql.append("\n MI.group_type ");
					sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V V   ");
					sql.append("\n ,PENSBI.XXPENS_BI_MST_CUSTOMER C  ");
					sql.append("\n ,PENSBI.XXPENS_BI_MST_ITEM P  ");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
					sql.append("\n   PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE  ( pens_value like  '"+Constants.STORE_TYPE_MTT_CODE_1+"%' " );
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_HISHER_CODE+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_2+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_3+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_4+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_5+"%')");
					sql.append("\n   AND M.reference_code ='Store' ");
			        sql.append("\n  ) M ");
			        sql.append("\n,( SELECT DISTINCT M.PENS_VALUE as pens_item ,PENS_DESC2 as group_type ");
			        sql.append("\n   from PENSBI.PENSBME_MST_REFERENCE M  ");
			        sql.append("\n   WHERE M.reference_code ='LotusItem' ");
			        sql.append("\n ) MI ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND P.inventory_item_code = MI.pens_item ");
					sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n AND V.customer_id = C.customer_id ");
					sql.append("\n AND M.cust_no = C.customer_code  ");
					sql.append("\n AND V.Customer_id IS NOT NULL   ");
					sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
					//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
					
					if(initDate != null){
						 sql.append("\n AND V.invoice_date > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND M.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND MI.GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
					}
					sql.append("\n UNION ALL");
					
					sql.append("\n SELECT ");
   				    sql.append("\n DISTINCT L.CUST_NO as customer_code ,M.customer_desc,M.cust_no,L.PENS_ITEM, ");
					sql.append("\n L.GROUP_CODE as group_type ");
					sql.append("\n FROM PENSBME_MTT_INIT_STK H,PENSBME_MTT_ONHAND_INIT_STK L");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
					sql.append("\n   PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE  ( pens_value like  '"+Constants.STORE_TYPE_MTT_CODE_1+"%' ");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_HISHER_CODE+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_2+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_3+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_4+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_5+"%')");
					sql.append("\n   AND M.reference_code ='Store' ");
			        sql.append("\n  ) M ");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n AND H.cust_no = L.cust_no  ");
					sql.append("\n AND M.customer_code = H.cust_no  ");
					sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.CUST_NO IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.GROUP_CODE LIKE '"+c.getGroup()+"%'");
					}
					
					sql.append("\n UNION ALL");
				
					sql.append("\n SELECT DISTINCT");
					
					if(    Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER)
						|| Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER_2)
						|| Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER_3)
						|| Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER_4)
						|| Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER_5)
					){
						sql.append("\n  L.CUST_NO as customer_code ");
						sql.append("\n ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = L.cust_no AND M.reference_code ='Store') as customer_desc ");
						sql.append("\n ,(select M.interface_value from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = L.cust_no AND M.reference_code ='Store') as cust_no ");
						sql.append("\n ,L.PENS_ITEM ,L.GROUP_CODE as group_type ");
						sql.append("\n FROM PENSBME_SALES_FROM_KING L");
						sql.append("\n WHERE 1=1 ");
					}else{
						sql.append("\n  L.CUST_NO as customer_code ");
						sql.append("\n ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = L.cust_no AND M.reference_code ='Store') as customer_desc ");
						sql.append("\n ,(select M.interface_value from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = L.cust_no AND M.reference_code ='Store') as cust_no ");
						sql.append("\n ,L.PENS_ITEM ,L.GROUP_CODE as group_type ");
						sql.append("\n FROM PENSBME_SALES_OUT L");
						sql.append("\n WHERE 1=1 ");
						sql.append("\n AND L.status <> '"+PickConstants.STATUS_CANCEL+"'");
					}

					if(initDate != null){
						 sql.append("\n AND L.sale_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.CUST_NO IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.GROUP_CODE LIKE '"+c.getGroup()+"%' ");
					}
					
					sql.append("\n )AA");
					
           sql.append("\n )M ");
           sql.append("\n LEFT OUTER JOIN(	 ");
	            /** INIT MTT STOCK **/
      		    sql.append("\n SELECT ");
				sql.append("\n L.CUST_NO as customer_code,L.PENS_ITEM, ");
				sql.append("\n L.GROUP_CODE as group_type, ");
				sql.append("\n SUM(QTY) AS INIT_SALE_QTY ");
				sql.append("\n FROM PENSBME_MTT_INIT_STK H,PENSBME_MTT_ONHAND_INIT_STK L");
				sql.append("\n ,( ");
				sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
				sql.append("\n   PENSBME_MST_REFERENCE M ");
				sql.append("\n   WHERE  ( pens_value like  '"+Constants.STORE_TYPE_MTT_CODE_1+"%' " );
				sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER+"%'");
				sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_HISHER_CODE+"%'");
				sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_2+"%'");
				sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_3+"%'");
				sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_4+"%'");
				sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_5+"%')");
				sql.append("\n   AND M.reference_code ='Store' ");
		        sql.append("\n  ) M ");
				sql.append("\n WHERE 1=1 ");
				sql.append("\n and H.cust_no = L.cust_no  ");
				sql.append("\n AND M.customer_code = H.cust_no  ");
				sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
				if( !Utils.isNull(initDateStr).equals("")){
					 sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND L.CUST_NO IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.GROUP_CODE LIKE '"+c.getGroup()+"%' ");
				}
				sql.append("\n  GROUP BY L.CUST_NO,L.PENS_ITEM,L.GROUP_CODE ");
				sql.append("\n )INIT_MTT ");
				sql.append("\n ON  M.customer_code = INIT_MTT.customer_code and M.pens_item = INIT_MTT.pens_item ");	 
				sql.append("\n AND M.group_type = INIT_MTT.group_type ");		
			
   		   sql.append("\n LEFT OUTER JOIN(	 ");
   				sql.append("\n SELECT ");
					if( Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER)
						|| Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER_2)
						|| Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER_3)
						|| Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER_4)
						|| Utils.isNull(c.getPensCustCodeFrom()).startsWith(Constants.STORE_TYPE_KING_POWER_5) ){
						
						sql.append("\n L.CUST_NO as customer_code,L.PENS_ITEM, ");
				        sql.append("\n L.GROUP_CODE as group_type, ");
						sql.append("\n NVL(SUM(L.QTY),0)AS SALE_OUT_QTY ");
						sql.append("\n FROM PENSBME_SALES_FROM_KING L");
						sql.append("\n WHERE 1=1 ");
					}else{
						sql.append("\n L.CUST_NO as customer_code,L.PENS_ITEM, ");
					    sql.append("\n L.GROUP_CODE as group_type,");
					    sql.append("\n COUNT(*) AS SALE_OUT_QTY ");
						sql.append("\n FROM PENSBME_SALES_OUT L");
						sql.append("\n WHERE 1=1 ");
						sql.append("\n AND L.status <> '"+PickConstants.STATUS_CANCEL+"'");
					}

					if(initDate != null){
						 sql.append("\n AND L.sale_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.CUST_NO IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.GROUP_CODE LIKE '"+c.getGroup()+"%' ");
					}
					sql.append("\n  GROUP BY L.CUST_NO,L.PENS_ITEM, L.GROUP_CODE ");
					sql.append("\n )SALE_OUT ");
					sql.append("\n ON  M.customer_code = SALE_OUT.customer_code and M.pens_item = SALE_OUT.pens_item ");	 
					sql.append("\n AND M.group_type = SALE_OUT.group_type ");
	
			sql.append("\n LEFT OUTER JOIN( ");
					sql.append("\n SELECT  ");
					sql.append("\n M.customer_code ,P.inventory_item_code as pens_item,  ");
					sql.append("\n MI.group_type ");
					sql.append("\n ,NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
					sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
					sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
					sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc from ");
					sql.append("\n   PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE  ( pens_value like  '"+Constants.STORE_TYPE_MTT_CODE_1+"%' " );
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_HISHER_CODE+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_2+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_3+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_4+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_5+"%')");
					sql.append("\n   AND M.reference_code ='Store' ");
			        sql.append("\n  ) M ");
			        sql.append("\n,( SELECT DISTINCT M.PENS_VALUE as pens_item ,PENS_DESC2 as group_type ");
			        sql.append("\n   from PENSBI.PENSBME_MST_REFERENCE M  ");
			        sql.append("\n   WHERE M.reference_code ='LotusItem' ");
			        sql.append("\n ) MI ");
					sql.append("\n WHERE P.inventory_item_code = MI.pens_item  ");
					sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n AND V.customer_id = C.customer_id  ");
					sql.append("\n AND M.cust_no = C.customer_code  ");
					sql.append("\n AND V.Customer_id IS NOT NULL   ");
					sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
					//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
					
					if(initDate != null){
						 sql.append("\n AND V.invoice_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND V.invoice_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND V.invoice_date   <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND M.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND MI.group_type LIKE '"+c.getGroup()+"%' ");
					}
						
					sql.append("\n GROUP BY M.customer_code,P.inventory_item_code, MI.group_type ");
			sql.append("\n )SALE_IN ");
			sql.append("\n ON  M.customer_code = SALE_IN.customer_code and M.pens_item = SALE_IN.pens_item ");
			sql.append("\n AND M.group_type  = SALE_IN.group_type ");
			sql.append("\n LEFT OUTER JOIN ( ");
					sql.append("\n SELECT  ");
					sql.append("\n  M.customer_code,P.inventory_item_code as pens_item ");
					sql.append("\n ,MI.group_type ");
					sql.append("\n ,NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
					sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
					sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
					sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc from ");
					sql.append("\n   PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE  ( pens_value like  '"+Constants.STORE_TYPE_MTT_CODE_1+"%' " );
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_HISHER_CODE+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_2+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_3+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_4+"%'");
					sql.append("\n           OR pens_value like '"+Constants.STORE_TYPE_KING_POWER_5+"%')");
					sql.append("\n   AND M.reference_code ='Store' ");
			        sql.append("\n  ) M ");
			        sql.append("\n,( SELECT DISTINCT M.PENS_VALUE as pens_item ,PENS_DESC2 as group_type ");
			        sql.append("\n   from PENSBI.PENSBME_MST_REFERENCE M  ");
			        sql.append("\n   WHERE M.reference_code ='LotusItem' ");
			        sql.append("\n ) MI ");
					sql.append("\n WHERE P.inventory_item_code = MI.pens_item  ");
					sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n AND V.customer_id = C.customer_id  ");
					sql.append("\n AND M.cust_no = C.customer_code  ");
					sql.append("\n AND V.Customer_id IS NOT NULL   ");
					sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
					//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
					
					if(initDate != null){
						 sql.append("\n AND V.invoice_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND V.invoice_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND V.invoice_date   <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}

					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND M.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND MI.group_type LIKE '"+c.getGroup()+"%' ");
					}
					sql.append("\n GROUP BY M.customer_code ,P.inventory_item_code, MI.group_type" );
			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON  M.customer_code = SALE_RETURN.customer_code and M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type");
			sql.append("\n ) A ");
			
			if("GroupCode".equalsIgnoreCase(summaryType)){
				sql.append("\n GROUP BY A.customer_code,A.customer_desc , A.cust_no ,A.group_type ");
			}
			
			sql.append("\n ORDER BY A.customer_code,A.group_type asc ");
			
			//debug write sql to file
			if(logger.isDebugEnabled()){
			   FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
			}

		} catch (Exception e) {
			throw e;
		} finally {
		
		}
		return sql;
 }
}
