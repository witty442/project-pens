package com.isecinc.pens.web.batchtask.task;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.page.ReportOnhandLotusAction;
import com.isecinc.pens.web.reportall.sql.ReportOnhandAsOfRobinsonSQL;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;

public class ExportReportOnhandRobinsonTask extends BatchTask implements BatchTaskInterface{
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
		ReportAllBean resultReport = null;
		ReportAllBean summary = null;
		ReportAllBean summaryAll = new ReportAllBean();
		String storeCodeCheck = "";
		String[] storeCodeCheckArr = null;
		List<StoreBean> storeList = null;
		StringBuffer sql = new StringBuffer("");
		List<ReportAllBean> allDataList = new ArrayList<ReportAllBean>();
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
			
			//Get AllStore Lotus
			if(storeCodeCheck.indexOf("ALL") != -1){
				//All
				storeList = StoreDAO.getStoreList(conn, com.isecinc.pens.dao.constants.Constants.STORE_TYPE_ROBINSON_CODE);
			}else{
				//StoreCode more 1> 020047-1,020049-4
				storeList = StoreDAO.getStoreList(conn, com.isecinc.pens.dao.constants.Constants.STORE_TYPE_ROBINSON_CODE,SQLHelper.converToTextSqlIn(storeCodeCheck));
			}
			//Loop By StoreList
			if(storeList != null && storeList.size() >0){
				Date initDate = null;
				for(int i=0;i<storeList.size();i++){
					//Loop Step by Store Code
					StoreBean storeBean = storeList.get(i);
					criteria.setPensCustCodeFrom(storeBean.getStoreCode());
					
					//Get InitDate By StoreCode 
					initDate = new SummaryDAO().searchInitDateMTT(conn,criteria.getPensCustCodeFrom());
					
					sql = ReportOnhandAsOfRobinsonSQL.genSQL(conn, criteria, initDate,aForm.getBean().getSummaryType());
					
					//search report step bu storeCode
					resultReport = searchReport(conn, sql, summaryType);
					summary = resultReport.getSummary();
					
					//add all Data Row
					allDataList.addAll(resultReport.getItemsList());
					//summary Total
					summaryAll = summaryTotalAll(summaryAll,summary);
				}//for
				
			}//if

			workbook = genSheetData(criteria,allDataList,summaryAll);	
			String fileName ="StockOnhandRobinsAsOf_"+monitorModel.getUser().getUserName()+".xls";
			
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
	public static ReportAllBean searchReport(Connection conn,StringBuffer sql,String summaryType) throws Exception{
		Statement stmt = null;
		ResultSet rst = null;
		double init_QTY = 0;
		double sale_in_qty = 0;
		double sale_out_qty = 0;
		double sale_return_qty = 0;
		double onhand_qty = 0;
	    double adjustQtyTemp = 0;
		double stockShortQtyTemp = 0;
		ReportAllBean reportBean = new ReportAllBean();
		List<ReportAllBean> rowAllList = new ArrayList<ReportAllBean>();
		ReportAllBean item = null;
		try{
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				item = new ReportAllBean();
				item.setStoreCode(rst.getString("customer_code"));
				item.setCustNo(rst.getString("cust_no"));
				item.setStoreName(rst.getString("customer_desc"));
				
				if(!"GroupCode".equalsIgnoreCase(summaryType)){	
				  item.setPensItem(rst.getString("pens_item"));
				}
				item.setGroup(rst.getString("group_type"));
				item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("init_sale_qty"),Utils.format_current_no_disgit));
				item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
				item.setAdjustQty(Utils.decimalFormat(rst.getDouble("adjust_qty"),Utils.format_current_no_disgit));
				item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
	
				rowAllList.add(item);
				
				//Sum All Row
				init_QTY += rst.getDouble("init_sale_qty");
				sale_in_qty += rst.getDouble("sale_in_qty");
				sale_return_qty += rst.getDouble("sale_return_qty");
				sale_out_qty += rst.getDouble("sale_out_qty");
				adjustQtyTemp += rst.getDouble("adjust_qty");
				stockShortQtyTemp += rst.getDouble("STOCK_SHORT_QTY");
				onhand_qty += rst.getDouble("onhand_qty");
	
			}//while
			//summary
			item = new ReportAllBean();
			item.setInitSaleQty(Utils.decimalFormat(init_QTY,Utils.format_current_no_disgit));
			item.setSaleInQty(Utils.decimalFormat(sale_in_qty,Utils.format_current_no_disgit));
			item.setSaleReturnQty(Utils.decimalFormat(sale_return_qty,Utils.format_current_no_disgit));
			item.setSaleOutQty(Utils.decimalFormat(sale_out_qty,Utils.format_current_no_disgit));
			item.setAdjustQty(Utils.decimalFormat(adjustQtyTemp,Utils.format_current_no_disgit));
			item.setStockShortQty(Utils.decimalFormat(stockShortQtyTemp,Utils.format_current_no_disgit));
			item.setOnhandQty(Utils.decimalFormat(onhand_qty,Utils.format_current_no_disgit));
			
			reportBean.setItemsList(rowAllList);
			reportBean.setSummary(item);
			return reportBean;
		}catch(Exception e){
			throw e;
		}finally{
			stmt.close();
			rst.close();
		}
	}
	public static ReportAllBean summaryTotalAll(ReportAllBean summaryAll,ReportAllBean summary) throws Exception{
		summaryAll.setInitSaleQty(Utils.decimalFormat( Utils.convertStrToInt(summaryAll.getInitSaleQty())+ Utils.convertStrToInt(summary.getInitSaleQty()),Utils.format_current_no_disgit));
		summaryAll.setSaleInQty(Utils.decimalFormat(Utils.convertStrToInt(summaryAll.getSaleInQty())+ Utils.convertStrToInt(summary.getSaleInQty()),Utils.format_current_no_disgit));
		summaryAll.setSaleReturnQty(Utils.decimalFormat(Utils.convertStrToInt(summaryAll.getSaleReturnQty())+ Utils.convertStrToInt(summary.getSaleReturnQty()),Utils.format_current_no_disgit));
		summaryAll.setSaleOutQty(Utils.decimalFormat(Utils.convertStrToInt(summaryAll.getSaleOutQty())+ Utils.convertStrToInt(summary.getSaleOutQty()),Utils.format_current_no_disgit));
		summaryAll.setAdjustQty(Utils.decimalFormat(Utils.convertStrToInt(summaryAll.getAdjustQty())+ Utils.convertStrToInt(summary.getAdjustQty()),Utils.format_current_no_disgit));
		summaryAll.setStockShortQty(Utils.decimalFormat(Utils.convertStrToInt(summaryAll.getStockShortQty())+ Utils.convertStrToInt(summary.getStockShortQty()),Utils.format_current_no_disgit));
		summaryAll.setOnhandQty(Utils.decimalFormat(Utils.convertStrToInt(summaryAll.getOnhandQty())+ Utils.convertStrToInt(summary.getOnhandQty()),Utils.format_current_no_disgit));
		return summaryAll;
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
