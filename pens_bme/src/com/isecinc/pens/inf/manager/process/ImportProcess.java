package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.Utils;

public class ImportProcess {
  
	public static Logger logger = Logger.getLogger("PENS");
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
        String errorMsg ="";
        String errorCode = "";
        String[] dataTextLineArr = null;
        String[] results = new String[3];

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
			  
			  psInsert = conn.prepareStatement(tableBean.getPrepareSqlIns());
			  psUpdate = conn.prepareStatement(tableBean.getPrepareSqlUpd());
			 
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
			    		  
			    		  /** Case Table m_inventory_onhand Delete by User_id and Insert new **/
			    		  if("m_inventory_onhand".equalsIgnoreCase(tableBean.getTableName())){
			    			  /** Delete form m_inventory_onhand where user_id =''   1 Transaction **/
			    			  if(tableBean.getActionDB().indexOf("D") != -1 && line ==0){
					    		  //logger.debug("**********Start Delete ******************");
					    		  psDelete = ImportHelper.spiltLineArrayToDeleteStatement(conn, tableBean, lineStr, psDelete,userBean);
						    	  canExc = psDelete.executeUpdate();
						    	  //logger.debug("canDelete:"+canExc);
				    		  }
			    			  if(tableBean.getActionDB().indexOf("I") != -1){
					    		  //logger.debug("**********Start Insert ******************");
					    		  psInsert = ImportHelper.spiltLineArrayToInsertStatement(conn,tableBean,lineStr,psInsert,userBean);
						    	  canExc = psInsert.executeUpdate();
					    	      //logger.debug("canIns:"+canExc);
			    			  }
			    			  
			    		  }else if("m_pd".equalsIgnoreCase(tableBean.getTableName())){
				    			  /** Delete form m_pd where user_id =''   1 Transaction **/
				    			  if(tableBean.getActionDB().indexOf("D") != -1 && line ==0){
						    		  //logger.debug("**********Start Delete ******************");
						    		  psDelete = ImportHelper.spiltLineArrayToDeleteStatement(conn, tableBean, lineStr, psDelete,userBean);
							    	  canExc = psDelete.executeUpdate();
							    	  //logger.debug("canDelete:"+canExc);
					    		  }
				    			  if(tableBean.getActionDB().indexOf("I") != -1){
						    		  //logger.debug("**********Start Insert line By SalesCode (V203)******************");
				    				  if(lineStr.indexOf(userBean.getUserName()) != -1){
						    		     psInsert = ImportHelper.spiltLineArrayToInsertStatement(conn,tableBean,lineStr,psInsert,userBean);
							    	     canExc = psInsert.executeUpdate();
						    	         //logger.debug("canIns:"+canExc);
				    				  }else{
				    					  canExc = -1;//case no import SalesCode no match
				    				  }
				    			  }

				    	  /** case Import SalesRep update or insert record = userLogin only **/
			    		  }else if("ad_user".equalsIgnoreCase(tableBean.getTableName())){
			    			  
			    			  if(tableBean.getActionDB().indexOf("U") != -1){
			    				//logger.debug("**********Start Insert line By SalesCode (V203)******************");
			    				  if(lineStr.indexOf(userBean.getUserName()) != -1){
			    					  psUpdate = ImportHelper.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdate,userBean);
							    	  canExc = psUpdate.executeUpdate();
							    	  //logger.debug("canUpdate:"+canExc);
						    	     
						    	     if(canExc ==0 && tableBean.getActionDB().indexOf("I") != -1){
							    		//logger.debug("**********Start Insert line By SalesCode (V203)******************");
					    				 if(lineStr.indexOf(userBean.getUserName()) != -1){
							    		     psInsert = ImportHelper.spiltLineArrayToInsertStatement(conn,tableBean,lineStr,psInsert,userBean);
								    	     canExc = psInsert.executeUpdate();
							    	         //logger.debug("canIns:"+canExc);
					    				 }else{
					    					 canExc = -1;//case no import SalesCode no match
					    				 }
								     }
			    				  }else{
			    					  canExc = -1;//case no import SalesCode no match
			    				  }
				    		  }

			    		  }else{
				    		  if(tableBean.getActionDB().indexOf("U") != -1){
					    		  //logger.debug("**********Start Update ******************");
					    		  psUpdate = ImportHelper.spiltLineArrayToUpdateStatement(conn, tableBean, lineStr, psUpdate,userBean);
						    	  canExc = psUpdate.executeUpdate();
						    	  logger.debug("canUpdate:"+canExc);
				    		  }
	               
					    	  if(canExc ==0 && tableBean.getActionDB().indexOf("I") != -1){
					    		  //logger.debug("**********Start Insert ******************");
					    		  psInsert = ImportHelper.spiltLineArrayToInsertStatement(conn,tableBean,lineStr,psInsert,userBean);
						    	  canExc = psInsert.executeUpdate();
					    	      logger.debug("canIns:"+canExc);
					    	  }
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
		    		  
		    		  /** Case Debug No Rollback ALL**/
		    		  if(EnvProperties.getInstance().getProperty("conversion.master.transaction.rollback.all").equals("true")){
			    		    break;
			    	  }
		    		  if(EnvProperties.getInstance().getProperty("conversion.trans.transaction.rollback.all").equals("true")){
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
