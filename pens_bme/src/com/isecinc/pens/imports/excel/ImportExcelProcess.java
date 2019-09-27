package com.isecinc.pens.imports.excel;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.bean.FTPFileBean;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.exception.FTPException;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.manager.process.PreFunction;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;
import com.pens.util.meter.MonitorTime;

public class ImportExcelProcess extends InterfaceUtils{
	private static Logger logger = Logger.getLogger("PENS");
	public final static String PARAM_REAL_PATH_TEMP = "REAL_PATH_TEMP";
	public final static String PARAM_FILE_NAME = "FILE_NAME";
	public static String PATH_CONTROL ="inf-config/table-mapping-import-excel/";

	
	public static void runProcess(User user,MonitorBean m,String tableName) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		int countFileMap = 0;
		List<FTPFileBean> fileImportSuccessList = new ArrayList<FTPFileBean> ();
		List<FTPFileBean> fileImportErrorList = new ArrayList<FTPFileBean> ();
		MonitorTime monitorTime = null;
		Map<String, String> batchParamMap = new HashMap<String, String>();
		String fileType = "";
		String errorCode = "SuccessException";
		String errorMsg = ExceptionHandle.ERROR_MAPPING.get(errorCode);
		TableBean tableBean = null;
		try{
			/** prepare Paramenter **/
			batchParamMap = m.getBatchParamMap();
			String realPathTemp  = batchParamMap.get(PARAM_REAL_PATH_TEMP);
			String fileName  = batchParamMap.get(PARAM_FILE_NAME);
			
			FormFile dataFile  = m.getDataFile();
			  
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			monitorModel.setTransactionId(m.getTransactionId());
			monitorModel.setMonitorId(m.getMonitorId());
			monitorModel.setTransactionType(m.getTransactionType());
			
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			/**Validate Duplicate File**/
			if (dataFile != null) {
				isExc = true;
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				
				monitorTime = new MonitorTime("initImportConfig");
				tableBean = InterfaceHelperExcel.initImportConfig(conn,PATH_CONTROL,user,tableName);
				monitorTime.debugUsedTime();
				
				/** cehck FileName duplicate **/
				boolean dup = importFileNameIsDuplicate(conn,tableBean.getTableName(),fileName);
				if(dup){
					taskStatusInt = Constants.STATUS_FAIL;
					errorCode = "DuplicateImportFileException";
					errorMsg = ExceptionHandle.ERROR_MAPPING.get(errorCode);
				}
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());
			
				if(taskStatusInt != Constants.STATUS_FAIL){
					monitorTime = new MonitorTime("Start convert Excel to lineStr");
					tableBean = convertExcelToLineStr(tableBean,dataFile,fileName,tableBean.getStartRowData());
			        monitorTime.debugUsedTime();
				}
					
				if(taskStatusInt != Constants.STATUS_FAIL){
						monitorTime = new MonitorTime("Split Text To DB");
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
								modelItem.setFileSize(ftpBean.getFileSize());
								modelItem.setSubmitDate(new Date());
								modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
								
								if( ftpBean.getDataLineText() != null && ftpBean.getDataLineText().length >0){
									String[] results = null;
									
									results = importToDB(conn,tableBean,ftpBean,user,modelItem);
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
									modelItem.setSuccessCount(Utils.convertToInt(results[1]));
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
				        }
				}//if
			}else{
				logger.debug("TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
				taskStatusInt = Constants.STATUS_FAIL;
				errorCode = "FileNotFoundException";
				errorMsg = "‰¡Ëæ∫‰ø≈Ï";
			}
			
		    monitorTime.debugUsedTime();	
			logger.debug("isExc:"+isExc);

			/** End process Update status ***/
			logger.debug("Update Monitor to Success:"+taskStatusInt);
			monitorModel.setStatus(taskStatusInt);
			monitorModel.setFileCount(countFileMap);
			monitorModel.setTransactionType(m.getTransactionType());
			monitorModel.setErrorCode(errorCode);
			monitorModel.setErrorMsg(errorMsg);
			dao.updateMonitor(connMonitor,monitorModel);
			
		   //monitorTime.debugUsedTime();
			
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
	}
	
	public static String[] importToDB(Connection conn,TableBean tableBean,FTPFileBean ftpFileBean,User userBean,MonitorItemBean monitorItem) throws Exception {
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
				    		  psInsert = InterfaceHelperExcel.spiltLineArrayToInsertStatement(conn,tableBean,lineStr,psInsert,userBean);
				    		  try{
					    	      canExc = psInsert.executeUpdate();
				    	         // logger.debug("canIns:"+canExc);
				    		  }catch(SQLIntegrityConstraintViolationException e){
				    			  logger.debug("Error Duplicate Key ");
				    			  error = "Error Duplicate Key";
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
	
	public static TableBean convertExcelToLineStr(TableBean tableBean,FormFile dataFile,String fileName,int startRowData) throws Exception{
		try {			
        	/** 2 **/
        	String dataStreamStr = convertXlsToText(dataFile,fileName,startRowData).toString();
        	
        	//logger.debug("dataStreamStrXX:"+dataStreamStr);
        	
            /** Store DataStream in TableBean **/
        	String[] dataLineTextArr =  new String(dataStreamStr).split(Constants.newLine);
    
    		FTPFileBean ftpBean = new FTPFileBean();
    		ftpBean.setFileName(fileName);
    		ftpBean.setDataLineText(dataLineTextArr);
    		ftpBean.setFileSize(FileUtils.byteCountToDisplaySize(0));
    		ftpBean.setFileCount(Utils.isNull(dataStreamStr).equals("")?0:dataLineTextArr.length-1);
    		
    		tableBean.getDataLineList().add(ftpBean);
        	
            return tableBean;
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			
		}
	}
	
	private static StringBuffer convertXlsToText(FormFile dataFile,String fileName,int startRowData) {
        // For storing data into CSV files
        StringBuffer data = new StringBuffer();
        StringBuffer dataRow = new StringBuffer();
        String rowCheck = "";
        try {
	        // Get the workbook object for XLS file
	        //HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(dataFile));
	        
	        HSSFWorkbook workbook = new HSSFWorkbook(dataFile.getInputStream());
	        // Get first sheet from the workbook
	        HSSFSheet sheet = workbook.getSheetAt(0);
	        Cell cell;
	        Row row;
	
	        // Iterate through each rows from first sheet
	        Iterator<Row> rowIterator = sheet.iterator();
	        while (rowIterator.hasNext())  {
                row = rowIterator.next();
                if(row.getCell(0).getCellType() ==Cell.CELL_TYPE_NUMERIC){
                   rowCheck = String.valueOf(row.getCell(0).getNumericCellValue());
                }else{
                   rowCheck = row.getCell(0).getStringCellValue();
                }
               // logger.debug("RowNum Check:"+row.getRowNum()+":["+rowCheck+"]");
                
                if( row.getRowNum() >= startRowData){
                	 if(!rowCheck.equals("")){
		                // For each row, iterate through each columns
		                Iterator<Cell> cellIterator = row.cellIterator();
		                dataRow = new StringBuffer();
		                while (cellIterator.hasNext()){
		                        cell = cellIterator.next();
		                       // logger.debug("cellType["+cell.getCellType()+"]");
		                        switch (cell.getCellType())  {
			                        case Cell.CELL_TYPE_BOOLEAN:
			                        	 dataRow.append(cell.getBooleanCellValue() + "|");
			                             break;
			                                
			                        case Cell.CELL_TYPE_NUMERIC:
			                        	//logger.debug(cell.getNumericCellValue());
			                        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
			                        		//logger.debug("Date");
			                                dataRow.append(DateUtil.stringValue(cell.getDateCellValue(),DateUtil.DD_MM_YYYY_WITH_SLASH) + "|");
			                            }else{
			                        	    dataRow.append( Utils.convertToNumberSpecial(new BigDecimal(cell.getNumericCellValue()))+ "|");
			                            }
			                             break;
			                                
			                        case Cell.CELL_TYPE_STRING:
			                        
			                        	if(StringUtils.isNumeric(cell.getStringCellValue())){
			                        		dataRow.append( Utils.convertToNumberSpecial(new BigDecimal(cell.getStringCellValue()))+ "|");
			                        	}else{
			                        	   dataRow.append(cell.getStringCellValue() + "|");
			                        	}
			                            break;
			                            
			                        case Cell.CELL_TYPE_BLANK:
			                        	dataRow.append("" + "|");
			                            break;
			                        
			                        default:
			                        	dataRow.append(cell + "|");
			                      }  
		                }
		               // logger.debug("dataRow:"+dataRow.toString());
		                
	                     data.append(fileName+"|"+(row.getRowNum()+1)+"|"+dataRow.toString().substring(0,dataRow.toString().length()-1)+'\n'); 
            	  }else{
              	     break;
                  }
               }
	        }
        }catch (Exception e) {
              e.printStackTrace();
        }
      return data;
  }
	
	public static Boolean importFileNameIsDuplicate(Connection conn ,String tableName,String fileName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean dup = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from "+tableName+" WHERE  lower(file_name) ='"+Utils.isNull(fileName).toLowerCase()+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				dup = true;
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return dup;
	} 
	
}
