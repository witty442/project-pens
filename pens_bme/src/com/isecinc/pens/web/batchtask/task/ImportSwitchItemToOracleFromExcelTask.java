package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
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

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHelper;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ImportSwitchItemToOracleFromExcelTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
   
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate$Button Name
	 */
	public String getParam(){
		return "dataFormFile|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}
	public String getDescription(){
		return "Import File From Excel";
	}
	public String getDevInfo(){
		return "apps.xxpens_inv_vanmisc_temp";
	}
	public boolean isDispDetail(){
		return true;
	}
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
		script +="\n   var form = document.batchTaskForm;";
	    script +="\n   var extension = '';";
		script +="\n   var startFileName = '';";
		script +="\n    if(form.dataFormFile.value.indexOf('.') > 0){";
		script +="\n       extension = form.dataFormFile.value.substring(form.dataFormFile.value.lastIndexOf('.') + 1).toLowerCase();";
		script +="\n    }";
		script +="\n    if(form.dataFormFile.value.indexOf('_') > 0){";
		script +="\n       var pathFileName = form.dataFormFile.value;";
		script +="\n       startFileName = pathFileName.substring(pathFileName.lastIndexOf('\\\\')+1,pathFileName.indexOf('_')).toLowerCase();";
		script +="\n    }";
		script +="\n    if(form.dataFormFile.value != '' && (extension == 'xls' || extension == 'xlsx') ){";
		script +="\n    }else{";
		script +="\n       alert('กรุณาเลือกไฟล์นามสกุล  xls หรือ  xlsx ');";
		script +="\n       return false;";
		script +="\n    }";
		script +="\n    return true";
		script +="\n }";
		script +="\n </script>";
		return script;
	}
	
	public  MonitorBean run(MonitorBean monitorModel){
		Connection conn = null;
		Connection connMonitor = null;
		BatchTaskDAO dao = new BatchTaskDAO();
		MonitorTime monitorTime = null;
		try{
			//logger.debug("RealPath:"+request.getRealPath(""));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnectionApps();

			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnectionApps();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item ImportSwitchItemToOracleFromExcelTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
			/** Validate FileName is imported**/
			boolean isOrderTodayExist = isFileNameExist(connMonitor, monitorModel.getDataFile().getFileName());
			if(isOrderTodayExist){
				modelItem.setStatus(Constants.STATUS_FAIL);
				modelItem.setErrorMsg("มีการ Import File นี้ไปแล้ว ไม่สามารถ import ได้อีก");
				modelItem.setErrorCode("DuplicateImportFileException");
			}else{
			   /** Start process **/ 
			   modelItem = runProcess(connMonitor,conn,monitorModel,modelItem);
			}
			
			/**debug TimeUse **/
			monitorTime.debugUsedTime();
			
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			if(modelItem.getStatus()== Constants.STATUS_SUCCESS){
			    logger.debug("Transaction commit");
			    conn.commit();
			}else{
				logger.debug("Transaction Rolback");
				conn.rollback();
			}
			
			/** Update status Head Monitor From Monitor Item Status*/
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			
			/** Update Status Monitor **/
			dao.updateMonitor(connMonitor,monitorModel);
		}catch(Exception e){
			try{
				logger.error(e.getMessage(),e);
				
				/** End process ***/
				logger.debug("Update Monitor to Fail ");
				monitorModel.setStatus(Constants.STATUS_FAIL);
				monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
				monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
				
				dao.updateMonitorCaseError(connMonitor,monitorModel);
	
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),monitorModel.getName());
				
				if(conn != null){
				   logger.debug("Transaction Rolback");
				   conn.rollback();
				}
			}catch(Exception ee){}
		}finally{
		   try{
				if(conn != null){
					conn.setAutoCommit(true);
					conn.close();
					conn =null;
				}
				if(connMonitor != null){
					connMonitor.close();
					connMonitor=null;
				}
		   }catch(Exception ee){}
		}
		return monitorModel;
	}

	public static MonitorItemBean runProcess(Connection connMonitor ,Connection conn,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    int successCount = 0;
	    int failCount = 0;
	    String lineMsg = "";
	    String errorMsg = "";
	    boolean importAllError = false;
	    boolean importError = false;
	    int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		int maxColumnNo = 7; // max column of data per row
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		BigDecimal importId = new BigDecimal("0");
		try {
			FormFile dataFile = monitorModel.getDataFile();
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
			if (dataFile != null) {
				//xxpens_inv_vanmisc_temp
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO apps.xxpens_inv_vanmisc_temp( \n");
				sql.append(" ORGANIZATION_CODE, TRANSACTION_DATE, SUBINVENTORY,  \n");
				sql.append(" ITEM_NO ,TRANSACTION_QTY,UOM_CODE,GL_ACCOUNT, \n");
				sql.append(" FILE_NAME, STATUS, CREATION_DATE)  \n");
				sql.append(" VALUES(?,?,?,?,?,?,?,?,?,?)");
				ps = conn.prepareStatement(sql.toString());
	
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
				
				//Column ExcelValue
				String ORGANIZATION_CODE = "";
				java.util.Date TRANSACTION_DATE = null;String TRANSACTION_DATE_STR="";
				String SUBINVENTORY = "";
				String ITEM_NO = "";
				String TRANSACTION_QTY ="";
				String UOM_CODE = "";
			    String GL_ACCOUNT = "";
				int lineId = 0;
	         
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            Cell cellCheck =null;
				for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
					row = sheet.getRow(i);
					
					/** Check Row is null **/
					try{
						cellCheck = row.getCell((short) 0);
						Object cellCheckValue = xslUtils.getCellValue(0, cellCheck);
						String rowCheck = Utils.isNull(cellCheckValue);
						logger.debug("row["+i+"]rowCheckValue["+rowCheck+"]");
						if("".equals(rowCheck)){
							break;
						}
					}catch(Exception ee){
						break;
					}
					//logger.debug("Row:"+i);
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						//logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						
						if(colNo==0){
						   //ORGANIZATION_CODE
							ORGANIZATION_CODE = ExcelHelper.isCellNumberOrText(cellValue);
							logger.debug("ORGANIZATION_CODE:"+SUBINVENTORY);
						}else if(colNo==1){
						   //ordered_date
						   TRANSACTION_DATE =  (java.util.Date) cellValue;
						   logger.debug("TRANSACTION_DATE:"+TRANSACTION_DATE);
						   TRANSACTION_DATE_STR = DateUtil.stringValue(TRANSACTION_DATE, DateUtil.DD_MM_YYYY_WITH_SLASH);
						   logger.debug("TRANSACTION_DATE_STR Str:"+TRANSACTION_DATE_STR);
						}else if(colNo==2){
						   //SUBINVENTORY
							SUBINVENTORY = ExcelHelper.isCellNumberOrText(cellValue);
						   logger.debug("SUBINVENTORY:"+SUBINVENTORY);
						}else if(colNo==3){
						    //ITEM_NO
							ITEM_NO = ExcelHelper.isCellNumberOrText(cellValue);
							logger.debug("ITEM_NO:"+ITEM_NO);
						}else if(colNo==4){
						   //TRANSACTION_QTY
							TRANSACTION_QTY = ExcelHelper.isCellDouble(cellValue);
							logger.debug("TRANSACTION_QTY:"+TRANSACTION_QTY);
						}else if(colNo==5){
						  //UOM_CODE
							UOM_CODE = ExcelHelper.isCellNumberOrText(cellValue);
							logger.debug("UOM_CODE:"+UOM_CODE);
						}else if(colNo==6){
						   //GL_ACCOUNT
							GL_ACCOUNT = ExcelHelper.isCellNumberOrText(cellValue);
							logger.debug("GL_ACCOUNT:"+GL_ACCOUNT);
						}
					}//for column
					
					logger.debug("fileName:"+fileName);
					
					ps.setString(1, ORGANIZATION_CODE);
					ps.setDate(2, new java.sql.Date(TRANSACTION_DATE.getTime()));
					ps.setString(3, SUBINVENTORY);
					ps.setString(4, ITEM_NO);
					ps.setDouble(5, Utils.convertStrToDouble(TRANSACTION_QTY));
					ps.setString(6, UOM_CODE);
					ps.setString(7, GL_ACCOUNT);
					ps.setString(8, fileName);
					ps.setString(9, "N");//STATUS
					ps.setTimestamp(10, new java.sql.Timestamp(new Date().getTime()));
					
					ps.addBatch();
					
					importError = false;
					errorMsg = "";
					lineMsg ="";
					lineMsg += (i+1)+"|"+ORGANIZATION_CODE+"|"+TRANSACTION_DATE_STR+"|"+SUBINVENTORY+"|";
					lineMsg +=	ITEM_NO+"|"+TRANSACTION_QTY +"|"+UOM_CODE+"|"+GL_ACCOUNT;
			        
					//Step validate (no valid)
					//Validate transactionDate <= sysdate? OK: error 
					if(TRANSACTION_DATE.after(DateUtil.getCurrentDateNoTime()) ){
						errorMsg ="Transaction Date ["+TRANSACTION_DATE_STR+"] ต้อง น้อยกว่า หรือ เท่ากับ วันที่ปัจจุบันเท่านั้น ";
						importError = true;
					}
					
					//step 1 validate UOM_CODE (CTN OR EA )ONLY
					if( !"CTN".equals(UOM_CODE) && !"EA".equals(UOM_CODE)){
						errorMsg ="UOM CODE ต้องเป็น CTN หรือ EA เท่านั้น ";
						importError = true;
					}
					//step 2 validate SUBINVENTORY (Start with V,G,X,M) ONLY
					if( !SUBINVENTORY.startsWith("V") && !SUBINVENTORY.startsWith("G")
							&& !SUBINVENTORY.startsWith("X") && !SUBINVENTORY.startsWith("M")){
						errorMsg +=",SUBINVENTORY ต้องขึ้นต้นด้วย V,G,X,M เท่านั้น";
						importError = true;
					}
					
					/** Insert Log Msg **/
					if( !importError){
					    //Add Success Msg 
						
				         lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
	                 	 //logger.debug("lineMsg: \n"+lineMsg);
	     				 insertMonitorItemResult(connMonitor,monitorItemBean.getId(),(i+1),"SUCCESS",lineMsg);
	     				 successCount++;//count row success
				      
					 }else{
						 lineMsg += "|FAIL|"+errorMsg;
						 insertMonitorItemResult(connMonitor,monitorItemBean.getId(),(i+1),"FAIL",lineMsg);
						 failCount++;//count fail 
						 importAllError = true;
					 }
		             
				}//for Row
				
				//Insert Column For Display Result
				 lineMsg ="";
				 lineMsg += "No|Line Excel|ORGANIZATION CODE|TRANSACTION DATE|SUBINVENTORY|";
				 lineMsg +=	"ITEM NO|TRANSACTION QTY|UOM CODE|GL ACCOUNT|Status|Message";
				 insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
				 
				
				//Execute Batch Statement
				if(importAllError==false){
				  int[] e = ps.executeBatch();
				  logger.debug("excute count:"+e.length);
				}
			}//if
			
			logger.debug("importAllError:"+importAllError);
			
			/** Set Count Record **/
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setStatus(Constants.STATUS_SUCCESS);
			if(importAllError){
				monitorItemBean.setStatus(Constants.STATUS_FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		} finally {
			try {
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			     
			} catch (Exception e2) {}
		}
		return monitorItemBean;
	}
	public static MonitorItemBean prepareMonitorItemBean(MonitorBean monitorModel) throws Exception{
		MonitorItemBean modelItem = new MonitorItemBean();
		modelItem.setMonitorId(monitorModel.getMonitorId());
		modelItem.setSource("SALES");
		modelItem.setDestination("ORACLE");
		modelItem.setStatus(Constants.STATUS_START);
		modelItem.setSubmitDate(new Date());
		modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
		
		return modelItem;
	}
	public static boolean isFileNameExist(Connection conn,String fileName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean isExist = false;
		try {
			sql.append("\n SELECT count(*) as c FROM APPS.xxpens_inv_vanmisc_temp");
			sql.append("\n WHERE file_name ='"+fileName+"'");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			  if(rst.getInt("c") >0){
				  isExist = true;
			  }
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return isExist;
	}
	
	public static boolean isOrderNumberExist(Connection conn,String orderNumber,String orderDate) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean isExist = false;
		try {
			sql.append("\n SELECT count(*) as c FROM APPS.XXPENS_OM_EXCEL_ORDER_MST ");
			sql.append("\n WHERE order_number ='"+orderNumber+"'");
			sql.append("\n and Ordered_date =to_date('"+orderDate+"','dd/mm/yyyy')");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			  if(rst.getInt("c") >0){
				  isExist = true;
			  }
			  
			}//while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return isExist;
	}
	public static boolean isCustomerNumberExist(Connection conn,String customerNumber) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean isExist = false;
		try {
			sql.append("\n SELECT count(*) as c FROM PENSBI.PENSBME_MST_REFERENCE ");
			sql.append("\n WHERE reference_code = 'ShipToLocation'");
			sql.append("\n and pens_value ='"+customerNumber+"'");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			  if(rst.getInt("c") >0){
				  isExist = true;
			  }
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return isExist;
	}
	public static boolean isShipToLocationExist(Connection conn,String shipToLocation) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean isExist = false;
		try {
			sql.append("\n SELECT count(*) as c FROM PENSBI.PENSBME_MST_REFERENCE ");
			sql.append("\n WHERE reference_code = 'ShipToLocation'");
			sql.append("\n and interface_value ='"+shipToLocation+"'");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			  if(rst.getInt("c") >0){
				  isExist = true;
			  }
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return isExist;
	}
	public static void debug(Map<String,Order> orderSaveMap){
		//debug
		Iterator its = orderSaveMap.keySet().iterator();
		while(its.hasNext()){
		   String key = (String)its.next();
		   Order o = orderSaveMap.get(key); 
		   logger.debug("DEBUG StoreCode["+key+"]orderNo["+o.getQty()+"]");
		}
	}
}
