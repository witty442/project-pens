package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.TaskStoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.isecinc.pens.web.batchtask.subtask.GenStockOnhandRepTempBigCSubTask;
import com.isecinc.pens.web.batchtask.subtask.GenStockOnhandRepTempLotusSubTask;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class GenStockEndDateLotusTask extends BatchTask implements BatchTaskInterface{

	public static String PARAM_ASOF_DATE ="AS_OF_DATE";
	public static String PARAM_STORE_CODE ="STORE_CODE";
	
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate$Button Name
	 */
	public String[] getParam(){
		String[] param = new String[1];
		param[0] = "temp|temp|TEXT||VALID";
		return param;
	}
	public List<BatchTaskListBean> getParamListBox(){
		return null;
	}
	public String getButtonName(){
		return "No BTN Is Run From Popup";
	}
	
	public String getDescription(){
		String desc = "Gen Stock End Date Lotus <br/>";
		return desc;
	}
	public String getDevInfo(){
		return "PENSBI.PENSBME_ENDDATE_STOCK";
	}
	public boolean isDispDetail(){
		return true;
	}
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
		script +="\n    return true";
		script +="\n }";
		script +="\n </script>";
		return script;
	}
	public  MonitorBean run(MonitorBean monitorModel){
		Connection conn = null;
		Connection connMonitor = null;
		BatchTaskDAO dao = new BatchTaskDAO();
		MonitorTime monitorTime = null;
		try{
			//logger.debug("RealPath:"+request.getRealPath(""));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();

			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
			
			/** Validate StoreCode is generated**/
			String storeCode = monitorModel.getBatchParamMap().get(PARAM_STORE_CODE);
			logger.info("Start Gen StoreCode["+storeCode+"]...");
			
			/** Start process **/ 
			modelItem  = process(connMonitor, conn, monitorModel, modelItem);
			
			/**debug TimeUse **/
			monitorTime.debugUsedTime();
			logger.debug("modelItem status:"+modelItem.getStatus());
			
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			if(modelItem.getStatus()== Constants.STATUS_SUCCESS){
			    logger.debug("Transaction commit");
			    conn.commit();
			}else{
				logger.debug("Transaction Rollback");
				conn.rollback();
			}
			
			/** Update status Head Monitor From Monitor Item Status*/
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			monitorModel.setType("GEN");
			monitorModel.setThName("Gen Stock End Date Lotus");
			
			/** Update Status Monitor **/
			dao.updateMonitor(connMonitor,monitorModel);
		}catch(Exception e){
			try{
				logger.error(e.getMessage(),e);
				
				/** End process ***/
				logger.debug("Update Monitor to Fail ");
				monitorModel.setStatus(Constants.STATUS_FAIL);
				monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
				monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
				
				dao.updateMonitorCaseError(connMonitor,monitorModel);
	
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),monitorModel.getName());
				
				if(conn != null){
				   logger.debug("Transaction Rolback");
				   conn.rollback();
				}
			}catch(Exception ee){}
		}finally{
		   try{
				if(conn != null){
					conn.setAutoCommit(true);
					conn.close();
					conn =null;
				}
				if(connMonitor != null){
					connMonitor.close();
					connMonitor=null;
				}
		   }catch(Exception ee){}
		}
		return monitorModel;
	}
	public static MonitorItemBean process(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) throws Exception{
		int status = Constants.STATUS_FAIL;
		int successCount = 0;int dataCount=0;int failCount = 0;
		int r = 0;
		boolean foundError = false;boolean passValid = false;
		String lineMsg = "";String errorMsg = "";
		PreparedStatement ps =null,psIns=null;
		ResultSet rst = null;
		int index = 0;
		StringBuffer sql = new StringBuffer("");
		try{
		    //get parameter
			String asOfDate = monitorModel.getBatchParamMap().get(PARAM_ASOF_DATE);
			String storeCode = monitorModel.getBatchParamMap().get(PARAM_STORE_CODE);
			
			logger.debug("asOfDate:"+asOfDate);
			logger.debug("storeCode:"+storeCode);
			
			//Validate can gen Stock EndDate Lotus
			String re[] = ControlConstantsDB.canGenEndDateLotus(conn,storeCode,asOfDate);
			logger.debug("canGenEndDateLotus:"+re[0]);
			if("false".equals(re[0])){
				monitorItemBean.setErrorMsg("กรุณาตรวจสอบวันที่จะ End date ต้องมากกว่าการ End date ครั้งก่อนหน้านี้");
				monitorItemBean.setErrorCode("SalesDateException");
				monitorItemBean.setStatus(Constants.STATUS_FAIL);
				return monitorItemBean;
			}

			//sql insert 
			String sqlIns = "INSERT INTO PENSBI.PENSBME_ENDDATE_STOCK" +
					"( STORE_CODE, ENDING_DATE, GROUP_CODE, PENS_ITEM" +
					", SALE_IN_QTY, SALE_OUT_QTY, SALE_RETURN_QTY" +
					", ADJUST_QTY, SHORT_QTY, ENDING_QTY, CREATE_USER, CREATE_DATE) "+
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			psIns = conn.prepareStatement(sqlIns.toString());
			
			//gen sql data
			ReportAllBean cri = new ReportAllBean();
			cri.setSalesDate(asOfDate);
			cri.setPensCustCodeFrom(storeCode);
			sql = genSQLOnhandEndDateLotus(conn,cri);
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
				psIns.setString(1, rst.getString("customer_code"));
				psIns.setDate(2,new java.sql.Date(DateUtil.parse(asOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
				psIns.setString(3, rst.getString("group_type"));
				psIns.setString(4, rst.getString("pens_item"));
				psIns.setDouble(5, rst.getDouble("sale_in_qty"));
				psIns.setDouble(6, rst.getDouble("sale_out_qty"));
				psIns.setDouble(7, rst.getDouble("sale_return_qty"));
				psIns.setDouble(8, rst.getDouble("ADJUST_QTY"));
				psIns.setDouble(9, rst.getDouble("STOCK_SHORT_QTY"));
				psIns.setDouble(10, rst.getDouble("onhand_qty"));
				
				psIns.setString(11, monitorModel.getUserName());
				psIns.setTimestamp(12, new java.sql.Timestamp(new Date().getTime()));
				
		        psIns.addBatch();
			}
			
			if(foundError ==false){
				logger.info("Excute Batch");
				psIns.executeBatch();
				status = Constants.STATUS_SUCCESS;
			}
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);//successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}finally{
			if(ps != null){ps.close();ps =null;}
			if(psIns != null){psIns.close();psIns =null;}
		}
		return monitorItemBean;	
	}
	
// Gen MonthEnd From Report onhandLotus
 public static StringBuffer genSQLOnhandEndDateLotus(Connection conn,ReportAllBean c) throws Exception{
	 StringBuffer sql = new StringBuffer();
		String onhandDateAsOfConfigStr = "";
		Date onhandDateAsOfConfig = null;
		Date asofDate = null;
		try {
			logger.debug("genSQLOnhandEndDateLotus");
			onhandDateAsOfConfigStr = ControlConstantsDB.getOnhandDateAsOfControl(conn,ControlConstantsDB.TYPE_ONHAND_DATE_LOTUS_AS_OF);
			onhandDateAsOfConfig = DateUtil.parse(onhandDateAsOfConfigStr,DateUtil.DD_MM_YYYY_WITHOUT_SLASH);
			
			//prepare parameter
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getSalesDate()).equals("")){
				asofDate = DateUtil.parse(c.getSalesDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = DateUtil.stringValue(asofDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
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
					    sql.append("\n AND C.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
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
					    sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
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
						sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
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
						sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
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
				sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
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
				sql.append("\n AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
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
		
		} catch (Exception e) {
			throw e;
		} 
		return sql;
    }

}
