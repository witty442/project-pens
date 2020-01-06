package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHelper;
import com.pens.util.meter.MonitorTime;

public class ImportB2BSalesItemMakroFromExcelTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
   
	/**
	 * Return :P Name|P label|P Type|default value|valid,P2...$processName,Button Name|....
	*/
	public String getParam(){
		return "dataFormFile|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}
	public String getDescription(){
		return "Import B2B Sales By Item Makro From File Excel";
	}
	public String getDevInfo(){
		return "PENSBI.XXPENS_BI_B2B_SALES_ITEM_TEMP ";
	}
	public List<BatchTaskListBean> getParamListBox(){
		List<BatchTaskListBean> listAll = new ArrayList<BatchTaskListBean>();
		return listAll;
	}
	//Show detail BatchTaskResult or no
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
			logger.debug("Insert Monitor Item ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			monitorModel.setTransactionType(Constants.TRANSACTION_B2B_TYPE);
			
			/** Start process **/ 
		    modelItem = process(connMonitor,conn,monitorModel,modelItem);	
			
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

	public static MonitorItemBean process(Connection connMonitor ,Connection conn
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
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 0; // row of begin data
		boolean passValid = false;String lineMsg = "";
		int colNo = 0 ;int r = 0;
		boolean foundError = false;
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    Map<String, String> storeNoMasterMap = new HashMap<String, String>();
	    Map<String, String> storeNoErrorMap = new HashMap<String, String>();
	    
	    String supplierNumber  = "",locationNumber = "",locationName = "";
	    String classNumber="",subClassNumber="",itemNumber="",barcode="";
	    String itemDesc ="",eohQty="",onOrderInTransitQty ="",packType ="";
	    String makroUnit="",AVG_NET_SALES_QTY="",Net_Sales_Qty_YTD="";
	    String Last_Received_Date=null,Last_Sold_Date = null;
	    String Stock_Cover_Days="",Net_Sales_Qty_LY_YTD="",Net_Sales_Qty_YTM="";
	    String qty1="",qty2="",qty3="",qty4="";
	    String qty5="",qty6="",qty7="",qty8="";
	    String qty9="",qty10="",qty11="",qty12="" ,qty13="";
	    
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
			
            //delete prev all data
			SQLHelper.excUpdate(conn, "delete from PENSBI.XXPENS_BI_B2B_SALES_ITEM_TEMP ");
			
			//init StoreNoMaster for Validate
			storeNoMasterMap = initStoreNoMasterMap(conn);
			
			//prepare insert
			sql.append("INSERT INTO PENSBI.XXPENS_BI_B2B_SALES_ITEM_TEMP( \n");
			sql.append("SUPPLIER_NUMBER, LOCATION_NUMBER, LOCATION_NAME,REGION, \n"); 
			sql.append("CLASS_NUMBER, SUBCLASS_NUMBER, ITEM_NUMBER, BARCODE,  \n");
			sql.append("ITEM_DESC, EOQ_QTY, ODER_IN_TRANSIT_QTY, PACK_TYPE,  \n");
			sql.append("MAKRO_UNIT, AVG_NET_SALES_QTY, NET_SALES_QTY_YTD, LAST_RECEIVED_DATE, \n"); 
			sql.append("LAST_SOLD_DATE, STOCK_COVER_DAYS, NET_SALES_QTY_LY_YTD, NET_SALES_QTY_YTM, \n"); 
			sql.append("QTY_1, QTY_2, QTY_3, QTY_4,  \n");
			sql.append("QTY_5, QTY_6, QTY_7, QTY_8,  \n");
			sql.append("QTY_9, QTY_10, QTY_11, QTY_12, QTY_13) \n");//32
			
			sql.append(" VALUES(?,?,?,? ,?,?,?,? ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
			
				    
				cellObjValue = xslUtils.getCellValue(colNo, row.getCell((short) 0));
				supplierNumber = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				locationNumber =ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 2));
				locationName = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 3));
				classNumber = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 4));
				subClassNumber = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 5));
				itemNumber = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 6));
				barcode = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_number_no_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 7));
				itemDesc = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 8));
				eohQty = ExcelHelper.getCellValue(cellObjValue,"INTEGER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 9));
				onOrderInTransitQty = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 10));
				packType = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 11));
				makroUnit = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 12));
				AVG_NET_SALES_QTY = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 13));
				Net_Sales_Qty_YTD = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 14));
				Last_Received_Date = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 15));
				Last_Sold_Date = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 16));
				Stock_Cover_Days = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 17));
				Net_Sales_Qty_LY_YTD = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 18));
				Net_Sales_Qty_YTM = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 19));
				qty1 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 20));
				qty2 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 21));
				qty3 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 22));
				qty4 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 23));
				qty5 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 24));
				qty6 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 25));
				qty7 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 26));
				qty8 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 27));
				qty9 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 28));
				qty10 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 29));
				qty11 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 30));
				qty12 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 31));
				qty13 = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				
				//step validate 
				//Validate locationNumber is found in Master Table
				/*if(storeNoMasterMap.get(locationNumber) ==null){
					passValid = false;
					errorMsg ="ไม่พบข้อมูล LocationNumber ใน Master";
					storeNoErrorMap.put(locationNumber, locationNumber);
				}*/
				
				//insert log to display
			/*	lineMsg  = (r+1)+"|"+supplierNumber+"|"+locationNumber+"|"+locationName+"|"+classNumber+"|";
				lineMsg += subClassNumber+"|"+itemNumber+"|"+barcode+"|"+itemDesc+"|";
				lineMsg += eohQty+"|"+onOrderInTransitQty+"|"+packType+"|"+makroUnit+"|";
				lineMsg += AVG_NET_SALES_QTY+"|"+Net_Sales_Qty_YTD+"|"+Last_Received_Date+"|"+Last_Sold_Date+"|";
				lineMsg += Stock_Cover_Days+"|"+Net_Sales_Qty_LY_YTD+"|"+Net_Sales_Qty_YTM+"|"+qty1+"|";
				lineMsg += qty2+"|"+qty3+"|"+qty4+"|"+qty5+"|";
				lineMsg += qty6+"|"+qty7+"|"+qty8+"|"+qty9+"|";
				lineMsg += qty10+"|"+qty11+"|"+qty12+"|"+qty13;*/
				
				//logger.debug("lineMsg:"+lineMsg);
				//is valid
				int index =1;
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					//Insert Log
				    //insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
					
				}else{
					failCount++;
					//foundError = true; no rollback
					lineMsg += "|FAIL|"+errorMsg;
					//Insert Log
				    //insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				}
				//add statement
				ps.setString(index++, Utils.isNull(supplierNumber));
			    ps.setString(index++, Utils.isNull(locationNumber));
			    ps.setString(index++, Utils.isNull(locationName));
			    ps.setString(index++, Utils.isNull(storeNoMasterMap.get(locationNumber)));
			    ps.setString(index++, Utils.isNull(classNumber));
			    ps.setString(index++, Utils.isNull(subClassNumber));
			    ps.setString(index++, Utils.isNull(itemNumber));
			    ps.setString(index++, Utils.isNull(barcode));
			    ps.setString(index++, Utils.isNull(itemDesc));
			    ps.setDouble(index++, Utils.convertStrToDouble(eohQty));
			    ps.setDouble(index++, Utils.convertStrToDouble(onOrderInTransitQty));
			    ps.setString(index++, Utils.isNull(packType));
			    ps.setDouble(index++, Utils.convertStrToDouble(makroUnit));
			    ps.setDouble(index++, Utils.convertStrToDouble(AVG_NET_SALES_QTY));
			    ps.setDouble(index++, Utils.convertStrToDouble(Net_Sales_Qty_YTD));
			    ps.setString(index++,Utils.isNull(Last_Received_Date));
			    ps.setString(index++, Utils.isNull(Last_Sold_Date) );
			    ps.setDouble(index++, Utils.convertStrToDouble(Stock_Cover_Days));
			    ps.setDouble(index++, Utils.convertStrToDouble(Net_Sales_Qty_LY_YTD));
			    ps.setDouble(index++, Utils.convertStrToDouble(Net_Sales_Qty_YTM));
			    
			    ps.setDouble(index++, Utils.convertStrToDouble(qty1));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty2));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty3));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty4));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty5));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty6));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty7));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty8));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty9));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty10));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty11));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty12));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty13));
			    
			    ps.addBatch();
	    	}//for row
		
			//Insert Column For Display Result (1:no, last coloumn ->status,Message )default column
			/*lineMsg ="";
			lineMsg  = "No|Line Excel|supplierNumber|locationNumber|locationName|classNumber|";
			lineMsg += "subClassNumber|itemNumber|barcode|itemDesc|";
			lineMsg += "eohQty|onOrderInTransitQty|packType|makroUnit|";
			lineMsg += "AVG_NET_SALES_QTY|Net_Sales_Qty_YTD|Last_Received_Date|Last_Sold_Date|";
			lineMsg += "Stock_Cover_Days|Net_Sales_Qty_LY_YTD|Net_Sales_Qty_YTM|qty1|";
			lineMsg += "qty2|qty3|qty4|qty5|";
			lineMsg += "qty6|qty7|qty8|qty9|";
			lineMsg += "qty10|qty11|qty12|qty13|";
			lineMsg += "Status|Message";*/
			 
			 //insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
			 
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
			
			//debug streoNoError
			logger.debug(storeNoErrorMap.toString());
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
	
	 public static Map<String, String> initStoreNoMasterMap(Connection conn ) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Map<String, String> storeNoMap = new HashMap<String, String>();
			try {
				
				sql.append("\n select distinct store_no,region FROM PENSBI.XXPENS_BI_B2B_MST_STORE ");
				sql.append("\n WHERE STORE_TYPE ='MAKRO'  \n");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
			    while (rst.next()) {
			    	storeNoMap.put(Utils.isNull(rst.getString("STORE_NO")), Utils.isNull(rst.getString("region")));
				}//if
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return storeNoMap;
		}
	
}
