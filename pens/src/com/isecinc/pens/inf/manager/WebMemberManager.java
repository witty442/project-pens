package com.isecinc.pens.inf.manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ConvertUtils;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.batchwork.BatchImportWorker;
import com.isecinc.pens.inf.manager.process.UpdateSalesProcess;
import com.isecinc.pens.inf.manager.process.WebMemberProcess;

/**
 * @author WITTY
 *
 */
public class WebMemberManager {
   
	protected Logger logger = Logger.getLogger("PENS");
	public static Object IMPORT_Q = new Object();
	public static String  PATH_CONTROL = "inf-config/table-mapping-webmember/";
	public static String  FILE_CONTROL_NAME = "control_import.csv";
	public static String  PATH_IMPORT = EnvProperties.getInstance().getProperty("path.web.member.in");
	public static String  PATH_MEMBER_IN_PROCESSED = EnvProperties.getInstance().getProperty("path.web.member.in.processed");
	public static String  PATH_MEMBER_IN_RESULT = EnvProperties.getInstance().getProperty("path.web.member.in.result");
	

	public MonitorBean importMain(User userLogin,User userRequest,String requestTable,HttpServletRequest request,boolean importAll) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("WM-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_IMPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType(Constants.TRANSACTION_WEB_MEMBER_TYPE);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
			//start Thread
			new BatchImportWorker(monitorModel.getTransactionId(),monitorModel.getMonitorId(),monitorModel.getTransactionType(),userLogin,userRequest, requestTable, request,importAll).start();
			
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
	
	
	public MonitorBean importTxt(User userLogin,User userRequest,String requestTable,HttpServletRequest request,boolean importAll,BigDecimal transactionId) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setTransactionId(transactionId);
			monitorModel.setName("WM-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_IMPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType(Constants.TRANSACTION_WEB_MEMBER_TYPE);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
			monitorModel = importFileToDB(monitorModel.getTransactionId(), monitorModel.getMonitorId(), Constants.TRANSACTION_WEB_MEMBER_TYPE,userLogin, userRequest, requestTable, request,importAll);
		
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
	public  MonitorBean importFileToDB(BigDecimal transactionId ,BigDecimal monitorId,String transType,User userLogin ,User userRequest,String requestTable,HttpServletRequest request,boolean importAll) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap initConfigMap = new LinkedHashMap();
		WebMemberProcess imtProcess = new WebMemberProcess();
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		List monitorItemList = new ArrayList();
		boolean stepBreak = false;
		boolean rollBackFlag = false;
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		int countFileMap = 0;
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
			ftpManager.canConnectFTPServer();
			
			/** Initial Connection ***/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);

			/** init table config to import **/
			countFileMap = ImportHelper.initImportConfig(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,conn,PATH_IMPORT,transType,userRequest,requestTable,importAll);
			
			List fileImportSuccessList = new ArrayList();
			Set s = initConfigMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) { //for 1
				if(stepBreak){
					break;
				}
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) initConfigMap.get(tableName);
				logger.debug("Import TableName:"+tableBean.getTableName());
				
		        if(tableBean.getDataLineList() != null && tableBean.getDataLineList().size() > 0){
		        	/** case have more one file **/
					for(int k =0;k<tableBean.getDataLineList().size();k++){ //for 2
						FTPFileBean ftpBean = (FTPFileBean)tableBean.getDataLineList().get(k);	
						
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
						
						if(ftpBean.getDataLineText() != null && ftpBean.getDataLineText().length >0){
							String[] results = null;
							results = imtProcess.importToDB(conn,initConfigMap,tableBean,ftpBean,userRequest);
							if( !Utils.isNull(results[0]).equals("")){
								//Task ALL Fail
								taskStatusInt = Constants.STATUS_FAIL; //Status Header
								
								modelItem.setStatus(Constants.STATUS_FAIL);
								modelItem.setSuccessCount(ConvertUtils.convertToInt(results[1]));
								modelItem.setErrorMsg(Utils.isNull(results[0]));
								modelItem.setErrorCode(Utils.isNull(results[2]));
							}else{
								//Task Item Success
								modelItem.setStatus(Constants.STATUS_SUCCESS);
								modelItem.setSuccessCount(ConvertUtils.convertToInt(results[1]));
								modelItem.setErrorMsg(Utils.isNull(results[0]));
								modelItem.setErrorCode(Utils.isNull(results[2]));
								
								fileImportSuccessList.add(ftpBean);
							}
							isExc = true;
						}else{
							logger.debug("Ftp File TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
						}
						
						/** verify status by check record import vs line form Text*/
						if(modelItem.getDataCount() == modelItem.getSuccessCount()){
							modelItem.setStatus(Constants.STATUS_SUCCESS);
						}else{
							modelItem.setStatus(Constants.STATUS_FAIL);
							taskStatusInt = Constants.STATUS_FAIL;
						}
						/** Insert Monitor Item */
						modelItem = dao.insertMonitorItem(connMonitor,modelItem);
						monitorItemList.add(modelItem);
					}//for 2
		        }//if	
			}//for 1
			
			logger.debug("isExc:"+isExc+" ,rollBackFlag:"+rollBackFlag);
			
			/** Check Is Execute and No error **/
			if(isExc && rollBackFlag == false){
				logger.debug("Transaction commit");
				conn.commit();
				
				/** Case Transaction Import By User_id by FileName  and after import move File to  Sales-In-Processed */
				logger.info("Move File To Member-In-Processed");
				ftpManager.moveFileFTP_NEW(PATH_IMPORT,PATH_MEMBER_IN_PROCESSED, fileImportSuccessList);
				
				/** End process ***/
				logger.debug("Update Monitor to Success:"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(transType);
				dao.updateMonitor(connMonitor,monitorModel);
				
			}else if(isExc && rollBackFlag == true){
				logger.debug("Transaction Rolback");
				conn.rollback();
				
				logger.debug("Update Monitor to Fail :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(transType);
				monitorModel.setErrorCode("WebMemberException");
				dao.updateMonitor(connMonitor,monitorModel);
			}else{
				logger.debug("Transaction commit");
				conn.commit();
				
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
			    ftpManager.writeFileToFTP( PATH_MEMBER_IN_RESULT, initConfigMap,userLogin);
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
