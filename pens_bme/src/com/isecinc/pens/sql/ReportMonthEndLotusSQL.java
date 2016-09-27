package com.isecinc.pens.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BMECControlDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;

public class ReportMonthEndLotusSQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static StringBuilder genSQL(Connection conn,OnhandSummary c,User user,String summaryType) throws Exception{
			StringBuilder sql = new StringBuilder();
			try {
				BMEControlBean control = BMECControlDAO.calcMonthEndOnhandDateLotusAsOf(conn,c.getPensCustCodeFrom(),c.getSalesDate());
				
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
				    sql.append("\n SELECT A.* FROM(");
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
						sql.append("\n\t SELECT DISTINCT ");
						sql.append("\n\t C.customer_code,P.inventory_item_code as pens_item,   ");
						sql.append("\n\t (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = C.customer_code AND M.reference_code ='Store') as customer_desc, ");
						sql.append("\n\t substr(P.inventory_item_desc,0,6) as group_type ");
						sql.append("\n\t FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n\t ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n\t ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n\t WHERE 1=1   ");
						sql.append("\n\t AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n\t AND V.customer_id = C.customer_id  ");
						sql.append("\n\t AND V.Customer_id IS NOT NULL   ");
						sql.append("\n\t AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n\t AND P.inventory_item_desc LIKE 'ME%' ");
						
						//Lotus Only 020047
						sql.append("\n\t AND C.customer_code LIKE '020047%'");
						
						//sql.append(genWhereCondDateMonthEnd(control,"V.invoice_date"));
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n\t AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n\t AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n\t AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n\t AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n\t UNION ");
						
						sql.append("\n\t SELECT distinct ");
						sql.append("\n\t J.store_code as customer_code,I.pens_item,  ");
						sql.append("\n\t (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = J.store_code AND M.reference_code ='Store') as customer_desc, ");
						sql.append("\n\t I.group_code as group_type ");
						
						sql.append("\n\t FROM PENSBME_PICK_JOB J ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
						sql.append("\n\t WHERE 1=1   ");
						sql.append("\n\t AND J.job_id = B.job_id  ");
						sql.append("\n\t AND B.job_id = I.job_id ");
						sql.append("\n\t AND B.box_no = I.box_no ");
						//Lotus Only 020047
						sql.append("\n\t AND J.cust_group = '020047'");
						//sql.append(genWhereCondDateMonthEnd(control,"J.close_date"));
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n\t AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n\t AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n\t AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n\t AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						
                 sql.append("\n\t UNION ");
						
                 sql.append("\n\t SELECT distinct ");
 				   sql.append("\n\t  L.PENS_CUST_CODE as customer_code,L.PENS_ITEM ");
 				   sql.append("\n\t ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
 				   sql.append("\n\t   M.pens_value = L.PENS_CUST_CODE AND M.reference_code ='Store') as customer_desc ");
					   sql.append("\n\t ,L.PENS_GROUP_TYPE as group_type ");
					   sql.append("\n\t  FROM PENSBME_SALES_FROM_LOTUS L ");
					   sql.append("\n\t WHERE 1=1 ");
					  // sql.append(genWhereCondDateMonthEnd(control,"L.sales_date"));
						
					   if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						 sql.append("\n\t AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					   }
					   if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						 sql.append("\n\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						 sql.append("\n\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					   }
					   if( !Utils.isNull(c.getGroup()).equals("")){
						 sql.append("\n\t AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					   }
					   
					   sql.append("\n\t UNION ");
						
                 sql.append("\n\t SELECT distinct ");
 				   sql.append("\n\t  L.STORE_CODE as customer_code,L.PENS_ITEM ");
 				   sql.append("\n\t ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
 				   sql.append("\n\t   M.pens_value = L.STORE_CODE AND M.reference_code ='Store') as customer_desc ");
					   sql.append("\n\t ,L.GROUP_CODE as group_type ");
					   sql.append("\n\t  FROM PENSBME_ENDING_STOCK L ");
						
					   sql.append("\n\t WHERE 1=1 ");
					   if( !Utils.isNull(control.getYearMonth()).equals("")){
	                      // sql.append("\n\t AND L.YEAR_MONTH ='"+control.getYearMonth()+"'");
						}else{
							//not found data no show
						}
						
					   if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						 sql.append("\n\t AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					   }
					   if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						 sql.append("\n\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						 sql.append("\n\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					   }
					   if( !Utils.isNull(c.getGroup()).equals("")){
						 sql.append("\n\t AND L.GROUP_CODE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					   }
						
         sql.append("\n )M ");
         sql.append("\n LEFT OUTER JOIN(	 ");
         sql.append("\n /******************* BEGINING ****************************************/ ");
				    sql.append("\n\t SELECT ");
				    sql.append("\n\t L.STORE_CODE as customer_code,L.PENS_ITEM, ");
					sql.append("\n\t L.GROUP_CODE as group_type, ");
					sql.append("\n\t NVL(SUM(Ending_qty),0) AS BEGINING_QTY ");
					sql.append("\n\t FROM PENSBME_ENDING_STOCK L ");
					sql.append("\n\t WHERE 1=1 ");
						 
					if( !Utils.isNull(control.getYearMonth()).equals("")){
                 sql.append("\n\t AND L.YEAR_MONTH ='"+control.getYearMonth()+"'");
					}else{
						//not found data no show
					}
					 
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n\t AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n\t AND L.GROUP_CODE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n\t  GROUP BY L.STORE_CODE,L.PENS_ITEM, L.GROUP_CODE ");
					sql.append("\n ) ENDING ");
					sql.append("\n ON  M.customer_code = ENDING.customer_code and M.pens_item = ENDING.pens_item ");	 
					sql.append("\n AND M.group_type = ENDING.group_type ");
					
 		   sql.append("\n LEFT OUTER JOIN(	 ");
 		 sql.append("\n/******************* SALE OUT ****************************************/");
 				sql.append("\n\t SELECT ");
 				sql.append("\n\t L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
						sql.append("\n\t L.PENS_GROUP_TYPE as group_type, ");
						sql.append("\n\t NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
						sql.append("\n\t FROM ");
						sql.append("\n\t PENSBI.PENSBME_SALES_FROM_LOTUS L ");
						sql.append("\n\t WHERE 1=1 ");
						sql.append(genWhereCondDateMonthEnd(control,"L.sales_date"));
						  
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n\t AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n\t AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n\t AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n\t AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n\t  GROUP BY L.PENS_CUST_CODE,L.PENS_ITEM,L.PENS_GROUP_TYPE ");
						sql.append("\n ) SALE_OUT ");
						sql.append("\n ON  M.customer_code = SALE_OUT.customer_code and M.pens_item = SALE_OUT.pens_item ");	 
						sql.append("\n AND M.group_type = SALE_OUT.group_type ");
						
				sql.append("\n LEFT OUTER JOIN( ");
				sql.append("\n/******************* SALE IN ****************************************/");
						sql.append("\n\t SELECT  ");
						sql.append("\n\t C.customer_code ,P.inventory_item_code as pens_item,  ");
						sql.append("\n\t substr(P.inventory_item_desc,0,6) as group_type, ");
						sql.append("\n\t NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n\t FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n\t ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n\t ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n\t WHERE 1=1   ");
						sql.append("\n\t AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n\t AND V.customer_id = C.customer_id  ");
						sql.append("\n\t AND V.Customer_id IS NOT NULL   ");
						sql.append("\n\t AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n\t AND P.inventory_item_desc LIKE 'ME%' ");
						
						//Lotus Only 020047
						sql.append("\n\t AND C.customer_code LIKE '020047%'");
						sql.append(genWhereCondDateMonthEnd(control,"V.invoice_date"));
						
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n\t AND C.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n\t AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n\t AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n\t AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
							
						sql.append("\n\t GROUP BY C.customer_code,P.inventory_item_code, substr(P.inventory_item_desc,0,6) ");
				sql.append("\n )SALE_IN ");
				sql.append("\n ON  M.customer_code = SALE_IN.customer_code and M.pens_item = SALE_IN.pens_item ");
				sql.append("\n AND M.group_type  = SALE_IN.group_type ");
				sql.append("\n LEFT OUTER JOIN ( ");
				
				sql.append("\n/******************* SALE RETURN ****************************************/");
					sql.append("\n\t SELECT J.store_code as customer_code,I.pens_item,  ");
					sql.append("\n\t I.group_code as group_type, ");
					sql.append("\n\t COUNT(*) as SALE_RETURN_QTY ");
					sql.append("\n\t FROM PENSBME_PICK_JOB J,PENSBME_PICK_BARCODE B,PENSBME_PICK_BARCODE_ITEM I ");
					sql.append("\n\t WHERE 1=1   ");
					sql.append("\n\t AND J.job_id = B.job_id  ");
					sql.append("\n\t AND B.job_id = I.job_id ");
					sql.append("\n\t AND B.box_no = I.box_no ");
					sql.append("\n\t AND I.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
					
					//Lotus Only 020047
					sql.append("\n\t AND J.cust_group = '020047'");
					sql.append(genWhereCondDateMonthEnd(control,"J.close_date"));
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n\t AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n\t AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n\t AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n\t AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n\t GROUP BY J.store_code ,I.pens_item ,I.group_code ");
				sql.append("\n )SALE_RETURN ");
				sql.append("\n  ON  M.customer_code = SALE_RETURN.customer_code and M.pens_item = SALE_RETURN.pens_item ");
				sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
				
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n /******************* STOCK_ISSUE ****************************************/");
				 	sql.append("\n\t SELECT ");
				 	sql.append("\n\t L.store_code as customer_code,L.item_issue as pens_item,L.item_issue_desc as group_type, ");
				 	sql.append("\n\t (NVL(SUM(ITEM_ISSUE_QTY),0)*-1) AS ISSUE_QTY ");
				 	sql.append("\n\t FROM PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
				 	// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
				 	sql.append(genWhereCondDateMonthEnd(control,"L.transaction_date"));
				 	if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				 		sql.append("\n\t AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				 	}
				 	if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				 		sql.append("\n\t AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				 		sql.append("\n\t AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
				 	}
				 	if( !Utils.isNull(c.getGroup()).equals("")){
				 		sql.append("\n\t AND L.item_issue_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				 	}
				 	sql.append("\n\t  GROUP BY L.STORE_CODE,L.item_issue, L.item_issue_desc ");
				sql.append("\n )STOCK_ISSUE ");
				sql.append("\n ON  M.customer_code = STOCK_ISSUE.customer_code and M.pens_item = STOCK_ISSUE.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_ISSUE.group_type ");
				
				//Stock Receipt
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n /******************* STOCK RECEIPT ****************************************/");
				  sql.append("\n\t SELECT ");
				  sql.append("\n\t L.STORE_CODE as customer_code,L.item_receipt as pens_item,L.item_receipt_desc as group_type, ");
				  sql.append("\n\t NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
				  sql.append("\n\t FROM PENSBME_ADJUST_INVENTORY L  WHERE 1=1 ");
				  //L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
				  sql.append(genWhereCondDateMonthEnd(control,"L.transaction_date"));
				  
				  if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n\t AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				  }
				  if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
				  }
				  if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND L.item_receipt_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				 }
				  sql.append("\n\t  GROUP BY  L.STORE_CODE,L.item_receipt,  L.item_receipt_desc ");
				sql.append("\n )STOCK_RECEIPT ");
				sql.append("\n ON  M.customer_code = STOCK_RECEIPT.customer_code and M.pens_item = STOCK_RECEIPT.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_RECEIPT.group_type ");
				
				//STOCK_SHORT
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n /******************* STOCK_SHORT ****************************************/");
				  sql.append("\n\t SELECT ");
				  sql.append("\n\t L.STORE_CODE as customer_code,L.item_adjust as pens_item,L.item_adjust_desc as group_type, ");
				  sql.append("\n\t NVL(SUM(ITEM_ADJUST_QTY),0) AS STOCK_SHORT_QTY ");
				  sql.append("\n\t FROM PENSBME_ADJUST_SALES L  WHERE 1=1 ");	 
				  sql.append(genWhereCondDateMonthEnd(control,"L.transaction_date"));
				  
				  if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					  sql.append("\n\t AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				  }
				  if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n\t AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n\t AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
				  }
				  if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n\t AND L.item_adjust_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				  }
				  sql.append("\n\t  GROUP BY L.STORE_CODE,L.item_adjust, L.item_adjust_desc ");
				sql.append("\n )STOCK_SHORT ");
				sql.append("\n ON  M.customer_code = STOCK_SHORT.customer_code and M.pens_item = STOCK_SHORT.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_SHORT.group_type ");
				
				sql.append("\n ) A ");
				if("GroupCode".equalsIgnoreCase(summaryType)){
					sql.append("\n GROUP BY A.customer_code,A.customer_desc ,A.group_type");
				}
				sql.append("\n ORDER BY A.customer_code,A.group_type asc ");
				
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
	 

	  public static String genWhereCondDateMonthEnd(BMEControlBean bean ,String symnoname){
		  String whereSQL = "";
		  if("1".equals(bean.getCaseType())){
			  whereSQL  ="\n\t AND "+ symnoname +">= to_date('"+bean.getStartDate()+"','dd/mm/yyyy')";
			  whereSQL +="\n\t AND "+ symnoname +"<= to_date('"+bean.getEndDate()+"','dd/mm/yyyy')";
		  }else   if("2".equals(bean.getCaseType())){
			  whereSQL  ="\n\t AND "+ symnoname +">= to_date('"+bean.getStartDate()+"','dd/mm/yyyy')";
			  whereSQL +="\n\t AND "+ symnoname +"<= to_date('"+bean.getEndDate()+"','dd/mm/yyyy')"; 
		  }else   if("3".equals(bean.getCaseType())){
			  whereSQL ="\n\t AND "+ symnoname +"<= to_date('"+bean.getStartDate()+"','dd/mm/yyyy')";
		  }else   if("4".equals(bean.getCaseType())){
			  whereSQL ="\n\t AND "+ symnoname +"<= to_date('"+bean.getStartDate()+"','dd/mm/yyyy')";
		  }
		  return whereSQL;
	  }
}
