package com.isecinc.pens.summary.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class GenerateMonthEndLotus {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static String[] generateMonthLotus(OnhandSummary c,User user) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement psIns = null;
		PreparedStatement psDel = null;
	    ResultSet rst = null;
	    StringBuilder sql = new StringBuilder();
	    String[] results = new String[3];
	    String yearMonth = "";
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//validate 
			results = ControlConstantsDB.canGenMonthEndLotus(conn,c.getPensCustCodeFrom(),c.getSalesDate());
			
			if(results[0].equals("false")){
				return results;
			}
			yearMonth = results[1];
			
			//Validate last day of month
			Calendar ca = Calendar.getInstance();
			ca.setTime(DateUtil.parse(c.getSalesDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			int lastDayofMonth = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
			int dayAsOfDate = ca.get(Calendar.DATE);
			logger.debug("lastDayofMonth:"+lastDayofMonth+"dayAsOfDate:"+dayAsOfDate);
			/*if(true){
				throw new Exception("Test");
			}*/
			if(lastDayofMonth != dayAsOfDate){
				results[2] ="��س��к����ѹ����ش���¢ͧ��͹";
				return results;
			}
			//delete old month by yearMonth
			psDel = conn.prepareStatement("delete from PENSBME_ENDING_STOCK where year_month ='"+yearMonth+"' and store_code ='"+c.getPensCustCodeFrom()+"'");
			psDel.execute();
			
			//gen sql
			sql = genSQLOnhandMonthEndLotus(conn,c,user);
			//sql insert 
			String sqlIns = "INSERT INTO PENSBME_ENDING_STOCK" +
					"( STORE_CODE, YEAR_MONTH, GROUP_CODE, PENS_ITEM" +
					", SALE_IN_QTY, SALE_OUT_QTY, SALE_RETURN_QTY" +
					", ADJUST_QTY, SHORT_QTY, ENDING_QTY, CREATE_USER, CREATE_DATE) "+
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			psIns = conn.prepareStatement(sqlIns.toString());
					
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
	
				psIns.setString(1, rst.getString("customer_code"));
				psIns.setString(2, yearMonth);
				psIns.setString(3, rst.getString("group_type"));
				psIns.setString(4, rst.getString("pens_item"));
				psIns.setDouble(5, rst.getDouble("sale_in_qty"));
				psIns.setDouble(6, rst.getDouble("sale_out_qty"));
				psIns.setDouble(7, rst.getDouble("sale_return_qty"));
				psIns.setDouble(8, rst.getDouble("ADJUST_QTY"));
				psIns.setDouble(9, rst.getDouble("STOCK_SHORT_QTY"));
				psIns.setDouble(10, rst.getDouble("onhand_qty"));
				
				psIns.setString(11, user.getUserName());
				psIns.setDate(12, new java.sql.Date(new Date().getTime()));
				
				psIns.execute();
			}
			
			conn.commit();
		}catch(Exception e){
			results[2] = ""+e.getMessage();
			conn.rollback();
			throw e;
		}finally{
			if(rst != null)rst.close();
			if(ps != null) ps.close();
			if(psIns != null)psIns.close();
			if(conn != null)conn.close();
		}
		return results;
	}
	
	// Gen MonthEnd From Report onhandLotus
	 public static StringBuilder genSQLOnhandMonthEndLotus(Connection conn,OnhandSummary c,User user) throws Exception{
			StringBuilder sql = new StringBuilder();
			String onhandDateAsOfConfigStr = "";
			Date onhandDateAsOfConfig = null;
			Date asofDate = null;
			boolean unionAllFlag = false;
			try {
				onhandDateAsOfConfigStr = ControlConstantsDB.getOnhandDateAsOfControl(conn,ControlConstantsDB.TYPE_ONHAND_DATE_LOTUS_AS_OF);
				onhandDateAsOfConfig = DateUtil.parse(onhandDateAsOfConfigStr,DateUtil.DD_MM_YYYY_WITHOUT_SLASH);
				
				//prepare parameter
				String christSalesDateStr ="";
				if( !Utils.isNull(c.getSalesDate()).equals("")){
					asofDate = DateUtil.parse(c.getSalesDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStr = DateUtil.stringValue(asofDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				}
				sql.append("\n SELECT A.* FROM(");
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
						//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047%'");
						
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
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n UNION ");
						
						sql.append("\n SELECT distinct ");
						sql.append("\n J.store_code as customer_code,I.pens_item,  ");
						sql.append("\n (select M.pens_desc from PENSBME_MST_REFERENCE M WHERE " +
								"       M.pens_value = J.store_code AND M.reference_code ='Store') as customer_desc, ");
						sql.append("\n I.group_code as group_type ");
						
						sql.append("\n FROM PENSBME_PICK_JOB J   ");
						sql.append("\n ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
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
						    sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
						}
						
                      sql.append("\n UNION ");
						
                      sql.append("\n SELECT distinct ");
      				  sql.append("\n  L.PENS_CUST_CODE as customer_code,L.PENS_ITEM ");
      				  sql.append("\n ,(select M.pens_desc from PENSBME_MST_REFERENCE M WHERE ");
  				      sql.append("\n   M.pens_value = L.PENS_CUST_CODE AND M.reference_code ='Store') as customer_desc ");
					  sql.append("\n ,L.PENS_GROUP_TYPE as group_type ");
					  sql.append("\n  FROM PENSBME_SALES_FROM_LOTUS L ");
					  sql.append("\n WHERE 1=1 ");
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
						sql.append("\n AND L.PENS_GROUP_TYPE IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
					  }
					  
					  sql.append("\n UNION");
						
						/** Adjust issue **/
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
							sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.item_issue_desc IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
						}
						
						sql.append("\n UNION");
						
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
							sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.item_receipt_desc IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
						}
						
						sql.append("\n UNION");
						
						/** Adjust **/
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
							sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.item_adjust_desc IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
						}
						
              sql.append("\n )M ");
      		sql.append("\n LEFT OUTER JOIN(	 ");
      				sql.append("\n SELECT ");
      				sql.append("\n L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
						sql.append("\n L.PENS_GROUP_TYPE as group_type, ");
						sql.append("\n NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
						sql.append("\n FROM ");
						sql.append("\n PENSBI.PENSBME_SALES_FROM_LOTUS L ");
						sql.append("\n WHERE 1=1 ");
							 
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
							sql.append("\n AND L.PENS_GROUP_TYPE IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
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
						//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047%'");
						
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
							sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
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
					
				        if(asofDate != null && asofDate.before(onhandDateAsOfConfig)){// asofDate < onhandDate
				        	
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
							//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
							//Lotus Only 020047
							sql.append("\n AND C.customer_code LIKE '020047%'");
							
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
								sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
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
							//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
							//Lotus Only 020047
							sql.append("\n AND C.customer_code LIKE '020047%'");
							
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
								sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
							}
							sql.append("\n GROUP BY ");
							sql.append("\n C.customer_code ,P.inventory_item_code,  ");
							sql.append("\n substr(P.inventory_item_desc,0,6)" );
							
				        	sql.append("\n UNION ALL ");
				        	
				        	sql.append("\n SELECT  ");
							sql.append("\n J.store_code as customer_code,I.pens_item,  ");
							sql.append("\n I.group_code as group_type, ");
							sql.append("\n COUNT(*) as SALE_RETURN_QTY ");
							
							sql.append("\n FROM PENSBME_PICK_JOB J   ");
							sql.append("\n ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
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
							    sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
							}
							if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
								sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
								sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
							}
							if( !Utils.isNull(c.getGroup()).equals("")){
								sql.append("\n AND I.group_code IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
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
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
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
					sql.append("\n AND L.item_issue_desc IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n  GROUP BY ");
				sql.append("\n  L.STORE_CODE,L.item_issue, ");
				sql.append("\n  L.item_issue_desc ");
				sql.append("\n )STOCK_ISSUE ");
				sql.append("\n ON  M.customer_code = STOCK_ISSUE.customer_code and M.pens_item = STOCK_ISSUE.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_ISSUE.group_type ");
				
				//Stock Receipt
				sql.append("\n LEFT OUTER JOIN(	 ");
				sql.append("\n SELECT ");
				sql.append("\n L.STORE_CODE as customer_code,L.item_receipt as pens_item,L.item_receipt_desc as group_type, ");
				sql.append("\n NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_ADJUST_INVENTORY L  WHERE 1=1 ");
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
					sql.append("\n AND L.item_receipt_desc IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
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
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_ADJUST_SALES L  WHERE 1=1 ");	 
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
					sql.append("\n AND L.item_adjust_desc IN("+SQLHelper.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n  GROUP BY ");
				sql.append("\n  L.STORE_CODE,L.item_adjust, ");
				sql.append("\n  L.item_adjust_desc ");
				sql.append("\n )STOCK_SHORT ");
				sql.append("\n ON  M.customer_code = STOCK_SHORT.customer_code and M.pens_item = STOCK_SHORT.pens_item ");	 
				sql.append("\n AND M.group_type = STOCK_SHORT.group_type ");
				
				sql.append("\n ) A ");
				sql.append("\n WHERE A.group_type IS NOT NULL ");
				sql.append("\n ORDER BY A.customer_code,A.group_type asc ");
				
				//debug write sql to file
				if(logger.isDebugEnabled()){
				   FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
				}
				//logger.debug("sql:"+sql);

			} catch (Exception e) {
				throw e;
			} finally {
				try {
			
				} catch (Exception e) {}
			}
			return sql;
	    }
	

}
