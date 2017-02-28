package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.isecinc.pens.inf.helper.InterfaceHelperWacoal;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManagerWacoal;
import com.isecinc.pens.process.SequenceProcess;

public class ImportWacoalProcess extends InterfaceUtils{
	private static Logger logger = Logger.getLogger("PENS");
	public final static String PARAM_REAL_PATH_TEMP = "REAL_PATH_TEMP";
	public static String PATH_CONTROL ="inf-config/table-mapping-import-wacoal/";
	public static String FILE_CONTROL_NAME ="control_import.csv";
	
	public static void runProcess(User user,MonitorBean m,String[] tableNameSelects) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = new LinkedHashMap<String,TableBean>();
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		boolean stepBreak = false;
		boolean step2Break = false;
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		String patheImport = "";
		int countFileMap = 0;
		List<FTPFileBean> fileImportSuccessList = new ArrayList<FTPFileBean> ();
		List<FTPFileBean> fileImportErrorList = new ArrayList<FTPFileBean> ();
		MonitorTime monitorTime = null;
		Map<String, String> batchParamMap = new HashMap<String, String>();
		try{
			/** prepare Paramenter **/
			batchParamMap = m.getBatchParamMap();
			String realPathTemp  = batchParamMap.get(PARAM_REAL_PATH_TEMP);
			
			if("PENSBME_INISTK_WACOAL".equalsIgnoreCase(tableNameSelects[0])){
				patheImport = env.getProperty("path.import.wacoal.stock");
			}else{
				patheImport = env.getProperty("path.import.wacoal.salesin");
			}

			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			monitorModel.setTransactionId(m.getTransactionId());
			monitorModel.setMonitorId(m.getMonitorId());
			monitorModel.setTransactionType(m.getTransactionType());
			
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManagerWacoal ftpManager = new FTPManagerWacoal(env.getProperty("ftp.wacoal.ip.server"), env.getProperty("ftp.wacoal.username"), env.getProperty("ftp.wacoal.password"));
			ftpManager.canConnectFTPServer();
			
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			monitorTime = new MonitorTime("initImportConfig");
			InterfaceHelperWacoal.initImportConfigWacoal(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,conn,patheImport,user,tableNameSelects);
			monitorTime.debugUsedTime();

			if(taskStatusInt != Constants.STATUS_FAIL){
				monitorTime = new MonitorTime("initImportConfig>>Download file from Ftp Server");
				/** Load File From FTP Server To Table Map By Table**/
			    countFileMap = ftpManager.downloadFileMappingToTableWacoal(user,initConfigMap,patheImport,realPathTemp);
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
							modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
							
							if( ftpBean.getDataLineText() != null && ftpBean.getDataLineText().length >0){
								String[] results = null;
								
								results = importToDB(conn,initConfigMap,tableBean,ftpBean,user,modelItem);
								logger.debug("Utils.isNull(results[0]):"+Utils.isNull(results[0]));
								
								if( !Utils.isNull(results[0]).equals("")){
									//Task ALL False
									taskStatusInt = Constants.STATUS_FAIL;
				
									modelItem.setStatus(Constants.STATUS_FAIL);
									fileImportErrorList.add(ftpBean);//Add For Delete file and Move to In Processed
									logger.debug("Fail.taskStatusInt:"+taskStatusInt);
									
									logger.debug("Transaction FileName["+ftpBean.getFileName()+"] Rollback");
									conn.rollback();
								}else{
									fileImportSuccessList.add(ftpBean);//Add For Delete file and Move to In Processed
									modelItem.setStatus(Constants.STATUS_SUCCESS);
									logger.debug("Success.taskStatusInt:"+taskStatusInt);
									
									logger.debug("Transaction FileName["+ftpBean.getFileName()+"] Commit");
									conn.commit();
								}
								
								/** Update Monitor Item To Success **/
								modelItem.setErrorMsg(Utils.isNull(results[0]));
								modelItem.setSuccessCount(ConvertUtils.convertToInt(results[1]));
								modelItem.setErrorCode(Utils.isNull(results[2]));
								modelItem.setDataCount(Utils.convertStrToInt(results[3]));
								isExc = true;
								
								/** verify status */
								/*if(modelItem.getDataCount() == modelItem.getSuccessCount()){
									modelItem.setStatus(Constants.STATUS_SUCCESS);
								}else{
									modelItem.setStatus(Constants.STATUS_FAIL);
									taskStatusInt = Constants.STATUS_FAIL;
								}*/
								
							}else{
								logger.debug("TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
								taskStatusInt = Constants.STATUS_FAIL;
								monitorModel.setErrorCode("FileNotFoundException");
								monitorModel.setErrorMsg("‰¡Ëæ∫‰ø≈Ï");
							}
							//Insert DB
							modelItem = dao.insertMonitorItem(connMonitor,modelItem);
						    
						}//for 2
			        }else{
			        	logger.debug("TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
						taskStatusInt = Constants.STATUS_FAIL;
						monitorModel.setErrorCode("FileNotFoundException");
						monitorModel.setErrorMsg("‰¡Ëæ∫‰ø≈Ï");
						dao.updateMonitor(connMonitor,monitorModel);
						
			        }//if 1
				}//for 1
				monitorTime.debugUsedTime();	
			}//if

			logger.debug("isExc:"+isExc);
			
			/** Check Is Excute**/
			if(isExc){
				logger.debug("Move file import success to in-processed");           

				if("PENSBME_INISTK_WACOAL".equalsIgnoreCase(tableNameSelects[0])){
					  ftpManager.moveFileFTP(env.getProperty("path.import.wacoal.stock"),env.getProperty("path.import.wacoal.stock.in.processed"),fileImportSuccessList);
				}else{
					  ftpManager.moveFileFTP(env.getProperty("path.import.wacoal.salesin"),env.getProperty("path.import.wacoal.salesin.in.processed"),fileImportSuccessList);
				}

				/** End process ***/
				logger.debug("Update Monitor to Success:"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				dao.updateMonitor(connMonitor,monitorModel);
				
				//monitorTime.debugUsedTime();
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
			dao.updateControlMonitor(new BigDecimal(0),monitorModel.getType());
			
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
	
	public static String[] importToDB(Connection conn,LinkedHashMap<String,TableBean> initConfigMap,TableBean tableBean,FTPFileBean ftpFileBean,User userBean,MonitorItemBean monitorItem) throws Exception {
	    PreparedStatement psInsert = null;
	    PreparedStatement psUpdate = null;
	    PreparedStatement psDelete = null;
	    StringBuffer resultTxt = new StringBuffer("");
	    List<FTPFileBean> dataStrBuffList = new ArrayList<FTPFileBean>();
	    String lineStr = "";
        int line=0;
        int lineNo=0;
        int canExc = 0;
        int totalRos = 0;
        int completeRow = 0;
        int errorRow= 0;
        String errorMsg ="";
        String errorCode = "";
        String[] dataTextLineArr = null;
        String[] results = new String[4];
        String error = ""; 
        MonitorTime monitorTime = null;
	    try {
	    	 //Debug Time
	    	  monitorTime = new MonitorTime("Split Line To DB>>importToDB table:"+tableBean.getTableName());
	    	  
	    	  /*Case Table Have Pre Function **/
	    	  if( !"N".equalsIgnoreCase(tableBean.getPreFunction())){
	    		  logger.info("**** Script "+tableBean.getTableName()+" Pre Function name:"+tableBean.getPreFunction());
	    		  String[] errors = PreFunction.process(conn, tableBean,userBean);
	    	  }
	    	  
	    	  logger.debug("ImportToDB");
	    	  dataTextLineArr = ftpFileBean.getDataLineText();
			  // init prepareStatment
			  logger.debug("sqlInsert:"+tableBean.getPrepareSqlIns());
			  logger.debug("sqlUpd:"+tableBean.getPrepareSqlUpd());
			  
			  if(tableBean.getActionDB().indexOf("I") != -1){
			     psInsert = conn.prepareStatement(tableBean.getPrepareSqlIns());
			  }
			  if(tableBean.getActionDB().indexOf("U") != -1){
			     psUpdate = conn.prepareStatement(tableBean.getPrepareSqlUpd());
			  }
			
			  if(tableBean.getActionDB().indexOf("D") != -1){
				 logger.debug("sqlDelete:"+tableBean.getPrepareSqlDelete());
			     psDelete = conn.prepareStatement(tableBean.getPrepareSqlDelete());
			  }
			  
			  totalRos = dataTextLineArr.length;
			  logger.debug("strTxts length:"+totalRos);

			  for(line = 0;line<totalRos;line++){
				  error  ="";
				  try{
					  lineNo++;
					  lineStr = dataTextLineArr[line];   	  
			    	  if( !Utils.isNull(lineStr).equals("")){
			    		  
			    		/*  if(tableBean.getActionDB().indexOf("U") != -1){
				    		  //logger.debug("**********Start Update ******************");
				    		  psUpdate = InterfaceHelperWacoal.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdate,userBean);
					    	  canExc = psUpdate.executeUpdate();
					    	  logger.debug("canUpdate:"+canExc);
			    		  }*/
               
				    	  if(canExc ==0 && tableBean.getActionDB().indexOf("I") != -1){
				    		  //logger.debug("**********Start Insert ******************");
				    		  psInsert = InterfaceHelperWacoal.spiltLineArrayToInsertStatement(conn,tableBean,lineStr,psInsert,userBean);
				    		  try{
					    	      canExc = psInsert.executeUpdate();
				    	         // logger.debug("canIns:"+canExc);
				    		  }catch(SQLIntegrityConstraintViolationException e){
				    			  logger.debug("Error Duplicate Key ");
				    			  error = "Error Duplicate Key";
				    		  }catch(Exception ee){
				    			  ee.printStackTrace();
				    		  }
				    	  }
		    		  
				    	  //stamp log result
				    	  if(canExc == 0){
				    		  if(error.equals("Error Duplicate Key")){
				    		     errorMsg ="NO INSERT DUPLICATE KEY";
				    		     errorCode = "DuplicateImportFileException";
				    		     resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->ERROR:NO INSERT DUPLICATE KEY]").append(Constants.newLine);
				    		  }else{
				    			  errorMsg ="NO UPDATE KEY NOT FOUND";
					    		  errorCode = "NOUPDATE";
					    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->WARNING:NO UPDATE CANNOT FIND KEY UPDATE 002]").append(Constants.newLine);
				    		  }
				    	     errorRow++;
				    	     
				    	    //insert result msg
							insertMonitorItemResult(conn,monitorItem.getId(),"FAIL",lineStr,(line+1)+"");
				    	  }else{
				    		  
				    		 completeRow++;
				    		 //insert result msg
							 insertMonitorItemResult(conn,monitorItem.getId(),"SUCCESS",lineStr,(line+1)+"");
				    	  }
				    	  //initail 
				    	  canExc = 0;
			    	  }
		    	  }catch(Exception e){
		    		  logger.error(e.getMessage(),e);
		    		  errorMsg ="Error:Line{"+lineNo+"}:{LineText:"+lineStr+"}{ErrorMsg:"+e.getMessage()+"}";
		    		  errorCode = ExceptionHandle.getExceptionCode(e);
		    		  errorRow++;
		    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->ERROR:"+e.getMessage()+"]").append(Constants.newLine);  
		    	  }

			  }//for
			  
			  if(errorRow > 0){
				  /**Optional add Result Log */
				  resultTxt.append("----------------------------------").append("\n");
				  resultTxt.append("Total Rows :"+dataTextLineArr.length).append("\n");
				  resultTxt.append("Success Rows :"+completeRow).append("\n");
				  resultTxt.append("Error Rows :"+errorRow).append("\n");
				  StringBuffer columnHeader = new StringBuffer("");
				  columnHeader.append(tableBean.getColumnTableAll()+",ERROR_MSG").append("\n");
				  resultTxt = columnHeader.append(resultTxt);
				 
				  /**Optional Log File Case one table have more one file **/
		          if(tableBean.getDataStrBuffList() ==null){
		          	  dataStrBuffList = new ArrayList<FTPFileBean>();
		          	  FTPFileBean fileBean = new FTPFileBean();
		          	  fileBean.setFileName(ftpFileBean.getFileName());
		          	  fileBean.setDataResultStr(resultTxt.toString());
		          	  dataStrBuffList.add(fileBean);
		          	  tableBean.setDataStrBuffList(dataStrBuffList);
		          }else{
		        	  FTPFileBean fileBean = new FTPFileBean();
			          fileBean.setFileName(ftpFileBean.getFileName());
			          fileBean.setDataResultStr(resultTxt.toString());
		          	  tableBean.getDataStrBuffList().add(fileBean);
		          }
			  }
			  
			 /** Put to MAP **/	
			 initConfigMap.put(tableBean.getTableName(), tableBean);
			 
			 /** Add Result **/
			 results[0] = errorMsg;
			 results[1] = completeRow+"";
			 results[2] = errorCode;
			 results[3] = lineNo+"";
			 
			 //Debug Time
			 monitorTime.debugUsedTime();
			 
	    } catch(Exception e){
		     throw e;
	    }finally{
		    if(psInsert != null){
		    	psInsert.close();psInsert=null;
		    }
		    if(psUpdate != null){
		    	psUpdate.close();psUpdate=null;
		    }
		    if(psDelete != null){
		    	psDelete.close();psDelete=null;
		    }
	    }
	    return results;
	}
	

	private static void insertMonitorItemResult(Connection conn,BigDecimal monitorItemId,String status,String msg,String lineInExcel) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO MONITOR_ITEM_RESULT(MONITOR_ITEM_ID, STATUS, MESSAGE,NO)VALUES(?,?,?,?) ";
			//logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, monitorItemId);
			ps.setString(++index,status);
			ps.setString(++index,msg);
			ps.setString(++index,lineInExcel);
			int r = ps.executeUpdate();
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
}
