package com.isecinc.pens.inf.manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.MonitorItemDetailBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.ExportSQL;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.batchwork.BatchExportWorker;
import com.isecinc.pens.inf.manager.process.ExportProcess;

/**
 * @author WITTY
 *
 */
public class ExportManager {

	public static Logger logger = Logger.getLogger("PENS");
	public static String PATH_CONTROL ="inf-config/table-mapping-export/";
	public static String FILE_CONTROL_NAME ="control_export.csv";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public  MonitorBean exportMain(User userLogin,User userRequest ,String requestTable,String transType,HttpServletRequest request) throws Exception{
		if(requestTable != null && !Utils.isNull(requestTable).equals("")){
			return exportByRequestTable(userLogin,userRequest, requestTable, request,transType);
		}
		return export(userLogin, userLogin,requestTable, request,Constants.TRANSACTION_MASTER_TYPE);
	}
	/**
	 * exportMain
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean exportByRequestTable(User userLogin,User userRequest,String requestTable,HttpServletRequest request,String transType) throws Exception{
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			/** insert to monitor_interface **/
			MonitorBean monitorModel = new MonitorBean();
			monitorModel.setName("EXP-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_EXPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType(transType);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
			new BatchExportWorker(monitorModel.getTransactionId(), monitorModel.getMonitorId(), monitorModel.getTransactionType(),userLogin, userRequest, requestTable, request).start();
			
			return monitorModel;
		}catch(Exception e){
			throw e;
		}finally{
			if(connMonitor != null){
				connMonitor.close();
				connMonitor =null;
			}
		}
	}
	
	/**
	 * exportMain
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean export(User userLogin,User userRequest,String requestTable,HttpServletRequest request,String transType) throws Exception{
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			/** Export Master**/
			MonitorBean monitorModel = new MonitorBean();
			monitorModel.setName("EXP-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_EXPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType(transType);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
			new BatchExportWorker(monitorModel.getTransactionId(), monitorModel.getMonitorId(), monitorModel.getTransactionType(), userLogin,userRequest, requestTable, request).start();
			
			return monitorModel;
		}catch(Exception e){
			throw e;
		}finally{
			if(connMonitor != null){
				connMonitor.close();
				connMonitor =null;
			}
		}
	}
	/**
	 * exportTransaction
	 * @param userBean
	 * @param transType
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean exportTransaction(BigDecimal transactionId ,User userLogin,User userRequest,String requestTable,HttpServletRequest request) throws Exception{
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			/** insert to monitor_interface **/
			MonitorBean monitorModel = new MonitorBean();
			monitorModel.setName("EXP-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_EXPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType(Constants.TRANSACTION_TRANS_TYPE);
			monitorModel.setTransactionId(transactionId);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
			monitorModel = exportToTxt(monitorModel.getTransactionId() ,monitorModel.getMonitorId(),monitorModel.getTransactionType(), userLogin,userRequest, requestTable, request);
			
			return monitorModel;
		}catch(Exception e){
			throw e;
		}finally{
			if(connMonitor != null){
				connMonitor.close();
				connMonitor =null;
			}
		}
	}
	
	/**
	 * 
	 * @param transactionId
	 * @param monitorId
	 * @param transactionType
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean exportToTxt(BigDecimal transactionId,BigDecimal monitorId, String transactionType,User userLogin,User userRequest,String requestTable,HttpServletRequest request) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = null;
		MonitorBean monitorModel = new MonitorBean();
		InterfaceDAO dao = new InterfaceDAO();
		EnvProperties env = EnvProperties.getInstance();
		boolean isExc = false;
		ExportProcess exProcess = new ExportProcess();
		int fileCount = 0;
		InterfaceDAO infDAO = new InterfaceDAO();
		String fileSize = "0 bytes";
		int statusTaskAll = Constants.STATUS_SUCCESS;
		FTPManager ftpManager = null;
		try{
			//initail FTP Manager
			ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			monitorModel.setTransactionId(transactionId);
			monitorModel.setMonitorId(monitorId);
			monitorModel.setTransactionType(transactionType);
			
			/** Check Status FTP Server Alive  if Can't Connection Throw Exception*/
			ftpManager.canConnectFTPServer();
			
			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

			/** init table config to import **/
			initConfigMap = ExportHelper.initExportConfig(PATH_CONTROL,FILE_CONTROL_NAME,conn,transactionType,userRequest,requestTable);

			Set s = initConfigMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) {
				String fileFtpName = (String) it.next();
				TableBean tableBean = (TableBean) initConfigMap.get(fileFtpName);
				logger.debug("-Export TableName:"+tableBean.getTableName());
				MonitorItemBean modelItem = new MonitorItemBean();
				MonitorItemDetailBean[] modelDetailItem = null;
                try{
                	
					if(tableBean.getTableName().equalsIgnoreCase("pensbme_barcode_scan")){
						logger.debug("--Start Export BARCODE_SCAN --");
						/** Count Record and Prepare Monitor_item_detail(Data Export)  */
						modelDetailItem = infDAO.prepareMonitorItemDetail(conn,tableBean.getPrepareSqlSelect(), tableBean.getTableName());	
					    /** Check Data Found Before Export **/
						if(modelDetailItem != null && modelDetailItem.length > 0){
						   tableBean = exProcess.exportBarcodeScan(conn,tableBean,userRequest);	
						}
						
						logger.debug("--End Export BARCODE_SCAN --");
			
					}else{
						
						logger.debug("--Start Export "+tableBean.getTableName()+"--");
						// other case no insert monitor_item_detail ->Authen
					    tableBean = exProcess.exportDataDB(conn,tableBean,userRequest); 
					    logger.debug("--End Export "+tableBean.getTableName()+"--");
					}
					
					modelItem.setStatus(Constants.STATUS_SUCCESS);
                }catch(Exception e){
                	logger.error(e.getMessage(),e);
                	modelItem.setStatus(Constants.STATUS_FAIL);
                	modelItem.setErrorMsg(e.getMessage());
                	modelItem.setErrorCode(ExceptionHandle.getExceptionCode(e));
                	
                	/** SET Task Fail In Header **/
                	statusTaskAll = Constants.STATUS_FAIL;
                }
			
                initConfigMap.put(fileFtpName, tableBean);
                
                /** Case More File Per Table **/
                if(tableBean.getFileExportList() != null && tableBean.getFileExportList().size() > 0){
                	fileCount = 0;
                	for(int f=0;f<tableBean.getFileExportList().size();f++){
                		TableBean fileExportBean =(TableBean)tableBean.getFileExportList().get(f);
                		/** DataString is null no insert monitor_item **/
                		if( fileExportBean.getDataStrExport()!= null && !Utils.isNull(fileExportBean.getDataStrExport().toString()).equals("")){ 
	                		fileCount++;
	                		 /** get Size of File  **/
	    	                fileSize = "0 bytes";
	    	                if(fileExportBean.getDataStrExport() != null){
	    	                   fileSize = FileUtil.getFileSize(fileExportBean.getDataStrExport().toString());
	    	                }
	    					/** insert to monitor_item **/
	    					logger.debug("-Start Insert Monitor Item  TableName:"+fileExportBean.getTableName());
	    					modelItem.setMonitorId(monitorModel.getMonitorId());
	    					modelItem.setId(null);//running new
	    					modelItem.setSource(fileExportBean.getSource());
	    					modelItem.setDestination(fileExportBean.getDestination());
	    					modelItem.setTableName(fileExportBean.getTableName());
	    					modelItem.setFileName(fileExportBean.getFileFtpNameFull());
	    					modelItem.setSubmitDate(new Date());
	    					modelItem.setFileSize(fileSize);
	    					/** Set Rows Count Of Text **/
	    					if(modelDetailItem != null && modelDetailItem.length >0){
	    					    modelItem.setDataCount(modelDetailItem.length);
	    					}else{
	    						modelItem.setDataCount(fileExportBean.getExportCount());
	    					}
	    					/** Insert Monitor_item  ********/
	    					modelItem = dao.insertMonitorItem(connMonitor,modelItem);
                		}
                	}//for
                	
                /** Case one file Export per Table   
                 *  DataString is null no insert monitor_item 
                 * **/
                }else if( tableBean.getDataStrExport() != null && !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){ 
	                /** get Size of File  **/
                	fileCount++;
	                fileSize = "0 bytes";
	                if(tableBean.getDataStrExport() != null){
	                   fileSize = FileUtil.getFileSize(tableBean.getDataStrExport().toString());
	                }
					/** insert to monitor_item **/
					logger.debug("-Start Insert Monitor Item  TableName:"+tableBean.getTableName());
					modelItem.setMonitorId(monitorModel.getMonitorId());
					modelItem.setSource(tableBean.getSource());
					modelItem.setDestination(tableBean.getDestination());
					modelItem.setTableName(tableBean.getTableName());
					modelItem.setFileName(tableBean.getFileFtpNameFull());
					modelItem.setSubmitDate(new Date());
					modelItem.setFileSize(fileSize);
					/** Set Rows Count Of Text **/
					if(modelDetailItem != null && modelDetailItem.length >0){
					    modelItem.setDataCount(modelDetailItem.length);
					}else{
						modelItem.setDataCount(tableBean.getExportCount());
					}
					/** Insert Monitor_item  ********/
					modelItem = dao.insertMonitorItem(connMonitor,modelItem);
                }
                
				/** Insert To Monitor_Item_Detail ***/
				if(modelDetailItem != null && modelDetailItem.length > 0){
					logger.debug("-modelDetailItem Size:"+modelDetailItem.length);
					dao.insertMonitorItemDetail(connMonitor, modelDetailItem);
				}
				
				/** Update Exported  Flag  ********/
				if(tableBean.getSqlUpdateExportFlagList() != null &&  tableBean.getSqlUpdateExportFlagList().size() > 0){
					logger.debug("***** -Start Update Interfaces "+tableBean.getTableName()+" Flag *************");
				    int updateRecord = exProcess.updateExportFlag(conn, userRequest,tableBean);
				    logger.debug("***** -Result Update Interfaces "+tableBean.getTableName()+"  Flag "+updateRecord+" *************");
				}

				isExc = true;
			}//for
			
			logger.info("Step Upload ALL File To FTP Server");
			ftpManager.uploadAllFileToFTP_OPT2(initConfigMap, "");

			logger.info("Step Success Transaction Commit");
			conn.commit();
			
			/** End process ***/
			logger.debug("-Update Monitor to Success");
			monitorModel.setStatus(statusTaskAll);
			monitorModel.setSubmitDate(new Date());
			monitorModel.setFileCount(fileCount);
			dao.updateMonitor(connMonitor,monitorModel);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			if(conn != null){
			   logger.debug("Error:Step Transaction Rollback");
			   conn.rollback();
			   
			   logger.debug("Error:Step delete file in FTP Case Roolback ");
			   ftpManager.deleteAllFileInFTPCaseRollback(initConfigMap, "");
			   
			}
			/** End process ***/
			logger.debug("-Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
			monitorModel.setSubmitDate(new Date());
			monitorModel.setFileCount(fileCount);
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			monitorModel.setErrorMsg(e.getMessage());
			
			dao.updateMonitorCaseError(connMonitor,monitorModel);
			
			//clear Task running for next run
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT);
            
			logger.error(e.getMessage(),e);
		}finally{
			if(initConfigMap != null){
				initConfigMap.clear();
				initConfigMap = null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor =null;
			}
			if(conn != null){
				conn.close();
				conn =null;
			}
		}
		return monitorModel;
	}
}
