package com.isecinc.pens.web.batchtask.task;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskDispBean;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.meter.MonitorTime;

public class ExportB2BMakroToExcelTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * Return :P Name|P label|P Type|default value|valid,P2...$processName,Button Name|....
	*/
	/*public String getParam(){
		return "dataType|ระบุประเภทไฟล์|LIST||VALID$Export ข้อมูล";
	} */
	public String[] getParam(){
		String[] param = new String[1];
		param[0] = "dataType|ระบุประเภทไฟล์|LIST||VALID";
		return param;
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
	
		return listAll;
	}
	public String getButtonName(){
		return "Export ข้อมูล";
	}
	public String getDescription(){
		return "Import B2B Makro From File Excel";
	}
	public String getDevInfo(){
		return "APPS.XXPENS_OM_PUSH_ORDER_ITEM,APPS.XXPENS_OM_PUSH_ORDER_TEMP  ";
	}
	//Show detail BatchTaskResult or no
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
			conn = DBConnection.getInstance().getConnectionApps();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item ExportB2BMakroToExcelTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
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
			monitorModel.setTransactionType("B2B");
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			monitorModel.setFileName(modelItem.getFileName());
			
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
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		PreparedStatement ps = null;
		String errorMsg ="",errorCode ="";
		XSSFWorkbook workbook = null;
		EnvProperties env = EnvProperties.getInstance();
		String rootPathTemp = "";
		String pathFile = "";
		String fileName = "";
		int status = Constants.STATUS_SUCCESS;
		try{
			//get RootOathTemp
			rootPathTemp = FileUtil.getRootPathTemp(env);
			
		    //Step 1 Call Procedure
			try{
			   callProcedure(conn);
			}catch(Exception ee){
				status = Constants.STATUS_FAIL;
				errorMsg = "Exception Cannot Call Procedure :apps.xxpens_om_push_order_pkg.b2b() \n";
				errorMsg += ""+ee.getMessage();
				ee.printStackTrace();
			}
			
			if(status != Constants.STATUS_FAIL){
			    //Step2 Gen Sheet1 data
				workbook = genSheetData(conn);
				
				//Step3 Gen sheet2 PO_UPLOAD
				workbook = genSheetPOUploadData(conn,workbook);
				
				// gen file to temp server dev_temp
				fileName = "B2B_MAKRO_"+monitorModel.getCreateUser()+".xls";
				pathFile = rootPathTemp+fileName;
				
				logger.debug("Write to :"+pathFile);
				FileOutputStream fileOut = new FileOutputStream(pathFile);
			    workbook.write(fileOut);
			    fileOut.close();
			}
			
			//step 3 delete temp
			//SQLHelper.excUpdate(conn, "delete from apps.XXPENS_OM_PUSH_ORDER_ITEM ");
			//SQLHelper.excUpdate(conn, "delete from apps.XXPENS_OM_PUSH_ORDER_TEMP ");
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setFileName(fileName);
			monitorItemBean.setErrorCode(errorCode);
			monitorItemBean.setErrorMsg(errorMsg);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();ps = null;
				}
				if(workbook != null){
					workbook =null;
				}
			}catch(Exception ee){}
		}
		return monitorItemBean;	
	}
	
	 public static void callProcedure(Connection conn ) throws Exception {
			CallableStatement callStmt = null;
			try {
				callStmt = conn.prepareCall("{call xxpens_om_push_order_pkg.b2b()}");
				callStmt.executeUpdate();
			   
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					callStmt.close();
				} catch (Exception e) {}
			}
		}
	 
	public static XSSFWorkbook genSheetData(Connection conn) throws Exception{
		XSSFWorkbook workbook = new XSSFWorkbook();

		String[] columns = {"","SUPPLIER_NUMBER","LOCATION_NUMBER", "LOCATION_NAME","ITEM_NUMBER","BARCODE","PACK_SIZE"
							,"EOH_QTY","ON_ORDER_QTY","ON_HAND","AVG_NET_SALES_QTY","STOCK_COVER_DAYS","COVERDAY","FORECAST_DAY"
							,"FORECAST_QTY","MOD_QTY","PUSH_QTY","NEW_CVD","ORGANIZATION_ID","INVENTORY_ITEM_ID","SEGMENT1","DESCRIPTION"};
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet("DATA");
			
			// Create a Font for styling header cells
	        Font headerFont = workbook.createFont();
	        headerFont.setFontHeightInPoints((short) 14);
	       // headerFont.setColor(IndexedColors.RED.getIndex());
	        
	       // Create a CellStyle with the font
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFont(headerFont);
	        
	        // Create a Row
	        Row headerRow = sheet.createRow(0);
	        // Create cells
	        for(int i = 0; i < columns.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(columns[i]);
	            cell.setCellStyle(headerCellStyle);
	        }
	        // Create Cell Style for formatting Date
	        CellStyle numStyle = workbook.createCellStyle();
	        numStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));
	        
	        CellStyle currencyStyle = workbook.createCellStyle();
	        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
	        
	        /** get Data from view **/
	        ps = conn.prepareStatement("select * from apps.xxpens_om_push_order_vl");
	        rs = ps.executeQuery();
	        int rowNum = 1;
	        int no=0;
	        Cell cell = null;
	        while(rs.next()) {
	        	no++;
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(no);//
	            row.createCell(1).setCellValue(rs.getString("SUPPLIER_NUMBER"));
	            row.createCell(2).setCellValue(rs.getString("LOCATION_NUMBER"));
	            row.createCell(3).setCellValue(rs.getString("LOCATION_NAME"));
	            row.createCell(4).setCellValue(rs.getString("ITEM_NUMBER"));//
	            row.createCell(5).setCellValue(rs.getString("BARCODE"));//
	            
	            cell =  row.createCell(6);cell.setCellValue(rs.getDouble("PACK_SIZE"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(7);cell.setCellValue(rs.getDouble("EOH_QTY"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(8);cell.setCellValue(rs.getDouble("ON_ORDER_QTY"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(9);cell.setCellValue(rs.getDouble("ON_HAND"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(10);cell.setCellValue(rs.getDouble("AVG_NET_SALES_QTY"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(currencyStyle);
	            
	            cell =  row.createCell(11);cell.setCellValue(rs.getDouble("STOCK_COVER_DAYS"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(currencyStyle);
	            
	            cell =  row.createCell(12);cell.setCellValue(rs.getDouble("COVERDAY"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(13);cell.setCellValue(rs.getDouble("FORECAST_DAY"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(currencyStyle);
	            
	            cell =  row.createCell(14);cell.setCellValue(rs.getDouble("FORECAST_QTY"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(15);cell.setCellValue(rs.getDouble("MOD_QTY"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(16);cell.setCellValue(rs.getDouble("PUSH_QTY"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(17);cell.setCellValue(rs.getDouble("NEW_CVD"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(currencyStyle);
	            
	            cell =  row.createCell(18);cell.setCellValue(rs.getDouble("ORGANIZATION_ID"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	            
	            cell =  row.createCell(19);cell.setCellValue(rs.getDouble("INVENTORY_ITEM_ID"));
	            cell.setCellType(Cell.CELL_TYPE_NUMERIC);cell.setCellStyle(numStyle);
	           
	            row.createCell(20).setCellValue(rs.getString("SEGMENT1"));//
	            row.createCell(21).setCellValue(rs.getString("DESCRIPTION"));//
	            
	        }//for

			// Resize all columns to fit the content size
	        for(int i = 0; i < columns.length; i++) {
	            sheet.autoSizeColumn(i);
	        }
	        
			return workbook;
		}catch(Exception e){
			throw e;
		}finally{
			rs.close();
			ps.close();
		}
	}
	
	public static XSSFWorkbook genSheetPOUploadData(Connection conn,XSSFWorkbook workbook) throws Exception{

		String[] columns = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		PreparedStatement ps = null;
		try{
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet("PO_UPLOAD");
			
			// Create a Font for styling header cells
	        Font headerFont = workbook.createFont();
	        headerFont.setFontHeightInPoints((short) 14);
	        
	       // Create a CellStyle with the font
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFont(headerFont);
	        
	        //init column all
	        ps = conn.prepareStatement("select * from apps.xxpens_om_push_order_v1");
	        rsmd = ps.getMetaData();
	        
	        logger.debug("columnCount:"+rsmd.getColumnCount());
	        
	        columns = new String[rsmd.getColumnCount()+1];
	        columns[0] = "";//no
	        for(int i=1;i<=rsmd.getColumnCount();i++){
	        	columns[i] = rsmd.getColumnName(i);
	        }
	        
	        // Create a Row
	        Row headerRow = sheet.createRow(0);
	        // Create cells
	        for(int i = 0; i < columns.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(columns[i]);
	            cell.setCellStyle(headerCellStyle);
	        }
	       
	        CellStyle numStyle = workbook.createCellStyle();
	        numStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));
	        
	        CellStyle currencyStyle = workbook.createCellStyle();
	        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
	        
	        /** get Data from view **/
	        int rowNum = 1;
	        int no=0;
	        int c =0;
	        Cell cell = null;
	        rs = ps.executeQuery();
	        while(rs.next()) {
	        	no++;
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(c++).setCellValue(no);//
	            //Get all column
	            for(int i=1;i<=rsmd.getColumnCount();i++){
	            	if(i<=2){
	            		row.createCell(c++).setCellValue(rs.getString(rsmd.getColumnName(i)));
	            	}else{
	                    cell = row.createCell(c++);
	                    if(rs.getDouble(rsmd.getColumnName(i))!=0){
	                       cell.setCellValue(rs.getDouble(rsmd.getColumnName(i)));
	                       cell.setCellStyle(numStyle);
		                   cell.setCellType(Cell.CELL_TYPE_NUMERIC);
	                    }else{
	                       cell.setCellValue("");
	                    }
	                   
	            	}
	            }
	            c=0;
	        }//for

			// Resize all columns to fit the content size
	        for(int i = 0; i < columns.length; i++) {
	            sheet.autoSizeColumn(i);
	        }
	        
			return workbook;
		}catch(Exception e){
			throw e;
		}finally{
			rs.close();
			ps.close();
		}
	}
	
}
