package com.isecinc.pens.interfaces.imports;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.FTPFileBean;
import com.isecinc.pens.bean.FileImportTransBean;
import com.isecinc.pens.bean.KeyNoImportTransBean;
import com.isecinc.pens.bean.LineImportTransBean;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExternalFunctionHelper;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.TransactionHelper;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.process.ResultImportBean;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptHelper;
import com.isecinc.pens.model.MOrder;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.Utils;

public class ImportReceiptProcess {

	public static Logger logger = Logger.getLogger("PENS");
	public static String  PATH_CONTROL = "inf-config/config-receipt-import/";
	public static String  FILE_CONTROL_NAME = "control_receipt_import.csv";
	public static String  PATH_IMPORT = EnvProperties.getInstance().getProperty("path.transaction.sales.in");
	
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
	public  MonitorBean importReceiptFileToDB(BigDecimal transactionId ,BigDecimal monitorId,String transType,User userLogin ,boolean importAll) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = new LinkedHashMap<String,TableBean>();
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		List<MonitorItemBean> monitorItemList = new ArrayList<MonitorItemBean>();
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		int countFileMap = 0;
		TransactionHelper helper = new TransactionHelper();
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
			
			/** initial table config to import **/
			if(Constants.TRANSACTION_REUTS_TRANS_TYPE.equalsIgnoreCase(transType)){
			   /** No Download File From FTP Case Re Import**/
			   //ImportHelper.initImportConfigCaseReImportError(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,conn,PATH_IMPORT,transType,userLogin,"",importAll);
			  
			}else{
			   /** Case Normal Download file from ftp server **/
			   ImportHelper.initImportConfigReceipt(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,connMonitor,PATH_IMPORT,transType,userLogin,importAll);
			  
			   /**  Insert Data FTP File To t_import_temp_trans **/
			   logger.info("--start insert data file to t_temp_import_trans");
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
								resultImportBean = importLineToDB(transactionId,connMonitor,conn,initConfigMap,tableBean,keyNoBean,userLogin);
								 
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
	
	/**
	 * importToDB
	 * @param conn
	 * @param initConfigMap
	 * @param tableBean
	 * @param dataTextLineArr
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public  ResultImportBean importLineToDB(BigDecimal transId,Connection conMonitor,Connection conn
			,LinkedHashMap initConfigMap,TableBean tableBean
			,KeyNoImportTransBean keyNoBean,User userBean) throws Exception {
	    PreparedStatement psUpdateH = null;
	    PreparedStatement psInsH = null;
	    PreparedStatement psUpdateD = null;
	    PreparedStatement psUpdateDCS = null;
	    PreparedStatement psInsD = null;
	    StringBuffer resultTxt = new StringBuffer("");
	    List<FTPFileBean> dataStrBuffList = new ArrayList<FTPFileBean>();
	    List<String> orderList = new ArrayList<String>();
	    String lineStr = "";
        int line=0;int lineNo = 0;
        int canExc = 0;int totalRos = 0;
        int successRow = 0;int allRow=0;int errorRow= 0;
        String errorCode="";String errorMsg="";
        String firstErrorMsg ="";String firstErrorCode = "";
        ResultImportBean resultImportBean = null;
        TableBean childBean = null;
        TransactionHelper helper = new TransactionHelper();
        String receiptNoAll = "";
        ImportReceiptFunction importReceipt2 = new ImportReceiptFunction();
        boolean useImportReceipt2 = false;
        LineImportTransBean lineBean = null;
	    try {  
	    	  /**  init prepareStatment Ins H **/
	    	  if(Utils.isNull(tableBean.getActionDB()).indexOf("I") != -1 && !Utils.isNull(tableBean.getPrepareSqlIns()).equals("")){
			     logger.debug("sqlIns_H:"+tableBean.getPrepareSqlIns());
			     psInsH = conn.prepareStatement(tableBean.getPrepareSqlIns());
	          }
			  /**  init prepareStatment Update H **/
	    	  if(Utils.isNull(tableBean.getActionDB()).indexOf("U") != -1 && !Utils.isNull(tableBean.getPrepareSqlUpd()).equals("")){
				  logger.debug("sqlUpd_H:"+tableBean.getPrepareSqlUpd());
				  psUpdateH = conn.prepareStatement(tableBean.getPrepareSqlUpd());
	          }
			  if(!"N".equalsIgnoreCase(tableBean.getChildTable())){
				  /** init ChildBean  **/
				  childBean = new TableBean(); 
				  childBean.setTransactionType(Constants.TRANSACTION_UTS_TRANS_TYPE);
				  childBean.setTableName(tableBean.getChildTable());
				  childBean = ImportHelper.initColumn(PATH_CONTROL,childBean);
				  childBean = ImportHelper.genPrepareSQLUTS(childBean);

				  if(Utils.isNull(tableBean.getActionDB()).indexOf("U") != -1 && !Utils.isNull(childBean.getPrepareSqlUpd()).equals("")){
					  logger.debug("sqlUpddate_Line:"+childBean.getPrepareSqlUpd());
					  psUpdateD = conn.prepareStatement(childBean.getPrepareSqlUpd());
				  }
				  
				  if(Utils.isNull(tableBean.getActionDB()).indexOf("U") != -1 && !Utils.isNull(childBean.getPrepareSqlUpdCS()).equals("")){
					  logger.debug("sqlUpdate_Line_CS:"+childBean.getPrepareSqlUpdCS());
				      psUpdateDCS = conn.prepareStatement(childBean.getPrepareSqlUpdCS());
				  }

				  if(Utils.isNull(tableBean.getActionDB()).indexOf("I") != -1 && !Utils.isNull(childBean.getPrepareSqlIns()).equals("")){
					  logger.debug("sqlInsert_Line:"+childBean.getPrepareSqlIns());
					  psInsD = conn.prepareStatement(childBean.getPrepareSqlIns());
				  }
			  }
			  
			  totalRos = keyNoBean.getLineList().size();
			  logger.debug("totalRos:"+totalRos);
			  for(line = 0;line<totalRos;line++){
				  try{
					  lineNo++;
					  /*Count All Record */
					  allRow++;//logger.debug("allRow["+allRow+"]");
					  canExc = 0;
					  lineBean = keyNoBean.getLineList().get(line);
					  lineStr = lineBean.getLineStr();
					  
			    	  if( !Utils.isNull(lineStr).equals("")){
				    		  //**Check Header Or Line**/
			    		      logger.debug("seq["+lineBean.getSeq()+"],lineStr["+lineStr+"]");
			    		      
			    		      /** Update B (receipt only) **/
				    		  if(lineStr.startsWith("B")){  
				    			  if(tableBean.getTableName().startsWith("t_receipt")){
			    					  logger.debug("********Start Update Batch(t_receipt) *********************");
			    					  canExc = importReceipt2.processReceiptBatch(conn, keyNoBean, userBean, lineStr,canExc);
				    			      
			    					  logger.debug("********End Update Batch (t_receipt) canUpdate:"+canExc +"");
				    			  }				    		  
				    		  
			    		      /** Update H **/
			    	          }else if(lineStr.startsWith("H")){  
				    			  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(tableBean.getPrepareSqlUpd())){
				    				  
				    				  if(tableBean.getTableName().startsWith("t_receipt")){
				    					  logger.debug("********Start Update H(t_receipt) *********************");
				    					  
				    					  canExc = importReceipt2.processReceiptHead(conn, keyNoBean, userBean, lineStr,canExc);
				    					  
							    	      logger.debug("********End Update H (t_receipt) canUpdate:"+canExc +"");
							    	      
				    				  }else{
						    			  logger.debug("********Start Update H *********************");
						    			  logger.debug("sql update:"+tableBean.getPrepareSqlUpd());
						    		      psUpdateH = ImportHelper.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdateH,userBean);
							    	      canExc = psUpdateH.executeUpdate();
							    	      logger.debug("********End Update H canUpdate:"+canExc +"");
							    	      
							    	      if(canExc != 0 && tableBean.getTableName().startsWith("t_order")){
							    	    	  /** after Update Receipt from ORCL and add for update payment flag  **/
								    	      orderList.add(lineStr);
								    	   }
				    				  }
				    			  }
				    			  
				    			  /** Insert Head **/
				    			  if(canExc == 0 && tableBean.getActionDB().indexOf("I") != -1 && !"".equals(tableBean.getPrepareSqlIns()) ){
				    				  if(tableBean.getTableName().startsWith("t_receipt")){
				    					  
				    				  }else{
					    				  logger.debug("**********Start Insert H ******************");
						    		      psInsH = ImportHelper.spiltLineArrayToInsertStatement(conn, tableBean, lineStr, psInsH,userBean);
							    	      canExc = psInsH.executeUpdate();
							    	      logger.debug("canIns H:"+canExc); 
							    	      
							    	      if(canExc != 0 && tableBean.getTableName().startsWith("t_order")){
							    	    	  /** after Update Receipt from ORCL and add for update payment flag  **/
								    	      orderList.add(lineStr);
								    	  }
				    				  }
				    			  }  
				    			  
				    		 }else  if( !lineStr.startsWith("L")){ // Case t_visit No  String :'H'
				    			  // end if Header
				    			  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(tableBean.getPrepareSqlUpd())){
					    			  logger.debug("**********Start Update H ******************");
					    		      psUpdateH = ImportHelper.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdateH,userBean);
						    	      canExc = psUpdateH.executeUpdate();
						    	      logger.debug("canUpdate H:"+canExc);
				    			  }
				    		  }
				    		  
				    		  /** Line **/
				    		  if(lineStr.startsWith("L")){
				    			  if(!"".equalsIgnoreCase(tableBean.getChildTable())){
			    					  /** Update Line  normal**/
				    				  if(tableBean.getActionDB().indexOf("U") != -1 && !"".equals(childBean.getPrepareSqlUpd())){
				    					  /** Create Receipt Line ONLY **/
				    					  if(childBean.getTableName().startsWith( "t_receipt_line") ){ 
				    						  canExc = importReceipt2.processReceiptLine(conn, keyNoBean, userBean, lineStr, psUpdateD,canExc,transId);
				    						  
				    						  logger.debug("canUpdate(receipt_line) Line:"+canExc);
                                          }else{
				    					     logger.debug("**********Start Update L normal ******************");
						    			     psUpdateD = ImportHelper.spiltLineArrayToUpdateStatement(conn, childBean, lineStr, psUpdateD,userBean);
								    	     canExc = psUpdateD.executeUpdate();
								    	     logger.debug("canUpdate L:"+canExc);
                                         }
				    				  }
				    				 
				    			  }//if 4 childTable not null
				    		  }//if Line
				    		  
				    	  //stamp log result
				    	  if(canExc == 0){
				    		  errorMsg  = "NO UPDATE KEY NOT FOUND";
				    		  errorCode = "NOUPDATE";
				    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->WARNING:NO UPDATE CANNOT FIND KEY UPDATE 001]").append(Constants.newLine);
				    	      errorRow++;
				    	      //logger.debug("errorRow["+errorRow+"]");
				    	     
				    	      // Add First Error To Save In monitor_item
				    	      if(firstErrorMsg.equals("")){
				    		     firstErrorMsg  = "NO UPDATE KEY NOT FOUND";
				    		     firstErrorCode = "NOUPDATE";
				    		  }
				    	     
				    	      //Insert Line to t_temp_import_trans
				    	      helper.updateStatusFailLineToTempImportDAO(conMonitor,keyNoBean.getFileName(),tableBean.getTableName(),lineBean.getKeyNo(),lineBean.getSeq(),errorCode);
				    	  }else{
				    		  errorCode = "";
				    		  successRow++;
				    		  //logger.debug("successRow["+successRow+"]");
				    		  //Insert Line to t_temp_import_trans
				    		  helper.updateStatusSuccessLineToTempImportDAO(conMonitor,keyNoBean.getFileName(),tableBean.getTableName(),lineBean.getKeyNo(),lineBean.getSeq(),errorCode);
				    		 
				    		  //set for return Calculate receipt Head
							  receiptNoAll += lineBean.getReceiptNo()+"|";
				    	  }
				    	
				    	  //initial
				    	  canExc = 0;
			    	  }//if 1
			       
		    	  }catch(Exception e){
		    		  errorMsg ="Error:Line{"+lineNo+"}:{LineText:"+lineStr+"}{ErrorMsg:"+e.getMessage()+"}";
		    		  errorCode = ExceptionHandle.getExceptionCode(e);
		    		  errorRow++;
		    		  logger.debug("errorRow["+errorRow+"]");
		    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->ERROR:"+e.getMessage()+"]").append(Constants.newLine);
		    		  
		    		  logger.error(errorMsg,e);
		    		  logger.debug("firstErrorMsg:"+firstErrorMsg);
		    		  
		    		  // Add First Error To Save In monitor_item
		    		  if(firstErrorMsg.equals("")){
		    		     firstErrorMsg  = errorMsg;
		    		     firstErrorCode = errorCode;
		    		  } 
		    		  
		    		 //Insert Line to t_temp_import_trans
		    		  helper.updateStatusFailLineToTempImportDAO(conMonitor,keyNoBean.getFileName(),tableBean.getTableName(),lineBean.getKeyNo(),lineBean.getSeq(),errorCode);
		    	  }//Exception
				  
			  }//for lineStr array
			  
			  if(errorRow <=0){
			  }else if(errorRow > 0){
				  /**Optional add Result Log */
				  resultTxt.append("----------------------------------").append("\n");
				  resultTxt.append("Total Rows :"+keyNoBean.getLineList().size()).append("\n");
				  resultTxt.append("Success Rows :"+successRow).append("\n");
				  resultTxt.append("Error Rows :"+errorRow).append("\n");
				  StringBuffer columnHeader = new StringBuffer("");
				  columnHeader.append("H -"+tableBean.getColumnTableAll()+",ERROR_MSG").append("\n");
				  if(childBean != null){
				     columnHeader.append("L -"+childBean.getColumnTableAll()+",ERROR_MSG").append("\n");
				  }
				  resultTxt = columnHeader.append(resultTxt);
				  
			      /**Optional Log File Case one table have more one file **/
		          if(tableBean.getDataStrBuffList() ==null){
		          	  dataStrBuffList = new ArrayList();
		          	  FTPFileBean fileBean = new FTPFileBean();
		          	  fileBean.setFileName(keyNoBean.getFileName());
		          	  fileBean.setDataResultStr(resultTxt.toString());
		          	  dataStrBuffList.add(fileBean);
		          	  tableBean.setDataStrBuffList(dataStrBuffList);
		          }else{
		        	  FTPFileBean fileBean = new FTPFileBean();
			          fileBean.setFileName(keyNoBean.getFileName());
			          fileBean.setDataResultStr(resultTxt.toString());
		          	  tableBean.getDataStrBuffList().add(fileBean);
		          }
		          
			  }	
			 /** Put to MAP **/	
			 initConfigMap.put(tableBean.getTableName(), tableBean);
		
			 /** Return Result import **/
			 resultImportBean = new ResultImportBean();
			 resultImportBean.setFirstErrorMsg(firstErrorMsg);
			 resultImportBean.setFirstErrorCode(firstErrorCode);
			 resultImportBean.setSuccessRow(successRow);
			 resultImportBean.setErrorRow(errorRow);
			 resultImportBean.setAllRow(allRow);
			 resultImportBean.setReceiptNoAll(receiptNoAll);
	    } catch(Exception e){
		     throw e;
	    }finally{
	    	if(psInsH != null){
		    	psInsH.close();psInsH=null;
		    }
		    if(psUpdateH != null){
		    	psUpdateH.close();psUpdateH=null;
		    }
		    if(psUpdateD != null){
		    	psUpdateD.close();psUpdateD=null;
		    }
		    if(psUpdateDCS != null){
		    	psUpdateDCS.close();psUpdateDCS=null;
		    }
		    if(psInsD != null){
		    	psInsD.close();psInsD=null;
		    }
	    }
	    return resultImportBean;
	}

}
