package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class ImportSaleOutWacoalProcess extends InterfaceUtils{

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
		Map<String, String> batchParamMap = new HashMap<String, String>();
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
				
				/** cehck FileName duplicate **/
				boolean dup = importDAO.importSaleOutWacoalFileNameIsDuplicate(conn, fileName);
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
					modelItem.setTableName("Import SaleOut Wacoal");
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
	
	public static MonitorItemBean importToDB(Connection conn,Connection conMoni ,MonitorItemBean mi,User user,FormFile dataFile,String fileType ,String fileName) {
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    Map<String, ImportSummary> successMap = new HashMap<String, ImportSummary>();
	    Map<String, ImportSummary> errorMap = new HashMap<String, ImportSummary>();
	    boolean importError = false;
		try {
			if (dataFile != null) {
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_WACOAL_SALEOUT \n");
				sql.append(" (Store_no, Sales_date ,Item_wacoal ,Qty ,  \n");
				sql.append(" Branch_id, Branch_name, CREATE_DATE,CREATE_USER,FILE_NAME )  \n");
				sql.append(" VALUES( ?,?,?,? ,?,?,?,?,?) \n");
                
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 1; // row of begin data
				int maxColumnNo = 4; // max column of data per row
				
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
			
				String storeNo = "";
				String salesDate = "";
				String Item_wacoal = "";
				String qty = "";

	            int no = 0;
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
				for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
					row = sheet.getRow(i);
					
					/** Check Row is null **/
					Cell cellCheck = row.getCell((short) 0);
					Object cellCheckValue = xslUtils.getCellValue(0, cellCheck);
					String rowCheck = Utils.convertDoubleToStr(Utils.isDoubleNull(cellCheckValue));
					logger.debug("rowCheck["+rowCheck+"]");
					if("0".equals(rowCheck)){
						break;
					}
					logger.debug("Row:"+i);
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						//logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						
						if(colNo==0){
						   storeNo =  Utils.convertDoubleToStrNoDigit(cellValue);
						   logger.debug("storeNo:"+storeNo);
						   ps.setString(1,storeNo+"");
						   
						}else if(colNo==1){
							java.util.Date asOfDate =  (java.util.Date) cellValue;
						    logger.debug("Date:"+asOfDate);
							salesDate = DateUtil.stringValue(asOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
							logger.debug("salesDate:"+salesDate);
							ps.setDate(2, new java.sql.Date(asOfDate.getTime()));  
							
						}else if(colNo==2){
						    Item_wacoal = Utils.isNull(cellValue);
						    logger.debug("itemWacoal:"+Item_wacoal);
						    ps.setString(3,Item_wacoal);
						    
						}else if(colNo==3){
							qty = Utils.convertToNumberStr((Double)cellValue);
							logger.debug("qty:"+qty);
						    ps.setInt(4, Utils.convertStrToInt(qty));
						}
					}//for column
					
			         //Validate Get Branch id By StoreNo
					 String[] branchId = new ImportDAO().getBranchID(conn,storeNo);
					 if(branchId == null){
						 //Add Error Msg
				         importError = true;
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName("");
				         s.setQty(qty);
				         s.setItemWacoal(Item_wacoal);
				         s.setMessage("ไม่พบข้อมูล Mapping Store no กับ Branch id");
				         errorMap.put(i+"", s);
				         
				         //set prepare
				         ps.setString(5,"");//branch_id
				         ps.setString(6,"");//branch Name
			         }else{
				
			        	  //Add Success Msg 
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName(Utils.isNull(branchId[1]));
				         s.setItemWacoal(Item_wacoal);
				         s.setQty(qty);
				         s.setMessage("Success ");
				         successMap.put(i+"", s); 
				         
				       //set prepare
				         ps.setString(5,branchId[0]);//branch_id
				         ps.setString(6,branchId[1]);//branch Name
			         }
					
					 //CREATE_DATE
					 ps.setTimestamp(7, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(8, user.getUserName());
			         //fileName
			         ps.setString(9,Utils.isNull(fileName));
			   
			         ps.execute();
				}//for Row

			}//if data file not null
			
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
				  String rowMsg = "";
				  if(successList != null && successList.size() >0){
					  for(int r=0;r<successList.size();r++){
						  ImportSummary m = successList.get(r);
						  rowMsg = m.getRow()+","+m.getSalesDate()+","+m.getStoreNo()+","+m.getStoreName()+","+m.getItemWacoal()+","+m.getQty()+","+m.getMessage();
				          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"SUCCESS",rowMsg);
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
							  rowMsg = m.getRow()+","+m.getSalesDate()+","+m.getStoreNo()+","+m.getStoreName()+","+m.getItemWacoal()+","+m.getQty()+","+m.getMessage();
					          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"FAIL",rowMsg);
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
				  String rowMsg = "";
				  if(successList != null && successList.size() >0){
					  for(int r=0;r<successList.size();r++){
						  ImportSummary m = successList.get(r);
						  rowMsg = m.getRow()+","+m.getSalesDate()+","+m.getStoreNo()+","+m.getStoreName()+","+m.getItemWacoal()+","+m.getQty()+","+m.getMessage();
				          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"SUCCESS",rowMsg);
					  }
				  }
				/** Set Count Record **/
				mi.setSuccessCount(successList.size());
			    mi.setFailCount(errorList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			//request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			importError = true;
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
