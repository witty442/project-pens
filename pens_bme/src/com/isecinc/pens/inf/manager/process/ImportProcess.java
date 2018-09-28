package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.Message;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.InterfaceHelper;
import com.pens.util.Utils;

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
			  
			 // psInsert = conn.prepareStatement(tableBean.getPrepareSqlIns());
			 // psUpdate = conn.prepareStatement(tableBean.getPrepareSqlUpd());
			 
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
			    		  canExc = insertScanBarcodeTable(conn,userBean,lineStr,lineId);
			    		  
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
	
	public static int insertScanBarcodeTable(Connection conn,User user,String dataLineText,int lineId)  throws Exception {
		logger.debug("importFileScanBarcode :Text");
		int i=0;
		int l=0;
        int r = 1;
	    PreparedStatement psH = null;
	    PreparedStatement psL = null;
		try {
			logger.debug("Start insert scan barcode DB");
			
			 StringBuffer sql = new StringBuffer("");
			 sql.append("INSERT INTO pensbme_barcode_scan(doc_no, Doc_date, Cust_Group, Cust_no, Remark, Status, FILE_NAME,Create_date, Create_user)");
			 sql.append("VALUES(?,?,?, ?,?,?, ?,?,?)");
			  
			 psH = conn.prepareStatement(sql.toString());
			 
			 StringBuffer sqlLine = new StringBuffer("");
			 sqlLine.append("INSERT INTO pensbme_barcode_scan_item(doc_no, LINE_ID, Barcode, Material_master, Group_code, Pens_item, Create_date, Create_user)");
			 sqlLine.append("VALUES( ?,?,?, ?,?,? ,?,?)");
			  
			 psL = conn.prepareStatement(sqlLine.toString());
			 
			String[] lineStrArrPipe =  dataLineText.split("\\|");
			
			if(lineStrArrPipe[0].startsWith("H")){
				logger.debug("Insert H dataLineText:"+dataLineText);
				//H020047-99-201504-001|02042015|020047|020047-99||C|20150403095903-admin-barcode.txt
				/*logger.debug("0:"+lineStrArrPipe[0].substring(1,lineStrArrPipe[0].length()));
				logger.debug("1:"+lineStrArrPipe[1]);
				logger.debug("2:"+lineStrArrPipe[2]);
				logger.debug("3:"+lineStrArrPipe[3]);
				logger.debug("4:"+lineStrArrPipe[4]);
				logger.debug("5:"+lineStrArrPipe[5]);
				logger.debug("6:"+lineStrArrPipe[6]);*/
				
				Date docDate = Utils.parse(lineStrArrPipe[1], Utils.DD_MM_YYYY_WITHOUT_SLASH);
				
				psH.setString(1,lineStrArrPipe[0].substring(1,lineStrArrPipe[0].length()));//docNo
				psH.setDate(2, new java.sql.Date(docDate.getTime()));//docDate
				psH.setString(3,lineStrArrPipe[2]);//custGroup
				psH.setString(4,lineStrArrPipe[3]);//custNo
				psH.setString(5,lineStrArrPipe[4]);//remark
				psH.setString(6,lineStrArrPipe[5]);//status
				psH.setString(7,lineStrArrPipe[6]);//fileName
				psH.setDate(8, new java.sql.Date(new Date().getTime()));//createDate
				psH.setString(9, user.getUserName());//createUser
				
				psH.execute();
			}else{
				logger.debug("Insert L dataLineText:"+dataLineText);
				logger.debug("lineId:"+lineId);
				
				//D020047-99-201504-001|8850009385309|ME1106A4VI|ME1106|835057
                psL.setString(1,lineStrArrPipe[0].substring(1,lineStrArrPipe[0].length()));//DocNo
                psL.setInt(2,lineId);//lineId
                psL.setString(3,lineStrArrPipe[1]);//Barcode
                psL.setString(4,lineStrArrPipe[2]);//Material
                psL.setString(5,lineStrArrPipe[3]);//GroupCode
                psL.setString(6,lineStrArrPipe[4]);//PENSITEM
                psL.setDate(7, new java.sql.Date(new Date().getTime()));
                psL.setString(8, user.getUserName());//createUser
				
                psL.execute();
			}

		    logger.debug("End insert Scan barcode DB");
		} catch (Exception e) {
			r = 0;
			throw e;
		}finally{
		      if(psH != null){
		    	  psH.close();psH=null;
		      }
		      if(psL != null){
		    	  psL.close();psL=null;
			  }
	    }
		return r;
	}
	
	
}
