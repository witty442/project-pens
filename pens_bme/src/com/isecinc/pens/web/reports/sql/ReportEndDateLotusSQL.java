package com.isecinc.pens.web.reports.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class ReportEndDateLotusSQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static StringBuilder genSQL(Connection conn,ReportAllBean c,User user,String summaryType,String typeGen) throws Exception{
			StringBuilder sql = new StringBuilder();
			//Date asofDate = null;
			BMEControlBean control = null;
			try {
				if("GenReportEndDate".equalsIgnoreCase(typeGen)){
				    control = calcDueDate(conn,c.getPensCustCodeFrom(),c.getSalesDate());
				}else{
					control = calcDueDate(conn,c.getPensCustCodeFrom(),c.getSalesDate());
				}
			sql.append("\n SELECT A.* " );
			sql.append("\n,(SELECT NVL(MAX(RETAIL_PRICE_BF),0) FROM PENSBME_ONHAND_BME_LOCKED T "
					 + "\n WHERE A.group_type = T.group_item"
					 + "\n OR A.group_type = T.material_master) as retail_price_bf \n");
			sql.append("\n,(A.ONHAND_QTY *(SELECT NVL(MAX(RETAIL_PRICE_BF),0) "
				 	 + "\n FROM PENSBME_ONHAND_BME_LOCKED T "
					 + "\n WHERE A.group_type = T.group_item"
					 + "\n OR A.group_type = T.material_master)) as onhand_amt \n");
			sql.append("\n FROM( ");
				if("GroupCode".equalsIgnoreCase(summaryType)){
					sql.append("\n SELECT A.customer_code,A.customer_desc ,A.group_type ");
					sql.append("\n ,SUM(A.BEGINING_QTY) as BEGINING_QTY");
					sql.append("\n ,SUM(A.SALE_IN_QTY) AS SALE_IN_QTY");
					sql.append("\n ,SUM(A.SALE_OUT_QTY) AS SALE_OUT_QTY");
					sql.append("\n ,SUM(A.SALE_RETURN_QTY) AS SALE_RETURN_QTY");
					sql.append("\n ,SUM(A.ADJUST_QTY) AS ADJUST_QTY");
					sql.append("\n ,SUM(A.STOCK_SHORT_QTY) AS STOCK_SHORT_QTY");
					sql.append("\n ,SUM(A.ONHAND_QTY) AS ONHAND_QTY");
					sql.append("\n FROM(");
				}else{
				    sql.append("\n SELECT A.* ");
					sql.append("\n FROM(");
				}
				sql.append("\n SELECT M.*");
				sql.append("\n , NVL(ENDING.BEGINING_QTY,0) as BEGINING_QTY ");
				sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
				sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
				sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
				sql.append("\n , NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) as ADJUST_QTY ");
				sql.append("\n , NVL(STOCK_SHORT.STOCK_SHORT_QTY,0) as STOCK_SHORT_QTY ");
				
				sql.append("\n , NVL(ENDING.BEGINING_QTY,0)+ (NVL(SALE_IN.SALE_IN_QTY,0) " + //beginQty + SaleIn
								"\n -(" +
								"\n    NVL(SALE_OUT.SALE_OUT_QTY,0)  " +//SaleOut
								"\n  + NVL(SALE_RETURN.SALE_RETURN_QTY,0) " +//Return
								"\n  )" +
								"\n  + ( NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) )" + //Adjust
								"\n  + NVL(STOCK_SHORT.STOCK_SHORT_QTY,0) " +//Short
						   "\n  ) ONHAND_QTY");
				
				sql.append("\n FROM(  ");
						sql.append("\n\t\t SELECT DISTINCT ");
						sql.append("\n\t\t C.customer_code,P.inventory_item_code as pens_item,   ");
						sql.append("\n\t\t (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " );
						sql.append("\n\t\t M.pens_value = C.customer_code AND M.reference_code ='Store') as customer_desc, ");
						sql.append("\n\t\t MP.MATERIAL_MASTER as group_type ");
						sql.append("\n\t\t FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n\t\t ,XXPENS_BI_MST_CUSTOMER C ");
						sql.append("\n\t\t ,XXPENS_BI_MST_ITEM P ");
						sql.append("\n\t\t ,PENSBME_STYLE_MAPPING MP ");
						sql.append("\n\t\t WHERE 1=1 ");
						sql.append("\n\t\t AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n\t\t AND V.customer_id = C.customer_id  ");
						sql.append("\n\t\t AND P.inventory_item_code = MP.pens_item");
						sql.append("\n\t\t AND V.Customer_id IS NOT NULL   ");
						sql.append("\n\t\t AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n\t\t AND P.inventory_item_desc LIKE 'ME%' ");
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n\t\t AND MP.MATERIAL_MASTER NOT IN(select group_code from pensbme_group_unuse_lotus)");
						//Lotus Only 020047
						sql.append("\n\t\t AND C.customer_code LIKE '020047-%'");
						
						//sql.append(genWhereCondDateMonthEnd(control,"V.invoice_date"));
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n\t\t AND C.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n\t\t AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n\t\t AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n\t\t AND MP.MATERIAL_MASTER like '"+c.getGroup()+"%'");
						}
						sql.append("\n\t\t UNION ");
						
						sql.append("\n\t\t SELECT distinct ");
						sql.append("\n\t\t J.store_code as customer_code,I.pens_item,  ");
						sql.append("\n\t\t (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
						sql.append("\n\t\t  M.pens_value = J.store_code AND M.reference_code ='Store') as customer_desc,");
						sql.append("\n\t\t I.group_code as group_type ");
						sql.append("\n\t\t FROM PENSBME_PICK_JOB J ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I ");
						sql.append("\n\t\t WHERE 1=1   ");
						sql.append("\n\t\t AND J.job_id = B.job_id  ");
						sql.append("\n\t\t AND B.job_id = I.job_id ");
						sql.append("\n\t\t AND B.box_no = I.box_no ");
						//NOT IN pensbme_group_unuse_lotus
						sql.append("\n\t\t AND I.group_code NOT IN(select group_code from pensbme_group_unuse_lotus)");
						//Lotus Only 020047
						sql.append("\n\t\t AND J.cust_group = '020047'");
						//sql.append(genWhereCondDateMonthEnd(control,"J.close_date"));
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n\t\t AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n\t\t AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n\t\t AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n\t\t AND I.group_code like '"+c.getGroup()+"%'");
						}
                 sql.append("\n\t\t UNION ");
						
                 sql.append("\n\t\t SELECT distinct ");
 				   sql.append("\n\t\t  L.PENS_CUST_CODE as customer_code,L.PENS_ITEM ");
 				   sql.append("\n\t\t ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
 				   sql.append("\n\t\t   M.pens_value = L.PENS_CUST_CODE AND M.reference_code ='Store') as customer_desc ");
				   sql.append("\n\t\t ,L.pens_group_type as group_type ");
				   sql.append("\n\t\t  FROM PENSBME_SALES_FROM_LOTUS L ");
				   sql.append("\n\t\t  WHERE 1=1 ");
				 //NOT IN pensbme_group_unuse_lotus
					sql.append("\n\t\t AND L.pens_group_type NOT IN(select group_code from pensbme_group_unuse_lotus)");
				  // sql.append(genWhereCondDateMonthEnd(control,"L.sales_date"));
						
				   if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					 sql.append("\n\t\t AND L.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				   }
				   if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					 sql.append("\n\t\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					 sql.append("\n\t\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				   }
				   if( !Utils.isNull(c.getGroup()).equals("")){
					 sql.append("\n\t\t AND L.pens_group_type like '"+c.getGroup()+"%'");
				   }
				   sql.append("\n\t\t UNION ");
						
                 sql.append("\n\t\t SELECT distinct ");
 				   sql.append("\n\t\t  L.STORE_CODE as customer_code,L.PENS_ITEM ");
 				   sql.append("\n\t\t ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
 				   sql.append("\n\t\t   M.pens_value = L.STORE_CODE AND M.reference_code ='Store') as customer_desc ");
				   sql.append("\n\t\t , L.group_code as group_type ");
				   sql.append("\n\t\t FROM PENSBME_ENDDATE_STOCK L");
				   sql.append("\n\t\t WHERE 1=1 ");
				  //NOT IN pensbme_group_unuse_lotus
				   sql.append("\n\t\t AND L.group_code NOT IN(select group_code from pensbme_group_unuse_lotus)");
				   if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					 sql.append("\n\t\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				   }
				   if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					 sql.append("\n\t\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					 sql.append("\n\t\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				   }
				   if( !Utils.isNull(c.getGroup()).equals("")){
					 sql.append("\n\t\t AND L.group_code like '"+c.getGroup()+"%'");
				   }
						
         sql.append("\n )M ");
         sql.append("\n LEFT OUTER JOIN(	 ");
         sql.append("\n /******************* BEGINING ****************************************/ ");
				    sql.append("\n\t SELECT L.STORE_CODE as customer_code,L.PENS_ITEM, ");
					sql.append("\n\t L.group_code as group_type, NVL(SUM(Ending_qty),0) AS BEGINING_QTY ");
					sql.append("\n\t FROM PENSBME_ENDDATE_STOCK L");
					sql.append("\n\t WHERE 1=1 ");
					//NOT IN pensbme_group_unuse_lotus
					sql.append("\n\t AND L.group_code NOT IN(select group_code from pensbme_group_unuse_lotus)");
					sql.append("\n\t AND L.ENDING_DATE  = to_date('"+control.getStartDate()+"','dd/mm/yyyy')  ");
					
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n\t AND L.group_code like '"+c.getGroup()+"%'");
					}
					sql.append("\n\t  GROUP BY L.STORE_CODE,L.PENS_ITEM, L.group_code ");
				sql.append("\n ) ENDING ");
				sql.append("\n ON  M.customer_code = ENDING.customer_code ");
				sql.append("\n AND M.pens_item = ENDING.pens_item ");	 
				sql.append("\n AND M.group_type = ENDING.group_type ");
					
 		   sql.append("\n LEFT OUTER JOIN(	 ");
 		 sql.append("\n/******************* SALE OUT ****************************************/");
 				sql.append("\n\t SELECT L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
				sql.append("\n\t L.pens_group_type as group_type, ");
				sql.append("\n\t NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
				sql.append("\n\t FROM PENSBME_SALES_FROM_LOTUS L ");
				sql.append("\n\t WHERE 1=1 ");
				//NOT IN pensbme_group_unuse_lotus
				sql.append("\n\t AND L.pens_group_type NOT IN(select group_code from pensbme_group_unuse_lotus)");
				sql.append(genWhereCondDate(control,"L.sales_date"));
				  
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n\t AND L.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND L.pens_group_type like '"+c.getGroup()+"%'");
				}
				sql.append("\n\t  GROUP BY L.PENS_CUST_CODE,L.PENS_ITEM,L.pens_group_type ");
				sql.append("\n ) SALE_OUT ");
				sql.append("\n ON  M.customer_code = SALE_OUT.customer_code ");
				sql.append("\n AND M.pens_item = SALE_OUT.pens_item ");	 
				sql.append("\n AND M.group_type = SALE_OUT.group_type ");
						
				sql.append("\n LEFT OUTER JOIN( ");
				sql.append("\n/******************* SALE IN ****************************************/");
			        sql.append("\n\t\t SELECT  ");
					sql.append("\n\t\t  C.customer_code ,P.inventory_item_code as pens_item");
					sql.append("\n\t\t ,MP.MATERIAL_MASTER as group_type  ");
					sql.append("\n\t\t ,NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
					sql.append("\n\t\t FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
					sql.append("\n\t\t ,XXPENS_BI_MST_CUSTOMER C  ");
					sql.append("\n\t\t ,XXPENS_BI_MST_ITEM P  ");
					sql.append("\n\t\t ,PENSBME_STYLE_MAPPING MP ");
					sql.append("\n\t\t WHERE 1=1   ");
					sql.append("\n\t\t AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n\t\t AND V.customer_id = C.customer_id  ");
					sql.append("\n\t\t AND P.inventory_item_code = MP.pens_item");
					sql.append("\n\t\t AND V.Customer_id IS NOT NULL   ");
					sql.append("\n\t\t AND V.inventory_item_id IS NOT NULL  ");
					sql.append("\n\t\t AND P.inventory_item_desc LIKE 'ME%' ");
					//NOT IN pensbme_group_unuse_lotus
					sql.append("\n\t\t AND MP.MATERIAL_MASTER NOT IN(select group_code from pensbme_group_unuse_lotus)");
					//Lotus Only 020047
					sql.append("\n\t\t AND C.customer_code LIKE '020047-%'");
					sql.append(genWhereCondDate(control,"V.invoice_date"));
					
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n\t\t AND C.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n\t\t AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n\t\t AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n\t AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%'");
					}
					sql.append("\n\t GROUP BY C.customer_code,P.inventory_item_code,MP.MATERIAL_MASTER");
				sql.append("\n )SALE_IN ");
				sql.append("\n ON  M.customer_code = SALE_IN.customer_code ");
				sql.append("\n AND M.pens_item = SALE_IN.pens_item ");
				sql.append("\n AND M.group_type  = SALE_IN.group_type ");
				sql.append("\n LEFT OUTER JOIN ( ");
				
				sql.append("\n /******************* SALE RETURN ****************************************/");
					sql.append("\n\t SELECT J.store_code as customer_code,I.pens_item,  ");
					sql.append("\n\t I.group_code as group_type, ");
					sql.append("\n\t COUNT(*) as SALE_RETURN_QTY ");
					sql.append("\n\t FROM PENSBME_PICK_JOB J,PENSBME_PICK_BARCODE B ");
					sql.append("\n\t ,PENSBME_PICK_BARCODE_ITEM I ");
					sql.append("\n\t WHERE 1=1   ");
					sql.append("\n\t AND J.job_id = B.job_id  ");
					sql.append("\n\t AND B.job_id = I.job_id ");
					sql.append("\n\t AND B.box_no = I.box_no ");
					sql.append("\n\t AND I.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
					//NOT IN pensbme_group_unuse_lotus
					sql.append("\n\t AND I.group_code NOT IN(select group_code from pensbme_group_unuse_lotus)");
					//Lotus Only 020047
					sql.append("\n\t AND J.cust_group = '020047'");
					sql.append(genWhereCondDate(control,"J.close_date"));
					
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
				sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
				
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n /******************* STOCK_ISSUE ****************************************/");
				 	sql.append("\n\t SELECT ");
				 	sql.append("\n\t L.store_code as customer_code,L.item_issue as pens_item,L.item_issue_desc as group_type, ");
				 	sql.append("\n\t (NVL(SUM(ITEM_ISSUE_QTY),0)*-1) AS ISSUE_QTY ");
				 	sql.append("\n\t FROM PENSBME_ADJUST_INVENTORY L ");
				 	sql.append("\n\t WHERE 1=1 " );
				 	//NOT IN pensbme_group_unuse_lotus
					sql.append("\n\t AND L.item_issue_desc NOT IN(select group_code from pensbme_group_unuse_lotus)");
					sql.append("\n\t AND L.STORE_CODE LIKE '020047-%'");
				 	// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
				 	sql.append(genWhereCondDate(control,"L.transaction_date"));
				 	if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				 		sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				 	}
				 	if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				 		sql.append("\n\t AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				 		sql.append("\n\t AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
				 	}
				 	if( !Utils.isNull(c.getGroup()).equals("")){
				 		sql.append("\n\t AND L.item_issue_desc like '"+c.getGroup()+"%'");
				 	}
				 	sql.append("\n\t  GROUP BY L.STORE_CODE,L.item_issue, L.item_issue_desc ");
				sql.append("\n )STOCK_ISSUE ");
				sql.append("\n ON  M.customer_code = STOCK_ISSUE.customer_code ");
				sql.append("\n AND M.pens_item = STOCK_ISSUE.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_ISSUE.group_type ");
				
				//Stock Receipt
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n /******************* STOCK RECEIPT ****************************************/");
				  sql.append("\n\t SELECT ");
				  sql.append("\n\t L.STORE_CODE as customer_code,L.item_receipt as pens_item,L.item_receipt_desc as group_type, ");
				  sql.append("\n\t NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
				  sql.append("\n\t FROM PENSBME_ADJUST_INVENTORY L");
				  sql.append("\n\t WHERE 1=1 ");
				 //NOT IN pensbme_group_unuse_lotus
				  sql.append("\n\t AND L.item_receipt_desc NOT IN(select group_code from pensbme_group_unuse_lotus)");
				  sql.append("\n\t AND L.STORE_CODE LIKE '020047-%'");
				  //L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
				  sql.append(genWhereCondDate(control,"L.transaction_date"));
				  
				  if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				  }
				  if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
				  }
				  if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND L.item_receipt_desc  like '"+c.getGroup()+"%'");
				 }
				  sql.append("\n\t  GROUP BY  L.STORE_CODE,L.item_receipt,L.item_receipt_desc");
				sql.append("\n )STOCK_RECEIPT ");
				sql.append("\n ON  M.customer_code = STOCK_RECEIPT.customer_code and M.pens_item = STOCK_RECEIPT.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_RECEIPT.group_type ");
				
				//STOCK_SHORT
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n /******************* STOCK_SHORT ****************************************/");
				  sql.append("\n\t SELECT ");
				  sql.append("\n\t L.STORE_CODE as customer_code,L.item_adjust as pens_item,L.item_adjust_desc as group_type, ");
				  sql.append("\n\t NVL(SUM(ITEM_ADJUST_QTY),0) AS STOCK_SHORT_QTY ");
				  sql.append("\n\t FROM PENSBME_ADJUST_SALES L ");
				  sql.append("\n\t WHERE 1=1 ");	 
				 //NOT IN pensbme_group_unuse_lotus
				  sql.append("\n\t AND L.item_adjust_desc NOT IN(select group_code from pensbme_group_unuse_lotus)");
				  sql.append("\n\t AND L.STORE_CODE LIKE '020047-%'");
				  sql.append(genWhereCondDate(control,"L.transaction_date"));
				  
				  if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					  sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				  }
				  if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
				  }
				  if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND L.item_adjust_desc like '"+c.getGroup()+"%'");
				  }
				  sql.append("\n\t  GROUP BY L.STORE_CODE,L.item_adjust,L.item_adjust_desc ");
				sql.append("\n )STOCK_SHORT ");
				sql.append("\n ON  M.customer_code = STOCK_SHORT.customer_code and M.pens_item = STOCK_SHORT.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_SHORT.group_type ");
				
				sql.append("\n ) A ");
				
				if("GroupCode".equalsIgnoreCase(summaryType)){
					sql.append("\n GROUP BY A.customer_code,A.customer_desc ,A.group_type");
				}
				sql.append("\n ORDER BY A.customer_code,A.group_type asc ");
				sql.append("\n ) A ");
				if( !"".equalsIgnoreCase(c.getDispHaveQty())){
					sql.append("\n WHERE (A.BEGINING_QTY <> 0 ");
					sql.append("\n OR A.SALE_IN_QTY <> 0 ");
					sql.append("\n OR A.SALE_OUT_QTY <> 0 ");
					sql.append("\n OR A.SALE_RETURN_QTY <> 0 ");
					sql.append("\n OR A.ADJUST_QTY <> 0 ");
					sql.append("\n OR A.STOCK_SHORT_QTY <> 0 ");
					sql.append("\n OR A.ONHAND_QTY <> 0 )");
				}
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
	 
	 public static BMEControlBean calcDueDate(Connection conn,String storeCode,String asOfdate) throws Exception {
			BMEControlBean bean = new BMEControlBean();
            String maxEndDate = "";
			try {
				//Budish to ChristDate
				Date asofDateTemp = DateUtil.parse(asOfdate, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String christAsOfDateStr = DateUtil.stringValue(asofDateTemp, DateUtil.DD_MM_YYYY_WITH_SLASH);
				
				//Get Max End Date By StoreCode and asOfdate
				maxEndDate =  getMaxEndDateByStoreCode(conn,storeCode);
				logger.debug("calcDueDate Lotus ReportEndDate");
				logger.debug("asOfDate:"+asOfdate);
				logger.debug("maxEndDate:"+maxEndDate);
				
				if ( !"".equals(Utils.isNull(maxEndDate))){
					bean.setStartDate(maxEndDate);
					bean.setEndDate(christAsOfDateStr);
				}else{
					bean.setYearMonth("");
					bean.setStartDate(christAsOfDateStr);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
				
				} catch (Exception e) {}
			}
			return bean;
		}
	 
	 
	 public static String getMaxEndDateByStoreCode(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String yearMonth = "";
			try {
				sql.append("\n select distinct max(ending_date) as ending_date "
						+ "FROM PENSBME_ENDDATE_STOCK "
						+ "WHERE 1=1 and store_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()){
					yearMonth = DateUtil.stringValue(rst.getDate("ending_date"),DateUtil.DD_MM_YYYY_WITH_SLASH);
				}
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return yearMonth;
		}
	 
	  public static String genWhereCondDate(BMEControlBean bean ,String symnoname){
		  String whereSQL = "";
	      if( !"".equals(Utils.isNull(bean.getStartDate())) && !"".equals(Utils.isNull(bean.getEndDate()))){
	    	  whereSQL  ="\n\t AND "+ symnoname +" > to_date('"+bean.getStartDate()+"','dd/mm/yyyy')";
	    	  whereSQL +="\n\t AND "+ symnoname +"<= to_date('"+bean.getEndDate()+"','dd/mm/yyyy')";
	      }else{
		      whereSQL ="\n\t AND "+ symnoname +"<= to_date('"+bean.getStartDate()+"','dd/mm/yyyy')";
	      }
		  return whereSQL;
	  }
}
