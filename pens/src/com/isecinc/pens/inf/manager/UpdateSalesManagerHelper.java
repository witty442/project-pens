package com.isecinc.pens.inf.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.process.bean.KeyNoImportTransBean;
import com.isecinc.pens.inf.manager.process.bean.FileImportTransBean;
import com.isecinc.pens.inf.manager.process.bean.LineImportTransBean;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MReceipt;

public class UpdateSalesManagerHelper {
	public static Logger logger = Logger.getLogger("PENS");
	
	
	public List<FileImportTransBean> getDataFileFromTempListByTable(Connection conn,String transType,String tableName) throws Exception{
		PreparedStatement  ps = null;
		ResultSet rs =null;
		StringBuffer sql = new StringBuffer();
		List<FileImportTransBean> dataFileList = new ArrayList<FileImportTransBean>();
		try{
			sql.append("\n select file_name,count(*) as c from t_temp_import_trans");
			sql.append("\n where table_name ='"+tableName+"'");
			//Case ReImport Run line created < today
			if(Constants.TRANSACTION_REUTS_TRANS_TYPE.equalsIgnoreCase(transType)){
				//sql.append("\n and DATE(created) < DATE(sysdate()) ");
				sql.append("\n and import_type ='ERROR'");
			}else{
				sql.append("\n and import_type ='TEMP'");
			}
			sql.append("\n group by file_name ");
			sql.append("\n order by file_name ");
			//logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				FileImportTransBean item = new FileImportTransBean();
				item.setFileName(Utils.isNull(rs.getString("file_name")));
				item.setFileCount(rs.getInt("c"));
				//get Key_no by file_name
				item.setKeyNoImportTransList(getKeyNoListByFileName(conn, tableName, item.getFileName()));
				dataFileList.add(item);
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
		return dataFileList;
	}
	
	public List<KeyNoImportTransBean> getKeyNoListByFileName(Connection conn,String tableName,String fileName) throws Exception{
		PreparedStatement  ps = null;
		ResultSet rs =null;
		StringBuffer sql = new StringBuffer();
		List<KeyNoImportTransBean> dataList = new ArrayList<KeyNoImportTransBean>();
		try{
			sql.append("\n select key_no,receipt_no,count(*) as c from t_temp_import_trans");
			sql.append("\n where table_name ='"+tableName+"'");
			sql.append("\n and file_name ='"+fileName+"'");
			sql.append("\n group by key_no,receipt_no ");
			sql.append("\n order by key_no ");
			//logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				KeyNoImportTransBean item = new KeyNoImportTransBean();
				item.setFileName(fileName);
				item.setTableName(tableName);
				item.setKeyNo(Utils.isNull(rs.getString("key_no")));
				item.setReceiptNo(Utils.isNull(rs.getString("receipt_no")));
				//get All LineStr by Key No
				item.setLineList(getLineStrByKeyNo(conn, tableName, fileName, item.getKeyNo()));
				dataList.add(item);
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
		return dataList;
	}
	public List<LineImportTransBean> getLineStrByKeyNo(Connection conn,String tableName,String fileName,String keyNo) throws Exception{
		PreparedStatement  ps = null;
		ResultSet rs =null;
		StringBuffer sql = new StringBuffer();
		List<LineImportTransBean> dataList = new ArrayList<LineImportTransBean>();
		try{
			sql.append("\n select key_no,line_str ,seq from t_temp_import_trans");
			sql.append("\n where table_name ='"+tableName+"'");
			sql.append("\n and file_name ='"+fileName+"'");
			sql.append("\n and key_no ='"+keyNo+"'");
			sql.append("\n order by seq ");
			//logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				LineImportTransBean item = new LineImportTransBean();
				item.setFileName(fileName);
				item.setTableName(tableName);
				item.setKeyNo(keyNo);
				item.setSeq(rs.getLong("seq"));
				item.setLineStr(Utils.isNull(rs.getString("line_str")));
				dataList.add(item);
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
		return dataList;
	}
	
	public void insertDataFileToTemp(Connection conn,LinkedHashMap<String,TableBean> initConfigMap) throws Exception{
		try{
			Set s = initConfigMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) { //for 1
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) initConfigMap.get(tableName);
				logger.info("---Import TableName:"+tableBean.getTableName()+"---");

		        if(tableBean.getDataLineList() != null && tableBean.getDataLineList().size() > 0){
		        	/** case have more one file **/
					for(int k =0;k<tableBean.getDataLineList().size();k++){ //for 2
						FTPFileBean ftpFileBean = (FTPFileBean)tableBean.getDataLineList().get(k);	
						if(ftpFileBean.getDataLineText() != null && ftpFileBean.getDataLineText().length >0){
							insertLineToTempByFileName(conn, tableBean, ftpFileBean);
						}else{
							logger.debug("Ftp File TableName:"+tableBean.getTableName()+":NOT FOUND FILE");
						}
						
					}//for 2
		        }//if	
			}//for 1
		}catch(Exception e){
			throw e;
		}
	}
	
	public void insertLineToTempByFileName(Connection conn,TableBean tableBean,FTPFileBean ftpFileBean) throws Exception{
		String[] dataTextLineArr = null;
		String fileName = "";
		int totalRos = 0;
		int line = 0;
		int lineNo =0;
		String lineStr = "";
		String[] lineStrArr = null;
		double amount = 0;
		String receiptNo = "";
		String docStatus = "";
		try{
			//Clear Temp Old duplicate file_name By manual copy to folder 
			deleteTempImportByFileName(conn, ftpFileBean.getFileName());
			
		   dataTextLineArr = ftpFileBean.getDataLineText();
   	       fileName = ftpFileBean.getFileName();
   	       String keyInLine = "";
		   totalRos = dataTextLineArr.length;
		   logger.debug("strTxts length:"+totalRos);
		   
		   for(line = 0;line<totalRos;line++){
			  lineNo++;
			  lineStr = Utils.isNull(dataTextLineArr[line]);
	    	  if( !Utils.isNull(lineStr).equals("")){
	    		  //**Check Header Or Line**/
    		      logger.debug("tableName["+tableBean.getTableName()+"]childTable["+tableBean.getChildTable()+"] lineStr["+lineStr+"]");
    		      amount = 0;
    		      receiptNo = "";
    		      /****** Set Data Check Line Error for import again ****/
    		      lineStr += " "; /** case last String | **/
    			  lineStrArr = lineStr.split(Constants.delimeterPipe);
    			  
    			  if(tableBean.getTableName().equalsIgnoreCase("t_receipt")  && lineStr.startsWith("B")){
					  receiptNo = new ImportReceiptFunction().findReceiptByKeyNo(conn,  Utils.isNull(lineStrArr[2]));
					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
					  amount = Utils.convertStrToDouble(lineStrArr[4]);
					  docStatus =  Utils.isNull(lineStrArr[5]);
					  
    			  }else if(tableBean.getTableName().equalsIgnoreCase("t_receipt")  && lineStr.startsWith("H")){
					  receiptNo = new ImportReceiptFunction().findReceiptByKeyNo(conn,  Utils.isNull(lineStrArr[2]));
					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
					  amount = Utils.convertStrToDouble(lineStrArr[7]);
					  docStatus =  Utils.isNull(lineStrArr[4]);
					  
				  }else  if(tableBean.getChildTable().equalsIgnoreCase("t_receipt_line") && lineStr.startsWith("L")){
					  receiptNo = new ImportReceiptFunction().findReceiptByKeyNo(conn,  Utils.isNull(lineStrArr[2]));
					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
					  amount = Utils.convertStrToDouble(lineStrArr[5]);
				  
				  }else if(tableBean.getTableName().equalsIgnoreCase("t_receipt_orcl")  && lineStr.startsWith("B")){
					  receiptNo = new ImportReceiptFunction().findReceiptByKeyNo(conn,  Utils.isNull(lineStrArr[2]));
					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
					  amount = Utils.convertStrToDouble(lineStrArr[4]);
					  docStatus =  Utils.isNull(lineStrArr[5]);
					  
    			  }else if(tableBean.getTableName().equalsIgnoreCase("t_receipt_orcl")  && lineStr.startsWith("H")){
					  receiptNo = new ImportReceiptFunction().findReceiptByKeyNo(conn,  Utils.isNull(lineStrArr[2]));
					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
					  amount = Utils.convertStrToDouble(lineStrArr[7]);
					  docStatus =  Utils.isNull(lineStrArr[4]);
					  
				  }else  if(tableBean.getChildTable().equalsIgnoreCase("t_receipt_line_orcl") && lineStr.startsWith("L")){
					  receiptNo = new ImportReceiptFunction().findReceiptByKeyNo(conn,  Utils.isNull(lineStrArr[2]));
					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
					  amount = Utils.convertStrToDouble(lineStrArr[5]);
				  }else if(tableBean.getTableName().equalsIgnoreCase("t_order") && lineStr.startsWith("H")){
					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
				  }else if(tableBean.getChildTable().equalsIgnoreCase("t_order_line") && lineStr.startsWith("L")){
					  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
				  }else if(tableBean.getTableName().equalsIgnoreCase("t_order_orcl") && lineStr.startsWith("H")){
					  keyInLine = Utils.isNull(lineStrArr[2])+"|"+tableBean.getTableName();
				  }else if(tableBean.getChildTable().equalsIgnoreCase("t_order_line_orcl") && lineStr.startsWith("L")){
					  keyInLine = Utils.isNull(lineStrArr[3])+"|"+tableBean.getTableName();
				  }else if(tableBean.getTableName().equalsIgnoreCase("t_bill_plan") && lineStr.startsWith("H")){
    				  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
    			  }else if(tableBean.getChildTable().equalsIgnoreCase("t_bill_plan_line") && lineStr.startsWith("L")){
	    			  keyInLine = Utils.isNull(lineStrArr[1])+"|"+tableBean.getTableName();
    			  }else if(tableBean.getTableName().equalsIgnoreCase("t_adjust") && lineStr.startsWith("H")){
		    		 keyInLine = Utils.isNull(lineStrArr[2])+"|"+tableBean.getTableName();
		    	 }
    				  
				  //Insert Line to table t_temp_import_trans 
				  insertLineToTempImportDAO(conn,fileName,tableBean.getTableName(),keyInLine
						  ,lineStr,"",lineNo,"TEMP",amount,receiptNo,docStatus);
	    	  }//if check null
		   }//for
		   
		}catch(Exception e){
			throw e;
		}
	}
	
	public void  insertLineToTempImportDAO(Connection conn,String fileName,String tableName
			,String keyNo,String lineStr ,String errMsg,int seq,String importType,double amount
			,String receiptNo,String docStatus) throws Exception{
		PreparedStatement  ps = null;
		StringBuffer sql = new StringBuffer();
		try{
			//keyNo = key_no|tableName
		    String[] keyNoArr = keyNo.split("\\|");
		   
			sql.append("\n insert into t_temp_import_trans");
			sql.append("\n (file_name,table_name,line_str,created,key_no,seq"
					+ ",error_msg,import_type,amount,receipt_no,doc_status)");
			sql.append("\n values(?,?,?,?,?,?,?,?,?,?,?)");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, fileName);
			ps.setString(2, tableName);
			ps.setString(3, lineStr);
			ps.setTimestamp(4 , new java.sql.Timestamp(new Date().getTime()));
			ps.setString(5, keyNoArr[0]);
			ps.setInt(6, seq);
			ps.setString(7, errMsg);
			ps.setString(8, importType);
			ps.setDouble(9, amount);
			ps.setString(10, receiptNo);
			ps.setString(11, docStatus);
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}
	public void updateStatusLineToTempImportDAO(Connection conn,String fileName,String tableName
			,String keyNo,long seq,String errMsg) throws Exception{
		PreparedStatement  ps = null;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n update t_temp_import_trans");
			sql.append("\n set error_msg = '"+errMsg+"',import_type='ERROR'");
			sql.append("\n where table_name ='"+tableName+"' and file_name ='"+fileName+"'");
			sql.append("\n and key_no ='"+keyNo+"' and seq ="+seq);
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}

	public void  deleteTempImportByFileName(Connection conn,String fileName) throws Exception{
		PreparedStatement  ps = null;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n delete from t_temp_import_trans");
			sql.append("\n where file_name ='"+fileName+"'");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}
	
	public void  checkDeleteImportTempTrans(Connection conn) throws Exception{
		PreparedStatement  ps = null;
		ResultSet rs =null;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n select  table_name,key_no,count(*) as total_row from t_temp_import_trans_error");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				if(checkAllLineImportTempTransNoError(conn,rs.getString("table_name"),rs.getString("key_no"))){
					//deleteTempImportByKey(conn, "'"+rs.getString("table_name")+"'", "'"+rs.getString("key_no")+"'");
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
	}
	public boolean  checkAllLineImportTempTransNoError(Connection conn,String tableName,String keyNo) throws Exception{
		PreparedStatement  ps = null;
		ResultSet rs =null;
		StringBuffer sql = new StringBuffer();
		boolean r = true;
		try{
			sql.append("\n select count(*) as c from t_temp_import_trans_error");
			sql.append("\n where table_name ='"+tableName+"'");
			sql.append("\n and key_no ='"+keyNo+"'");
			sql.append("\n and (error_msg is not null and error_msg <>'' )");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				if(rs.getInt("c") >0){
					r = false;
				}
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
		return r;
	}
	
	/** Import LineStr to t_temp_import_trans **/
	public void  deleteTempImportByKey(Connection conn,String fileName,String tableName,String keyNo) throws Exception{
		PreparedStatement  ps = null;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n delete from t_temp_import_trans");
			sql.append("\n where table_name='"+tableName+"'");
			sql.append("\n and file_name ='"+fileName+"'");
			sql.append("\n and key_no ='"+keyNo+"'");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}
	
	/** Get Error LineStr to t_temp_import_trans **/
	public static List<FTPFileBean>  getLineErrorToReImport(Connection conn,String tableName){
		PreparedStatement  ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		FTPFileBean ftpDetailBean = null;
		List<FTPFileBean> dataLineList = new ArrayList<FTPFileBean>();
		String fileName = "";
		try{
			logger.debug("*** Start getLineErrorToReImport ***");
			sql.append("\n select distinct file_name from  t_temp_import_trans_error");
			sql.append("\n where table_name ='"+tableName+"'");
			sql.append("\n order by seq asc");
	
			ps = conn.prepareStatement(sql.toString());
			rs= ps.executeQuery();
			while(rs.next()){
				fileName = Utils.isNull(rs.getString("file_name"));
				ftpDetailBean = getLineDetailErrorToReImport(conn, tableName, fileName);
				dataLineList.add(ftpDetailBean);
			}
			
			deleteTempImportErrorByTableName(conn,tableName);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();
				}
				if(rs != null){
					rs.close();
				}
			}catch(Exception ee){}
		}
		return dataLineList;
	}
	
	public static FTPFileBean  getLineDetailErrorToReImport(Connection conn,String tableName,String fileName){
		PreparedStatement  ps = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		FTPFileBean ftpBean = null;
		List<String> dataLineTextList = new ArrayList<String>();
		String[] dataLineTextArr = null;
		try{
			logger.debug("*** Start getLineErrorToReImport ***");
			sql.append("\n select * from  t_temp_import_trans_error");
			sql.append("\n where table_name ='"+tableName+"'");
			sql.append("\n and file_name ='"+fileName+"'");
			sql.append("\n order by seq asc");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs=ps.executeQuery();
			while(rs.next()){
				dataLineTextList.add(Utils.isNull(rs.getString("line_str")));
				fileName = Utils.isNull(rs.getString("file_name"));
				logger.debug("dataLineRrror:"+Utils.isNull(rs.getString("line_str")));
			}
			if(dataLineTextList != null && dataLineTextList.size() >0){
				ftpBean = new FTPFileBean();
				dataLineTextArr = dataLineTextList.toArray(new String[0]);
	    		ftpBean.setFileName(fileName+"");
	    		ftpBean.setDataLineText(dataLineTextArr);
	    		ftpBean.setFileSize("1");
        		ftpBean.setFileCount(dataLineTextArr.length);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();
				}
				if(rs != null){
					rs.close();
				}
			}catch(Exception ee){}
		}
		return ftpBean;
	}
	
	/** Import LineStr to t_temp_import_trans **/
	public static void  deleteTempImportErrorByTableName(Connection conn,String tableName) throws Exception{
		PreparedStatement  ps = null;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n delete from t_temp_import_trans_error");
			sql.append("\n where table_name =? ");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, tableName);
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}
	
	
}
