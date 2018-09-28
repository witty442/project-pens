package com.isecinc.pens.web.imports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.sql.ReportMonthEndLotusSQL;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;

public class ReconcileProcess {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public  ActionForward run(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		ImportForm importForm = (ImportForm) form;
		String fileName = "";
	    String fileType ="";
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
			}
			
			//Read Excel to TABLE BARCODE_TEMP
			try{
			   importForm = readExcel(conn,importForm);
			   
			   if(importForm.getSummaryErrorList() != null){
					request.setAttribute("errorList", importForm.getSummaryErrorList());
					conn.rollback();
					request.setAttribute("Message","ไม่สามารถ Import Excel ได้ โปรดตรวจสอบ Format Excel");
				}else{
					//Compare PENSBME_ENDDATE_STOCK_TEMP Group by Group Code
					conn.commit();
					
					//Reconcile
					List<OnhandSummary> reconcileList = searchReconcileList(conn,importForm.getStoreCode());
					if(reconcileList != null && reconcileList.size() >0){
					   request.setAttribute("reconcileList",reconcileList);
					   request.setAttribute("Message","Reconcile ไฟล์ "+fileName+" สำเร็จ");
					}else{
					   request.setAttribute("Message","ไม่พบข้อมูลตั้งต้น ที่จะนำมา Reconcile กรุณากดปุ่ม Gen Data เปรียบเทียบนับสต๊อก ก่อน");
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				request.setAttribute("Message","ไม่สามารถ Import Excel ได้ โปรดตรวจสอบ Format Excel");
				conn.rollback();
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
			}catch(Exception e){}
		}
		return mapping.findForward("success");
	}
	
	  public List<OnhandSummary> searchReconcileList(Connection conn,String storeCode) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<OnhandSummary> pos = new ArrayList<OnhandSummary>();
			StringBuilder sql = new StringBuilder();
			try {
				//Gen SQL
			    sql.append("select h.* \n" +
				         " ,(SELECT COUNT(*) FROM SCAN_BARCODE_TEMP T WHERE h.store_code = T.store_code " +
				         " and h.group_code = t.group_code GROUP BY h.store_code) as scan_qty \n"+
				         " ,(SELECT NVL(MAX(RETAIL_PRICE_BF),0) FROM PENSBME_ONHAND_BME_LOCKED T WHERE h.group_code = T.group_item) as retail_price_bf \n"+
			    		 " from PENSBME_ENDDATE_STOCK_TEMP h \n" +
			    		 " where store_code ='"+storeCode+"' \n");
			    sql.append(" ORDER BY h.group_code \n");
			    
			    logger.debug("sql:"+sql.toString());
			    
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					OnhandSummary item = new OnhandSummary();
					item.setStoreCode(rst.getString("store_code"));
					item.setGroup(rst.getString("group_code"));
					item.setBeginingQty(Utils.decimalFormat(rst.getDouble("begining_qty"),Utils.format_current_no_disgit));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("Ending_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("ADJUST_QTY"),Utils.format_current_no_disgit));
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("SHORT_QTY"),Utils.format_current_no_disgit));
					if(rst.getDouble("scan_qty") > 0){
					  item.setScanQty(Utils.decimalFormat(rst.getDouble("SCAN_QTY"),Utils.format_current_no_disgit));
					}else{
					  item.setScanQty("");
					}
					item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price_bf"),Utils.format_current_2_disgit));
					
					pos.add(item);
				}//while
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				
				} catch (Exception e) {}
			}
		return pos;
	 }
	  
	public ImportForm readExcel(Connection conn ,ImportForm importForm) throws Exception{

        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    String fileName = "";
	    String fileType ="";
	    List<ImportSummary> summarySuccessList = null;
		List<ImportSummary> summaryErrorList = null;
	    int idx = 0;
	    boolean excute = false;
		try {
			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				
				logger.debug("fileType: " + fileType);
				logger.debug("fileName: " + fileName);

				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO SCAN_BARCODE_TEMP(BARCODE ,GROUP_CODE,STORE_CODE)\n");
				sql.append(" VALUES(?,?,?)");

				
				 /** Delete All before Import **/
				psDelete = conn.prepareStatement("delete from SCAN_BARCODE_TEMP where store_code ='"+importForm.getStoreCode()+"'");
				psDelete.executeUpdate();
				  
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 0; // row of begin data
				int maxColumnNo = 1; // max column of data per row
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
				String barcode = "";
				Cell cell = null;
				Object columnCheck = null;
	            int no = 0;
				
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
				for (int i = rowNo; i <= sheet.getLastRowNum(); i++) {
					row = sheet.getRow(i);
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						//logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
					    
						if(colNo==0){
						   //BARCODE
						   barcode = Utils.isNull(cellValue).toString();
						   ps.setString(1, barcode);
						   //Get Group Code
						   PopupForm cri = new PopupForm();
						   cri.setCodeSearch(barcode);
						   Barcode b = GeneralDAO.searchProductByBarcode(cri);
						   if(b != null){
							   //SuccessList 
							   if(summarySuccessList==null){
								   summarySuccessList = new ArrayList<ImportSummary>();
								   ImportSummary m = new ImportSummary();
								   m.setRow(i+1);
								   m.setBarcode(barcode);
								   summarySuccessList.add(m);
								   
								   //Set 
								   ps.setString(2, b.getGroupCode());
							   }else{
								   ImportSummary m = new ImportSummary();
								   m.setRow(i+1);
								   m.setBarcode(barcode);
								   summarySuccessList.add(m);
								   
								   //Set 
								   ps.setString(2, b.getGroupCode());
							   }
						   }else{
							   //ErrorList
							   if(summaryErrorList==null){
								   summaryErrorList = new ArrayList<ImportSummary>();
								   ImportSummary m = new ImportSummary();
								   m.setRow(i+1);
								   m.setBarcode(barcode);
								   m.setMessage("ไม่พบข้อมูลใน Master ");
								   summaryErrorList.add(m);
								   
								   //Set 
								   ps.setString(2, "");
							   }else{
								   ImportSummary m = new ImportSummary();
								   m.setRow(i+1);
								   m.setBarcode(barcode);
								   m.setMessage("ไม่พบข้อมูลใน Master ");
								   summaryErrorList.add(m);
								   
								   //Set 
								   ps.setString(2, "");
							   }
						   }
						   
						   columnCheck = cellValue;
						}
						
					} //for column
					
			         if(columnCheck != null){
			        	 excute = true;
			        	idx++;//Uniq
                        ps.setString(3, importForm.getStoreCode());
                        
					    ps.executeUpdate();
			         }
				}//for Row
			}
		   
			logger.debug("excute:"+excute);
			if(excute==false){
				throw new Exception("ExcelException");
			}
			
		   importForm.setSummarySuccessList(summarySuccessList);
		   importForm.setSummaryErrorList(summaryErrorList);
		} catch (Exception e) {
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
		return importForm;
	}
}
