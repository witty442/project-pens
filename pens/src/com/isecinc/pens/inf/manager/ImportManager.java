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

import meter.MonitorTime;

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
import com.isecinc.pens.inf.manager.process.ImportProcess;

/**
 * @author WITTY
 *
 */
public class ImportManager {
   
	public static Logger logger = Logger.getLogger("PENS");
	public static Object IMPORT_Q = new Object();
	public static String PATH_CONTROL ="inf-config/table-mapping-import/";
	public static String FILE_CONTROL_NAME ="control_import.csv";

	public MonitorBean importMain(User userLogin,User userRequest,String requestTable,HttpServletRequest request,boolean importAll,String requestTableTransType) throws Exception{
		if(requestTable != null && !requestTable.equals("")){
			return importByRequestTable(userLogin,userRequest, requestTable, request,importAll,requestTableTransType);
		}
		return importTxt(userLogin,userLogin, request,importAll);
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
	public MonitorBean importByRequestTable(User userLogin,User userRequest,String requestTable,HttpServletRequest request,boolean importAll,String transType) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("INF-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_IMPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
	        monitorModel.setTransactionType(transType);
		    
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
	/**
	 * 
	 * @param transType
	 * @param userBean
	 * @param requestTable
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public MonitorBean importTxt(User userLogin,User userRequest,HttpServletRequest request ,boolean importAll) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("UTS-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_IMPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType(Constants.TRANSACTION_UTS_TRANS_TYPE);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
		    //start Thread
			new BatchImportWorker(monitorModel.getTransactionId(),monitorModel.getMonitorId(),monitorModel.getTransactionType(),userLogin,userRequest, null, request,importAll).start();
			
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
	public MonitorBean importTxtByTransType(BigDecimal transactionId,String transType,User userLogin ,User userRequest,String requestTable,HttpServletRequest request,boolean importAll) throws Exception{
		Connection connMonitor = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("INF-"+request.getRemoteAddr()+"-"+request.getRemotePort());
			monitorModel.setType(Constants.TYPE_IMPORT);
			monitorModel.setStatus(Constants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType(transType);
			monitorModel.setTransactionId(transactionId);
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
			
			monitorModel = importFileToDB(monitorModel.getTransactionId(),monitorModel.getMonitorId(), monitorModel.getTransactionType(),userLogin, userRequest, requestTable, request,importAll);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(connMonitor != null){
				connMonitor.close();connMonitor=null;
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
	public  MonitorBean importFileToDB(BigDecimal transactionId ,BigDecimal monitorId,String transType,User userLogin,User userRequest,String requestTable,HttpServletRequest request,boolean importAll) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = new LinkedHashMap<String,TableBean>();
		ImportProcess imtProcess = new ImportProcess();
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
		try{
			logger.debug("importFile Type:"+transType);
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			monitorModel.setTransactionId(transactionId);
			monitorModel.setMonitorId(monitorId);
			monitorModel.setTransactionType(transType);
			
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			ftpManager.canConnectFTPServer();
			
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			
			/** init table config to import **/
			if(Constants.TRANSACTION_MASTER_TYPE.equals(transType)){
				patheImport = env.getProperty("path.master.sales.in");
			}else{
				patheImport = env.getProperty("path.transaction.sales.in");
			}
			
			monitorTime = new MonitorTime("initImportConfig");
			
			/** Init Config and Get Ftp File */
			countFileMap = ImportHelper.initImportConfig(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,conn,patheImport,transType,userRequest,requestTable,importAll);
			
			monitorTime.debugUsedTime();
			
			
			monitorTime = new MonitorTime("Split Line To DB");
			
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
						modelItem.setDataCount(ftpBean.getFileCount());
						modelItem.setFileSize(ftpBean.getFileSize());
						modelItem.setSubmitDate(new Date());
						
						if( ftpBean.getDataLineText() != null && ftpBean.getDataLineText().length >0){
							String[] results = null;
							if(tableBean.getTransactionType().equals(Constants.TRANSACTION_TRANS_TYPE)){
								 //Transaction RollBack By Record
								results = imtProcess.importToDB(conn,initConfigMap,tableBean,ftpBean,userRequest);
								if( !Utils.isNull(results[0]).equals("")){
									//Task ALL False
									taskStatusInt = Constants.STATUS_FAIL;
									fileTransImportErrorList.add(ftpBean);//Add For Delete file and Move to In Processed
								}else{
									fileTransImportSuccessList.add(ftpBean);//Add For Delete file and Move to In Processed
								}
								/** Update Monitor Item To Success **/
								modelItem.setStatus(Constants.STATUS_SUCCESS);
								modelItem.setSuccessCount(ConvertUtils.convertToInt(results[1]));
								modelItem.setErrorMsg(Utils.isNull(results[0]));
								modelItem.setErrorCode(Utils.isNull(results[2]));
								
								isExc = true;
							}else{
								 //Master Rollback ALL
								results = imtProcess.importToDB(conn,initConfigMap,tableBean,ftpBean,userRequest);
								logger.debug("ERR:["+results[0]+"]");
								if( !Utils.isNull(results[0]).equals("")){
									logger.debug("Insert Monitor Item FAIL TableName:"+tableBean.getTableName());
									modelItem.setStatus(Constants.STATUS_FAIL);
									modelItem.setSuccessCount(ConvertUtils.convertToInt(results[1]));
									modelItem.setErrorMsg(Utils.isNull(results[0]));
									modelItem.setErrorCode(Utils.isNull(results[2]));
									
									//Task All False
									taskStatusInt = Constants.STATUS_FAIL;
									
									//Control Transaction Rollback ALL Case Master Table 
					    			if(tableBean.getTransactionType().equals(Constants.TRANSACTION_MASTER_TYPE)){
					    				rollBackFlag = true;
										stepBreak = true; //break All Loop
										step2Break = true;//break 2 loop
					    			} 
						    		
								}else{
									/** Update Monitor Item To Success **/
									modelItem.setStatus(Constants.STATUS_SUCCESS);
									modelItem.setSuccessCount(ConvertUtils.convertToInt(results[1]));
									modelItem.setErrorMsg(Utils.isNull(results[0]));
									modelItem.setErrorCode(Utils.isNull(results[2]));
									
									if(tableBean.getTableName().equalsIgnoreCase("m_customer")
											|| tableBean.getTableName().equalsIgnoreCase("m_address")
											|| tableBean.getTableName().equalsIgnoreCase("m_contact")
											|| tableBean.getTableName().equalsIgnoreCase("m_trip")
											|| tableBean.getTableName().equalsIgnoreCase("m_sales_target_new")
											){
										
										fileMasterImportSuccessList.add(ftpBean); //Add For Delete file and Move to In Processed
									}
								}
								isExc = true;
							}// if
						}else{
							logger.debug("Ftp File TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
						}

						/** verify status */
						if(modelItem.getDataCount() == modelItem.getSuccessCount()){
							modelItem.setStatus(Constants.STATUS_SUCCESS);
						}else{
							modelItem.setStatus(Constants.STATUS_FAIL);
							taskStatusInt = Constants.STATUS_FAIL;
						}
						modelItem = dao.insertMonitorItem(connMonitor,modelItem);
					    
					}//for 2
		        }//if 1
			}//for 1

			monitorTime.debugUsedTime();
			
			logger.debug("isExc:"+isExc+" ,rollBackFlag:"+rollBackFlag);
			
			/** Check Is Excute and No error **/
			if(isExc && rollBackFlag == false){
				logger.debug("Transaction commit");
				conn.commit();
				
				monitorTime = new MonitorTime("Move File FTP To In Process");
				
				/** Case Transaction and Cust,Address,Contact Import By sale code by FileName  and after import move File to  Sales-In-Processed */
				logger.debug("File Transaction Success To Move:"+fileTransImportSuccessList.size());
				if(fileTransImportSuccessList != null && fileTransImportSuccessList.size() > 0){
					ftpManager.moveFileFTP_NEW(env.getProperty("path.transaction.sales.in"), env.getProperty("path.transaction.sales.in.processed"), fileTransImportSuccessList);
				}
				
				/** Case Transaction Type Import File Error Move To Sales-In-Error */
				if(fileTransImportErrorList != null && fileTransImportErrorList.size() >0){
				  logger.info("Transaction Move File To Sales-In-Error");
				  ftpManager.moveFileFTP_NEW(env.getProperty("path.transaction.sales.in"), env.getProperty("path.transaction.sales.in.error"), fileTransImportErrorList);
				}
				
				/** Case User Admin do not Move file Cust,custAddr,contact */
				if( !User.ADMIN.equals(userLogin.getType())){
					logger.debug("File Master (cust,address,contact,trip) Success To Move:"+fileMasterImportSuccessList.size());
					if(fileMasterImportSuccessList != null && fileMasterImportSuccessList.size() > 0){
						ftpManager.moveFileFTP_NEW(env.getProperty("path.master.sales.in"), env.getProperty("path.master.sales.in.processed"), fileMasterImportSuccessList);
					}
			    }
			
				/** End process ***/
				logger.debug("Update Monitor to Success:"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(transType);
				dao.updateMonitor(connMonitor,monitorModel);
				
				monitorTime.debugUsedTime();
				
			}else if(isExc && rollBackFlag == true){
				logger.debug("Transaction Rolback");
				conn.rollback();
				
				logger.debug("Update Monitor to Fail :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(transType);
				if(transType.equals(Constants.TRANSACTION_MASTER_TYPE)){
					monitorModel.setErrorCode("MasterException");
				}else if(transType.equals(Constants.TRANSACTION_TRANS_TYPE)){
					monitorModel.setErrorCode("TransactionException");
				}else{
					monitorModel.setErrorCode("UpdateSalesException");
				}
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
			
			/** Optional Step Write Log Result Process By File Name and UserName **/
			if("true".equals(EnvProperties.getInstance().getProperty("ftp.export.logs.result.enable"))){
				
				logger.info("*** Step Write Log Result *******");
			    if(Constants.TRANSACTION_MASTER_TYPE.equals(transType)){
			        ftpManager.uploadFileToFTP( EnvProperties.getInstance().getProperty("path.master.sales.in.result"), initConfigMap,userLogin);
			    } else{
			        ftpManager.uploadFileToFTP( EnvProperties.getInstance().getProperty("path.transaction.sales.in.result"), initConfigMap,userLogin);
			    }
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			
			/** End process ***/
			logger.debug("Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
			monitorModel.setFileCount(countFileMap);
			monitorModel.setTransactionType(transType);
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			
			dao.updateMonitorCaseError(connMonitor,monitorModel);

			//clear Task running for next run
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT);
			
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
		return monitorModel;
	}
	
}
