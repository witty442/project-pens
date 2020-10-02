package com.isecinc.pens.inf.manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.ExternalFunctionHelper;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.batchwork.BatchImportWorker;
import com.isecinc.pens.inf.manager.process.ResultImportBean;
import com.isecinc.pens.inf.manager.process.UpdateSalesProcess;
import com.isecinc.pens.inf.manager.process.bean.FileImportTransBean;
import com.isecinc.pens.inf.manager.process.bean.KeyNoImportTransBean;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptHelper;
import com.pens.util.EnvProperties;

/**
 * @author WITTY
 *
 */
public class UpdateSalesManager {
   
	public static Logger logger = Logger.getLogger("PENS");
	//public static LoggerUtils logger = new LoggerUtils("UpdateSalesManager");
	
	public static Object IMPORT_Q = new Object();
	public static String  PATH_CONTROL = "inf-config/table-mapping-transaction/";
	public static String  FILE_CONTROL_NAME = "control_transaction.csv";
	public static String  PATH_IMPORT = EnvProperties.getInstance().getProperty("path.transaction.sales.in");
	
    public static boolean debug = true;
    
	public MonitorBean importMain(User userLogin,User userRequest,String requestTable,HttpServletRequest request,boolean importAll) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("UST-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_IMPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType(Constants.TRANSACTION_UTS_TRANS_TYPE);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
				
			//start Thread
			new BatchImportWorker(monitorModel.getTransactionId(),monitorModel.getMonitorId(),monitorModel.getTransactionType(),userLogin,userRequest, requestTable,importAll).start();
			
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
	 * t_order , t_visit ,t_order_orcl
       t_receipt , t_receipt_orcl ,t_bill_plan ,t_adjust
	 */
	public  MonitorBean importFileToDB(BigDecimal transactionId ,BigDecimal monitorId,String transType,User userLogin ,User userRequest,String requestTable,boolean importAll) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = new LinkedHashMap<String,TableBean>();
		UpdateSalesProcess imtProcess = new UpdateSalesProcess();
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		List<MonitorItemBean> monitorItemList = new ArrayList<MonitorItemBean>();
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		int countFileMap = 0;
		UpdateSalesManagerHelper helper = new UpdateSalesManagerHelper();
		ResultImportBean resultImportBean = null;
		List<FileImportTransBean> dataFileList = null;//FileImportTransBean
		List<KeyNoImportTransBean> dataKeyNoList = null;
		List<FileImportTransBean> fileImportSuccessList = new ArrayList<FileImportTransBean>();
		List<FileImportTransBean> fileImportErrorList = new ArrayList<FileImportTransBean>();
		Map<String,String> fileImportSuccessMap = new HashMap<String,String> ();
		Map<String,String> fileImportErrorMap = new HashMap<String,String> ();
		Map<String,String> receiptNoMap = new HashMap<String, String>();
		int dataCount = 0;
		int successCount = 0;
		String errorMsg="";
		String errorCode = "";
		String receiptNoAll = "";
		try{
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			
			logger.debug("importFile Type:"+transType);
			/** Initial Monitor ***/
			monitorModel.setTransactionId(transactionId);
			monitorModel.setMonitorId(monitorId);
			monitorModel.setTransactionType(transType);
			
			/** Check Status FTP Server Alive  if Can't Connection Throw Exception*/
			if(!Constants.TRANSACTION_REUTS_TRANS_TYPE.equalsIgnoreCase(transType)){
			   ftpManager.canConnectFTPServer();
			}
			
			/** initial table config to import **/
			if(Constants.TRANSACTION_REUTS_TRANS_TYPE.equalsIgnoreCase(transType)){
			   /** No Download File From FTP Case Re Import**/
			   ImportHelper.initImportConfigCaseReImportError(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,conn,PATH_IMPORT,transType,userRequest,requestTable,importAll);
			}else{
			   /** Case Normal Download file from ftp server **/
			   ImportHelper.initImportConfig(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,connMonitor,PATH_IMPORT,transType,userRequest,requestTable,importAll);
			  
			   /**  Insert Data FTP File To t_import_temp_trans **/
			   helper.insertDataFileToTemp(connMonitor,initConfigMap);
			}
			
			/** Initial Connection Transaction ***/
			conn = DBConnection.getInstance().getConnection();
			
			//Reset RECEIPT_MAP 
	    	ExternalFunctionHelper.RECEIPT_MAP.clear();
	    	ImportReceiptFunction.RECEIPT_BATCH_MAP.clear();
	    	ImportReceiptFunction.RECEIPT_HEAD_MAP.clear();
	    	ImportReceiptFunction.RECEIPT_LINE_MAP.clear();
	    	
			Set s = initConfigMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) { //for 1
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) initConfigMap.get(tableName);
				logger.info("---Import TableName:"+tableBean.getTableName()+"---");
				logger.debug("Get Data File By Table Name");
				dataFileList = helper.getDataFileFromTempListByTable(connMonitor, transType,tableName);	
				
		        if(dataFileList != null && dataFileList.size() > 0){
		        	logger.debug("dataFileList Size["+dataFileList.size()+"]");
		        	/** Loop By file name **/
					for(int k =0;k<dataFileList.size();k++){ //for 2
						dataCount = 0;
						successCount = 0;
						countFileMap++;
						FileImportTransBean ftpBean = (FileImportTransBean)dataFileList.get(k);	
						logger.debug("Start File Name:"+ftpBean.getFileName());
						/** insert to monitor_item **/
						logger.debug("Insert Monitor Item  TableName:"+tableBean.getTableName());
						MonitorItemBean modelItem = new MonitorItemBean();
						modelItem.setMonitorId(monitorModel.getMonitorId());
						modelItem.setSource(tableBean.getSource());
						modelItem.setDestination(tableBean.getDestination());
						modelItem.setTableName(tableBean.getTableName());
						modelItem.setFileName(ftpBean.getFileName());
						modelItem.setStatus(Constants.STATUS_START);
						modelItem.setDataCount(ftpBean.getFileCount());
						modelItem.setFileSize(ftpBean.getFileSize());
						modelItem.setSubmitDate(new Date());
						
						/** Loop by KeyNo for Control Transaction **/
						dataKeyNoList = ftpBean.getKeyNoImportTransList();
						Savepoint savepoint = null;
						if(dataKeyNoList != null && dataKeyNoList.size() >0){
							for(int m=0;m<dataKeyNoList.size();m++){
								KeyNoImportTransBean keyNoBean = dataKeyNoList.get(m);
								//debug
								 logger.debug("keyNoBean:"+keyNoBean.getKeyNo());
								 logger.debug("keyNoBean LineList Size:"+keyNoBean.getLineList().size());
								
								//Set RollBack Point
								conn.setAutoCommit(false);
								//Savepoint savepoint =  conn.setSavepoint(keyNoBean.getKeyNo());
								savepoint =  conn.setSavepoint();
								
								resultImportBean = null;
								resultImportBean = imtProcess.importToDB(transactionId,connMonitor,conn,initConfigMap,tableBean,keyNoBean,userRequest);
								 
								logger.debug("keyNo["+keyNoBean.getKeyNo()+"]result["+Utils.isNull(resultImportBean.getFirstErrorMsg())+"]");
								
								if( !Utils.isNull(resultImportBean.getFirstErrorMsg()).equals("")){
									//Task ALL Fail
									taskStatusInt = Constants.STATUS_FAIL; //Status Header
							        errorMsg = Utils.isNull(resultImportBean.getFirstErrorMsg());
									errorCode = Utils.isNull(resultImportBean.getFirstErrorCode());
									successCount +=resultImportBean.getSuccessRow();
									dataCount +=resultImportBean.getAllRow();
									
									logger.debug("Transaction Rollback By savepoint["+savepoint+"]");
									logger.debug("KeyNo["+keyNoBean.getKeyNo()+"]dataCount["+resultImportBean.getAllRow()+"]successCount["+resultImportBean.getSuccessRow()+"]");
									conn.rollback();
									
									if(Constants.TRANSACTION_REUTS_TRANS_TYPE.equalsIgnoreCase(transType)){
										//Case ReImport Fail: No delete t_temp_import_trans_error
									}else{
									   //Case Error Move data to t_temp_import_trans_error
									   helper.moveToTempImportErrorByKey(connMonitor, keyNoBean.getFileName(),keyNoBean.getTableName(), keyNoBean.getKeyNo());
									   //Delete t_temp_import_trans
									   logger.debug("delete t_temp_import_trans keyNo["+keyNoBean.getKeyNo()+"] Case table["+keyNoBean.getTableName()+"]");
									   helper.deleteTempImportByKey(connMonitor, keyNoBean.getFileName(),keyNoBean.getTableName(), keyNoBean.getKeyNo());
									}
								}else{
									//Task Item Success
									taskStatusInt = Constants.STATUS_SUCCESS;
									errorMsg = Utils.isNull(resultImportBean.getFirstErrorMsg());
								    errorCode = Utils.isNull(resultImportBean.getFirstErrorCode());
									successCount +=resultImportBean.getSuccessRow();
									dataCount +=resultImportBean.getAllRow();
								
									logger.debug("Transaction Commit");
									logger.debug("KeyNo["+keyNoBean.getKeyNo()+"]dataCount["+resultImportBean.getAllRow()+"]successCount["+resultImportBean.getSuccessRow()+"]");
									conn.commit();
									
									if(Constants.TRANSACTION_REUTS_TRANS_TYPE.equalsIgnoreCase(transType)){
									   /**** Clear data t_temp_import_trans_err By KeyNo ********************************/
									   logger.debug("Start delete t_temp_import_trans_err key_no:"+keyNoBean.getKeyNo());
									   helper.deleteTempImportErrByKey(connMonitor, keyNoBean.getFileName(),keyNoBean.getTableName(), keyNoBean.getKeyNo());
									}else{
										/**** Clear data t_temp_import_trans By KeyNo ********************************/
										logger.debug("Start delete t_temp_import_trans key_no:"+keyNoBean.getKeyNo());
										helper.deleteTempImportByKey(connMonitor, keyNoBean.getFileName(),keyNoBean.getTableName(), keyNoBean.getKeyNo());
									}
									
									/** Case Table t_receipt : Case cancel cheque 1 to do calc ReceiptAmount from Receipt line**/
									if(keyNoBean.getTableName().toLowerCase().startsWith("t_receipt")){
										receiptNoAll += Utils.isNull(resultImportBean.getReceiptNoAll());
									}
								}
								isExc = true;
							}//for line_str by key_no
						}else{
							logger.debug("Ftp File TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
						}
						
						logger.debug("By fileName DataCount["+dataCount+"]");
						logger.debug("By fileName SuccessCount["+successCount+"]");
						
						/** verify status by check record import vs line form Text*/
						if(dataCount == successCount){
							modelItem.setStatus(Constants.STATUS_SUCCESS);
							
							if(fileImportSuccessMap.get(ftpBean.getFileName()) == null){
							   fileImportSuccessList.add(ftpBean);
							}
						}else{
							modelItem.setStatus(Constants.STATUS_FAIL);
							taskStatusInt = Constants.STATUS_FAIL;
							/** Check Duplicate file */
							if(fileImportErrorMap.get(ftpBean.getFileName()) == null){
							   fileImportErrorList.add(ftpBean);
							}
						}
						
						modelItem.setStatus(taskStatusInt);
						modelItem.setDataCount(dataCount);
						modelItem.setSuccessCount(successCount);
						modelItem.setErrorMsg(errorMsg);
						modelItem.setErrorCode(errorCode);
		
						/** Insert Monitor Item */
						modelItem = dao.insertMonitorItem(connMonitor,modelItem);
						monitorItemList.add(modelItem);
					}//for (By file)2
		        }//if	
			}//for 1
			
			/**** Case Receipt Cancel i line must calc sum line to Head **************/
			/** Clear receptNo dup **/
			if( Utils.isNull(receiptNoAll).length() >0){
				String[] receiptNoAllArr = receiptNoAll.split("\\|");
				for(int r=0;r<receiptNoAllArr.length;r++){
					if( !Utils.isNull(receiptNoAllArr[r]).equals("")){
			            receiptNoMap.put(receiptNoAllArr[r], receiptNoAllArr[r]);
					}
				}
			}
			/** Calc Amount Receipt **/
			/** Delete receipt line wrong data **/
			  if( !receiptNoMap.isEmpty()){
				  ImportReceiptHelper.recalcReceiptHead(receiptNoMap);
			  }//if
			/*************************************************************************/
	
			logger.debug("isExc:"+isExc);
			
			/** Check Is Execute and No error **/
			if(isExc){
				/** Case Transaction Import By User_id by FileName  and after import move File to  Sales-In-Processed */
				if(fileImportSuccessList != null && fileImportSuccessList.size() >0){
					/** Case ReImport No Mover File **/
					if( !Constants.TRANSACTION_REUTS_TRANS_TYPE.equalsIgnoreCase(transType)){
				       logger.info("Move File To Sales-In-Processed");
				       ftpManager.moveFileFTP(env.getProperty("path.transaction.sales.in"), env.getProperty("path.transaction.sales.in.processed"), fileImportSuccessList);
					}
				}
				
				/** Case Import File Error Move To Sales-In-Error */
				if(fileImportErrorList != null && fileImportErrorList.size() >0){
					/** Case ReImport No Mover File **/
					if( !Constants.TRANSACTION_REUTS_TRANS_TYPE.equalsIgnoreCase(transType)){
				      logger.info("Move File To Sales-In-Processed");
				      ftpManager.moveFileFTP(env.getProperty("path.transaction.sales.in"), env.getProperty("path.transaction.sales.in.error"), fileImportErrorList);
					}
				}
				
				/** End process ***/
				logger.debug("Update Monitor to Success:"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(transType);
				dao.updateMonitor(connMonitor,monitorModel);
				
			}else{
				
				/** End process ***/
				logger.debug("Update Monitor to Success :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(transType);
				dao.updateMonitor(connMonitor,monitorModel);
			}
			
			/** Optional Step Write Result Logs Process By File Name **/
			if("true".equals(EnvProperties.getInstance().getProperty("ftp.export.logs.result.enable"))){
				logger.info("**** Step Write Log Result In Ftp Server ******");
			    //ftpManager.uploadFileToFTP( EnvProperties.getInstance().getProperty("path.transaction.sales.in.result"), initConfigMap,userLogin);
			    
			}

			monitorModel.setMonitorItemList(monitorItemList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			
			/** End process ***/
			logger.debug("Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setFileCount(countFileMap);
			monitorModel.setTransactionType(transType);
			monitorModel.setErrorMsg(e.getMessage());
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			dao.updateMonitor(connMonitor,monitorModel);

			if(conn != null){
				logger.debug("Transaction Rolback");
				conn.rollback();
			}
		}finally{
			//Clear RECEIPT_MAP 
	    	ExternalFunctionHelper.RECEIPT_MAP.clear();
	    	ImportReceiptFunction.RECEIPT_BATCH_MAP.clear();
	    	ImportReceiptFunction.RECEIPT_HEAD_MAP.clear();
	    	ImportReceiptFunction.RECEIPT_LINE_MAP.clear();
	    	
			if(initConfigMap != null){
				initConfigMap.clear();
				initConfigMap = null;
			}
			if(conn != null){
				conn.close();
				conn =null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
			logger.debug("initConfigMap:"+initConfigMap);
			logger.debug("conn:"+conn);
		}
		return monitorModel;
	}
	

}
