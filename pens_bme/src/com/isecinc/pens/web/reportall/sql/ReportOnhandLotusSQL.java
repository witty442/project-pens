package com.isecinc.pens.web.reportall.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class ReportOnhandLotusSQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static StringBuilder genSQL(Connection conn,ReportAllBean c,String summaryType) throws Exception{
		StringBuilder sql = new StringBuilder();
		String onhandDateAsOfConfigStr = "";
		Date onhandDateAsOfConfig = null;
		Date asofDate = null;
			try {
				logger.debug("SummaryType:"+summaryType);
				
				onhandDateAsOfConfigStr = ControlConstantsDB.getOnhandDateAsOfControl(conn,ControlConstantsDB.TYPE_ONHAND_DATE_LOTUS_AS_OF);
				onhandDateAsOfConfig = DateUtil.parse(onhandDateAsOfConfigStr,DateUtil.DD_MM_YYYY_WITHOUT_SLASH);
				
				//prepare parameter
				String christSalesDateStr ="";
				if( !Utils.isNull(c.getSalesDate()).equals("")){
					asofDate = DateUtil.parse(c.getSalesDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStr = DateUtil.stringValue(asofDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				}
				sql.append("\n SELECT A.*");
				sql.append("\n,(SELECT NVL(MAX(RETAIL_PRICE_BF),0) "
						+ "\n FROM PENSBI.PENSBME_ONHAND_BME_LOCKED T "
						+ "\n WHERE A.group_type = T.group_item"
						+ "\n OR A.group_type = T.material_master) as retail_price_bf \n");
				sql.append("\n,(A.ONHAND_QTY *(SELECT NVL(MAX(RETAIL_PRICE_BF),0) "
						+ "\n FROM PENSBI.PENSBME_ONHAND_BME_LOCKED T "
						+ "\n WHERE A.group_type = T.group_item"
						+ "\n OR A.group_type = T.material_master)) as onhand_amt \n");
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
						sql.append("\n (select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = C.customer_code AND M.reference_code ='Store') as customer_desc, ");
						sql.append("\n MP.MATERIAL_MASTER as group_type ");
						sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,PENSBI.XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,PENSBI.XXPENS_BI_MST_ITEM P  ");
						sql.append("\n ,PENSBI.PENSBME_STYLE_MAPPING MP ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND P.inventory_item_code = MP.pens_item");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n AND MP.MATERIAL_MASTER NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
						
						//Lotus Only 020047
						sql.append("\n AND C.customer_code IN(");
						sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
						sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
						sql.append("\n )");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
		                    sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%'");
						}
						
						sql.append("\n UNION");
						
						sql.append("\n SELECT distinct ");
						sql.append("\n J.store_code as customer_code,I.pens_item,  ");
						sql.append("\n (select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = J.store_code AND M.reference_code ='Store') as customer_desc, ");
						sql.append("\n I.group_code as group_type ");
						sql.append("\n FROM PENSBI.PENSBME_PICK_JOB J ,PENSBI.PENSBME_PICK_BARCODE B ,PENSBI.PENSBME_PICK_BARCODE_ITEM I ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND J.job_id = B.job_id  ");
						sql.append("\n AND B.job_id = I.job_id ");
						sql.append("\n AND B.box_no = I.box_no ");
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n AND I.group_code NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
						
						//Lotus Only 020047
						sql.append("\n AND J.cust_group = '020047'");
						sql.append("\n AND J.store_code IN(");
						sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
						sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
						sql.append("\n )");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
							sql.append("\n AND J.close_date >= to_date('"+onhandDateAsOfConfigStr+"','ddmmyyyy')  ");
	                        sql.append("\n AND J.close_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code LIKE '"+c.getGroup()+"%' ");
						}
						
	                    sql.append("\n UNION");
						
	                    sql.append("\n SELECT distinct ");
	    				sql.append("\n L.PENS_CUST_CODE as customer_code,L.PENS_ITEM ");
	    				sql.append("\n ,(select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE ");
	    				sql.append("\n   M.pens_value = L.PENS_CUST_CODE AND M.reference_code ='Store') as customer_desc ");
						sql.append("\n ,L.PENS_GROUP_TYPE as group_type ");
						sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_LOTUS L ");
						sql.append("\n WHERE 1=1 ");
						//Lotus ONLY
						sql.append("\n AND L.PENS_CUST_CODE IN(");
						sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
						sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
						sql.append("\n )");
						
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n AND L.PENS_GROUP_TYPE NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND L.sales_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.PENS_GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
						}
						
						sql.append("\n UNION");
						
						/** Adjust issue  **/
						sql.append("\n SELECT DISTINCT L.store_code as customer_code,L.item_issue as pens_item ");
						sql.append("\n ,(select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE ");
	    				sql.append("\n   M.pens_value = L.store_code AND M.reference_code ='Store') as customer_desc ");
						sql.append("\n ,L.item_issue_desc as group_type");
						sql.append("\n FROM PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
						//Lotus ONLY
						sql.append("\n AND L.STORE_CODE IN(");
						sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
						sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
						sql.append("\n )");
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n AND L.item_issue_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
						
						// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
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
						
	                   sql.append("\n UNION");
						
						/** Adjust receipt **/
						sql.append("\n SELECT DISTINCT L.store_code as customer_code,L.item_receipt as pens_item ");
						sql.append("\n ,(select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE ");
	    				sql.append("\n   M.pens_value = L.store_code AND M.reference_code ='Store') as customer_desc ");
						sql.append("\n ,L.item_receipt_desc as group_type");
						sql.append("\n FROM PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
						//Lotus ONLY
						sql.append("\n AND L.STORE_CODE IN(");
						sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
						sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
						sql.append("\n )");
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n AND L.item_receipt_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
						
						// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
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
						
						sql.append("\n UNION");
						
						/** Adjust item **/
						sql.append("\n SELECT DISTINCT ");
						sql.append("\n  L.STORE_CODE as customer_code,L.item_adjust as pens_item ");
						sql.append("\n ,(select M.pens_desc from PENSBI.PENSBME_MST_REFERENCE M WHERE ");
	    				sql.append("\n   M.pens_value = L.store_code AND M.reference_code ='Store') as customer_desc ");
						sql.append("\n ,L.item_adjust_desc as group_type ");
						sql.append("\n FROM PENSBI.PENSBME_ADJUST_SALES L  WHERE 1=1 ");
						//Lotus ONLY
						sql.append("\n AND L.STORE_CODE IN(");
						sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
						sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
						sql.append("\n )");
						
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n AND L.item_adjust_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
			                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.item_adjust_desc LIKE '"+c.getGroup()+"%' ");
						}
	            sql.append("\n )M ");
	    		sql.append("\n LEFT OUTER JOIN(	 ");
	    				sql.append("\n SELECT ");
	    				sql.append("\n L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
						sql.append("\n L.PENS_GROUP_TYPE as group_type, ");
						sql.append("\n NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
						sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_LOTUS L ");
						sql.append("\n WHERE 1=1 ");
						//Lotus ONLY
						sql.append("\n AND L.PENS_CUST_CODE IN(");
						sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
						sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
						sql.append("\n )");
						
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n AND L.PENS_GROUP_TYPE NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND L.sales_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.PENS_GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
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
						sql.append("\n MP.MATERIAL_MASTER as group_type, ");
						sql.append("\n NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,PENSBI.XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,PENSBI.XXPENS_BI_MST_ITEM P  ");
						sql.append("\n ,PENSBI.PENSBME_STYLE_MAPPING MP ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND P.inventory_item_code = MP.pens_item");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n AND MP.MATERIAL_MASTER NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
						
						//Lotus ONLY
						sql.append("\n AND C.CUSTOMER_CODE IN(");
						sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
						sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
						sql.append("\n )");
						
						if( !Utils.isNull(c.getSalesDate()).equals("")){
	                        sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND C.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%' ");
						}
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_code,P.inventory_item_code, MP.MATERIAL_MASTER ");
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
							sql.append("\n MP.MATERIAL_MASTER as group_type, ");
							sql.append("\n NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
							sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V V   ");
							sql.append("\n ,PENSBI.XXPENS_BI_MST_CUSTOMER C  ");
							sql.append("\n ,PENSBI.XXPENS_BI_MST_ITEM P  ");
							sql.append("\n ,PENSBI.PENSBME_STYLE_MAPPING MP ");
							sql.append("\n WHERE 1=1   ");
							sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
							sql.append("\n AND V.customer_id = C.customer_id  ");
							sql.append("\n AND P.inventory_item_code = MP.pens_item");
							sql.append("\n AND V.Customer_id IS NOT NULL   ");
							sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
							sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
							//NOT IN pensbme_group_unuse_lotus
							sql.append("\n AND MP.MATERIAL_MASTER NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
							
							//Lotus ONLY
							sql.append("\n AND C.CUSTOMER_CODE IN(");
							sql.append("\n   SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
							sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
							sql.append("\n )");
							
							if( !Utils.isNull(c.getSalesDate()).equals("")){
		                        sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
							}
							if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							    sql.append("\n AND C.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
							}
							if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
								sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
								sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
							}
							if( !Utils.isNull(c.getGroup()).equals("")){
								sql.append("\n AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%' ");
							}
							sql.append("\n GROUP BY C.customer_code ,P.inventory_item_code,MP.MATERIAL_MASTER");
				         }
						
				        if(asofDate.equals(onhandDateAsOfConfig) || asofDate.after(onhandDateAsOfConfig) ){ //asOfDate >= onhandDateConfig
				        	
				        	sql.append("\n SELECT  ");
							sql.append("\n C.customer_code,P.inventory_item_code as pens_item,  ");
							sql.append("\n MP.MATERIAL_MASTER as group_type, ");
							sql.append("\n NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
							sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V V   ");
							sql.append("\n ,PENSBI.XXPENS_BI_MST_CUSTOMER C  ");
							sql.append("\n ,PENSBI.XXPENS_BI_MST_ITEM P  ");
							sql.append("\n ,PENSBI.PENSBME_STYLE_MAPPING MP ");
							sql.append("\n WHERE 1=1   ");
							sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
							sql.append("\n AND V.customer_id = C.customer_id  ");
							sql.append("\n AND P.inventory_item_code = MP.pens_item");
							sql.append("\n AND V.Customer_id IS NOT NULL   ");
							sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
							sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
							//NOT IN pensbme_group_unuse_lotus
							sql.append("\n AND MP.MATERIAL_MASTER NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
							//Lotus ONLY
							sql.append("\n AND C.CUSTOMER_CODE IN(");
							sql.append("\n   SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
							sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
							sql.append("\n )");
							
							if( !Utils.isNull(c.getSalesDate()).equals("")){
		                        sql.append("\n AND V.invoice_date < to_date('"+onhandDateAsOfConfigStr+"','ddmmyyyy')  ");
							}
							if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							    sql.append("\n AND C.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
							}
							if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
								sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
								sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
							}
							if( !Utils.isNull(c.getGroup()).equals("")){
								sql.append("\n AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%' ");
							}
							sql.append("\n GROUP BY C.customer_code ,P.inventory_item_code, MP.MATERIAL_MASTER");
							
				        	sql.append("\n UNION ALL ");
				        	
				        	sql.append("\n SELECT  ");
							sql.append("\n J.store_code as customer_code,I.pens_item,  ");
							sql.append("\n I.group_code as group_type, ");
							sql.append("\n COUNT(*) as SALE_RETURN_QTY ");
							sql.append("\n FROM PENSBI.PENSBME_PICK_JOB J,PENSBI.PENSBME_PICK_BARCODE B ,PENSBI.PENSBME_PICK_BARCODE_ITEM I  ");
							sql.append("\n WHERE 1=1   ");
							sql.append("\n AND J.job_id = B.job_id  ");
							sql.append("\n AND B.job_id = I.job_id ");
							sql.append("\n AND B.box_no = I.box_no ");
							sql.append("\n AND I.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
							//NOT IN pensbme_group_unuse_lotus
							sql.append("\n AND I.group_code NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
							//Lotus ONLY
							sql.append("\n AND J.STORE_CODE IN(");
							sql.append("\n   SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
							sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
							sql.append("\n )");
							
							if( !Utils.isNull(c.getSalesDate()).equals("")){
								sql.append("\n AND J.close_date >= to_date('"+onhandDateAsOfConfigStr+"','ddmmyyyy')  ");
		                        sql.append("\n AND J.close_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
							}
							if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							    sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
							}
							if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
								sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
								sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
							}
							if( !Utils.isNull(c.getGroup()).equals("")){
								sql.append("\n AND I.group_code LIKE '"+c.getGroup()+"%' ");
							}
							sql.append("\n GROUP BY J.store_code ,I.pens_item ,I.group_code ");
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
				sql.append("\n FROM PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
				//Lotus ONLY
				sql.append("\n AND L.STORE_CODE IN(");
				sql.append("\n   SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
				sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
				sql.append("\n )");
				//NOT IN pensbme_group_unuse_lotus
				sql.append("\n AND L.item_issue_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
				// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
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
				sql.append("\n  GROUP BY L.STORE_CODE,L.item_issue, L.item_issue_desc ");
				sql.append("\n )STOCK_ISSUE ");
				sql.append("\n ON  M.customer_code = STOCK_ISSUE.customer_code and M.pens_item = STOCK_ISSUE.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_ISSUE.group_type ");
				
				//Stock Receipt
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n SELECT ");
				sql.append("\n L.STORE_CODE as customer_code,L.item_receipt as pens_item,L.item_receipt_desc as group_type, ");
				sql.append("\n NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
				sql.append("\n FROM PENSBI.PENSBME_ADJUST_INVENTORY L  WHERE 1=1 ");
				//Lotus ONLY
				sql.append("\n AND L.STORE_CODE IN(");
				sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
				sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
				sql.append("\n )");
				//NOT IN pensbme_group_unuse_lotus
				sql.append("\n AND L.item_receipt_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
				//L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
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
				sql.append("\n FROM PENSBI.PENSBME_ADJUST_SALES L WHERE 1=1 ");	
				//Lotus ONLY
				sql.append("\n AND L.STORE_CODE IN(");
				sql.append("\n    SELECT pens_value FROM PENSBI.PENSBME_MST_REFERENCE where reference_code = 'Store'");
				sql.append("\n    and pens_value like '020047%' and pens_desc4 ='N' ");
				sql.append("\n )");
				//NOT IN pensbme_group_unuse_lotus
				sql.append("\n AND L.item_adjust_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
				if( !Utils.isNull(c.getSalesDate()).equals("")){
	                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.item_adjust_desc LIKE '"+c.getGroup()+"%' ");
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
				   FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
				}
				//logger.debug("sql:"+sql); 
				
			} catch (Exception e) {
				throw e;
			} finally {
			
			}
			return sql;
		}

}
