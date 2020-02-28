package com.isecinc.pens.web.batchtask.task;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import oracle.jpub.runtime.Util;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.TaskStoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.isecinc.pens.web.batchtask.subtask.GenStockOnhandRepTempLotusSubTask;
import com.isecinc.pens.web.report.ReportForm;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.page.ReportOnhandLotusAction;
import com.isecinc.pens.web.reportall.sql.ReportOnhandLotusSQL;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ExportReportOnhandLotusTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
	public static String PARAM_AS_OF_DATE ="AS_OF_DATE";
	public static String PARAM_STORE_CODE ="STORE_CODE";
	public static String PARAM_PENS_ITEM_FROM ="PENS_ITEM_FROM";
	public static String PARAM_PENS_ITEM_TO ="PENS_ITEM_TO";
	public static String PARAM_GROUP ="GROUP";
	public static String PARAM_SUMMARY_TYPE ="SUMMARY_TYPE";
	
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate$Button Name
	 */

	public String[] getParam(){
		String[] param = new String[1];
		param[0] = "temp|temp|TEXT||VALID";
		return param;
	}
	public List<BatchTaskListBean> getParamListBox(){
		return null;
	}
	public String getButtonName(){
		return "No BTN Is Run From Popup";
	}
	
	public String getDescription(){
		String desc = "Export Report Onhand Lotus (all store) <br/>";
		return desc;
	}
	public String getDevInfo(){
		return "";
	}
	//Display Result Batch MOnitor
	public boolean isDispDetail(){
		return true;
	}
    
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
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
			connMonitor = DBConnection.getInstance().getConnection();

			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
			
			/** Validate StoreCode is generated**/
			String storeCode = monitorModel.getBatchParamMap().get(PARAM_STORE_CODE);
			String asOfDate = monitorModel.getBatchParamMap().get(PARAM_AS_OF_DATE);
			String summaryType = monitorModel.getBatchParamMap().get(PARAM_SUMMARY_TYPE);
			logger.info("Start Gen StoreCode["+storeCode+"]OrderDate["+asOfDate+"]summaryType["+summaryType+"]...");
			
			/** Start process **/ 
			modelItem = process(connMonitor, connMonitor, monitorModel, modelItem);

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
			monitorModel.setFileName(modelItem.getFileName());
			//By BatchTask
			monitorModel.setType("EXPORT");
			monitorModel.setThName("Export ทุกสาขา To Excel");
			
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

	public static MonitorItemBean process(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) throws Exception{
		int status = Constants.STATUS_FAIL;
		int successCount = 0;int dataCount=0;int failCount = 0;
		int r = 0;
		boolean foundError = false;boolean passValid = false;String lineMsg = "";String errorMsg = "";
		XSSFWorkbook workbook = null;
		try{
		    //get parameter config 
			String asOfDate = monitorModel.getBatchParamMap().get(PARAM_AS_OF_DATE);
			String storeCode = monitorModel.getBatchParamMap().get(PARAM_STORE_CODE);
			String pensItemFrom = monitorModel.getBatchParamMap().get(PARAM_PENS_ITEM_FROM);
			String pensItemTo = monitorModel.getBatchParamMap().get(PARAM_PENS_ITEM_TO);
			String group = monitorModel.getBatchParamMap().get(PARAM_GROUP);
			String summaryType = monitorModel.getBatchParamMap().get(PARAM_SUMMARY_TYPE);
			
			//set criteria
			ReportAllForm aForm = new ReportAllForm();
			ReportAllBean criteria = new ReportAllBean();
			criteria.setPensCustCodeFrom(storeCode);
			criteria.setSalesDate(asOfDate);
			criteria.setPensItemFrom(Utils.isNull(pensItemFrom));
			criteria.setPensItemTo(Utils.isNull(pensItemTo));
			criteria.setGroup(Utils.isNull(group));
			criteria.setSummaryType(summaryType);
			aForm.setBean(criteria);
			//search report
			ReportAllBean resultReport = new ReportOnhandLotusAction().searchReportOnhandLotusAsOf(aForm,monitorModel.getUser());
			ReportAllBean summary = resultReport.getSummary();
			                                    
			workbook = genSheetData(criteria,resultReport.getItemsList(),summary);	
			String fileName ="StockOnhandLotusAsOf_"+monitorModel.getUser().getUserName()+".xls";
			
			//get RootPathTemp
			String rootPathTemp = FileUtil.getRootPathTemp(EnvProperties.getInstance());
			
			//write file to dev_temp
			String pathFile = rootPathTemp+fileName;
			
			logger.debug("Write to :"+pathFile);
			FileOutputStream fileOut = new FileOutputStream(pathFile);
		    workbook.write(fileOut);
		    fileOut.close();
		    
			if(foundError ==false){
				logger.info("Execute Batch");
				
				status = Constants.STATUS_SUCCESS;
			}
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);//successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
			monitorItemBean.setFileName(fileName);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			
		}
		return monitorItemBean;	
	}
	public static XSSFWorkbook genSheetData(ReportAllBean bean,List<ReportAllBean> dataList,ReportAllBean summary) throws Exception{
		XSSFWorkbook workbook = new XSSFWorkbook();
		String[] columns = null;
		if("PensItem".equalsIgnoreCase(bean.getSummaryType())){
			String[] columns1 = {"รหัสร้านค้า","ชื่อร้านค้า","PensItem", "Group","Sale In Qty","Sale Return Qty","Sales Out Qty "
						,"Adjust ","Stock short","Onhand Qty ","Price List","Amount"};
			columns = columns1;
		 }else{
		    String[] columns2 = {"รหัสร้านค้า","ชื่อร้านค้า", "Group","Sale In Qty","Sale Return Qty","Sales Out Qty "
						,"Adjust ","Stock short","Onhand Qty ","Price List","Amount"}; 
		    columns = columns2;
		 }
	
		try{
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet("DATA");
			
			// Create a Font for styling header cells
	        Font headerFont = workbook.createFont();
	        headerFont.setFontHeightInPoints((short) 14);
	        
	       // Create a CellStyle with the font
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFont(headerFont);
	        
	        Row headerRow = sheet.createRow(0);
	        Cell cell = headerRow.createCell(0);cell.setCellValue("รายงาน B'me Stock on-hand at Lotus(As Of)");
	        cell.setCellStyle(headerCellStyle);
	        //merge column
	        sheet.addMergedRegion(new CellRangeAddress(0,0,0,10));
	        
	        headerRow = sheet.createRow(1);
	        cell = headerRow.createCell(0);cell.setCellValue("จากวันที่ขาย :"+bean.getSalesDate());
	        cell.setCellStyle(headerCellStyle);
	        //merge column
	        sheet.addMergedRegion(new CellRangeAddress(1,1,0,10));
	        
	        headerRow = sheet.createRow(2);
	        cell = headerRow.createCell(0);cell.setCellValue("รหัสร้านค้า:"+bean.getPensCustCodeFrom());
	        cell.setCellStyle(headerCellStyle);
	        //merge column
	        sheet.addMergedRegion(new CellRangeAddress(2,2,0,10));
	        
	        headerRow = sheet.createRow(3);
	        cell = headerRow.createCell(0);cell.setCellValue("Pens Item From:"+bean.getPensItemFrom()+" Pens Item To:"+bean.getPensItemTo());
	        cell.setCellStyle(headerCellStyle);
	        //merge column
	        sheet.addMergedRegion(new CellRangeAddress(3,3,0,10));
	        
	        headerRow = sheet.createRow(4);
	        cell = headerRow.createCell(0);cell.setCellValue("Group:"+bean.getGroup());
	        cell.setCellStyle(headerCellStyle);
	        //merge column
	        sheet.addMergedRegion(new CellRangeAddress(4,4,0,10));
	        
	        // Create a Row
	        headerRow = sheet.createRow(5);
	        // Create cells
	        for(int i = 0; i < columns.length; i++) {
	            cell = headerRow.createCell(i);
	            cell.setCellValue(columns[i]);
	            cell.setCellStyle(headerCellStyle);
	        }
	        // Create Cell Style for formatting Date
	        CellStyle numStyle = workbook.createCellStyle();
	        numStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));
	        
	        CellStyle currencyStyle = workbook.createCellStyle();
	        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
	        
	        Row row = null;
	        int rowNum = 6;
	        int colNo=0;
	        for(int i=0;i<dataList.size();i++) {
	        	ReportAllBean item = dataList.get(i);
	      
	        	//running row
	            row = sheet.createRow(rowNum++);
	            //set column value
	            row.createCell(colNo++).setCellValue(item.getStoreCode());
	            row.createCell(colNo++).setCellValue(item.getStoreName());
	            if("PensItem".equalsIgnoreCase(bean.getSummaryType())){
	              row.createCell(colNo++).setCellValue(item.getPensItem());
	            }
	            row.createCell(colNo++).setCellValue(item.getGroup());
	            
	            //set type numeric value must double or float (no text)
	            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(item.getSaleInQty()));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(item.getSaleReturnQty()));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(item.getSaleOutQty()));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(item.getAdjustQty()));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(item.getStockShortQty()));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(item.getOnhandQty()));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(item.getRetailPriceBF()));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(currencyStyle);
	            
	            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(item.getOnhandAmt()));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(currencyStyle); 
	          
	            //set colNo
	            colNo=0;
	        }//for
	        
            //Gen Summary
	        colNo = 0;
            row = sheet.createRow(rowNum++);
            //set column value
            row.createCell(colNo++).setCellValue("");
            row.createCell(colNo++).setCellValue("");
            if("PensItem".equalsIgnoreCase(bean.getSummaryType())){
              row.createCell(colNo++).setCellValue("");
            }
            row.createCell(colNo++).setCellValue("Total");
            
            //set type numeric value must double or float (no text)
            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(summary.getSaleInQty()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
            
            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(summary.getSaleReturnQty()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
            
            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(summary.getSaleOutQty()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
            
            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(summary.getAdjustQty()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
            
            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(summary.getStockShortQty()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
            
            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(summary.getOnhandQty()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
            
            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(summary.getRetailPriceBF()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(currencyStyle);
            
            cell =  row.createCell(colNo++);cell.setCellValue(Utils.convertStrToDouble(summary.getOnhandAmt()));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(currencyStyle); 
	        
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columns.length; i++) {
	            sheet.autoSizeColumn(i);
	        }
	        
			return workbook;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
}
