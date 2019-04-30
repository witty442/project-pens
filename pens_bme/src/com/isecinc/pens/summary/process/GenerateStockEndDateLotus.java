package com.isecinc.pens.summary.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;
import com.pens.util.meter.MonitorTime;

public class GenerateStockEndDateLotus {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static MonitorBean processGenStockEndDateLotus(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		Map<String, Object> batchParamMap = new HashMap<String, Object>();
		MonitorTime monitorTime = null;
		int taskStatusInt = Constants.STATUS_START;
		try{
			/** prepare Parameter **/
			batchParamMap = monitorModel.getBatchParamMapObj();
			OnhandSummary summary = (OnhandSummary)batchParamMap.get("ONHAND_SUMMARY");
			logger.debug("customerCode:"+summary.getPensCustCodeFrom());
			logger.debug("salesdate:"+summary.getSalesDate());
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
				
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item  TableName:Genreate Stock End Date Lotus");
			MonitorItemBean modelItem = new MonitorItemBean();
			modelItem.setMonitorId(monitorModel.getMonitorId());
			modelItem.setSource("");
			modelItem.setDestination("");
			modelItem.setTableName("Genreate Stock End Date Lotus");
			modelItem.setFileName("");
			modelItem.setStatus(Constants.STATUS_START);
			modelItem.setDataCount(0);
			modelItem.setFileSize("");
			modelItem.setSubmitDate(new Date());
			modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
			
			monitorTime  = new MonitorTime("Generate Stock End Date Lotus");   
			
			//Validate BME_ORDER EXPORTED 
			String re[] = ControlConstantsDB.canGenEndDateLotus(conn,summary.getPensCustCodeFrom(),summary.getSalesDate());
			if("false".equals(re[0])){
				taskStatusInt = Constants.STATUS_FAIL;
			    modelItem.setErrorMsg("กรุณาตรวจสอบวันที่จะ End date ต้องมากกว่าการ End date ครั้งก่อนหน้านี้");
				modelItem.setErrorCode("SalesDateException");
			}
			
			if(taskStatusInt == Constants.STATUS_START){
				//Process Generate 
				modelItem = GenerateStockEndDateLotus.generateEndDateLotus(conn,modelItem,summary, user);
				
				monitorTime.debugUsedTime();
			
				//Insert Monitor Item
				modelItem = dao.insertMonitorItem(connMonitor,modelItem);
				
				logger.debug("Transaction commit");
				taskStatusInt = Constants.STATUS_SUCCESS;
				conn.commit();
				
				/** Update Head Monitor **/
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			}else{
				/** Update Head Monitor **/
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setErrorCode(modelItem.getErrorCode());
				monitorModel.setErrorMsg(modelItem.getErrorMsg());
				monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			}
	
			/** Update Monitor **/
			dao.updateMonitor(connMonitor,monitorModel);

		}catch(Exception e){
			logger.error(e.getMessage(),e);
			
			/** End process ***/
			logger.debug("Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
			monitorModel.setTransactionType(monitorModel.getTransactionType());
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			
			dao.updateMonitorCaseError(connMonitor,monitorModel);

			//clear Task running for next run
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS);
			
			if(conn != null){
			  logger.debug("Transaction Rolback");
			  conn.rollback();
			}
		
		}finally{
		
			if(conn != null){
				conn.setAutoCommit(true);
				conn.close();
				conn =null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
		return monitorModel;
	}
	
	public static MonitorItemBean generateEndDateLotus(Connection conn,MonitorItemBean monitorItemBean,OnhandSummary c,User user) throws Exception{
		PreparedStatement ps = null;
		PreparedStatement psIns = null;
		PreparedStatement psDel = null;
	    ResultSet rst = null;
	    StringBuilder sql = new StringBuilder();
	    //String[] results = new String[3];
	    //String yearMonth = "";
	    int result =0;
		try{
			Date asofDate = Utils.parse(c.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			//validate 
			//results = BMECControlDAO.canGenEndDateLotus(conn,c.getPensCustCodeFrom(),c.getSalesDate());
			//yearMonth = results[1];
			
			//delete old month by yearMonth
			//psDel = conn.prepareStatement("delete from PENSBME_ENDDATE_STOCK where year_month ='"+yearMonth+"' and store_code ='"+c.getPensCustCodeFrom()+"'");
			//psDel.execute();
			
			//gen sql
			sql = genSQLOnhandEndDateLotus(conn,c,user);
			//sql insert 
			String sqlIns = "INSERT INTO PENSBME_ENDDATE_STOCK" +
					"( STORE_CODE, ENDING_DATE, GROUP_CODE, PENS_ITEM" +
					", SALE_IN_QTY, SALE_OUT_QTY, SALE_RETURN_QTY" +
					", ADJUST_QTY, SHORT_QTY, ENDING_QTY, CREATE_USER, CREATE_DATE) "+
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			psIns = conn.prepareStatement(sqlIns.toString());
					
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
	
				psIns.setString(1, rst.getString("customer_code"));
				psIns.setDate(2,new java.sql.Date(asofDate.getTime()));
				psIns.setString(3, rst.getString("group_type"));
				psIns.setString(4, rst.getString("pens_item"));
				psIns.setDouble(5, rst.getDouble("sale_in_qty"));
				psIns.setDouble(6, rst.getDouble("sale_out_qty"));
				psIns.setDouble(7, rst.getDouble("sale_return_qty"));
				psIns.setDouble(8, rst.getDouble("ADJUST_QTY"));
				psIns.setDouble(9, rst.getDouble("STOCK_SHORT_QTY"));
				psIns.setDouble(10, rst.getDouble("onhand_qty"));
				
				psIns.setString(11, user.getUserName());
				psIns.setTimestamp(12, new java.sql.Timestamp(new Date().getTime()));
				
				psIns.execute();
			}
			
			result = Constants.STATUS_SUCCESS;
		
			monitorItemBean.setStatus(result);
			
		}catch(Exception e){
			throw e;
		}finally{
			if(rst != null)rst.close();
			if(ps != null) ps.close();
			if(psIns != null)psIns.close();
		}
		return monitorItemBean;	
	}
	
	// Gen MonthEnd From Report onhandLotus
	 public static StringBuilder genSQLOnhandEndDateLotus(Connection conn,OnhandSummary c,User user) throws Exception{
			StringBuilder sql = new StringBuilder();
			String onhandDateAsOfConfigStr = "";
			Date onhandDateAsOfConfig = null;
			Date asofDate = null;
			try {
				onhandDateAsOfConfigStr = ControlConstantsDB.getOnhandDateAsOfControl(conn,ControlConstantsDB.TYPE_ONHAND_DATE_LOTUS_AS_OF);
				onhandDateAsOfConfig = Utils.parse(onhandDateAsOfConfigStr,Utils.DD_MM_YYYY_WITHOUT_SLASH);
				
				//prepare parameter
				String christSalesDateStr ="";
				if( !Utils.isNull(c.getSalesDate()).equals("")){
					asofDate = Utils.parse(c.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStr = Utils.stringValue(asofDate, Utils.DD_MM_YYYY_WITH_SLASH);
				}
				logger.debug("asofDate:"+asofDate);
				logger.debug("onhandDateAsOfConfig:"+onhandDateAsOfConfig);
				
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
						sql.append("\n MP.MATERIAL_MASTER as group_type  ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n ,PENSBME_STYLE_MAPPING MP ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND P.inventory_item_code = MP.pens_item");
						sql.append("\n AND V.Customer_id IS NOT NULL   ");
						sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
						sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
						
						//Lotus Only 020047
						sql.append("\n AND C.customer_code LIKE '020047-%'");
						
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
							sql.append("\n AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%'");
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
						    sql.append("\n AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code LIKE '"+c.getGroup()+"%' ");
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
						sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					  }
					  if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					  }
					  if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.PENS_GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
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
							sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.item_issue_desc LIKE '"+c.getGroup()+"%' ");
						}
						//Lotus Only 020047
						sql.append("\n AND L.STORE_CODE LIKE '020047-%'");
						
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
							sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.item_receipt_desc LIKE '"+c.getGroup()+"%' ");
						}
						//Lotus Only 020047
						sql.append("\n AND L.STORE_CODE LIKE '020047-%'");
						
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
							sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.item_adjust_desc LIKE '"+c.getGroup()+"%' ");
						}
						//Lotus Only 020047
						sql.append("\n AND L.STORE_CODE LIKE '020047-%'");
              sql.append("\n )M ");
      		sql.append("\n LEFT OUTER JOIN(	 ");
      				sql.append("\n SELECT ");
      				sql.append("\n L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
						sql.append("\n L.PENS_GROUP_TYPE as group_type, ");
						sql.append("\n NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
						sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_LOTUS L ");
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
							sql.append("\n AND L.PENS_GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
						}
						sql.append("\n  GROUP BY ");
						sql.append("\n  L.PENS_CUST_CODE,L.PENS_ITEM, L.PENS_GROUP_TYPE ");
						sql.append("\n )SALE_OUT ");
						sql.append("\n ON  M.customer_code = SALE_OUT.customer_code and M.pens_item = SALE_OUT.pens_item ");	 
						sql.append("\n AND M.group_type = SALE_OUT.group_type ");
						
				sql.append("\n LEFT OUTER JOIN( ");
						sql.append("\n SELECT  ");
						sql.append("\n C.customer_code ,P.inventory_item_code as pens_item,  ");
						sql.append("\n MP.MATERIAL_MASTER as group_type, ");
						sql.append("\n NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
						sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
						sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
						sql.append("\n ,PENSBME_STYLE_MAPPING MP ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
						sql.append("\n AND V.customer_id = C.customer_id  ");
						sql.append("\n AND P.inventory_item_code = MP.pens_item");
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
							sql.append("\n AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%' ");
						}
							
						sql.append("\n GROUP BY ");
						sql.append("\n C.customer_code,P.inventory_item_code,MP.MATERIAL_MASTER ");
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
							sql.append("\n MP.MATERIAL_MASTER as group_type, ");
							sql.append("\n NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
							sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
							sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
							sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
							sql.append("\n ,PENSBME_STYLE_MAPPING MP ");
							sql.append("\n WHERE 1=1   ");
							sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
							sql.append("\n AND V.customer_id = C.customer_id  ");
							sql.append("\n AND P.inventory_item_code = MP.pens_item");
							sql.append("\n AND V.Customer_id IS NOT NULL   ");
							sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
							sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
							//NOT IN pensbme_group_unuse_lotus
							sql.append("\n AND MP.MATERIAL_MASTER NOT IN(select group_code from pensbme_group_unuse_lotus)");
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
								sql.append("\n AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%' ");
							}
							sql.append("\n GROUP BY ");
							sql.append("\n C.customer_code ,P.inventory_item_code, MP.MATERIAL_MASTER" );
				         }
						
				        if(asofDate.equals(onhandDateAsOfConfig) || asofDate.after(onhandDateAsOfConfig) ){ //asOfDate >= onhandDateConfig
				        	
				        	sql.append("\n SELECT  ");
							sql.append("\n C.customer_code,P.inventory_item_code as pens_item,  ");
							sql.append("\n MP.MATERIAL_MASTER as group_type, ");
							sql.append("\n NVL(SUM(RETURNED_QTY),0)  as SALE_RETURN_QTY ");
							sql.append("\n FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
							sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
							sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
							sql.append("\n ,PENSBME_STYLE_MAPPING MP ");
							sql.append("\n WHERE 1=1   ");
							sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
							sql.append("\n AND V.customer_id = C.customer_id  ");
							sql.append("\n AND P.inventory_item_code = MP.pens_item");
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
								sql.append("\n AND MP.MATERIAL_MASTER LIKE '"+c.getGroup()+"%' ");
							}
							sql.append("\n GROUP BY ");
							sql.append("\n C.customer_code ,P.inventory_item_code, MP.MATERIAL_MASTER" );
							
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
							    sql.append("\n AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
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
				sql.append("\n FROM ");
				sql.append("\n PENSBI.PENSBME_ADJUST_INVENTORY L WHERE 1=1 " );
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
					sql.append("\n AND L.item_issue_desc LIKE '"+c.getGroup()+"%' ");
				}
				//Lotus Only 020047
				sql.append("\n  AND L.STORE_CODE LIKE '020047%'");
				sql.append("\n  GROUP BY L.STORE_CODE,L.item_issue, L.item_issue_desc ");
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
					sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.item_receipt_desc LIKE '"+c.getGroup()+"%' ");
				}
				//Lotus Only 020047
				sql.append("\n  AND L.STORE_CODE LIKE '020047%'");
				sql.append("\n  GROUP BY L.STORE_CODE,L.item_receipt,L.item_receipt_desc ");
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
					sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.item_adjust_desc LIKE '"+c.getGroup()+"%' ");
				}
				//Lotus Only 020047
				sql.append("\n  AND L.STORE_CODE LIKE '020047%'");
				sql.append("\n  GROUP BY L.STORE_CODE,L.item_adjust, L.item_adjust_desc ");
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
	
	 public static String getEndDateStock(String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String r = "";
			try {
				//Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				//String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n select distinct max(ending_date) as max_ending_date FROM PENSBME_ENDDATE_STOCK WHERE 1=1 ");
				sql.append("\n and store_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()){
					r = Utils.stringValue(rst.getDate("max_ending_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				}
				
				return r;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			
		}
	 
	 public static String getEndDateStockTemp(String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String r = "";
			try {
				//Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				//String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n select distinct max(ending_date) as max_ending_date FROM PENSBME_ENDDATE_STOCK_TEMP WHERE 1=1 ");
				sql.append("\n and store_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()){
					r = Utils.stringValue(rst.getDate("max_ending_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				}
				
				return r;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			
		}
	 
	 public static String getEndSaleDateLotus(String storeCode,String asOfDate) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String r = "";
			try {
				Date asofDateTemp = Utils.parse(asOfDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				sql.append("\n select distinct max(sales_date) as max_ending_date FROM PENSBME_SALES_FROM_LOTUS WHERE 1=1 ");
				sql.append("\n and pens_cust_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()){
					if(rst.getDate("max_ending_date").before(asofDateTemp)){
					   r = Utils.stringValue(rst.getDate("max_ending_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					}else{
					   r = asOfDate;
					}
				}
				
				return r;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			
		}

}
