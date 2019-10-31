package com.isecinc.pens.inf.manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.imports.excel.ImportExcelProcess;
import com.isecinc.pens.inf.manager.batchwork.BatchProcessWorker;
import com.isecinc.pens.inf.manager.process.ExportOrderToICC;
import com.isecinc.pens.inf.manager.process.GenerateHISHER;
import com.isecinc.pens.inf.manager.process.GenerateItemMasterHISHER;
import com.isecinc.pens.inf.manager.process.GenerateOrderExcel;
import com.isecinc.pens.inf.manager.process.ImportBillICC;
import com.isecinc.pens.inf.manager.process.ImportPosProcess;
import com.isecinc.pens.inf.manager.process.ImportSaleOutWacoalProcess;
import com.isecinc.pens.inf.manager.process.ImportTransactionLotusProcess;
import com.isecinc.pens.inf.manager.process.ImportWacoalProcess;
import com.isecinc.pens.summary.process.GenerateStockEndDateLotus;
import com.isecinc.pens.summary.process.GenerateReportEndDateLotus;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

/**
 * @author WITTY
 *
 */
public class BatchProcessManager {
   
	public static Logger logger = Logger.getLogger("PENS");
	public static Object IMPORT_Q = new Object();

	
	public MonitorBean mainProcess(MonitorBean monitorModel,User user,HttpServletRequest request ) {
		logger.info("Start Thread:" + Thread.currentThread().getName());
		try {
           
			/** Process Interface ICC *******************************************************************************/
           if(monitorModel.getType().equals(Constants.TYPE_GEN_ITEM_MASTER_HISHER)){
        	   monitorModel = (new BatchProcessManager()).processGenerateItemMasterHisHerTxt(monitorModel, user,request);
        	   
           }else if(monitorModel.getType().equals(Constants.TYPE_GEN_HISHER)){
                monitorModel = (new BatchProcessManager()).processGenerateHisHerTxt(monitorModel, user,request); 
                
           }else if(monitorModel.getType().equals(Constants.TYPE_GEN_ORDER_EXCEL)){
        	   
              	monitorModel = (new BatchProcessManager()).processGenerateOrderExcel(monitorModel, user,request);
           	
            }else if(monitorModel.getType().equals(Constants.TYPE_IMPORT_BILL_ICC)){
            	monitorModel = (new BatchProcessManager()).processImportBillICC(monitorModel, user,request);
            	
            }else if(monitorModel.getType().equals(Constants.TYPE_EXPORT_BILL_ICC)){
            	monitorModel = (new BatchProcessManager()).processExportOrderToICC(monitorModel, user,request);
            	
            /** Process Interface ICC*******************************************************************************/
           	
            	
            /** Process Transaction BME ****************************************************************************/
            }else if(monitorModel.getType().equals(Constants.TYPE_IMPORT_TRANSACTION_LOTUS)){
            	monitorModel = (new BatchProcessManager()).processImportTransactionLotus(monitorModel, user,request);
            	
            }else if(monitorModel.getType().equals(Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS)){
            	monitorModel = (new BatchProcessManager()).processGenStockEndDateLotus(monitorModel, user,request);
            	
            }else if(monitorModel.getType().equals(Constants.TYPE_GEN_STOCK_REPORT_ENDDATE_LOTUS)){
            	monitorModel = (new BatchProcessManager()).processGenStockReportEndDateLotus(monitorModel, user,request);
            	
           /** Process Transaction BME ****************************************************************************/
           
           /** Process  Wacoal ****************************************************************************/
           }else if(monitorModel.getType().equals(Constants.TYPE_IMPORT_WACOAL_STOCK)){
        	    monitorModel = (new BatchProcessManager()).processImportWacoalStock(monitorModel, user,request);
        	    
           }else if(monitorModel.getType().equals(Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN)){
           	    monitorModel = (new BatchProcessManager()).processImportWacoalSalesInReturn(monitorModel, user,request);

		   }else if(monitorModel.getType().equals(Constants.TYPE_IMPORT_SALEOUT_WACOAL)){
         	    monitorModel = (new BatchProcessManager()).processImportSaleOutWacoal(monitorModel, user,request);
		   
           /** Process  Wacoal ****************************************************************************/
         
		   }else if(monitorModel.getType().equals(Constants.TYPE_IMPORT_POS)){
      	        monitorModel = (new BatchProcessManager()).processImportPos(monitorModel, user,request);
		   }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return monitorModel;
	}
	
	/**
	 * 
	 * @param transType
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public MonitorBean createBatchTask(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
		    //start Thread
			new BatchProcessWorker(monitorModel,user,request).start();
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
	    return monitorModel;
	}

	/**
	 * importFile
	 * @param monitorId
	 * @param transType
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean processGenerateHisHerTxt(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		EnvProperties env = EnvProperties.getInstance();
		MonitorTime monitorTime = null;
		try{
			//logger.debug("RealPath:"+request.getRealPath(""));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
				
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.icc.ip.server"), env.getProperty("ftp.icc.username"), env.getProperty("ftp.icc.password"));
			ftpManager.canConnectFTPServer();
			
			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item  TableName:Genreate HISHER");
			MonitorItemBean modelItem = new MonitorItemBean();
			modelItem.setMonitorId(monitorModel.getMonitorId());
			modelItem.setSource("");
			modelItem.setDestination("");
			modelItem.setTableName("Genreate HISHER");
			modelItem.setFileName("");
			modelItem.setStatus(Constants.STATUS_START);
			modelItem.setDataCount(0);
			modelItem.setFileSize("");
			modelItem.setSubmitDate(new Date());
			modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
			
			monitorTime  = new MonitorTime("Generate HISHER");   
			
			//Process Generate 
			modelItem = GenerateHISHER.runProcess(user,modelItem,monitorModel.getBatchParamMap());
			
			monitorTime.debugUsedTime();
			
			/** validate data **/
            if(modelItem.getFailCount() >0){
			   modelItem.setErrorMsg("ข้อมูลไม่ถูกต้อง");
			   modelItem.setErrorCode("DataFailException");
            }
            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
 			   modelItem.setErrorMsg("ไม่พบข้อมูลที่จะ Generate");
 			   modelItem.setErrorCode("DataNotFoundException");
             }
            
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			logger.debug("Transaction commit");
			conn.commit();
			
			/** Update status Head Monitor */
			/** validate data **/
            if(modelItem.getFailCount() >0){
            	monitorModel.setErrorMsg("ข้อมูลไม่ถูกต้อง");
            	monitorModel.setErrorCode("DataFailException");
            }
            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
            	monitorModel.setErrorMsg("ไม่พบข้อมูลที่จะ Generate");
            	monitorModel.setErrorCode("DataNotFoundException");
            }
            
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
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
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_HISHER);
			
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
	
	/**
	 * importFile
	 * @param monitorId
	 * @param transType
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean processGenerateItemMasterHisHerTxt(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		EnvProperties env = EnvProperties.getInstance();
		MonitorTime monitorTime = null;
		try{
			//logger.debug("RealPath:"+request.getRealPath(""));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
				
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.icc.ip.server"), env.getProperty("ftp.icc.username"), env.getProperty("ftp.icc.password"));
			ftpManager.canConnectFTPServer();
			
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item  TableName:Genreate Item Master HISHER");
			MonitorItemBean modelItem = new MonitorItemBean();
			modelItem.setMonitorId(monitorModel.getMonitorId());
			modelItem.setSource("");
			modelItem.setDestination("");
			modelItem.setTableName("Genreate Item Master HISHER");
			modelItem.setFileName("");
			modelItem.setStatus(Constants.STATUS_START);
			modelItem.setDataCount(0);
			modelItem.setFileSize("");
			modelItem.setSubmitDate(new Date());
			modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
			
			monitorTime  = new MonitorTime("Generate Item Master HISHER");   
			
			//Process Generate 
			modelItem = GenerateItemMasterHISHER.runProcess(user,modelItem,monitorModel.getBatchParamMap());
			
			monitorTime.debugUsedTime();
			
			/** validate data **/
            if(modelItem.getFailCount() >0){
			   modelItem.setErrorMsg("ข้อมูลไม่ถูกต้อง");
			   modelItem.setErrorCode("DataFailException");
            }
            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
 			   modelItem.setErrorMsg("ไม่พบข้อมูลที่จะ Generate");
 			   modelItem.setErrorCode("DataNotFoundException");
             }
            
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			logger.debug("Transaction commit");
			conn.commit();
			
			/** Update status Head Monitor */
			/** validate data **/
            if(modelItem.getFailCount() >0){
            	monitorModel.setErrorMsg("ข้อมูลไม่ถูกต้อง");
            	monitorModel.setErrorCode("DataFailException");
            }
            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
            	monitorModel.setErrorMsg("ไม่พบข้อมูลที่จะ Generate");
            	monitorModel.setErrorCode("DataNotFoundException");
            }
            
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
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
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_ITEM_MASTER_HISHER);
			
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
	
	/***
	 * 
	 * @param monitorModel
	 * @param user
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean processImportBillICC(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		MonitorTime monitorTime = null;
		try{
			logger.debug("import Type:"+monitorModel.getTransactionType());

			monitorTime  = new MonitorTime("Import Bill ICC");   
			
			//Process Generate 
			ImportBillICC.runProcess(user,monitorModel);
			
			monitorTime.debugUsedTime();
	
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		
		}
		return monitorModel;
	}
	
	public  MonitorBean processExportOrderToICC(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		MonitorTime monitorTime = null;
		try{
			logger.debug("Export Type:"+monitorModel.getTransactionType());

			monitorTime  = new MonitorTime("processExportOrderToICC");   
			
			//Process Generate 
			ExportOrderToICC.runProcess(user,monitorModel);
			
			monitorTime.debugUsedTime();
	
		}catch(Exception e){
			logger.error(e.getMessage(),e);

		}finally{
		
		}
		return monitorModel;
	}
	
	public  MonitorBean processGenerateOrderExcel(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		EnvProperties env = EnvProperties.getInstance();
		Map<String, String> batchParamMap = new HashMap<String, String>();
		MonitorTime monitorTime = null;
		int taskStatusInt = Constants.STATUS_START;
		try{
			
			/** prepare Paramenter **/
			batchParamMap = monitorModel.getBatchParamMap();
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
				
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.icc.ip.server"), env.getProperty("ftp.icc.username"), env.getProperty("ftp.icc.password"));
			ftpManager.canConnectFTPServer();
			
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item  TableName:Genreate HISHER");
			MonitorItemBean modelItem = new MonitorItemBean();
			modelItem.setMonitorId(monitorModel.getMonitorId());
			modelItem.setSource("");
			modelItem.setDestination("");
			modelItem.setTableName("Genreate Order Excel HISHER");
			modelItem.setFileName("");
			modelItem.setStatus(Constants.STATUS_START);
			modelItem.setDataCount(0);
			modelItem.setFileSize("");
			modelItem.setSubmitDate(new Date());
			modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
			
			monitorTime  = new MonitorTime("Generate  Order Excel HISHER");   
			
			//Validate BME_ORDER EXPORTED 
			boolean getExportedN = GenerateOrderExcel.validateExportedFlagN(connMonitor, Utils.isNull(batchParamMap.get(GenerateOrderExcel.PARAM_CUST_GROUP)),Utils.isNull(batchParamMap.get(GenerateOrderExcel.PARAM_TRANS_DATE)));
			if(getExportedN==true){
				taskStatusInt = Constants.STATUS_FAIL;
			    modelItem.setErrorMsg("Order ตามวันที่ระบุ ยังไม่ได้ทำการ Interface Onhand to ICC -twstock");
				modelItem.setErrorCode("OrderFailInterfacetwstockException");
			}else{
			  boolean getExportedG = GenerateOrderExcel.validateExportedFlagG(connMonitor,Utils.isNull(batchParamMap.get(GenerateOrderExcel.PARAM_CUST_GROUP)), Utils.isNull(batchParamMap.get(GenerateOrderExcel.PARAM_TRANS_DATE)));
			  if(getExportedG==true){
				  taskStatusInt = Constants.STATUS_FAIL;
				  modelItem.setErrorMsg("ข้อมูลตามวันที่ Order ที่ระบุ ได้เคย Generate excel file ไปแล้ว");
				  modelItem.setErrorCode("OrderDuplicationGenExcelException");
			  }
			}
			
			if(taskStatusInt == Constants.STATUS_START){
				//Process Generate 
				modelItem = GenerateOrderExcel.runProcess(user,modelItem,monitorModel.getBatchParamMap());
				
				monitorTime.debugUsedTime();
				
				/** validate data **/
	           /* if(modelItem.getFailCount() >0){
				   modelItem.setErrorMsg("ข้อมูลไม่ถูกต้อง");
				   modelItem.setErrorCode("DataFailException");
	            }
	            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
	 			   modelItem.setErrorMsg("ไม่พบข้อมูลที่จะ Generate");
	 			   modelItem.setErrorCode("DataNotFoundException");
	             }*/
	            
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
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_ORDER_EXCEL);
			
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
	
	public  MonitorBean processImportTransactionLotus(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		MonitorTime monitorTime = null;
		try{
			logger.debug("import Type:"+monitorModel.getTransactionType());

			monitorTime  = new MonitorTime("Import Transaction Lotus");   
			
			//Process Generate 
			ImportTransactionLotusProcess.runProcess(user,monitorModel);
			
			monitorTime.debugUsedTime();
	
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		
		}
		return monitorModel;
	}
	
	public  MonitorBean processGenStockEndDateLotus(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		try{
			return GenerateStockEndDateLotus.processGenStockEndDateLotus(monitorModel, user, request);
		}catch(Exception e){
			throw e;
		}
	}
	
	public  MonitorBean processGenStockReportEndDateLotus(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		try{
			return GenerateReportEndDateLotus.process(monitorModel, user, request);
		}catch(Exception e){
			throw e;
		}
	}
	
	public  MonitorBean processImportWacoalStock(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		MonitorTime monitorTime = null;
		try{
			logger.debug("import Type:"+monitorModel.getTransactionType());

			monitorTime  = new MonitorTime("Import Wacoal Stock");   
			
			String[] tableNameSelects = new String[1];
			tableNameSelects[0] = "PENSBME_INISTK_WACOAL";
			ImportWacoalProcess.runProcess(user,monitorModel,tableNameSelects);
			
			monitorTime.debugUsedTime();
	
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		
		}
		return monitorModel;
	}
	public  MonitorBean processImportWacoalSalesInReturn(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		MonitorTime monitorTime = null;
		try{
			logger.debug("import Type:"+monitorModel.getType());
			monitorTime  = new MonitorTime("Import Wacoal SaleIn Return");   

			String[] tableNameSelects = new String[2];
			tableNameSelects[0] = "PENSBME_WACOAL_SALEIN";
			tableNameSelects[1] = "PENSBME_WACOAL_RETURN";
			ImportWacoalProcess.runProcess(user,monitorModel,tableNameSelects);
			
			monitorTime.debugUsedTime();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		
		}
		return monitorModel;
	}
	
	public  MonitorBean processImportSaleOutWacoal(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		MonitorTime monitorTime = null;
		try{
			logger.debug("import Type:"+monitorModel.getType());
			monitorTime  = new MonitorTime("import processImportSaleOutWacoal");   

			ImportSaleOutWacoalProcess.runProcess(user,monitorModel);
			
			monitorTime.debugUsedTime();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		
		}
		return monitorModel;
	}
	
	public  MonitorBean processImportPos(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		MonitorTime monitorTime = null;
		try{
			logger.debug("import Type:"+monitorModel.getType());
			monitorTime  = new MonitorTime("import ImportPosProcess");   

			ImportPosProcess.runProcess(user,monitorModel);
			
			monitorTime.debugUsedTime();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		
		}
		return monitorModel;
	}
	
}
