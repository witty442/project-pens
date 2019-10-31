package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ImportPosProcess extends InterfaceUtils{

	private static Logger logger = Logger.getLogger("PENS");

	public static void runProcess(User user,MonitorBean m) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		boolean rollBackFlag = false;
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		int countFileMap = 0;
		MonitorTime monitorTime = null;
		Map<String, String> batchParamMap = null;
	    String fileName = "";
	    String fileType = "";
	    ImportDAO importDAO  = new ImportDAO();
	    String errorCode = "SuccessException";
	    String errorMsg = ExceptionHandle.ERROR_MAPPING.get(errorCode);
		try{
			/** prepare Paramenter **/
			batchParamMap = m.getBatchParamMap();
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
				
				/** check FileName duplicate **/
				boolean dup = importDAO.importPOSFileNameIsDuplicate(conn, fileName);
				if(dup){
					taskStatusInt = Constants.STATUS_FAIL;
				}
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());
			
				if(taskStatusInt == Constants.STATUS_FAIL){
					errorCode = "DuplicateImportFileException";
					errorMsg = ExceptionHandle.ERROR_MAPPING.get(errorCode);
				}
		
				if(taskStatusInt != Constants.STATUS_FAIL){
					monitorTime = new MonitorTime("Import Excel to DB");
					
					MonitorItemBean modelItem = new MonitorItemBean();
					modelItem.setMonitorId(monitorModel.getMonitorId());
					modelItem.setSource("");
					modelItem.setDestination("");
					modelItem.setTableName("Import SaleOut Wacoal Lotus");
					modelItem.setFileName(fileName);
					modelItem.setStatus(Constants.STATUS_START);
					modelItem.setDataCount(0);
					modelItem.setFileSize("");
					modelItem.setSubmitDate(new Date());
					modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
					dao.insertMonitorItem(connMonitor, modelItem);
					
					modelItem = importToDB(conn,connMonitor,modelItem,user,dataFile,fileType , fileName);
					logger.debug("Result FailCount:"+modelItem.getFailCount() );
					logger.debug("Result SuccessCount:"+modelItem.getSuccessCount() );
					
					if(modelItem.getFailCount() > 0){
						
						rollBackFlag = true;
						taskStatusInt = Constants.STATUS_FAIL;
						errorCode = "ImportErrorException";
						errorMsg = ExceptionHandle.ERROR_MAPPING.get(errorCode);
						errorMsg = errorMsg.replaceAll("\\$", fileName);
						logger.debug("errorMsg["+errorMsg+"]");
						
						modelItem.setStatus(Constants.STATUS_FAIL);
						modelItem.setErrorCode("ImportErrorException");
						modelItem.setErrorMsg(errorMsg);
						
					}else{
						modelItem.setStatus(Constants.STATUS_SUCCESS);
						rollBackFlag = false;
					}
					/**Update Status Monitor item **/
					dao.updateMonitorItem(connMonitor, modelItem);
					
					monitorTime.debugUsedTime();	
				}//if
			}//if data file
			
			logger.debug("isExc:"+isExc+" ,rollBackFlag:"+rollBackFlag);
			
			/** Check Is Excute and No error **/
			if(isExc && rollBackFlag == false){
				logger.debug("Transaction commit");
				conn.commit();
			    
				/** End process ***/
				logger.debug("Update Monitor to Success:"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				monitorModel.setErrorCode(errorCode);
				monitorModel.setErrorMsg(errorMsg);
				dao.updateMonitor(connMonitor,monitorModel);
				
				//monitorTime.debugUsedTime();
				
			}else if(isExc && rollBackFlag == true){
				logger.debug("Transaction Rolback");
				conn.rollback();
				
				logger.debug("Update Monitor to Fail :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				monitorModel.setErrorCode(errorCode);
				monitorModel.setErrorMsg(errorMsg);
				
				dao.updateMonitor(connMonitor,monitorModel);
			}else{
				logger.debug("Transaction commit");
				conn.commit();
				
				/** End process ***/
				logger.debug("Update Monitor to Success :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				monitorModel.setErrorCode(errorCode);
				monitorModel.setErrorMsg(errorMsg);
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
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_SALEOUT_WACOAL);
			
			if(conn != null){
			  logger.debug("Transaction Rolback");
			  conn.rollback();
			}
		
		}finally{
		
			if(conn != null){
				conn.close();
				conn =null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
	}
	
	public static MonitorItemBean importToDB(Connection conn,Connection conMoni ,MonitorItemBean mi,User user,FormFile dataFile,String fileType ,String fileName) throws Exception {
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    Map<String, ImportSummary> successMap = new HashMap<String, ImportSummary>();
	    Map<String, ImportSummary> errorMap = new HashMap<String, ImportSummary>();
	    boolean importError = false;
	    BigDecimal importId = SequenceProcess.getNextValueBig("POS_ORDER_IM_ID");
		try {
			if (dataFile != null) {
				//delete prev data
				//psDelete = conn.prepareStatement("delete from pensbme_pos_order_temp");
				//int delCount = psDelete.executeUpdate();
				//logger.debug("delete pensbme_pos_order_temp :total record["+delCount+"]");
				
				StringBuffer sql = new StringBuffer("");
			
				sql.append(" INSERT INTO pensbme_pos_order_temp( \n");
				sql.append(" order_number, account_number, ordered_date, subinventory,  \n");
				sql.append(" cust_po_number, salesrep_number, item_no,  \n");
				sql.append(" unit_price, qty, create_date,file_name,import_id)  \n");
				sql.append(" VALUES( ?,?,?,? ,?,?,? ,?,?,?,?,?)");

				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 1; // row of begin data
				int maxColumnNo = 9; // max column of data per row
				
				Workbook wb1 = null;
				XSSFWorkbook wb2 = null;
				Sheet sheet = null;
				
				if("xls".equalsIgnoreCase(fileType)){
				   wb1 = new HSSFWorkbook(dataFile.getInputStream());//97-2003
				   sheet = wb1.getSheetAt(sheetNo);
				   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
				}else{
				   OPCPackage pkg = OPCPackage.open(dataFile.getInputStream());
				   wb2 = new XSSFWorkbook(pkg);
				   sheet = wb2.getSheetAt(sheetNo);
				   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
				}
				
				Row row = null;
				Cell cell = null;
				
				String order_number = "";
				String account_number = "";
				String ordered_date = "";
				String subinventory = "";
				String cust_po_number ="";
				String salerep_number = "";
				String item_no = "";
				double unit_price = 0;
				double qty = 0;
				
	            int no = 0;
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
				for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
					row = sheet.getRow(i);
					
					/** Check Row is null **/
					Cell cellCheck = row.getCell((short) 0);
					Object cellCheckValue = xslUtils.getCellValue(0, cellCheck);
					String rowCheck = Utils.isNull(cellCheckValue);
					//logger.debug("row["+i+"]rowCheckValue["+rowCheck+"]");
					if("".equals(rowCheck)){
						break;
					}
					//logger.debug("Row:"+i);
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						//logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						
						if(colNo==0){
						   //order_number
						   order_number = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(1,  order_number);
						}else if(colNo==1){
						   //account_number
						   account_number = Utils.isNull(cellValue);
						   ps.setString(2, account_number);
						}else if(colNo==2){
						   //ordered_date
						  java.util.Date asOfDate =  (java.util.Date) cellValue;
						   //logger.debug("Date:"+asOfDate);
						   ordered_date = DateUtil.stringValue(asOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
						   //logger.debug("salesDate:"+salesDate);
						   ps.setTimestamp(3, new java.sql.Timestamp(asOfDate.getTime()));  
						}else if(colNo==3){
						    //subinventory
						    subinventory = Utils.isNull(cellValue);
						    ps.setString(4, subinventory);
						}else if(colNo==4){
						   //cust_po_number
							cust_po_number = Utils.isNull(cellValue);
						    ps.setString(5, cust_po_number);
						}else if(colNo==5){
						   //salerep_number
						   salerep_number = Utils.isNull(cellValue);
						   ps.setString(6, salerep_number);
						}else if(colNo==6){
						  //item_no
						   item_no = Utils.convertDoubleToStrNoDigit(cellValue);
						   ps.setString(7, item_no);
						}else if(colNo==7){
						  //unit_price
						  unit_price = Utils.isDoubleNull(cellValue);
						  ps.setDouble(8, unit_price);
						
						}else if(colNo==8){
						   //qty
						   qty = Utils.isDoubleNull(cellValue);
						   ps.setDouble(9, qty);
						}
						  
					}//for column
					
					 //CREATE_DATE
					 ps.setTimestamp(10, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //fileName
					 ps.setString(11, fileName);
					 //import_id (each import )
					 ps.setBigDecimal(12, importId);
						
					 //Add Success Msg No Check PensItem
			         ImportSummary s = new ImportSummary();
			         s.setRow(i+1);
			         s.setLineArr(s.getRow()+","+order_number+","+account_number+","+ordered_date+","+subinventory+","+cust_po_number+","+salerep_number+","+item_no+","+unit_price+","+qty+","+"Success");
			         s.setMessage("Success");
			         successMap.put(i+"", s); 
		
					 ps.addBatch();
				}//for Row
				
				//Execute Batch Statement
				ps.executeBatch();
			}
			
			logger.debug("importError:"+importError);
			
			if(importError){
			    importError = true;
			   /** Success List **/
				 Iterator it = successMap.keySet().iterator();
				 while(it.hasNext()){
					  String key = (String)it.next();
					  //logger.debug("key:"+key);
					  ImportSummary mm = (ImportSummary)successMap.get(key);
					  successList.add(mm);
				  }
				 
				 //Sort
				  Collections.sort(successList, new Comparator<ImportSummary>() {
					  public int compare(ImportSummary a, ImportSummary b) {
					        return a.getRow()- b.getRow();
					    }
					});
				  
				  //Insert Success
				  if(successList != null && successList.size() >0){
					  for(int r=0;r<successList.size();r++){
						  ImportSummary m = successList.get(r);
				          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"SUCCESS",m.getLineArr());
					  }
				  }
				  
				  /** Error List **/
				  it = errorMap.keySet().iterator();
					 while(it.hasNext()){
						  String key = (String)it.next();
						 // logger.debug("key:"+key);
						  ImportSummary mm = (ImportSummary)errorMap.get(key);
						  errorList.add(mm);
					  }
					 
					 //Sort
					  Collections.sort(errorList, new Comparator<ImportSummary>() {
						  public int compare(ImportSummary a, ImportSummary b) {
						        return a.getRow()- b.getRow();
						    }
						});
					  
					//Insert FALI
					  if(errorList != null && errorList.size() >0){
						  for(int r=0;r<errorList.size();r++){
							  ImportSummary m = errorList.get(r);
					          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"FAIL",m.getLineArr());
						  }
					  }
					 
					/** Set Count Record **/
					mi.setSuccessCount(successList.size());
				    mi.setFailCount(errorList.size());
					  
			}else{
				
				importError = false;
				 /** Success List **/
				 Iterator it = successMap.keySet().iterator();
				 while(it.hasNext()){
					  String key = (String)it.next();
					  //logger.debug("key:"+key);
					  ImportSummary mm = (ImportSummary)successMap.get(key);
					  successList.add(mm);
				  }
				 
				 //Sort
				  Collections.sort(successList, new Comparator<ImportSummary>() {
					  public int compare(ImportSummary a, ImportSummary b) {
					        return a.getRow()- b.getRow();
					    }
					});
				//Insert Success
				  if(successList != null && successList.size() >0){
					  for(int r=0;r<successList.size();r++){
						  ImportSummary m = successList.get(r);
				          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"SUCCESS",m.getLineArr());
					  }
				  }
				/** Set Count Record **/
				mi.setSuccessCount(successList.size());
			    mi.setFailCount(0);
			    
			    
			    //insert to Oracle 
			    insertHead(conn,importId);
			}
		} catch (Exception e) {
			
			//request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			throw e;
		} finally {
			try {
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mi;
	}
	
	private static void insertHead(Connection conn,BigDecimal importId) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement psIn = null;
		ResultSet rs = null;
		String sqlSelect = "";
		String sqlIn = "";
		int index = 0;
		BigDecimal headId = null;
		try {	
			sqlIn = "insert into XXPENS_OM_POS_ORDER_MST \n";
			sqlIn +=" ( created_by, creation_date,last_updated_by, last_update_date,last_update_login, \n";
			sqlIn +=" header_id ,account_number,ordered_date , \n ";
			sqlIn +=" subinventory,cust_po_number ,salesrep_number, int_flag,order_number ) \n";
			sqlIn +=" values(?,?,?,?, ?,?,?,? ,?,?,?,?,?) \n";
				  
			logger.info("SQL:"+sqlIn);
			psIn = conn.prepareStatement(sqlIn);
			
			sqlSelect +=" select distinct order_number ,account_number,ordered_date , \n ";
			sqlSelect +=" subinventory,cust_po_number ,salesrep_number  from pensbme_pos_order_temp \n";
			sqlSelect +=" where import_Id ="+importId+"";
			sqlSelect +=" order by order_number asc";

			//logger.info("SQL:"+sql);
			ps = conn.prepareStatement(sqlSelect);
			rs = ps.executeQuery();
			while(rs.next()){
				index = 0;
				psIn.setInt(++index, 2408);
				psIn.setTimestamp(++index,new java.sql.Timestamp(new Date().getTime()));
				psIn.setInt(++index, 2408);
				psIn.setTimestamp(++index,new java.sql.Timestamp(new Date().getTime()));
				psIn.setDouble(++index, -1);
				
				//header_id gen next sequence
				headId = SequenceProcess.getNextValueBig("POS_ORDER");//gen next head_id
				psIn.setBigDecimal(++index, headId);
				psIn.setString(++index,rs.getString("account_number"));
				psIn.setDate(++index,rs.getDate("ordered_date"));
				psIn.setString(++index,rs.getString("subinventory"));
				psIn.setString(++index,rs.getString("cust_po_number"));
				psIn.setString(++index,rs.getString("salesrep_number"));
				psIn.setString(++index,"N");
				psIn.setString(++index,rs.getString("order_number"));//order_number
				
				psIn.executeUpdate();
				
				//insert Line
				insertLine(conn,headId,rs.getString("order_number"),importId);
			}			
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	private static void insertLine(Connection conn,BigDecimal headId,String orderNumber,BigDecimal importId) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement psIn = null;
		ResultSet rs = null;
		String sqlSelect = "";
		String sqlIn = "";
		int index = 0;
		int lineId = 0;			  
		try {
			sqlIn = " insert into XXPENS_OM_POS_ORDER_DT \n";
			sqlIn +=" ( created_by, creation_date,last_updated_by, last_update_date,last_update_login, \n";
			sqlIn +="   header_id,line_id,item_no,unit_price , qty,int_flag )\n ";
			sqlIn += " values(?,?,?,? ,?,?,?,? ,?,?,?)";
			psIn = conn.prepareStatement(sqlIn);  
			
			sqlSelect +=" select order_number ,account_number,ordered_date , \n ";
			sqlSelect +=" subinventory,cust_po_number ,salesrep_number,item_no,unit_price,qty from pensbme_pos_order_temp  \n";
			sqlSelect +=" where order_number ='"+orderNumber+"'";
			sqlSelect +=" and import_Id ="+importId+"";
			sqlSelect +=" order by order_number asc";

			//logger.info("SQL:"+sql);
			ps = conn.prepareStatement(sqlSelect);
			rs = ps.executeQuery();
			while(rs.next()){
				index = 0;
				psIn.setInt(++index, 2408);
				psIn.setDate(++index,new java.sql.Date(new Date().getTime()));
				psIn.setInt(++index, 2408);
				psIn.setDate(++index,new java.sql.Date(new Date().getTime()));
				psIn.setDouble(++index, -1);
				
				psIn.setBigDecimal(++index, headId);
			    lineId= lineId+1;
				psIn.setInt(++index, lineId);
				
				psIn.setString(++index,rs.getString("item_no"));
				psIn.setDouble(++index,rs.getDouble("unit_price"));
				psIn.setDouble(++index,rs.getDouble("qty"));
				psIn.setString(++index,"N");
				
			    psIn.executeUpdate();
			}
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			if(psIn != null){
				psIn.close();psIn = null;
			}
		}
	}
	
	private static void insertMonitorItemResult(Connection conn,BigDecimal monitorItemId,int no,String status,String msg) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO MONITOR_ITEM_RESULT(MONITOR_ITEM_ID, STATUS, MESSAGE,NO)VALUES(?,?,?,?) ";
			//logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, monitorItemId);
			ps.setString(++index,status);
			ps.setString(++index,msg);
			ps.setInt(++index,no);
			
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
