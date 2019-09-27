package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.FTPFileBean;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.InterfaceHelper;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.manager.FTPManager;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;

public class ImportBillICC extends InterfaceUtils{
	private static Logger logger = Logger.getLogger("PENS");
	public final static String PARAM_TRANS_DATE = "TRANS_DATE";//Budish Date
	public static String PATH_CONTROL ="inf-config/table-mapping-import-icc/";
	public static String FILE_CONTROL_NAME ="control_import.csv";
	
	public static void runProcess(User user,MonitorBean m) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = new LinkedHashMap<String,TableBean>();
		ImportICCProcess imtICCProcess = new ImportICCProcess();
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		boolean stepBreak = false;
		boolean step2Break = false;
		boolean rollBackFlag = false;
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		String patheImport = "";
		int countFileMap = 0;
		List<FTPFileBean> fileTransImportSuccessList = new ArrayList<FTPFileBean> ();
		List<FTPFileBean> fileTransImportErrorList = new ArrayList<FTPFileBean> ();
		List<FTPFileBean> fileMasterImportSuccessList = new ArrayList<FTPFileBean>();
		MonitorTime monitorTime = null;
		Map<String, String> batchParamMap = new HashMap<String, String>();
		try{
			/** prepare Parameter **/
			batchParamMap = m.getBatchParamMap();
			patheImport = env.getProperty("path.icc.hisher.import.dlyr");
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			monitorModel.setTransactionId(m.getTransactionId());
			monitorModel.setMonitorId(m.getMonitorId());
			monitorModel.setTransactionType(m.getTransactionType());
			
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.icc.ip.server"), env.getProperty("ftp.icc.username"), env.getProperty("ftp.icc.password"));
			ftpManager.canConnectFTPServer();
			
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			monitorTime = new MonitorTime("initImportConfig");
			InterfaceHelper.initImportConfigICC(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,conn,patheImport,user,Utils.isNull(batchParamMap.get(PARAM_TRANS_DATE)));
			monitorTime.debugUsedTime();
			
			/**Validate Duplicate File**/
			taskStatusInt = imtICCProcess.validateFileImport(connMonitor, monitorModel, initConfigMap, Utils.isNull(batchParamMap.get(PARAM_TRANS_DATE)));
			
			if(taskStatusInt == Constants.STATUS_FAIL){
				monitorModel.setErrorCode("DuplicateImportFileException");
				monitorModel.setErrorMsg("äÁèÊÒÁÒÃ¶ import file ¹Õéä´éà¹×èÍ§¨Ò¡ ÁÕ¡ÒÃ importä»áÅéÇ");
			}
			
			if(taskStatusInt != Constants.STATUS_FAIL){
				monitorTime = new MonitorTime("initImportConfig>>Download file from Ftp Server");
				/** Load File From FTP Server To Table Map By Table**/
			    countFileMap = ftpManager.downloadFileMappingToTableICC(user,initConfigMap,patheImport,Utils.isNull(batchParamMap.get(PARAM_TRANS_DATE)));
		        monitorTime.debugUsedTime();
			}
		
			if(taskStatusInt != Constants.STATUS_FAIL){
				monitorTime = new MonitorTime("Split Text To DB");
				
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
							if(step2Break){
								break;
							}
							/** insert to monitor_item **/
							logger.debug("Insert Monitor Item  TableName:"+tableBean.getTableName());
							MonitorItemBean modelItem = new MonitorItemBean();
							modelItem.setMonitorId(monitorModel.getMonitorId());
							modelItem.setSource(tableBean.getSource());
							modelItem.setDestination(tableBean.getDestination());
							modelItem.setTableName(tableBean.getTableName());
							modelItem.setFileName(ftpBean.getFileName());
							modelItem.setStatus(Constants.STATUS_START);
							modelItem.setFileSize(ftpBean.getFileSize());
							modelItem.setSubmitDate(new Date());
							
							if( ftpBean.getDataLineText() != null && ftpBean.getDataLineText().length >0){
								String[] results = null;
								
								 //Transaction RollBack By Record
								results = imtICCProcess.importToDB(conn,initConfigMap,tableBean,ftpBean,user);
								logger.debug("Utils.isNull(results[0]):"+Utils.isNull(results[0]));
								
								if( !Utils.isNull(results[0]).equals("")){
									//Task ALL False
									rollBackFlag = true;
									taskStatusInt = Constants.STATUS_FAIL;
									modelItem.setStatus(Constants.STATUS_FAIL);
									fileTransImportErrorList.add(ftpBean);//Add For Delete file and Move to In Processed
								}else{
								
									fileTransImportSuccessList.add(ftpBean);//Add For Delete file and Move to In Processed
									modelItem.setStatus(Constants.STATUS_SUCCESS);
								
								}
								
								/** Update Monitor Item To Success **/
								modelItem.setDataCount(Utils.convertStrToInt(results[3]));
								modelItem.setSuccessCount(Utils.convertToInt(results[1]));
								modelItem.setErrorMsg(Utils.isNull(results[0]));
								modelItem.setErrorCode(Utils.isNull(results[2]));
								
								isExc = true;
								
								/** verify status */
								if(modelItem.getDataCount() == modelItem.getSuccessCount()){
									modelItem.setStatus(Constants.STATUS_SUCCESS);
								}else{
									modelItem.setStatus(Constants.STATUS_FAIL);
									taskStatusInt = Constants.STATUS_FAIL;
								}
								
							}else{
								logger.debug("TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
								taskStatusInt = Constants.STATUS_FAIL;
								monitorModel.setErrorCode("FileNotFoundException");
								monitorModel.setErrorMsg("äÁè¾ºä¿Åì");
							}
							modelItem = dao.insertMonitorItem(connMonitor,modelItem);
						    
						}//for 2
			        }else{
			        	logger.debug("TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
						taskStatusInt = Constants.STATUS_FAIL;
						monitorModel.setErrorCode("FileNotFoundException");
						monitorModel.setErrorMsg("äÁè¾ºä¿Åì");
						
			        }//if 1
				}//for 1
				monitorTime.debugUsedTime();	
			}//if

			logger.debug("isExc:"+isExc+" ,rollBackFlag:"+rollBackFlag);
			
			/** Check Is Excute and No error **/
			if(isExc && rollBackFlag == false){
				logger.debug("Transaction commit");
				conn.commit();
				
				if(fileTransImportSuccessList != null && fileTransImportSuccessList.size() >0){
					for(int i=0;i<fileTransImportSuccessList.size();i++){
						FTPFileBean ftpB = fileTransImportSuccessList.get(i);
						logger.debug("Ftp File Name:"+ftpB.getFileName());
						
						//Backup file to DD Server
						String ddServerPath = env.getProperty("path.backup.icc.hisher.import.dlyr")+"/"+ftpB.getFileName();
					    logger.debug("Backup Text File:"+ddServerPath);
					    
					    //FileUtil.writeFile(ddServerPath, ftpB.getDataLineText().toString(), "TIS-620");
					    FileUtil.writeFile(ddServerPath, Arrays.toString(ftpB.getDataLineText()), "TIS-620");
					    
						/** Case Transaction and Cust,Address,Contact Import By sale code by FileName  and after import move File to  Sales-In-Processed */
						logger.debug("Delete File After import:");
						
					    ftpManager.deleteFileFTPByFileName(env.getProperty("path.icc.hisher.import.dlyr"),ftpB.getFileName());
					}
				}
			    
				/** End process ***/
				logger.debug("Update Monitor to Success:"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				dao.updateMonitor(connMonitor,monitorModel);
				
				//monitorTime.debugUsedTime();
				
			}else if(isExc && rollBackFlag == true){
				logger.debug("Transaction Rolback");
				conn.rollback();
				
				logger.debug("Update Monitor to Fail :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				monitorModel.setErrorCode("BMEException");
				
				dao.updateMonitor(connMonitor,monitorModel);
			}else{
				logger.debug("Transaction commit");
				conn.commit();
				
				/** End process ***/
				logger.debug("Update Monitor to Success :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				
				dao.updateMonitor(connMonitor,monitorModel);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			
			/** End process ***/
			logger.debug("Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
			monitorModel.setFileCount(countFileMap);
			monitorModel.setTransactionType(m.getTransactionType());
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			
			dao.updateMonitorCaseError(connMonitor,monitorModel);

			//clear Task running for next run
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_BILL_ICC);
			
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
				conn.setAutoCommit(true);
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
	}
	
}
