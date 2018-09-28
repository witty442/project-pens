package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.InterfaceHelper;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;

public class ImportICCProcess {
  
	public static Logger logger = Logger.getLogger("PENS");
	
	
	public int validateFileImport(Connection connMonitor,MonitorBean monitorModel,LinkedHashMap<String,TableBean> controlTableMap,String transDate) {
		String fileNameFixByDate = "";
		InterfaceDAO dao = new InterfaceDAO();
		int taskStatusInt = Constants.STATUS_SUCCESS;
		try{
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int j = 1; it.hasNext(); j++) {
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(tableName);
				fileNameFixByDate = InterfaceUtils.getImportNameICC(tableName, transDate);
				
				MonitorItemBean modelItem = new MonitorItemBean();
				modelItem.setMonitorId(monitorModel.getMonitorId());
				modelItem.setSource(tableBean.getSource());
				modelItem.setDestination(tableBean.getDestination());
				modelItem.setTableName(tableBean.getTableName());
				modelItem.setFileName(fileNameFixByDate);
				modelItem.setDataCount(0);
				modelItem.setFileSize("");
				modelItem.setSubmitDate(new Date());
				
				if(fileNameImportExist(connMonitor, tableName, fileNameFixByDate)){
					modelItem.setStatus(Constants.STATUS_FAIL);
					modelItem.setSuccessCount(0);
					modelItem.setErrorMsg("ไม่สามารถ import file นี้ได้เนื่องจาก มีการ importไปแล้ว ");
					modelItem.setErrorCode("DuplicateImportFileException");
					
					dao.insertMonitorItem(connMonitor,modelItem);
					
					taskStatusInt = Constants.STATUS_FAIL;
				}

			}
		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return taskStatusInt;
	}
	
	public boolean fileNameImportExist(Connection conn,String tableName,String fileNameImport) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean exist = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(*) as c from "+tableName+" where file_name LIKE '"+fileNameImport+"%' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") >0){
				   exist = true;
				}
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
		return exist;
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
	
	public  String[] importToDB(Connection conn,LinkedHashMap<String,TableBean> initConfigMap,TableBean tableBean,FTPFileBean ftpFileBean,User userBean) throws Exception {
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
        int totalRecord = 0;
        String errorMsg ="";
        String errorCode = "";
        String[] dataTextLineArr = null;
        String[] results = new String[4];
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

			  /** Line pos =0 total_receord */
			  totalRecord = Utils.convertStrToBigDecimal(dataTextLineArr[line]).intValue();
			  logger.debug("totalReceord:"+totalRecord);
			  
			  for(line = 1;line<totalRos;line++){
				  try{
					  lineNo++;
					  lineStr = dataTextLineArr[line];   	  
			    	  if( !Utils.isNull(lineStr).equals("")){
			    		  
			    		/*  if(tableBean.getActionDB().indexOf("U") != -1){
				    		  //logger.debug("**********Start Update ******************");
				    		  psUpdate = ImportHelper.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdate,userBean);
					    	  canExc = psUpdate.executeUpdate();
					    	  logger.debug("canUpdate:"+canExc);
			    		  }*/
               
				    	  if(canExc ==0 && tableBean.getActionDB().indexOf("I") != -1){
				    		  //logger.debug("**********Start Insert ******************");
				    		  psInsert = InterfaceHelper.spiltLineArrayToInsertStatement(conn,tableBean,lineStr,psInsert,userBean);
					    	  canExc = psInsert.executeUpdate();
				    	      logger.debug("canIns:"+canExc);
				    	  }
		    		  
				    	  //stamp log result
				    	  if(canExc == 0){
				    		 errorMsg ="NO UPDATE KEY NOT FOUND";
				    		 errorCode = "NOUPDATE";
				    		 resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->WARNING:NO UPDATE CANNOT FIND KEY UPDATE 002]").append(Constants.newLine);
				    	     errorRow++;
				    	  }else{
				    		 completeRow++;
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
			  
			  /** Validate TotaRecord vs Count in Line **/
	    	  if(totalRecord != lineNo){
	    		
	    		  errorMsg = "จำนวน Total Record["+totalRecord+"] ที่ระบุ ไม่เท่ากับจำนวน Line["+lineNo+"]";
	    		  errorCode= "RecordCountException";
	    		  errorRow++;
	    	  }
			  
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
			  
			   /*Case Table Have Post Function **/
	    	  if( !"N".equalsIgnoreCase(tableBean.getPostFunction())){
	    		  logger.info("**** Script "+tableBean.getTableName()+" Post Function name:"+tableBean.getPostFunction());
	    		  String[] errors = PostFunction.process(conn, tableBean,userBean);
	    	  }
			  
			  
			 /** Put to MAP **/	
			 initConfigMap.put(tableBean.getTableName(), tableBean);
			 
			 /** Add Result **/
			 results[0] = errorMsg;
			 results[1] = completeRow+"";
			 results[2] = errorCode;
			 results[3] = totalRecord+"";
			 
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
	
	public  String[] importToDBBK(Connection conn,LinkedHashMap<String,TableBean> initConfigMap,TableBean tableBean,FTPFileBean ftpFileBean,User userBean) throws Exception {
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
        String[] results = new String[3];
        int lineId =0;
	    try {
	    	 //Debug Time
	    	  
	    	  /*Case Table Have Pre Function **/
	    	  if( !"N".equalsIgnoreCase(tableBean.getPreFunction())){
	    		  logger.debug("**** Script Pre Function name:"+tableBean.getPreFunction());
	    		  String[] errors = PreFunction.process(conn, tableBean,userBean);
	    	  }
	    	  
	    	  logger.debug("ImportToDB");
	    	  dataTextLineArr = ftpFileBean.getDataLineText();
	    	  
			  // init prepareStatment
			  logger.debug("sqlInsert:"+tableBean.getPrepareSqlIns());
			  logger.debug("sqlUpd:"+tableBean.getPrepareSqlUpd());
			  
			 
			  if(tableBean.getActionDB().indexOf("D") != -1){
				 logger.debug("sqlDelete:"+tableBean.getPrepareSqlDelete());
			     psDelete = conn.prepareStatement(tableBean.getPrepareSqlDelete());
			  }
			  totalRos = dataTextLineArr.length;
			  logger.debug("strTxts length:"+totalRos);

			  for(line = 0;line<totalRos;line++){
				  try{
					  lineNo++;
					  lineStr = Utils.isNull(dataTextLineArr[line]);   	  
			    	  if( !Utils.isNull(lineStr).equals("")){
			    		  
			    		  if(lineStr.startsWith("H")){
			    			lineId = 0;
			    		  }else{
			    		    lineId++;
			    		  }
			    		 // canExc = insertScanBarcodeTable(conn,userBean,lineStr,lineId);
			    		  
				    	  //stamp log result
				    	  if(canExc == 0){
				    		 errorMsg ="NO UPDATE KEY NOT FOUND";
				    		 errorCode = "NOUPDATE";
				    		 resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->WARNING:NO UPDATE CANNOT FIND KEY UPDATE 002]").append(Constants.newLine);
				    	     errorRow++;
				    	  }else{
				    		 completeRow++;
				    	  }
				    	  //initail 
				    	  canExc = 0;
			    	  }
			       
		    	  }catch(Exception e){
		    		  logger.error("ERROR: \n"+e.getMessage(),e);
		    		  errorMsg ="Error:Line{"+lineNo+"}:{LineText:"+lineStr+"}{ErrorMsg:"+e.getMessage()+"}";
		    		  errorCode = ExceptionHandle.getExceptionCode(e);
		    		  errorRow++;
		    		  resultTxt.append(lineStr.replaceAll("\\|", ",")).append(",[LINE["+lineNo+"]->ERROR:"+e.getMessage()+"]").append(Constants.newLine);
		    		  
		    		  /** Case Debug No Rollback ALL**/
		    		  if("true".equals("true")){
			    		    break;
			    	  }
		    		 
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
			  
			   /*Case Table Have Post Function **/
	    	  if( !"N".equalsIgnoreCase(tableBean.getPostFunction())){
	    		  logger.info("**** Script Post Function name:"+tableBean.getPostFunction());
	    		  String[] errors = PostFunction.process(conn, tableBean,userBean);
	    	  }
			  
			  
			 /** Put to MAP **/	
			 initConfigMap.put(tableBean.getTableName(), tableBean);
			 
			 /** Add Result **/
			 results[0] = errorMsg;
			 results[1] = completeRow+"";
			 results[2] = errorCode;
			 
			 
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
	
}
