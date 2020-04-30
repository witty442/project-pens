package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
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

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskDispBean;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHelper;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ImportItemBarcodeCrossRefToOracleFromExcelTask extends BatchTask implements BatchTaskInterface{

   
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate$Button Name
	 */
	/*public String getParam(){
		return "dataFormFile|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}*/
	public String[] getParam(){
		String[] param = new String[1];
		param[0] = "dataFormFile|เลือกไฟล์|FROMFILE||VALID";
		return param;
	}
	public List<BatchTaskListBean> getParamListBox(){
		return null;
	}
	public String getButtonName(){
		return "Import ข้อมูล";
	}
	public String getDescription(){
		return "Import File From Excel";
	}
	public String getDevInfo(){
		return "{call xxpens_inv_item_cross_pkg.import_cust_item(150, 600)} <br/> , apps.XXPENS_OM_CUST_ITEM_TMP";
	}
	public BatchTaskDispBean getBatchDisp(){
		BatchTaskDispBean dispBean = new BatchTaskDispBean();
		dispBean.setDispDetail(true);
		dispBean.setDispRecordFailHead(true);
		dispBean.setDispRecordFailDetail(true);
		dispBean.setDispRecordSuccessHead(true);
		dispBean.setDispRecordSuccessDetail(true);
		return dispBean;
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
			logger.debug("Insert Monitor Item ImportItemBarcodeCrossRefToOracleFromExcelTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
		    /** Start process **/ 
			modelItem = runProcess(connMonitor,conn,monitorModel,modelItem);
			   
			/**debug TimeUse **/
			monitorTime.debugUsedTime();
			
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			if(modelItem.getStatus()== Constants.STATUS_SUCCESS){
			    logger.debug("Transaction commit");
			    conn.commit();
			}else{
				logger.debug("Transaction Rollback");
				conn.rollback();
			}
			
			/** Update status Head Monitor From Monitor Item Status*/
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			monitorModel.setType("IMPORT");
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
				   logger.debug("Transaction Rollback");
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
		CallableStatement cs = null;
	    PreparedStatement ps = null;
	    PreparedStatement psDel = null;
	    int successCount = 0;
	    int failCount = 0;
	    String lineMsg = "";
	    String errorMsg = "";
	    boolean importAllError = false;
	    boolean importError = false;
	    int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		int maxColumnNo = 3; // max column of data per row
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		BigDecimal importId = new BigDecimal("0");
		int status = Constants.STATUS_START;
		try {
			FormFile dataFile = monitorModel.getDataFile();
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
			logger.debug("dataFile:"+dataFile);
			if (dataFile != null) {
				//call procedure
				StringBuffer sql = new StringBuffer("");
				sql.append("{call xxpens_inv_item_cross_pkg.import_cust_item(150, 600)} \n");
				cs  = conn.prepareCall(sql.toString());
				
				//xxpens_inv_vanmisc_temp
				sql = new StringBuffer("");
				sql.append(" INSERT INTO apps.XXPENS_OM_CUST_ITEM_TMP( \n");
				sql.append(" ACCOUNT_NUMBER, ITEM, BARCODE)  \n");
				sql.append(" VALUES(?,?,?)");
				ps = conn.prepareStatement(sql.toString());
				
				sql = new StringBuffer("");
				sql.append(" DELETE FROM apps.XXPENS_OM_CUST_ITEM_TMP ");
				psDel = conn.prepareStatement(sql.toString());
	            psDel.execute();
				
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
				String accountNumber = "";
				String item = "";
				String barcode = "";
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
						ee.printStackTrace();
						break;
					}
					//logger.debug("Row:"+i);
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						//logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						if(colNo==0){
						   //accountNumber
							accountNumber = ExcelHelper.isCellNumberOrText(cellValue);
							logger.debug("accountNumber:"+accountNumber);
						}else if(colNo==1){
							//item
							item = ExcelHelper.isCellNumberOrText(cellValue);
							logger.debug("item:"+item);
						}else if(colNo==2){
							//barcode
							barcode = ExcelHelper.isCellNumberOrText(cellValue);
							logger.debug("barcode:"+accountNumber);
						}
					}//for column
					
					logger.debug("fileName:"+fileName);
					
					ps.setString(1, accountNumber);
					ps.setString(2, item);
					ps.setString(3, barcode);
					
					ps.addBatch();
					
					importError = false;
					errorMsg = "";
					lineMsg ="";
					lineMsg += (i+1)+"|"+accountNumber+"|"+item+"|"+barcode;
			        
					//Step validate (no valid)
					 
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
					 }
		             
				}//for Row
				
				//Insert Column For Display Result
				 lineMsg ="";
				 lineMsg += "No|Line Excel|Account Number|Item|Barcode|Status|Message";
				 insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
				 
				//Execute Batch Statement
				if(importAllError==false){
				   int[] e = ps.executeBatch();
				   logger.debug("excute count:"+e.length);
				   
				   //call procedure
				   try{
				       int r= cs.executeUpdate();
				       logger.debug("excute procedure count:"+r);
				       status = Constants.STATUS_SUCCESS;
				   }catch(Exception ee){
					   status = Constants.STATUS_FAIL;
					   errorMsg ="ไม่สามารถ Import ข้อมูลได้  procedure error \n "+ee.getMessage();
				   }
				}else{
					 status = Constants.STATUS_FAIL;
					 errorMsg ="ไม่สามารถ Import ข้อมูลได้  โปรดตรวจสอบข้อมูล";
				}
			}//if
			
			logger.debug("importAllError:"+importAllError);
			
			/** Set Count Record **/
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setStatus(status);
			monitorItemBean.setErrorMsg(errorMsg);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDel != null){
			    	 psDel.close();psDel=null;
			      } 
			      if(cs != null){
				    cs.close();cs=null;
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
