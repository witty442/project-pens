package com.isecinc.pens.inf.manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import meter.MonitorTime;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.batchwork.BatchProcessWorker;
import com.isecinc.pens.inf.manager.process.ExportBillICC;
import com.isecinc.pens.inf.manager.process.GenerateHISHER;
import com.isecinc.pens.inf.manager.process.GenerateItemMasterHISHER;
import com.isecinc.pens.inf.manager.process.GenerateOrderExcel;
import com.isecinc.pens.inf.manager.process.ImportBillICC;
import com.isecinc.pens.process.SequenceProcess;

/**
 * @author WITTY
 *
 */
public class ProcessManager {
   
	public static Logger logger = Logger.getLogger("PENS");
	public static Object IMPORT_Q = new Object();

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
			
			/** Set Trasaction no Auto Commit **/
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
			   modelItem.setErrorMsg("���������١��ͧ");
			   modelItem.setErrorCode("DataFailException");
            }
            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
 			   modelItem.setErrorMsg("��辺�����ŷ��� Generate");
 			   modelItem.setErrorCode("DataNotFoundException");
             }
            
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			logger.debug("Transaction commit");
			conn.commit();
			
			/** Update status Head Monitor */
			/** validate data **/
            if(modelItem.getFailCount() >0){
            	monitorModel.setErrorMsg("���������١��ͧ");
            	monitorModel.setErrorCode("DataFailException");
            }
            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
            	monitorModel.setErrorMsg("��辺�����ŷ��� Generate");
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
			   modelItem.setErrorMsg("���������١��ͧ");
			   modelItem.setErrorCode("DataFailException");
            }
            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
 			   modelItem.setErrorMsg("��辺�����ŷ��� Generate");
 			   modelItem.setErrorCode("DataNotFoundException");
             }
            
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			logger.debug("Transaction commit");
			conn.commit();
			
			/** Update status Head Monitor */
			/** validate data **/
            if(modelItem.getFailCount() >0){
            	monitorModel.setErrorMsg("���������١��ͧ");
            	monitorModel.setErrorCode("DataFailException");
            }
            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
            	monitorModel.setErrorMsg("��辺�����ŷ��� Generate");
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
	
	public  MonitorBean processExportBillICC(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		MonitorTime monitorTime = null;
		try{
			logger.debug("Export Type:"+monitorModel.getTransactionType());

			monitorTime  = new MonitorTime("Export Bill ICC");   
			
			//Process Generate 
			ExportBillICC.runProcess(user,monitorModel);
			
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
			    modelItem.setErrorMsg("Order ����ѹ����к� �ѧ�����ӡ�� Interface Onhand to ICC -twstock");
				modelItem.setErrorCode("OrderFailInterfacetwstockException");
			}else{
			  boolean getExportedG = GenerateOrderExcel.validateExportedFlagG(connMonitor,Utils.isNull(batchParamMap.get(GenerateOrderExcel.PARAM_CUST_GROUP)), Utils.isNull(batchParamMap.get(GenerateOrderExcel.PARAM_TRANS_DATE)));
			  if(getExportedG==true){
				  taskStatusInt = Constants.STATUS_FAIL;
				  modelItem.setErrorMsg("�����ŵ���ѹ��� Order ����к� ���� Generate excel file �����");
				  modelItem.setErrorCode("OrderDuplicationGenExcelException");
			  }
			}
			
			if(taskStatusInt == Constants.STATUS_START){
				//Process Generate 
				modelItem = GenerateOrderExcel.runProcess(user,modelItem,monitorModel.getBatchParamMap());
				
				monitorTime.debugUsedTime();
				
				/** validate data **/
	           /* if(modelItem.getFailCount() >0){
				   modelItem.setErrorMsg("���������١��ͧ");
				   modelItem.setErrorCode("DataFailException");
	            }
	            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
	 			   modelItem.setErrorMsg("��辺�����ŷ��� Generate");
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
	
}
