package com.isecinc.pens.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.dao.BMECControlDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;

public class ReportOnhandLotus_SQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuilder genSQL(Connection conn,OnhandSummary c,String summaryType) throws Exception{
		StringBuilder sql = new StringBuilder();
		String onhandDateAsOfConfigStr = "";
		Date onhandDateAsOfConfig = null;
		Date asofDate = null;
		boolean unionAllFlag = false;
		try {
			logger.debug("SummaryType:"+summaryType);
			
			onhandDateAsOfConfigStr = BMECControlDAO.getOnhandDateLotusAsOf(conn);
			onhandDateAsOfConfig = Utils.parse(onhandDateAsOfConfigStr,Utils.DD_MM_YYYY_WITHOUT_SLASH);
			
			//prepare parameter
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getSalesDate()).equals("")){
				asofDate = Utils.parse(c.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = Utils.stringValue(asofDate, Utils.DD_MM_YYYY_WITH_SLASH);
			}
			sql.append("\n SELECT A.*");
			sql.append("\n,(SELECT NVL(MAX(RETAIL_PRICE_BF),0) FROM PENSBME_ONHAND_BME_LOCKED T WHERE A.group_type = T.group_item) as retail_price_bf \n");
			sql.append("\n,(A.ONHAND_QTY *(SELECT NVL(MAX(RETAIL_PRICE_BF),0) FROM PENSBME_ONHAND_BME_LOCKED T WHERE A.group_type = T.group_item)) as onhand_amt \n");
			sql.append("\n FROM ( ");
			if("GroupCode".equalsIgnoreCase(summaryType)){
				sql.append("\n SELECT A.customer_code,A.customer_desc ,A.group_type ");
				
				sql.append("\n ,SUM(A.SALE_IN_QTY) as SALE_IN_QTY");
				sql.append("\n ,SUM(A.SALE_OUT_QTY) AS SALE_OUT_QTY");
				sql.append("\n ,SUM(A.SALE_RETURN_QTY) AS SALE_RETURN_QTY");
				sql.append("\n ,SUM(A.ADJUST_QTY) AS ADJUST_QTY");
				sql.append("\n ,SUM(A.STOCK_SHORT_QTY) AS STOCK_SHORT_QTY");
				sql.append("\n ,SUM(A.ONHAND_QTY) AS ONHAND_QTY");
				sql.append("\n FROM(");
			}else{
			    sql.append("\n SELECT A.* FROM(");
			}
			sql.append("\n SELECT M.*");
			sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
			sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) as ADJUST_QTY ");
			sql.append("\n , NVL(STOCK_SHORT.STOCK_SHORT_QTY,0) as STOCK_SHORT_QTY ");
			
			sql.append("\n , (NVL(SALE_IN.SALE_IN_QTY,0) " + //SaleIn
							"\n -(" +
							"\n    NVL(SALE_OUT.SALE_OUT_QTY,0)  " +//SaleOut
							"\n  + NVL(SALE_RETURN.SALE_RETURN_QTY,0) " +//Return
							"\n  )" +
							"\n  + ( NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) )" + //Adjust
							"\n  + NVL(STOCK_SHORT.STOCK_SHORT_QTY,0) " +//Short
					   "\n  ) ONHAND_QTY");
			
			sql.append("\n FROM(  ");
					sql.append("\n SELECT DISTINCT ");
					sql.append("\n C.customer_code,P.inventory_item_code as pens_item,   ");
					sql.append("\n (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
							"       M.pens_value = C.customer_code AND M.reference_code ='Store') as customer_desc, ");
					sql.append("\n substr(P.inventory_item_desc,0,6) as group_type ");
					sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
					sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
					sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n AND V.customer_id = C.customer_id  ");
					sql.append("\n AND V.Customer_id IS NOT NULL   ");
					sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
					sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
					
					//Lotus Only 020047
					sql.append("\n AND C.customer_code LIKE '020047%'");
					
					if( !Utils.isNull(c.getSalesDate()).equals("")){
	                    sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("UNION");
					
					sql.append("\n SELECT distinct ");
					sql.append("\n J.store_code as customer_code,I.pens_item,  ");
					sql.append("\n (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
							"       M.pens_value = J.store_code AND M.reference_code ='Store') as customer_desc, ");
					sql.append("\n I.group_code as group_type ");
					
					sql.append("\n FROM PENSBME_PICK_JOB J ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND J.job_id = B.job_id  ");
					sql.append("\n AND B.job_id = I.job_id ");
					sql.append("\n AND B.box_no = I.box_no ");
	
					//Lotus Only 020047
					sql.append("\n AND J.cust_group = '020047'");
					
					if( !Utils.isNull(c.getSalesDate()).equals("")){
						sql.append("\n AND J.close_date >= to_date('"+onhandDateAsOfConfigStr+"','ddmmyyyy')  ");
                        sql.append("\n AND J.close_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					
                    sql.append("UNION");
					
                    sql.append("\n SELECT distinct ");
    				sql.append("\n L.PENS_CUST_CODE as customer_code,L.PENS_ITEM ");
    				sql.append("\n ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
    				sql.append("\n   M.pens_value = L.PENS_CUST_CODE AND M.reference_code ='Store') as customer_desc ");
					sql.append("\n ,L.PENS_GROUP_TYPE as group_type ");
					sql.append("\n FROM PENSBME_SALES_FROM_LOTUS L ");
					sql.append("\n WHERE 1=1 ");
					if( !Utils.isNull(c.getSalesDate()).equals("")){
                        sql.append("\n AND L.sales_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					
					sql.append("UNION");
					
					/** Adjust issue  **/
					sql.append("\n SELECT DISTINCT L.store_code as customer_code,L.item_issue as pens_item ");
					sql.append("\n ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
    				sql.append("\n   M.pens_value = L.store_code AND M.reference_code ='Store') as customer_desc ");
					sql.append("\n ,L.item_issue_desc as group_type");
					sql.append("\n FROM PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
					// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
					if( !Utils.isNull(c.getSalesDate()).equals("")){
		                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.item_issue_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					
                   sql.append("UNION");
					
					/** Adjust receipt **/
					sql.append("\n SELECT DISTINCT L.store_code as customer_code,L.item_receipt as pens_item ");
					sql.append("\n ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
    				sql.append("\n   M.pens_value = L.store_code AND M.reference_code ='Store') as customer_desc ");
					sql.append("\n ,L.item_receipt_desc as group_type");
					sql.append("\n FROM PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
					// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
					if( !Utils.isNull(c.getSalesDate()).equals("")){
		                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.item_receipt_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					
					sql.append("UNION");
					
					/** Adjust item **/
					sql.append("\n SELECT DISTINCT ");
					sql.append("\n  L.STORE_CODE as customer_code,L.item_adjust as pens_item ");
					sql.append("\n ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
    				sql.append("\n   M.pens_value = L.store_code AND M.reference_code ='Store') as customer_desc ");
					sql.append("\n ,L.item_adjust_desc as group_type ");
					sql.append("\n FROM PENSBME_ADJUST_SALES L  WHERE 1=1 ");	 
					if( !Utils.isNull(c.getSalesDate()).equals("")){
		                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.item_adjust_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
	
            sql.append("\n )M ");
    		sql.append("\n LEFT OUTER JOIN(	 ");
    				sql.append("\n SELECT ");
    				sql.append("\n L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
					sql.append("\n L.PENS_GROUP_TYPE as group_type, ");
					sql.append("\n NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
					sql.append("\n FROM ");
					sql.append("\n PENSBME_SALES_FROM_LOTUS L ");
					sql.append("\n WHERE 1=1 ");
						 
					if( !Utils.isNull(c.getSalesDate()).equals("")){
                        sql.append("\n AND L.sales_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n  GROUP BY ");
					sql.append("\n  L.PENS_CUST_CODE,L.PENS_ITEM, ");
					sql.append("\n  L.PENS_GROUP_TYPE ");
					sql.append("\n )SALE_OUT ");
					sql.append("\n ON  M.customer_code = SALE_OUT.customer_code and M.pens_item = SALE_OUT.pens_item ");	 
					sql.append("\n AND M.group_type = SALE_OUT.group_type ");
					
			sql.append("\n LEFT OUTER JOIN( ");
					sql.append("\n SELECT  ");
					sql.append("\n C.customer_code ,P.inventory_item_code as pens_item,  ");
					sql.append("\n substr(P.inventory_item_desc,0,6) as group_type, ");
					sql.append("\n NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
					sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
					sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
					sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n AND V.customer_id = C.customer_id  ");
					sql.append("\n AND V.Customer_id IS NOT NULL   ");
					sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
					sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
					
					//Lotus Only 020047
					sql.append("\n AND C.customer_code LIKE '020047%'");
					
					if( !Utils.isNull(c.getSalesDate()).equals("")){
                        sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
						
					sql.append("\n GROUP BY ");
					sql.append("\n C.customer_code,P.inventory_item_code,  ");
					sql.append("\n substr(P.inventory_item_desc,0,6) ");
			sql.append("\n )SALE_IN ");
			sql.append("\n ON  M.customer_code = SALE_IN.customer_code and M.pens_item = SALE_IN.pens_item ");
			sql.append("\n AND M.group_type  = SALE_IN.group_type ");
			sql.append("\n LEFT OUTER JOIN ( ");
			
				sql.append("\n SELECT  ");
				sql.append("\n A.customer_code,A.pens_item,A.group_type,  ");
				sql.append("\n NVL(SUM(A.SALE_RETURN_QTY),0)  as SALE_RETURN_QTY ");
				sql.append("\n FROM( ");
				
			        if(asofDate.before(onhandDateAsOfConfig)){// asofDate < onhandDate
			        	
						sql.append("\n SELECT  ");
						sql.append("\n C.customer_code,P.inventory_item_code as pens_item,  ");
						sql.append("\n substr(P.inventory_item_desc,0,6) as group_type, ");
						sql.append("\n NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047%'");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_code ,P.inventory_item_code,  ");
						sql.append("\n substr(P.inventory_item_desc,0,6)" );
			         }
					
			        if(asofDate.equals(onhandDateAsOfConfig) || asofDate.after(onhandDateAsOfConfig) ){ //asOfDate >= onhandDateConfig
			        	
			        	sql.append("\n SELECT  ");
						sql.append("\n C.customer_code,P.inventory_item_code as pens_item,  ");
						sql.append("\n substr(P.inventory_item_desc,0,6) as group_type, ");
						sql.append("\n NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047%'");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND V.invoice_date < to_date('"+onhandDateAsOfConfigStr+"','ddmmyyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_code ,P.inventory_item_code,  ");
						sql.append("\n substr(P.inventory_item_desc,0,6)" );
						
			        	sql.append("\n UNION ALL ");
			        	
			        	sql.append("\n SELECT  ");
						sql.append("\n J.store_code as customer_code,I.pens_item,  ");
						sql.append("\n I.group_code as group_type, ");
						sql.append("\n COUNT(*) as SALE_RETURN_QTY ");
						
						sql.append("\n FROM PENSBME_PICK_JOB J,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND J.job_id = B.job_id  ");
						sql.append("\n AND B.job_id = I.job_id ");
						sql.append("\n AND B.box_no = I.box_no ");
						sql.append("\n AND I.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
						
						//Lotus Only 020047
						sql.append("\n AND J.cust_group = '020047'");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
							sql.append("\n AND J.close_date >= to_date('"+onhandDateAsOfConfigStr+"','ddmmyyyy')  ");
	                        sql.append("\n AND J.close_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY ");
						sql.append("\n J.store_code ,I.pens_item ,I.group_code ");
			        }
			        sql.append("\n )A ");
			        sql.append("\n GROUP BY A.customer_code,A.pens_item,A.group_type ");

			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON  M.customer_code = SALE_RETURN.customer_code and M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
			
			//Stock Issue
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n SELECT ");
			sql.append("\n L.store_code as customer_code,L.item_issue as pens_item,L.item_issue_desc as group_type, ");
			sql.append("\n (NVL(SUM(ITEM_ISSUE_QTY),0)*-1) AS ISSUE_QTY ");
			sql.append("\n FROM PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
			// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
			if( !Utils.isNull(c.getSalesDate()).equals("")){
                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n AND L.item_issue_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
			}
			sql.append("\n  GROUP BY L.STORE_CODE,L.item_issue, L.item_issue_desc ");
			sql.append("\n )STOCK_ISSUE ");
			sql.append("\n ON  M.customer_code = STOCK_ISSUE.customer_code and M.pens_item = STOCK_ISSUE.pens_item ");	 
			sql.append("\n AND M.group_type = STOCK_ISSUE.group_type ");
			
			//Stock Receipt
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n SELECT ");
			sql.append("\n L.STORE_CODE as customer_code,L.item_receipt as pens_item,L.item_receipt_desc as group_type, ");
			sql.append("\n NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
			sql.append("\n FROM PENSBME_ADJUST_INVENTORY L  WHERE 1=1 ");
			//L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
			if( !Utils.isNull(c.getSalesDate()).equals("")){
                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n AND L.item_receipt_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
			}
			sql.append("\n  GROUP BY ");
			sql.append("\n  L.STORE_CODE,L.item_receipt, ");
			sql.append("\n  L.item_receipt_desc ");
			sql.append("\n )STOCK_RECEIPT ");
			sql.append("\n ON  M.customer_code = STOCK_RECEIPT.customer_code and M.pens_item = STOCK_RECEIPT.pens_item ");	 
			sql.append("\n AND M.group_type = STOCK_RECEIPT.group_type ");
			
			//STOCK_SHORT
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n SELECT ");
			sql.append("\n L.STORE_CODE as customer_code,L.item_adjust as pens_item,L.item_adjust_desc as group_type, ");
			sql.append("\n NVL(SUM(ITEM_ADJUST_QTY),0) AS STOCK_SHORT_QTY ");
			sql.append("\n FROM PENSBME_ADJUST_SALES L  WHERE 1=1 ");	 
			if( !Utils.isNull(c.getSalesDate()).equals("")){
                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n AND L.item_adjust_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
			}
			sql.append("\n  GROUP BY L.STORE_CODE,L.item_adjust, L.item_adjust_desc ");
			sql.append("\n )STOCK_SHORT ");
			sql.append("\n ON  M.customer_code = STOCK_SHORT.customer_code and M.pens_item = STOCK_SHORT.pens_item ");	 
			sql.append("\n AND M.group_type = STOCK_SHORT.group_type ");
			
			sql.append("\n ) A ");
			sql.append("\n WHERE A.GROUP_TYPE IS NOT NULL ");
			if("GroupCode".equalsIgnoreCase(summaryType)){
				sql.append("\n GROUP BY A.customer_code,A.customer_desc,A.group_type ");
			}
			sql.append("\n  ORDER BY A.customer_code,A.group_type asc ");
			sql.append("\n )A ");
			//debug write sql to file
			if(logger.isDebugEnabled()){
			   FileUtil.writeFile("d:/temp/sql.sql", sql.toString());
			}
			//logger.debug("sql:"+sql);
			
		} catch (Exception e) {
			throw e;
		} finally {
		
		}
		return sql;
	}
}
