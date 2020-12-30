package com.isecinc.pens.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.web.reportall.ReportAllSpecialUtils;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

/** KingPower = Duty-free */
public class ReportOnhandAsOfKingSQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuilder genSQL(Connection conn,OnhandSummary c,User user,Date initDate,String summaryType) throws Exception{
		StringBuilder sql = new StringBuilder();
		String storeType ="DUTYFREE";
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
			sql.append("\n /** ReportOnhandAsOfKingSQL **/ ");
			
			if("GroupCode".equalsIgnoreCase(summaryType)){
				sql.append("\n SELECT A.customer_code,A.customer_desc , A.cust_no ,A.group_type ");
				sql.append("\n ,SUM(A.INIT_SALE_QTY) as INIT_SALE_QTY");
				sql.append("\n ,SUM(A.SALE_IN_QTY) AS SALE_IN_QTY");
				sql.append("\n ,SUM(A.SALE_OUT_QTY) AS SALE_OUT_QTY");
				sql.append("\n ,SUM(A.SALE_RETURN_QTY) AS SALE_RETURN_QTY");
				sql.append("\n ,SUM(A.ADJUST_QTY) AS ADJUST_QTY");
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
			sql.append("\n , NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) as ADJUST_QTY ");
			
			sql.append("\n , ( (NVL(INIT_MTT.INIT_SALE_QTY,0) + NVL(SALE_IN.SALE_IN_QTY,0)) ");//Init+SalesIn
			sql.append("\n    - ( NVL(SALE_OUT.SALE_OUT_QTY,0)");//SalesOut
			sql.append("\n      + NVL(SALE_RETURN.SALE_RETURN_QTY,0) )");//RETURN
			sql.append("\n    + ( NVL(STOCK_ISSUE.ISSUE_QTY,0) + NVL(STOCK_RECEIPT.RECEIPT_QTY,0) )");//Adjust
			sql.append("\n    ) ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			   sql.append("\n SELECT DISTINCT AA.* FROM(");
					sql.append("\n SELECT DISTINCT ");
					sql.append("\n M.customer_code,M.customer_desc,M.cust_no,P.inventory_item_code as pens_item,   ");
					sql.append("\n MP.pens_desc2 as group_type ");
					sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS V   ");
					sql.append("\n ,PENSBI.XXPENS_BI_MST_CUSTOMER C  ");
					sql.append("\n ,PENSBI.XXPENS_BI_MST_ITEM P  ");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value , pens_desc2");
					sql.append("\n   from PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE M.reference_code ='LotusItem' ");
			        sql.append("\n  ) MP ");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no ");
					sql.append("\n   ,pens_desc as customer_desc from PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE M.reference_code ='Store' ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
			        sql.append("\n  ) M ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n AND V.customer_id = C.customer_id ");
					sql.append("\n AND M.cust_no = C.customer_code  ");
					sql.append("\n AND V.Customer_id IS NOT NULL   ");
					sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
					sql.append("\n AND P.inventory_item_code = MP.pens_value ");
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
						sql.append("\n AND MP.pens_desc2 LIKE '"+c.getGroup()+"%' ");
					}
					sql.append("\n UNION ");
					
					sql.append("\n SELECT ");
   				    sql.append("\n DISTINCT L.CUST_NO as customer_code ,M.customer_desc,M.cust_no,L.PENS_ITEM, ");
					sql.append("\n L.GROUP_CODE as group_type ");
					sql.append("\n FROM PENSBI.PENSBME_MTT_INIT_STK H,PENSBI.PENSBME_MTT_ONHAND_INIT_STK L");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
					sql.append("\n   PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   where M.reference_code ='Store' ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
			        sql.append("\n  ) M ");
					sql.append("\n WHERE H.cust_no = L.cust_no  ");
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
						sql.append("\n AND L.GROUP_CODE LIKE '"+c.getGroup()+"%' ");
					}
					sql.append("\n UNION ");
					sql.append("\n SELECT DISTINCT");
					sql.append("\n  L.CUST_NO as customer_code ");
					sql.append("\n ,(select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE " );
					sql.append("\n        M.pens_value = L.cust_no AND M.reference_code ='Store') as customer_desc ");
					sql.append("\n ,(select M.interface_value from PENSBI.PENSBME_MST_REFERENCE M WHERE " );
					sql.append("\n        M.pens_value = L.cust_no AND M.reference_code ='Store') as cust_no ");
					sql.append("\n ,L.PENS_ITEM ,L.GROUP_CODE as group_type ");
					sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_KING L");
					sql.append("\n WHERE 1=1 ");
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
					sql.append("\n UNION ");
					sql.append("\n\t SELECT DISTINCT J.store_code as customer_code  ");
					sql.append("\n ,(select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE " );
					sql.append("\n        M.pens_value = j.store_code AND M.reference_code ='Store') as customer_desc ");
					sql.append("\n ,(select M.interface_value from PENSBI.PENSBME_MST_REFERENCE M WHERE " );
					sql.append("\n        M.pens_value = j.store_code AND M.reference_code ='Store') as cust_no ");
					sql.append("\n\t ,I.pens_item,I.group_code as group_type");
					sql.append("\n\t FROM PENSBI.PENSBME_PICK_JOB J,PENSBI.PENSBME_PICK_BARCODE B ");
					sql.append("\n\t ,PENSBI.PENSBME_PICK_BARCODE_ITEM I ");
					sql.append("\n\t WHERE 1=1   ");
					sql.append("\n\t AND J.job_id = B.job_id  ");
					sql.append("\n\t AND B.job_id = I.job_id ");
					sql.append("\n\t AND B.box_no = I.box_no ");
					sql.append("\n\t AND I.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
					//Lotus DuttyFree
					sql.append("\n\t AND J.store_code IN (");
					sql.append("\n\t   select pens_value from PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n\t   WHERE M.reference_code ='Store' ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
			        sql.append("\n\t  ) ");
					
					if(initDate != null){
					     sql.append("\n\t AND J.close_date > to_date('"+initDateStr+"','dd/mm/yyyy')");
						 sql.append("\n\t AND J.close_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')");
				     }else{
				    	 sql.append("\n\t AND J.close_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')");
				     }
					  
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n\t AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n\t AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n\t AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n\t AND I.group_code like '"+c.getGroup()+"%'");
					}
                   sql.append("\n UNION");
					
					/** Adjust issue  **/
					sql.append("\n SELECT DISTINCT L.store_code as customer_code ");
					sql.append("\n ,(select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE ");
    				sql.append("\n   M.pens_value = L.store_code AND M.reference_code ='Store') as customer_desc ");
    				sql.append("\n ,(select M.interface_value from PENSBI.PENSBME_MST_REFERENCE M WHERE " );
					sql.append("\n        M.pens_value = L.store_code AND M.reference_code ='Store') as cust_no ");
					sql.append("\n ,L.item_issue as pens_item,L.item_issue_desc as group_type");
					sql.append("\n FROM PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " ); 
					if( !Utils.isNull(c.getSalesDate()).equals("")){
		                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.item_issue_desc LIKE '"+c.getGroup()+"%' ");
					}
					//Lotus DuttyFree
					sql.append("\n\t AND L.store_code IN (");
					sql.append("\n\t   select pens_value from PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n\t   WHERE M.reference_code ='Store' ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
			        sql.append("\n\t  ) ");
					
                   sql.append("\n UNION");
					
					/** Adjust receipt **/
					sql.append("\n SELECT DISTINCT L.store_code as customer_code");
					sql.append("\n ,(select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE ");
    				sql.append("\n   M.pens_value = L.store_code AND M.reference_code ='Store') as customer_desc ");
    				sql.append("\n ,(select M.interface_value from PENSBI.PENSBME_MST_REFERENCE M WHERE " );
					sql.append("\n        M.pens_value = L.store_code AND M.reference_code ='Store') as cust_no ");
					sql.append("\n ,L.item_receipt as pens_item ,L.item_receipt_desc as group_type");
					sql.append("\n FROM PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
					if( !Utils.isNull(c.getSalesDate()).equals("")){
		                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.item_receipt_desc LIKE '"+c.getGroup()+"%' ");
					}
					//Lotus DuttyFree
					sql.append("\n\t AND L.store_code IN (");
					sql.append("\n\t   select pens_value from PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n\t   WHERE M.reference_code ='Store' ");
					//Filter By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
			        sql.append("\n\t  ) ");
					
					sql.append("\n )AA");
           sql.append("\n )M ");
           sql.append("\n LEFT OUTER JOIN(	 ");
                sql.append("\n /**** INIT MTT STOCK *****************/ ");
      		    sql.append("\n SELECT L.CUST_NO as customer_code,L.PENS_ITEM, ");
				sql.append("\n L.GROUP_CODE as group_type, SUM(QTY) AS INIT_SALE_QTY ");
				sql.append("\n FROM PENSBI.PENSBME_MTT_INIT_STK H,PENSBI.PENSBME_MTT_ONHAND_INIT_STK L");
				sql.append("\n ,( ");
				sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
				sql.append("\n   WHERE M.reference_code ='Store' ");
				//Filter By StoreType
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
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
				sql.append("\n ON  M.customer_code = INIT_MTT.customer_code ");
				sql.append("\n AND M.pens_item = INIT_MTT.pens_item ");	 
				sql.append("\n AND M.group_type = INIT_MTT.group_type ");		
			
   		   sql.append("\n LEFT OUTER JOIN(	 ");
	   		    sql.append("\n\t /******** SALE_OUT *****************/ ");
			    sql.append("\n\t SELECT L.CUST_NO as customer_code,L.PENS_ITEM, ");
		        sql.append("\n\t L.GROUP_CODE as group_type, NVL(SUM(L.QTY),0)AS SALE_OUT_QTY ");
				sql.append("\n\t FROM PENSBI.PENSBME_SALES_FROM_KING L");
				sql.append("\n\t WHERE 1=1 ");
				if(initDate != null){
					 sql.append("\n\t AND L.sale_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n\t AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n\t AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n\t AND L.CUST_NO IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND L.GROUP_CODE LIKE '"+c.getGroup()+"%' ");
				}
				sql.append("\n\t  GROUP BY L.CUST_NO,L.PENS_ITEM, L.GROUP_CODE ");
				sql.append("\n )SALE_OUT ");
				sql.append("\n ON  M.customer_code = SALE_OUT.customer_code ");
				sql.append("\n AND M.pens_item = SALE_OUT.pens_item ");	 
				sql.append("\n AND M.group_type = SALE_OUT.group_type ");
		
			sql.append("\n LEFT OUTER JOIN( ");
				
				sql.append("\n /******** SALE_IN *****************/ ");
				sql.append("\n\t SELECT M.customer_code ,P.inventory_item_code as pens_item,  ");
				sql.append("\n\t MP.pens_desc2 as group_type ,");
				
				/** special case (product) Sum Qty (convert CTN(SA) to EA(BME) )**/
				sql.append(ReportAllSpecialUtils.genSQLSumSpecialProduct("V.INVOICED_QTY","SALE_IN_QTY"));
				
				sql.append("\n\t FROM PENSBI.XXPENS_BI_SALES_ANALYSIS V  ");
				sql.append("\n\t ,PENSBI.XXPENS_BI_MST_CUSTOMER C ");
				sql.append("\n\t ,PENSBI.XXPENS_BI_MST_ITEM P  ");
				sql.append("\n\t ,( ");
				sql.append("\n\t   select distinct pens_value , pens_desc2");
				sql.append("\n\t   from PENSBME_MST_REFERENCE M ");
				sql.append("\n\t   WHERE M.reference_code ='LotusItem' ");
		        sql.append("\n\t  ) MP ");
				sql.append("\n\t ,( ");
				sql.append("\n\t   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc from ");
				sql.append("\n\t   PENSBI.PENSBME_MST_REFERENCE M ");
				sql.append("\n\t   WHERE M.reference_code ='Store' ");
				//Filter By StoreType
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
		        sql.append("\n\t  ) M ");
				sql.append("\n\t WHERE 1=1   ");
				sql.append("\n\t AND V.inventory_item_id = P.inventory_item_id  ");
				sql.append("\n\t AND V.customer_id = C.customer_id  ");
				sql.append("\n\t AND M.cust_no = C.customer_code  ");
				sql.append("\n\t AND V.Customer_id IS NOT NULL   ");
				sql.append("\n\t AND V.inventory_item_id IS NOT NULL  ");
				sql.append("\n\t AND P.inventory_item_code = MP.pens_value ");
				//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
				
				if(initDate != null){
					 sql.append("\n\t AND V.invoice_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n\t AND V.invoice_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n\t AND V.invoice_date   <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				    sql.append("\n\t AND M.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND MP.pens_desc2 LIKE '"+c.getGroup()+"%' ");
				}
				sql.append("\n\t GROUP BY M.customer_code,P.inventory_item_code,MP.pens_desc2 ");
			sql.append("\n )SALE_IN ");
			sql.append("\n ON  M.customer_code = SALE_IN.customer_code and M.pens_item = SALE_IN.pens_item ");
			sql.append("\n AND M.group_type  = SALE_IN.group_type ");
			
			sql.append("\n LEFT OUTER JOIN ( ");
				sql.append("\n /******************* SALE RETURN ********************************/");
				sql.append("\n\t SELECT J.store_code as customer_code,I.pens_item,  ");
				sql.append("\n\t I.group_code as group_type, ");
				sql.append("\n\t COUNT(*) as SALE_RETURN_QTY ");
				sql.append("\n\t FROM PENSBI.PENSBME_PICK_JOB J,PENSBI.PENSBME_PICK_BARCODE B ");
				sql.append("\n\t ,PENSBI.PENSBME_PICK_BARCODE_ITEM I ");
				sql.append("\n\t WHERE 1=1   ");
				sql.append("\n\t AND J.job_id = B.job_id  ");
				sql.append("\n\t AND B.job_id = I.job_id ");
				sql.append("\n\t AND B.box_no = I.box_no ");
				sql.append("\n\t AND I.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
				//Lotus DuttyFree
				sql.append("\n\t AND J.store_code IN (");
				sql.append("\n\t   select pens_value from PENSBI.PENSBME_MST_REFERENCE M ");
				sql.append("\n\t   WHERE M.reference_code ='Store' ");
				//Filter By StoreType
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
		        sql.append("\n\t  ) ");
				
				if(initDate != null){
				     sql.append("\n\t AND J.close_date > to_date('"+initDateStr+"','dd/mm/yyyy')");
					 sql.append("\n\t AND J.close_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')");
			     }else{
			    	 sql.append("\n\t AND J.close_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')");
			     }
				  
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				    sql.append("\n\t AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND I.group_code like '"+c.getGroup()+"%'");
				}
				sql.append("\n\t GROUP BY J.store_code ,I.pens_item ,I.group_code ");
			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON  M.customer_code = SALE_RETURN.customer_code ");
			sql.append("\n  AND M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type");
			
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n\t /******** Stock Issue(adjust) **********************/");
			sql.append("\n\t SELECT ");
			sql.append("\n\t L.store_code as customer_code,L.item_issue as pens_item,L.item_issue_desc as group_type, ");
			sql.append("\n\t (NVL(SUM(ITEM_ISSUE_QTY),0)*-1) AS ISSUE_QTY ");
			sql.append("\n\t FROM PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
			if( !Utils.isNull(c.getSalesDate()).equals("")){
                sql.append("\n\t AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n\t AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n\t AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n\t AND L.item_issue_desc LIKE '"+c.getGroup()+"%' ");
			}
			// DuttyFree
			sql.append("\n\t AND L.store_code IN (");
			sql.append("\n\t   select pens_value from PENSBI.PENSBME_MST_REFERENCE M ");
			sql.append("\n\t   WHERE M.reference_code ='Store' ");
			//Filter By StoreType
			sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
	        sql.append("\n\t  ) ");
	        
			sql.append("\n\t  GROUP BY L.STORE_CODE,L.item_issue, L.item_issue_desc ");
			sql.append("\n )STOCK_ISSUE ");
			sql.append("\n ON  M.customer_code = STOCK_ISSUE.customer_code ");
			sql.append("\n AND M.pens_item = STOCK_ISSUE.pens_item ");	 
			sql.append("\n AND M.group_type = STOCK_ISSUE.group_type ");
		
			sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n\t /*********** Stock Receipt(adjust) *********************/ ");
				sql.append("\n\t SELECT ");
				sql.append("\n\t L.STORE_CODE as customer_code,L.item_receipt as pens_item,L.item_receipt_desc as group_type, ");
				sql.append("\n\t NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
				sql.append("\n\t FROM PENSBI.PENSBME_ADJUST_INVENTORY L  WHERE 1=1 ");
				//NOT IN pensbme_group_unuse_lotus
				sql.append("\n\t AND L.item_receipt_desc NOT IN(select group_code from pensbme_group_unuse_lotus)");
				//L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
				if( !Utils.isNull(c.getSalesDate()).equals("")){
	                sql.append("\n\t AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND L.item_receipt_desc LIKE '"+c.getGroup()+"%' ");
				}
				// DuttyFree
				sql.append("\n\t AND L.store_code IN (");
				sql.append("\n\t   select pens_value from PENSBI.PENSBME_MST_REFERENCE M ");
				sql.append("\n\t   WHERE M.reference_code ='Store' ");
				//Filter By StoreType
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "pens_value"));
		        sql.append("\n\t  ) ");
				sql.append("\n\t  GROUP BY L.STORE_CODE,L.item_receipt, L.item_receipt_desc ");
			sql.append("\n )STOCK_RECEIPT ");
			sql.append("\n ON  M.customer_code = STOCK_RECEIPT.customer_code ");
			sql.append("\n AND M.pens_item = STOCK_RECEIPT.pens_item ");	 
			sql.append("\n AND M.group_type = STOCK_RECEIPT.group_type ");
			
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
