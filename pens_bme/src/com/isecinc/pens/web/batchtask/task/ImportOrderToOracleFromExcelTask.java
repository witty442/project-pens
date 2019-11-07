package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.TaskStoreBean;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ImportOrderToOracleFromExcelTask extends BatchTask implements BatchTaskInterface{
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
		return "APPS.XXPENS_OM_EXCEL_ORDER_MST | APPS.XXPENS_OM_EXCEL_ORDER_DT";
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
			logger.debug("Insert Monitor Item ImportOrderToOracleFromExcelTask ");
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
	    PreparedStatement psD = null;
	    int successCount = 0;
	    int failCount = 0;
	    String lineMsg = "";
	    String errorMsg = "";
	    boolean importAllError = false;
	    boolean importError = false;
	    Map<String, String> ORDER_CHK_SHIPLOC = new HashMap<String, String>();
	    String keyOrderChkShipLoc = "";//order_number+ordered_date ,
	    int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		int maxColumnNo = 11; // max column of data per row
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Map<String, Integer> orderNumberMap = new HashMap<String, Integer>();//OrderNumber,lineId(max)
		BigDecimal importId = new BigDecimal("0");
		try {
			FormFile dataFile = monitorModel.getDataFile();
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
			if (dataFile != null) {
				//XXPENS_OM_FM_ORDER_MST
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO APPS.XXPENS_OM_EXCEL_ORDER_MST( \n");
				sql.append(" order_number, ordered_date, account_number,  \n");
				sql.append(" ship_to_location ,cust_po_number, Salesrep_number, Subinventory, \n");
				sql.append(" order_type, TRANSPORTER, INT_FLAG, \n");
				sql.append(" CREATION_DATE,file_name,HEADER_ID)  \n");//13
				sql.append(" VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps = conn.prepareStatement(sql.toString());
				
				//XXPENS_OM_FM_ORDER_DT
				sql = new StringBuffer("");
				sql.append(" INSERT INTO APPS.XXPENS_OM_EXCEL_ORDER_DT( \n");//5
				sql.append(" HEADER_ID, LINE_ID, ITEM_NO, QTY ,CREATION_DATE) VALUES( ?,?,?,?,?) \n");
				psD = conn.prepareStatement(sql.toString());

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
				String order_number = "";
				String orderedDateStr = "";
				java.util.Date ordered_dateDate = null;
				String account_number = "";
				String ship_to_location = "";
				String cust_po_number ="";
				String item_no = "";
			    String qty = "";
				String salerep_number = "";
				String subinventory = "";
				String order_type = "";
				String logistics_type = "";
				int lineId = 0;
	         
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
						}else if(colNo==1){
						   //ordered_date
						   ordered_dateDate =  (java.util.Date) cellValue;
						   logger.debug("ordered_dateDate:"+ordered_dateDate);
						   orderedDateStr = DateUtil.stringValue(ordered_dateDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
						   logger.debug("ordered_date Str:"+orderedDateStr);
						}else if(colNo==2){
						   //account_number
						   if(Utils.isNumeric(Utils.isNull(cellValue)) && !(cellValue instanceof String)){
							  //logger.debug("account_number : is Number:"+cellValue);
							   account_number = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
							}else{
							   //logger.debug("account_number : No Number:"+cellValue);
								account_number = Utils.isNull(cellValue);
							}
						   logger.debug("result account_number:"+account_number);
						}else if(colNo==3){
						    //ship_to_location
							ship_to_location = Utils.convertDoubleToStrNoDigit(cellValue);
						}else if(colNo==4){
						   //cust_po_number
							if(Utils.isNumeric(Utils.isNull(cellValue))){
							 //  logger.debug("cust_po_number : is Number:"+cellValue);
							   cust_po_number = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
							}else{
							   //logger.debug("cust_po_number : No Number:"+cellValue);
							   cust_po_number = Utils.isNull(cellValue);
							}
							
							//cust_po_number = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
							logger.debug("result cust_po_number:"+cust_po_number);
						}else if(colNo==5){
						  //item_no
						   item_no = Utils.convertDoubleToStrNoDigit(cellValue);
						}else if(colNo==6){
						   //qty
						   qty = Utils.convertDoubleToStrNoDigit(cellValue);
						}else if(colNo==7){
						   //salerep_number
						   salerep_number = Utils.isNull(cellValue);
						}else if(colNo==8){
						   //subinventory
						   subinventory = Utils.isNull(cellValue);
						}else if(colNo==9){
						   //order_type
							order_type = Utils.isNull(cellValue);
						}else if(colNo==10){
						   //logistics_type
							logistics_type = Utils.isNull(cellValue);
						}
						  
					}//for column
					
					if(orderNumberMap.get(order_number) == null){
						 //Init lineId= 1 and gen New HeaderId
						 lineId =1;
						 orderNumberMap.put(order_number, lineId);
						 importId = SequenceProcess.getNextValueBig("ORDER_TO_ORACLE_IM_ID");
						 logger.debug("importID:"+importId);
						 
						//prepare Header
						 ps.setString(1,  order_number);
						 ps.setTimestamp(2, new java.sql.Timestamp(ordered_dateDate.getTime()));  
						 ps.setString(3, account_number);
					     ps.setString(4, ship_to_location);
					     ps.setString(5, cust_po_number);
						 ps.setString(6, salerep_number);
						 ps.setString(7, subinventory);
					     ps.setString(8, order_type);
					     ps.setString(9, logistics_type);
						 //status
						 ps.setString(10, "");
						 //CREATE_DATE
						 ps.setTimestamp(11, new java.sql.Timestamp(new java.util.Date().getTime()));
						 //fileName
						 ps.setString(12, fileName);
				         //Import ID
						 ps.setBigDecimal(13,importId);
						 
						 ps.addBatch();  
					}else{
						//prevLineId+1
						lineId = orderNumberMap.get(order_number)+1;
						orderNumberMap.put(order_number, lineId);
					}
					
					 
					//prepare Detail  HEADER_ID, LINE_ID, ITEM_NO,QTY ,CREATION_DATE
					 psD.setBigDecimal(1,importId);
					 psD.setInt(2,lineId );
					 psD.setString(3, item_no);
					 psD.setDouble(4, new Double(qty));
					 psD.setTimestamp(5, new java.sql.Timestamp(new java.util.Date().getTime()));//CREATE_DATE
					 psD.addBatch();
					 
					 importError = false;
					 errorMsg = "";
					 lineMsg ="";
					 lineMsg += (i+1)+"|"+order_number+"|"+orderedDateStr+"|"+account_number+"|";
					 lineMsg +=	ship_to_location+"|"+cust_po_number +"|"+item_no+"|"+qty+"|";
					 lineMsg += salerep_number+"|"+subinventory+"|"+order_type+"|"+logistics_type;
			         
					 //validate order_number ,order_date not in 
					 if(isOrderNumberExist(conn, order_number, orderedDateStr)){
						 importError = true;
						 importAllError = true;
						 errorMsg =" Order number และ Order date  เคยมีประวัติการ Load ไปแล้ว<br/>";
					 }
					 //validate customer_number must exist in master_reference
					 /*if( !isCustomerNumberExist(conn, account_number)){
						 importError = true;
						 importAllError = true;
						 errorMsg +=", ไม่พบ Customer code ใน Master Table<br/>";
					 }*/
					 //validate ship_to_location must exist in master_reference
					 if( !isShipToLocationExist(conn, ship_to_location)){
						 importError = true;
						 importAllError = true;
						 errorMsg +=", ไม่พบ Ship to Location code  ใน Master Table<br/>";
					 }
					 
					 //validate ship_to_location must same value in orde_number and ordered_date
					 keyOrderChkShipLoc = order_number+orderedDateStr;
					 if(ORDER_CHK_SHIPLOC.get(keyOrderChkShipLoc) != null){
						 //check value second row
						 if( !ship_to_location.equalsIgnoreCase(ORDER_CHK_SHIPLOC.get(keyOrderChkShipLoc))){
							 importError = true;
							 importAllError = true;
							 errorMsg +=",1 Order number มีหลาย Ship to Location กรุณาตรวจสอบความผิดปกติ";
						 }
					 }else{
						 ORDER_CHK_SHIPLOC.put(keyOrderChkShipLoc, ship_to_location); 
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
	     				
						 failCount++;
					 }
		             
				}//for Row
				
				//Execute Batch Statement
				if( !importAllError){
				  ps.executeBatch();
				  psD.executeBatch();
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
			logger.error(e.getMessage(),e);
		} finally {
			try {
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psD != null){
			    	 psD.close();psD=null;
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
			sql.append("\n SELECT count(*) as c FROM APPS.XXPENS_OM_EXCEL_ORDER_MST ");
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
