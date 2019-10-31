package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

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
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.SQLHelper;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHelper;
import com.pens.util.meter.MonitorTime;

public class ImportB2BMakroFromExcelTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
	public static String PARAM_DATA_TYPE = "dataType";
   
	/**
	 * Return :P Name|P label|P Type|default value|valid,P2...$processName,Button Name|....
	*/
	public String getParam(){
		return "dataType|ระบุประเภทไฟล์|LIST||VALID,dataFormFile|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}
	public String getDescription(){
		return "Import B2B Makro From File Excel";
	}
	public String getDevInfo(){
		return "APPS.XXPENS_OM_PUSH_ORDER_ITEM,APPS.XXPENS_OM_PUSH_ORDER_TEMP  ";
	}
	//Show detail BatchTaskResult or no
	public boolean isDispDetail(){
		return true;
	}
	public List<BatchTaskListBean> getParamListBox(){
		List<BatchTaskListBean> listAll = new ArrayList<BatchTaskListBean>();
		
		//LIST 1
		BatchTaskListBean listHeadBean = new BatchTaskListBean();
		List<BatchTaskListBean> listBoxData = new ArrayList<BatchTaskListBean>();
		listBoxData.add(new BatchTaskListBean("",""));
		listBoxData.add(new BatchTaskListBean("B2B_ITEM","B2B_ITEM"));
		listBoxData.add(new BatchTaskListBean("Sales_By_Item","Sales_By_Item"));
		
		listHeadBean.setListBoxName("LIST");
		listHeadBean.setListBoxData(listBoxData);
		listAll.add(listHeadBean);
		
		//LIST 2
		/*listHeadBean = new BatchTaskListBean();
		listBoxData = new ArrayList<BatchTaskListBean>();
		listBoxData.add(new BatchTaskListBean("",""));
		listBoxData.add(new BatchTaskListBean("xxx","xxx"));
		listBoxData.add(new BatchTaskListBean("yyy","yyy"));
		
		listHeadBean.setListBoxName("LIST_2");
		listHeadBean.setListBoxData(listBoxData);
		listAll.add(listHeadBean);*/
		
		return listAll;
	}
	
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
		script +="\n   var form = document.batchTaskForm;";
		script +="\n   if(form.dataType.value ==''){";
		script +="\n     alert('กรุณา ระบุประเภทไฟล์');";
		script +="\n     form.dataType.focus();";
		script +="\n     return false;";
		script +="\n   }";
	    script +="\n   var extension = '';";
		script +="\n   var startFileName = '';";
		script +="\n   if(form.dataFormFile.value.indexOf('.') > 0){";
		script +="\n       extension = form.dataFormFile.value.substring(form.dataFormFile.value.lastIndexOf('.') + 1).toLowerCase();";
		script +="\n   }";
		script +="\n   if(form.dataFormFile.value.indexOf('_') > 0){";
		script +="\n       var pathFileName = form.dataFormFile.value;";
		script +="\n       startFileName = pathFileName.substring(pathFileName.lastIndexOf('\\\\')+1,pathFileName.indexOf('_')).toLowerCase();";
		script +="\n   }";
		script +="\n   if(form.dataFormFile.value != '' && (extension == 'xls' || extension == 'xlsx') ){";
		script +="\n   }else{";
		script +="\n       alert('กรุณาเลือกไฟล์นามสกุล  xls หรือ  xlsx ');";
		script +="\n       return false;";
		script +="\n   }";
	
		script +="\n   return true";
		script +="\n }";
	
		script +="\n </script>";
		return script;
	}
	
	/** Run Process **/
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
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item ImportB2BMakroFromExcelTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			monitorModel.setTransactionType(Constants.TRANSACTION_B2B_TYPE);
			
			/** Start process **/ 
			String dataType =Utils.isNull(monitorModel.getBatchParamMap().get(PARAM_DATA_TYPE)) ;
			if("B2B_ITEM".equalsIgnoreCase(dataType)){
			   modelItem = runProcessByB2BItem(connMonitor,conn,monitorModel,modelItem);
			}else{
			   modelItem = runProcessBySalesByItem(connMonitor,conn,monitorModel,modelItem);	
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
			
			logger.debug("errorMsg:"+monitorModel.getErrorMsg());
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

	public static MonitorItemBean runProcessByB2BItem(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		PreparedStatement ps = null;
		StringBuffer sql  =new StringBuffer();
		int status = Constants.STATUS_FAIL;int dataCount=0;
		int successCount = 0;int failCount = 0;int no = 0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		int maxColumnNo = 4; // max column of data per row
		boolean passValid = false;String lineMsg = "";
		int colNo = 0 ;int r = 0;
		boolean foundError = false;
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    String pensItem = "";
	    String description = "";
	    String barcode = "";
	    String coverday = "";
		try{
			//Get Parameter Value
			String dataType =Utils.isNull(monitorModel.getBatchParamMap().get(PARAM_DATA_TYPE)) ;
			FormFile dataFile = monitorModel.getDataFile();
			
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
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
			
			 /** Validate dataType != data col DataType */
			row = sheet.getRow(0);
			cellCheck = row.getCell((short) 0);
		    cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			logger.debug("DataType cellCheckValue:"+cellCheckValue);
			try{
			    if( !"ITEM_NUMBER".equalsIgnoreCase(Utils.isNull(cellCheckValue))){
			    	monitorItemBean.setStatus(Constants.STATUS_FAIL);;
					monitorItemBean.setErrorMsg("ข้อมูลภายในไฟล์ ไม่ตรงกับประเภทไฟล์ ที่ระบุในหน้าจอ ");
					return monitorItemBean;
			    }
			}catch(Exception ee){
				monitorItemBean.setStatus(Constants.STATUS_FAIL);;
				monitorItemBean.setErrorMsg("ข้อมูลภายในไฟล์ ไม่ตรงกับประเภทไฟล์ ที่ระบุในหน้าจอ ");
				return monitorItemBean;
			}
			
            //delete prev all data
			SQLHelper.excUpdate(conn, "delete from apps.XXPENS_OM_PUSH_ORDER_ITEM ");
			
			//prepare insert
			sql.append("INSERT INTO apps.XXPENS_OM_PUSH_ORDER_ITEM(ITEM_NUMBER,DESCRIPTION,BARCODE,COVERDAY)VALUES(?,?,?,?)");
			ps = conn.prepareStatement(sql.toString());
			
		    /** Loop Row **/
			for (r = rowNo; r <=sheet.getLastRowNum() ; r++) {
				row = sheet.getRow(r);
				//logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
			       cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			       if(cellCheckValue != null){
				      //logger.debug("RowCheck[c,r][0,"+r+"]value["+Utils.isNull(cellCheckValue.toString())+"]");
			       }
				}else{
				   cellCheckValue = null;
				}
				
				if( cellCheckValue != null && !Utils.isNull(cellCheckValue.toString()).equals("")){
					//Clear Value By Row
					passValid = false;
					lineMsg = "";
				}else{
					logger.debug("Break :rowCheck Not Found");
				    break;	
				}
				
			    /** Loop Column **/
				for (colNo = 0; colNo < maxColumnNo; colNo++) {
				//	logger.debug("read row["+r+"]colNo["+colNo+"]");
					cell = row.getCell((short) colNo);
					
					if(cell == null){
					  logger.debug("last column :"+colNo);
					  break;
					}
					
					cellObjValue = xslUtils.getCellValue(colNo, cell);
					
					if(colNo ==0){
						 pensItem = ExcelHelper.getCellValue(cellObjValue,"STRING","");
					}
					if(colNo ==1){
						description =ExcelHelper.getCellValue(cellObjValue,"STRING","");
					}
					if(colNo ==2){
						barcode = ExcelHelper.getCellValue(cellObjValue,"STRING",Utils.format_number_no_digit);
					}
					if(colNo ==3){
						coverday = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_digit);
					}
					
				}//for colNo
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				
				//step validate 
				
				//insert log to display
				lineMsg = (r+1)+"|"+pensItem+"|"+description+"|"+barcode+"|"+coverday;
				
				//logger.debug("lineMsg:"+lineMsg);
				//is valid
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
					
				    ps.setString(1, Utils.isNull(pensItem));
				    ps.setString(2, Utils.isNull(description));
				    ps.setString(3, Utils.isNull(barcode));
				    ps.setInt(4, Utils.convertStrToInt(coverday));
				    ps.addBatch();
				}else{
					failCount++;
					foundError = true;
					lineMsg += "|FAIL|"+errorMsg;
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				}
				
	    	}//for row
			
			//Insert Column For Display Result (1:no, last coloumn ->status,Message )default column
			 lineMsg ="";
			 lineMsg += "No|Line Excel|ItemNumber|Description|Barcode|CoverDay|Status|Message";
			 insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
			 
			logger.debug("result foundError:"+foundError);
			if(foundError ==false){
				status = Constants.STATUS_SUCCESS;
				//no error
				int e[] = ps.executeBatch();
				logger.debug("excute count:"+e.length);
			}
			
			logger.debug("result status:"+status);
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();ps = null;
				}
			}catch(Exception ee){}
		}
		return monitorItemBean;	
	}
	
	public static MonitorItemBean runProcessBySalesByItem(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		PreparedStatement ps = null;
		StringBuffer sql  =new StringBuffer();
		int status = Constants.STATUS_FAIL;int dataCount=0;
		int successCount = 0;int failCount = 0;int no = 0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 0; // row of begin data
		boolean passValid = false;String lineMsg = "";
		int colNo = 0 ;int r = 0;
		boolean foundError = false;
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    
	    String supplierNumber  = "",locationNumber = "",locationName = "";
	    String itemNumber="",barcode="";
	    String eohQty="",onOrderInTransitQty ="",makroUnit="";
	    String avgNetSalesQty="",stockCoverDay="";
		try{
			//Get Parameter Value
			FormFile dataFile = monitorModel.getDataFile();
			
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			logger.debug("fileType:"+fileType);
			
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
			
			/** Validate dataType != data col DataType */
			row = sheet.getRow(2);
			cellCheck = row.getCell((short) 0);
		    cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			logger.debug("DataType cellCheckValue["+cellCheckValue+"]");
			try{
			    if( !"Supplier".equalsIgnoreCase(Utils.isNull(cellCheckValue).trim())){
			    	monitorItemBean.setStatus(Constants.STATUS_FAIL);;
					monitorItemBean.setErrorMsg("ข้อมูลภายในไฟล์ ไม่ตรงกับประเภทไฟล์ ที่ระบุในหน้าจอ ");
					return monitorItemBean;
			    }
			}catch(Exception ee){
				monitorItemBean.setStatus(Constants.STATUS_FAIL);;
				monitorItemBean.setErrorMsg("ข้อมูลภายในไฟล์ ไม่ตรงกับประเภทไฟล์ ที่ระบุในหน้าจอ ");
				return monitorItemBean;
			}
			
			//Get RowNo Start for Data Table
			/** Loop Row **/
			for (r = 0; r <=sheet.getLastRowNum() ; r++) {
				row = sheet.getRow(r);
				//logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
			       cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			       logger.debug("check row["+r+"][0]="+cellCheckValue);
			       if(cellCheckValue != null && Utils.isNull(cellCheckValue.toString()).equalsIgnoreCase("Supplier Number")){
			    	   rowNo = r;
			    	   rowNo++;
			    	   break;
			       }
				}
			}//for
			
			logger.debug("rowNo="+rowNo);
			
            //delete prev all data
			SQLHelper.excUpdate(conn, "delete from apps.XXPENS_OM_PUSH_ORDER_TEMP ");
			
			//prepare insert
			sql.append("INSERT INTO apps.XXPENS_OM_PUSH_ORDER_TEMP");
			sql.append("(SUPPLIER_NUMBER,LOCATION_NUMBER,LOCATION_NAME,ITEM_NUMBER,BARCODE,EOH_QTY  ");
			sql.append(" ,ON_ORDER_QTY,MAKRO_UNIT,AVG_NET_SALES_QTY,STOCK_COVER_DAYS ) ");
			sql.append(" VALUES(?,?,?,? ,?,?,?,?,?,?)");
			ps = conn.prepareStatement(sql.toString());
			
		    /** Loop Row **/
			for (r = rowNo; r <=sheet.getLastRowNum() ; r++) {
				row = sheet.getRow(r);
				//logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
			       cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			       if(cellCheckValue != null){
				      //logger.debug("RowCheck[c,r][0,"+r+"]value["+Utils.isNull(cellCheckValue.toString())+"]");
			       }
				}else{
				   cellCheckValue = null;
				}
				
				if( cellCheckValue != null && !Utils.isNull(cellCheckValue.toString()).equals("")){
					//Clear Value By Row
					passValid = false;
					lineMsg = "";
				}else{
					logger.debug("Break :rowCheck Not Found");
				    break;	
				}
				
			 
				/*  String supplierNumber  = "",locationNumber = "",locationName = "";
				    String eohQty="",onOrderInTransitQty ="",makroUnit="";
				    String avgNetSalesQty="",stockCoverDay="";*/
				//cell = row.getCell((short) colNo);
				
				cellObjValue = xslUtils.getCellValue(colNo, row.getCell((short) 0));
				supplierNumber = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_digit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				locationNumber =ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_digit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 2));
				locationName = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 5));
				itemNumber = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_digit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 6));
				barcode = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_digit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 8));
				eohQty = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 9));
				onOrderInTransitQty = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 11));
				makroUnit = ExcelHelper.getCellValue(cellObjValue,"STRING",Utils.format_number_no_digit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 12));
				avgNetSalesQty = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 16));
				stockCoverDay = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				
				//step validate 
				 
				/*  String supplierNumber  = "",locationNumber = "",locationName = "";
				    String eohQty="",onOrderInTransitQty ="",makroUnit="";
				    String avgNetSalesQty="",stockCoverDay="";*/
				
				//insert log to display
				lineMsg = (r+1)+"|"+supplierNumber+"|"+locationNumber+"|"+locationName+"|";
				lineMsg += itemNumber+"|"+barcode+"|"+eohQty+"|";
				lineMsg += onOrderInTransitQty+"|"+makroUnit+"|"+avgNetSalesQty+"|"+stockCoverDay;
				
				//logger.debug("lineMsg:"+lineMsg);
				//is valid
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					//Insert Log
				    //insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
					

					/*  String supplierNumber  = "",locationNumber = "",locationName = "";
					    String eohQty="",onOrderInTransitQty ="",makroUnit="";
					    String avgNetSalesQty="",stockCoverDay="";*/
				    
				    ps.setString(1, Utils.isNull(supplierNumber));
				    ps.setString(2, Utils.isNull(locationNumber));
				    ps.setString(3, Utils.isNull(locationName));
				    ps.setString(4, Utils.isNull(itemNumber));
				    ps.setString(5, Utils.isNull(barcode));
				    
				    ps.setDouble(6, Utils.convertStrToDouble(eohQty));
				    ps.setDouble(7, Utils.convertStrToDouble(onOrderInTransitQty));
				    ps.setDouble(8, Utils.convertStrToDouble(makroUnit));
				    ps.setDouble(9, Utils.convertStrToDouble(avgNetSalesQty));
				    ps.setDouble(10, Utils.convertStrToDouble(stockCoverDay));
				    
				    ps.addBatch();
				}else{
					failCount++;
					foundError = true;
					lineMsg += "|FAIL|"+errorMsg;
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				}
				
	    	}//for row
			
			/*  String supplierNumber  = "",locationNumber = "",locationName = "";
		    String eohQty="",onOrderInTransitQty ="",makroUnit="";
		    String avgNetSalesQty="",stockCoverDay="";*/
			
			//Insert Column For Display Result (1:no, last coloumn ->status,Message )default column
			 lineMsg ="";
			 lineMsg += "No|Line Excel|supplierNumber|locationNumber|locationName|itemNumber|barcode|eohQty|";
			 lineMsg += "onOrderInTransitQty|makroUnit|avgNetSalesQty|stockCoverDay|Status|Message";
			 
			 insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
			 
			logger.debug("result foundError:"+foundError);
			if(foundError ==false){
				status = Constants.STATUS_SUCCESS;
				//no error
				int e[] = ps.executeBatch();
				logger.debug("excute count:"+e.length);
			}
			
			logger.debug("result status:"+status);
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();ps = null;
				}
			}catch(Exception ee){}
		}
		return monitorItemBean;	
	}

	
}
