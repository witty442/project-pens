package com.isecinc.pens.web.imports;

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

import util.Constants;
import util.UploadXLSUtil;

import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MasterBean;
import com.isecinc.pens.bean.Message;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.export.ExportReturnWacoal;

/**
 * ImportAction Class
 * 
 * @author Witty.B
 * @version $Id: ConversionAction.java,v 1.0 19/10/2010 00:00:00
 * 
 */

public class ImportProcess {

	protected static Logger logger = Logger.getLogger("PENS");
	
	public ActionForward importMaster(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		ImportForm importForm = (ImportForm) form;
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    User user = (User) request.getSession().getAttribute("user");
	    String fileName = "";
	    String fileType ="";
	    int idx = 0;
		try {
			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				
				logger.debug("fileType: " + fileType);
				logger.debug("fileName: " + fileName);

				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_MST_REFERENCE( \n");
				sql.append(" REFERENCE_CODE,INTERFACE_VALUE,INTERFACE_DESC ,PENS_VALUE,PENS_DESC,PENS_DESC2," +
						"PENS_DESC3,sequence,status,CREATE_DATE,CREATE_USER ,FILE_NAME ,ID)\n");
				
				sql.append(" VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?)");

				conn = DBConnection.getInstance().getConnection();
				conn.setAutoCommit(false);
				
				 /** Delete All before Import **/
				psDelete = conn.prepareStatement("delete from PENSBME_MST_REFERENCE");
				psDelete.executeUpdate();
				  
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 1; // row of begin data
				int maxColumnNo = 9; // max column of data per row
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
						   //REFERENCE_CODE
						   ps.setString(1, Utils.isNull(cellValue).toString());
						   
						   columnCheck = cellValue;
						}else if(colNo==1){
						   //INTERFACE_VALUE
						   ps.setString(2, Utils.convertDoubleStrToStr(Utils.isNull(cellValue).toString()));
						}else if(colNo==2){
						   //INTERFACE_DESC
							//logger.debug("InterfaceDesc:"+Utils.isNull(cellValue));
						   if(Utils.isDouble(Utils.isNull(cellValue))){
						       ps.setString(3, Utils.convertStrToDoubleStr(Utils.isNull(cellValue).toString()));
							}else{
							   ps.setString(3, Utils.isNull(cellValue).toString());	
							}
						}else if(colNo==3){
						   //PENS_VALUE
							if(Utils.isNumeric(Utils.isNull(cellValue))){
						       ps.setString(4, Utils.convertDoubleStrToStr(Utils.isNull(cellValue).toString()));
							}else{
							   ps.setString(4, Utils.isNull(cellValue).toString());	
							}
						}else if(colNo==4){
						  //PENS_DESC
							if(Utils.isNumeric(Utils.isNull(cellValue))){
							   ps.setString(5, Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue)));
							}else{
							  ps.setString(5, Utils.isNull(cellValue));
							}
						}else if(colNo==5){
						  //PENS_DESC2
							if(Utils.isNumeric(Utils.isNull(cellValue))){
							   ps.setString(6, Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue)));
							}else{
							   ps.setString(6, Utils.isNull(cellValue));
							}
						
						}else if(colNo==6){
						  //PENS_DESC3
							if(Utils.isNumeric(Utils.isNull(cellValue))){
							   ps.setString(7, Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue)));
							}else{
							   ps.setString(7, Utils.isNull(cellValue));
							}
							
						}else if(colNo==7){
						  //SEQUENCE
							if(Utils.isNumeric(Utils.isNull(cellValue))){
							   ps.setString(8, Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue)));
							}else{
							   ps.setString(8, Utils.isNull(cellValue));
							}
						}else if(colNo==8){
							//STATUS
						    if(Utils.isNumeric(Utils.isNull(cellValue))){
							   ps.setString(9, Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue)));
							}else{
								ps.setString(9, Utils.isNull(cellValue));
							}
						}
						
					} //for column
				
					 //CREATE_DATE
					 ps.setTimestamp(10, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(11, user.getUserName());
			         //FILE_NAME
			         ps.setString(12, fileName);

			         if(columnCheck != null){
			        	idx++;//Uniq
			        	ps.setInt(13, idx);
			        	
					    ps.executeUpdate();
			         }
				}//for Row
			}
			
			request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mapping.findForward("success");
	}
	
	
	public ActionForward importPhysical(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		StringBuffer whereCause = null;
		String errorDesc = null;
		int id = 0;
		int allCount = 0;
		int successCount = 0;
	    int errorCount = 0;
		String forward = "view";
		ImportForm importForm = (ImportForm) form;
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    User user = (User) request.getSession().getAttribute("user");
	    String fileName = "";
	    String fileType ="";
	    ImportDAO importDAO = new ImportDAO();
	    boolean importError = false;
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    
	    Map<String, ImportSummary> successMap = new HashMap<String, ImportSummary>();
	    Map<String, ImportSummary> errorMap = new HashMap<String, ImportSummary>();
		try {
			String storeTypeItem = "";
			if("020047".startsWith(importForm.getCustCode())){
				storeTypeItem = "LotusItem";	
	    	}else if("020049".startsWith(importForm.getCustCode())){
	    		storeTypeItem = "BigCitem";
	    	}
			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());

				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PHYSICAL_COUNT \n");
				sql.append(" (COUNT_DATE,CUST_CODE, BARCODE, ITEM, CREATE_DATE, CREATE_USER, FILE_NAME) \n");
				sql.append(" VALUES (?, ?,?, ?, ?, ?, ?)\n");

				conn = DBConnection.getInstance().getConnection();
				conn.setAutoCommit(false);
				
				/** cehck FileName duplicate **/
				boolean dupFile = importDAO.importPhyFileNameIsDuplicate(conn,importForm.getCountDate(),importForm.getCustCode(), fileName);
				Master mm = importDAO.getStoreName(conn,"Store",importForm.getCustCode());
				logger.debug("MM["+mm+"]");
				if(dupFile){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+" วันที่นับสต็อก :"+importForm.getCountDate()+" รหัสร้านค้า:"+mm.getPensDesc()+" ได้เนื่องจากมีการ  Upload ไปแล้ว");
					return mapping.findForward("success");
				}
 
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
				Cell cell = null;
	            int no = 0;
			
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
	            Date countDate = Utils.parse( importForm.getCountDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	            String custCode = importForm.getCustCode();
	            String barcode = "";
	            
				for (int i = rowNo; i <= sheet.getLastRowNum(); i++) {
					row = sheet.getRow(i);
					/** Check Row is null **/
					Cell cellCheck = row.getCell((short) 0);
					Object cellCheckValue = xslUtils.getCellValue(0, cellCheck);
					String rowCheck = Utils.isNull(cellCheckValue).toString();
					logger.debug("rowCheck["+rowCheck+"]");
					if("".equals(rowCheck)){
						break;
					}
					
					ps.setTimestamp(1, new java.sql.Timestamp(countDate.getTime()));
				    ps.setString(2, custCode);
				    
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
					
						if(colNo==0){
						   //BARCODE
						   barcode = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(3, barcode);
						   
						   String item = importDAO.getItemByBarcode(conn,storeTypeItem, barcode);
						   if(!"".equalsIgnoreCase(Utils.isNull(item))){
							   //ITEM 
							   ps.setString(4, Utils.isNull(item));
							   
							   ImportSummary s = new ImportSummary();
						       s.setRow(i+1);
						       s.setDescription(barcode);
						       s.setMessage("Success");
						       successMap.put((i+1)+"", s);
							   successCount++;
						   }else{
							   ps.setString(4, barcode);
							   
							   importError = true;
							   ImportSummary s = new ImportSummary();
						       s.setRow(i+1);
						       s.setDescription(barcode);
						       s.setMessage("Barcode นี้ไม่พบในข้อมูล Master");
						       errorMap.put((i+1)+"", s);
						       errorCount++;
						   }
						}
					} //for column
				
					 //CREATE_DATE
					 ps.setTimestamp(5, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(6, user.getUserName());
			         //FILE_NAME
			         ps.setString(7, fileName);

					 ps.executeUpdate();
			         
				}//for Row
			}
			
			if(importError){
				 request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
				  
				 /** Success List **/
				 Iterator it = successMap.keySet().iterator();
				 while(it.hasNext()){
					  String key = (String)it.next();
					  logger.debug("key:"+key);
					  ImportSummary mm = (ImportSummary)successMap.get(key);
					  successList.add(mm);
				  }
				 
				 //Sort
				  Collections.sort(successList, new Comparator<ImportSummary>() {
					  public int compare(ImportSummary a, ImportSummary b) {
					        return a.getRow()- b.getRow();
					    }
					});
				  
				  /** Error List **/
				  it = errorMap.keySet().iterator();
					 while(it.hasNext()){
						  String key = (String)it.next();
						  logger.debug("key:"+key);
						  ImportSummary mm = (ImportSummary)errorMap.get(key);
						  errorList.add(mm);
					  }
					 
					 //Sort
					  Collections.sort(errorList, new Comparator<ImportSummary>() {
						  public int compare(ImportSummary a, ImportSummary b) {
						        return a.getRow()- b.getRow();
						    }
						});
					  
				  
				  importForm.setSummaryErrorList(errorList);
				  importForm.setSummarySuccessList(successList);
				  
				  importForm.setTotalSize(errorList.size()+successList.size());
				  importForm.setSummaryPhyListErrorSize(errorList!=null?errorList.size():0);
				  importForm.setSummaryPhyListSuccessSize(successList!=null?successList.size():0);
                  conn.rollback();
			}else{
				
				/** Success List **/
				 Iterator it = successMap.keySet().iterator();
				 while(it.hasNext()){
					  String key = (String)it.next();
					  logger.debug("key:"+key);
					  ImportSummary mm = (ImportSummary)successMap.get(key);
					  successList.add(mm);
				  }
				 
				 //Sort
				  Collections.sort(successList, new Comparator<ImportSummary>() {
					  public int compare(ImportSummary a, ImportSummary b) {
					        return a.getRow()- b.getRow();
					    }
					});
			
			   importForm.setSummaryErrorList(errorList);
			   importForm.setSummarySuccessList(successList);
			   
			   importForm.setTotalSize(errorList.size()+successList.size());
			   importForm.setSummaryPhyListErrorSize(errorList!=null?errorList.size():0);
			   importForm.setSummaryPhyListSuccessSize(successList!=null?successList.size():0);
				  
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
			   importForm.setSummaryPhyListSuccessSize(successCount);
			 
			   conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mapping.findForward("success");
	}
	
	public ActionForward importReturnWacoal(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		String errorDesc = null;
		int id = 0;
		int allCount = 0;
		int successCount = 0;
	    int errorCount = 0;
		ImportForm importForm = (ImportForm) form;
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    User user = (User) request.getSession().getAttribute("user");
	    String fileName = "";
	    String fileType ="";
	    ImportDAO importDAO = new ImportDAO();
	    boolean importError = false;
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
	    int failCount = 0;
	    Map<String,ImportSummary> boxNoMapDelete = new HashMap<String,ImportSummary>();
		try {
			
			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());

				StringBuffer sql = new StringBuffer("");

				sql.append(" INSERT INTO PENSBI.PENSBME_RETURN_WACOAL \n");
				sql.append(" (STORE_TYPE, STORE_CODE, IMPORT_DATE, BOX_NO, STATUS, CREATE_DATE, CREATE_USER,  \n");
				sql.append(" MATERIAL_MASTER ,PENS_ITEM, GROUP_ITEM, COLOR_SIZE, RETAIL_PRICE_BF, RETURN_QTY ) \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? ,? , ?, ?, ?, ? , ?) \n");
				
				conn = DBConnection.getInstance().getConnection();
				conn.setAutoCommit(false);
				
				/** cehck FileName duplicate **//*
				boolean dupFile = importDAO.importPhyFileNameIsDuplicate(conn,importForm.getCountDate(),importForm.getCustCode(), fileName);
				Master mm = importDAO.getStoreName(conn,"Store",importForm.getCustCode());
				logger.debug("MM["+mm+"]");
				if(dupFile){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+" วันที่นับสต็อก :"+importForm.getCountDate()+" รหัสร้านค้า:"+mm.getPensDesc()+" ได้เนื่องจากมีการ  Upload ไปแล้ว");
					return mapping.findForward("success");
				}*/
 
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 1; // row of begin data
				int maxColumnNo = 2; // max column of data per row
				
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

				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
	            logger.debug("stroreType:"+importForm.getStoreType());
	            logger.debug("storeCode:"+importForm.getStoreCode());
	            
	            Date importDate = new Date();//Utils.parse( importForm.getCountDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
	            String stroreType = importForm.getStoreType();
	            String storeCode = importForm.getStoreCode();
	            String boxNo = "";
	            String materialmaster  ="";
                String group = "";
                String pensItem = "";
                String colSize = "";
                String qty = "0";
                String retailPriceBF = "0";
                OnhandSummary OHitem = null;
                String status = "A";

				for (int i = rowNo; i <= sheet.getLastRowNum(); i++) {
					errorMsgList = new ArrayList<Message>();
					ImportSummary s = new ImportSummary();
					OHitem = null;
					lineError = false;
					boxNo = "";
		            materialmaster  ="";
		            qty = "0";
		            retailPriceBF = "0";
		            group = "";
	                pensItem = "";
	                colSize = "";
					s.setRow(i);
					
					row = sheet.getRow(i);
					
					/** Check Row is null **/
					Cell cellCheck = row.getCell((short) 0);
					Object cellCheckValue = xslUtils.getCellValue(1, cellCheck);
					String rowCheck = Utils.isNull(cellCheckValue).toString();
					logger.debug("rowCheck["+rowCheck+"]");
					if("".equals(rowCheck) && !rowCheck.startsWith("ME")){
						break;
					}
					
                    try{
						for (int colNo = 0; colNo < maxColumnNo; colNo++) {
							cell = row.getCell((short) colNo);
							logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
							Object cellValue = xslUtils.getCellValue(colNo, cell);
							
							if(colNo==0){
								boxNo = Utils.convertDoubleStrToStr(Utils.isNull(cellValue));
							}
							if(colNo==1){
							   materialmaster = Utils.isNull(cellValue);
							   //ME1210E4NN
							   OHitem = importDAO.getItemByMaterialMaster(conn, materialmaster);
							  // groupFind = materialmaster.substring(0,6);
							   colSize = materialmaster.substring(6,10);

							   group = OHitem != null?OHitem.getGroup():"";
							   pensItem = OHitem != null?OHitem.getPensItem():"";
							}
							if(colNo==2){
								qty = Utils.isNull(cellValue);
							}
						}//for Column
						
						 //Set Display 
		               OnhandSummary oh = new OnhandSummary();
				       oh.setItem(Utils.isNull(pensItem));
				       oh.setOnhandQty(Utils.isNull(qty));
				       oh.setRetailPriceBF(Utils.isNull(retailPriceBF));
				       oh.setMaterialMaster(Utils.isNull(materialmaster));
				       s.setOnhandSummary(oh);
				       
				       logger.debug("OHitem["+OHitem+"]");
				       
					 /** validate **/
			         if(OHitem == null){
			        	 Message m = new Message();
			        	 m.setMessage("ไม่พบข้อมูล ในฐานข้อมูล");
			        	 errorMsgList.add(m);
			        	 lineError = true;
			         }
                     
			         if(lineError==false){
				         /** Validate RetailPriceBF **/
			        	 logger.debug("lineError:"+lineError+"retailPriceBF["+OHitem.getRetailPriceBF()+"]");
			        	 
			        	 retailPriceBF = OHitem.getRetailPriceBF();
				         BigDecimal retailPriceBFOracle = new BigDecimal(OHitem.getRetailPriceBF());
				         logger.debug("retailPriceBFOracle["+retailPriceBFOracle+"]");
				         
				         if(retailPriceBFOracle.compareTo(bigZero) ==0){ // =0
				        	 //Not found Add Fail Msg
				        	 Message m = new Message();
					         m.setMessage("ไม่พบราคาปลีกก่อน VAT");
					         errorMsgList.add(m);
					         lineError = true;
				         }
			         }
			         
			         if(lineError){
			        	 importError = true;
			        	 
			        	 s.setErrorMsgList(errorMsgList);
			        	 errorList.add(s);
			        	
			         }else{
			        	 Message m = new Message();
			        	 m.setMessage("Success");
			        	 errorMsgList.add(m);
			        	 
			        	 s.setErrorMsgList(errorMsgList);
			        	 successList.add(s);
			         }
			         
			         //Line No Error
			         if(lineError==false ){
			        	//STORE_TYPE, STORE_CODE, IMPORT_DATE, BOX_NO, ,STATUS, CREATE_DATE, CREATE_USER
			        	// MATERIAL_MASTER ,PENS_ITEM, GROUP_ITEM,, COLOR_SIZE, RETAIL_PRICE_BF, RETURN_QTY 
					   
			        	ps.setString(1, stroreType);
					    ps.setString(2, storeCode);
						ps.setDate(3, new java.sql.Date(importDate.getTime()));
					    ps.setInt(4, Integer.parseInt(boxNo));
				        ps.setString(5, status);//status 
						ps.setTimestamp(6, new java.sql.Timestamp(new java.util.Date().getTime()));
				        ps.setString(7, user.getUserName());
				        ps.setString(8, materialmaster);
				        ps.setString(9, pensItem);
				        ps.setString(10, group);
				        ps.setString(11, colSize);
				        ps.setBigDecimal(12, new BigDecimal(retailPriceBF));
				        ps.setBigDecimal(13, new BigDecimal(qty));
				        
				        //Add for Delete case Re Import 
				        ImportSummary ss = new ImportSummary();
				        ss.setStoreNo(storeCode);
				        ss.setStoreType(stroreType);
				        ss.setImportDate(importDate);
				        ss.setBoxNo(boxNo);
				        boxNoMapDelete.put(boxNo, ss);
	
				        ps.addBatch();

			         }
				  }catch(Exception e){
				     failCount++;
				     e.printStackTrace();
				     importError=true;
				  }
                   
			      allCount++;
	      
				} //for Row
				
				 if( !boxNoMapDelete.isEmpty()){
		        	 psDelete = conn.prepareStatement("delete from PENSBME_RETURN_WACOAL where store_type= ? and store_code =? and import_date =? and box_no = ? ");
		        	 Iterator<String> it = boxNoMapDelete.keySet().iterator();
		        	 while(it.hasNext()){
		        		 ImportSummary m = boxNoMapDelete.get(it.next());
		        		 logger.debug("delete return_wacoal StoreType:"+m.getStoreType()+"StoreCode:"+m.getStoreNo()+" boxNo:"+m.getBoxNo()+",ImportDate:"+m.getImportDate());
		        		 
		        		 psDelete.setString(1, m.getStoreType());
		        		 psDelete.setString(2, m.getStoreNo());
		        		 psDelete.setDate(3, new java.sql.Date(m.getImportDate().getTime()));
		        		 psDelete.setInt(4, Integer.parseInt(m.getBoxNo()));
		        		 
		        		 psDelete.executeUpdate();
		        	 }
		        	 
		        	 ps.executeBatch();
		         }
				
				 importForm.setSummaryErrorList(errorList);
				 importForm.setSummarySuccessList(successList);
				  
				 importForm.setTotalSize(errorList.size()+successList.size());
				 importForm.setSummaryReturnWacoalListErrorSize(errorList!=null?errorList.size():0);
				 importForm.setSummaryReturnWacoalListSuccessSize(successList!=null?successList.size():0);
			     
				 if(importError){
				    request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
				    conn.rollback();
				    logger.debug("Transaction Rollback");
				}else{
				    request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
				    logger.debug("Transaction Commit");
				    conn.commit();
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mapping.findForward("success");
	}
	
	public ActionForward importFromLotus(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		StringBuffer whereCause = null;
		String errorDesc = null;
		int id = 0;
		int allCount = 0;
		int successCount = 0;
	    int errorCount = 0;
		String forward = "view";
		ImportForm importForm = (ImportForm) form;
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    User user = (User) request.getSession().getAttribute("user");
	    String fileName = "";
	    String fileType = "";
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	 
	    Map<String, ImportSummary> successMap = new HashMap<String, ImportSummary>();
	    Map<String, ImportSummary> errorMap = new HashMap<String, ImportSummary>();
	    boolean importError = false;
		try {

			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				conn = DBConnection.getInstance().getConnection();
				
				/** cehck FileName duplicate **/
				boolean dup = importDAO.importLotusFileNameIsDuplicate(conn, fileName);
				if(dup){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+"ได้เนื่องจากมีการ  Upload ไปแล้ว");
					return mapping.findForward("success");
				}
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());

				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_SALES_FROM_LOTUS( \n");
				sql.append(" VENDOR, NAME, AP_TYPE, LEASE_VENDOR_TYPE,  \n");
				sql.append(" STORE_NO, STORE_NAME, STYLE_NO,  \n");
				sql.append(" DESCRIPTION, COL, SIZE_TYPE,  \n");
				sql.append(" SIZES, SALES_DATE, QTY,  \n");
				sql.append(" GROSS_SALES, RETURN_AMT, NET_SALES_INCL_VAT,  \n");
				sql.append(" VAT_AMT, NET_SALES_EXC_VAT, GP_PERCENT,  \n");
				sql.append(" GP_AMOUNT, VAT_ON_GP_AMOUNT, GP_AMOUNT_INCL_VAT,  \n");
				sql.append(" AP_AMOUNT, TOTAL_VAT_AMT, AP_AMOUNT_INCL_VAT, \n");
				sql.append(" CREATE_DATE, CREATE_USER ,PENS_CUST_CODE, \n");
				sql.append(" PENS_CUST_DESC ,PENS_GROUP ,PENS_GROUP_TYPE, \n");
				sql.append(" SALES_YEAR ,SALES_MONTH ,File_name,PENS_ITEM ,RETAIL_PRICE_BF,TOTAL_WHOLE_PRICE_BF ) \n");
				sql.append(" VALUES( ?,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,?,? ,?,?)");

				conn.setAutoCommit(false);
				
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 4; // row of begin data
				int maxColumnNo = 26; // max column of data per row
				
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
				String salesDate = "";
				String storeNo = "";
				String groupNo = "";
				String storeName = "";
				String description ="";
				String qty = "";
				String styleNo = "";
				String lotusItem = "";
				double netSalesIncVat = 0;
				double apAmount = 0;
				
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
					
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						
						if(colNo==0){
						   //VENDOR
						   String vendor = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(1,  vendor);
						   
						}else if(colNo==1){
						   //NAME
						   ps.setString(2, Utils.isNull(cellValue));
						}else if(colNo==2){
						   //AP_TYPE
						   ps.setString(3, Utils.isNull(cellValue));
						}else if(colNo==3){
						   //LEASE_VENDOR_TYPE
						   ps.setString(4, Utils.isNull(cellValue));
						}else if(colNo==4){
						  //STORE_NO
						   storeNo = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(5, storeNo);
						}else if(colNo==5){
						  //STORE_NAME
						  storeName = Utils.isNull(cellValue);
						  ps.setString(6, storeName);
						}else if(colNo==6){
						  //STYLE_NO
						   styleNo = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(7, styleNo);
						}else if(colNo==7){
						   //DESCRIPTION
						   description = Utils.isNull(cellValue);
						   ps.setString(8, description);
						   groupNo = description.substring(0,6);
						   lotusItem = description.substring(0,10);
						}else if(colNo==8){
						  //COL
						  String value = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						  ps.setString(9, value);
						}else if(colNo==9){
						  //SIZE_TYPE
						  ps.setString(10, Utils.isNull(cellValue));
						}else if(colNo==10){
						  //SIZES
						  String value = Utils.convertDoubleStrToStr(Utils.isNull(cellValue).toString());
						  ps.setString(11,value);
						}else if(colNo==11){
						  //SALES_DATE
						  java.util.Date asOfDate =  (java.util.Date) cellValue;
						  logger.debug("Date:"+asOfDate);
						  salesDate = Utils.stringValue(asOfDate, Utils.DD_MM_YYYY_WITH_SLASH);
						  logger.debug("salesDate:"+salesDate);
						  ps.setTimestamp(12, new java.sql.Timestamp(asOfDate.getTime()));  
						}else if(colNo==12){
						  //QTY
						  qty = ""+Utils.isDoubleNull(cellValue);
						  ps.setDouble(13, Utils.isDoubleNull(cellValue));
						}else if(colNo==13){
						  //GROSS_SALES
						  ps.setDouble(14, Utils.isDoubleNull(cellValue));
						}else if(colNo==14){
						  //RETURN_AMT
						  ps.setDouble(15, Utils.isDoubleNull(cellValue));
						}else if(colNo==15){
						  //NET_SALES_INCL_VAT
						  netSalesIncVat = Utils.isDoubleNull(cellValue);
						  ps.setDouble(16, Utils.isDoubleNull(cellValue));
						}else if(colNo==16){
						  //VAT_AMT
						  ps.setDouble(17, Utils.isDoubleNull(cellValue));
						}else if(colNo==17){
						  //NET_SALES_EXC_VAT
						  ps.setDouble(18,Utils.isDoubleNull(cellValue));
						}else if(colNo==18){
						  //GP_PERCENT
						  ps.setDouble(19,Utils.isDoubleNull(cellValue));
						}else if(colNo==19){
						  //GP_AMOUNT
						  ps.setDouble(20, Utils.isDoubleNull(cellValue));
						}else if(colNo==20){
						  //VAT_ON_GP_AMOUNT
						  ps.setDouble(21, Utils.isDoubleNull(cellValue));
						}else if(colNo==21){
						  //GP_AMOUNT_INCL_VAT
						  ps.setDouble(22, Utils.isDoubleNull(cellValue));
						}else if(colNo==22){
						  //AP_AMOUNT
						  apAmount = Utils.isDoubleNull(cellValue);
						  ps.setDouble(23,apAmount);
						}else if(colNo==23){
						  //TOTAL_VAT_AMT
						  ps.setDouble(24, Utils.isDoubleNull(cellValue));
						}else if(colNo==24){
						  //AP_AMOUNT_INCL_VAT
						  ps.setDouble(25,Utils.isDoubleNull(cellValue));
						}
						 
					}//for column
					
					 //CREATE_DATE
					 ps.setTimestamp(26, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(27, user.getUserName());
			         
			         Master mStore = importDAO.getMasterMapping(conn, "Store", storeNo);
			         Master mGroup = importDAO.getMasterMapping(conn, "Group", groupNo);
			         
			         String pensItem = "";
			         /** case Start with  'W' no check "WB7805D4BL"**/
			         if(lotusItem.startsWith("W")){
			        	  //Add Success Msg No Check PensItem
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName(storeName);
				         s.setDescription(description);
				         s.setQty(qty);
				         s.setMessage("Success :No Validate Pens Item");
				         successMap.put(i+"", s); 
				          
				         if(mStore !=null){
					         // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(28, mStore.getPensValue());
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(29, mStore.getPensDesc());
				         }else if(mStore ==null){
				        	 // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(28, storeNo);
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(29,storeNo); 
				         }
				         if(mGroup !=null){
					          // PENS_GROUP VARCHAR2(30),
					          ps.setString(30, mGroup.getPensValue());
					          // PENS_GROUP_TYPE VARCHAR2(30),
					          ps.setString(31, groupNo);//m.getPensDesc());
					     }else if(mGroup ==null){
						     ps.setString(30, groupNo);//m.getPensDesc());
					         ps.setString(31, groupNo);//m.getPensDesc());
					     } 
				         
			         }else{
				         pensItem = importDAO.getItemByInterfaceValueTypeLotusCase1(conn, Constants.STORE_TYPE_LOTUS_ITEM, lotusItem);
	
				         if(Utils.isNull(pensItem).equals("")){
				        	 lotusItem = lotusItem.substring(0,6);
				        	 pensItem = importDAO.getItemByInterfaceValueTypeLotusCase2(conn, Constants.STORE_TYPE_LOTUS_ITEM, lotusItem);
				         }

				         if(mStore != null && mGroup != null && !Utils.isNull(pensItem).equals("")){
				        	   //Add Success Msg
					         ImportSummary s = new ImportSummary();
					         s.setRow(i+1);
					         s.setSalesDate(salesDate);
					         s.setStoreNo(storeNo);
					         s.setStoreName(storeName);
					         s.setDescription(description);
					         s.setQty(qty);
					         s.setMessage("Success");
					         successMap.put(i+"", s);
				         }
				         
				         if(mStore !=null){
					         // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(28, mStore.getPensValue());
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(29, mStore.getPensDesc());
				         }else if(mStore ==null){
				        	 // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(28, storeNo);
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(29,storeNo); 
				         }
				         if(mGroup !=null){
					          // PENS_GROUP VARCHAR2(30),
					          ps.setString(30, mGroup.getPensValue());
					          // PENS_GROUP_TYPE VARCHAR2(30),
					          ps.setString(31, groupNo);//m.getPensDesc());
					     }else if(mGroup ==null){
						     ps.setString(30, groupNo);//m.getPensDesc());
					         ps.setString(31, groupNo);//m.getPensDesc());
					     } 
				         
				         if(mStore ==null || mGroup==null || Utils.isNull(pensItem).equals("")){
				        	 //Add Error Msg
					         importError = true;
					         ImportSummary s = new ImportSummary();
					         s.setRow(i+1);
					         s.setSalesDate(salesDate);
					         s.setStoreNo(storeNo);
					         s.setStoreName(storeName);
					         s.setDescription(description);
					         s.setQty(qty);
					         
					         String ms = "";
					         if(mStore ==null){
					        	 ms +="ไม่พบข้อมูล STORE NAME";
					         }
					         if(mGroup==null){
					        	 ms +="ไม่พบข้อมูล Group";
					         }
					         if(Utils.isNull(pensItem).equals("")){
					        	 ms +="ไม่พบข้อมูล Pens Item"; 
					         }
					         s.setMessage(ms);
					         errorMap.put(i+"", s);
				         }  
			         }
			         
			         String year = "";
			         String month ="";
			         
			         // dd/mm/yyyy
			         year = salesDate.substring(6,10);
			         month = salesDate.substring(3,5);
			         
			         // SALES_YEAR VARCHAR2(10),
			         ps.setString(32, year);
			         //SALES_MONTH VARCHAR2(10),
			         ps.setString(33, month);
			         // File_name VARCHAR2(100),
			         ps.setString(34, fileName);
			         //PENS_ITEM
			         ps.setString(35, pensItem);
			         
			         //Case LOTUS
			         //RETAIL_PRICE_BF 	= NET SALES INC. VAT (P) ขายปลีกสุทธิ รวม vat
			         //TOTAL_WHOLE_PRICE_BF 	= A/P AMOUNT (W) ขายส่ง
			         
			         ps.setDouble(36, netSalesIncVat);
			         ps.setDouble(37, apAmount);
			         
					 ps.executeUpdate();
				}//for Row
			}
			
			if(importError){
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
			   
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
					  
				  
				  importForm.setSummaryErrorList(errorList);
				  importForm.setSummarySuccessList(successList);
				  
				  importForm.setTotalSize(errorList.size()+successList.size());
				  importForm.setSummaryLotusErrorSize(errorList!=null?errorList.size():0);
				  importForm.setSummaryLotusSuccessSize(successList!=null?successList.size():0);
				  
			      conn.rollback();
			}else{
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
				
			   importForm.setSummaryErrorList(errorList);
			   importForm.setSummarySuccessList(successList);
			   
			   importForm.setTotalSize(errorList.size()+successList.size());
			   importForm.setSummaryLotusErrorSize(errorList!=null?errorList.size():0);
			   importForm.setSummaryLotusSuccessSize(successList!=null?successList.size():0);
				  
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
			   conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mapping.findForward("success");
	}
	
	public ActionForward importFromTops(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		StringBuffer whereCause = null;
		String errorDesc = null;
		int id = 0;
		int allCount = 0;
		int successCount = 0;
	    int errorCount = 0;
		String forward = "view";
		ImportForm importForm = (ImportForm) form;
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    User user = (User) request.getSession().getAttribute("user");
	    String fileName = "";
	    String fileType = "";
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	 
	    Map<String, ImportSummary> successMap = new HashMap<String, ImportSummary>();
	    Map<String, ImportSummary> errorMap = new HashMap<String, ImportSummary>();
	    boolean importError = false;
		try {

			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				conn = DBConnection.getInstance().getConnection();
				
				/** cehck FileName duplicate **/
				boolean dup = importDAO.importTopsFileNameIsDuplicate(conn, fileName);
				if(dup){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+"ได้เนื่องจากมีการ  Upload ไปแล้ว");
					return mapping.findForward("success");
				}
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());

				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_SALES_FROM_TOPS( \n");
				
				sql.append(" SALES_DATE, STORE_NO, STORE_NAME, SUPPLIER,  \n");//1
				sql.append(" SUPPLIER_NAME, ITEM, ITEM_DESC, BRANCH_NAME,  \n");//2
				sql.append(" GROUP_NO, GROUP_NAME, DEPT, DEPT_NAME, \n");//3
				sql.append(" UNIT_COST, RETAIL_PRICE ,GP_PERCENT, QTY, NET_SALES_INCL_VAT,  \n");//4
				sql.append(" NET_SALES_EXCL_VAT, GP_AMOUNT, GROSS_SALES, DISCOUNT, \n");//5
				sql.append(" CUS_RETURN, DISCOUNT_CUS_RETURN, NET_CUS_RETURN, COGS,  \n");//6 25
				sql.append(" CREATE_DATE, CREATE_USER ,PENS_CUST_CODE,PENS_CUST_DESC , \n");//7
				sql.append(" PENS_GROUP ,PENS_GROUP_TYPE,SALES_YEAR ,SALES_MONTH , \n");//8
				sql.append(" File_name,PENS_ITEM ,RETAIL_PRICE_BF,TOTAL_WHOLE_PRICE_BF ) \n");//9  36
				
				sql.append(" VALUES(?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?)");

				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 1; // row of begin data
				int maxColumnNo = 24; // max column of data per row
				
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
				String salesDate = "";
				String storeNo = "";
				String storeName = "";
				String qty = "";
				String item = "";
				double netSalesIncVat = 0;
				double retailPrice = 0;
				Master mStore = null;
	
				int index = 0;
	            int no = 0;
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
				for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
					row = sheet.getRow(i);
					
					/** Check Row is null **/
					Cell cellCheck = row.getCell((short) 0);
					Object cellCheckValue = xslUtils.getCellValue(0, cellCheck);
					
					//java.util.Date rowCheck =  (java.util.Date) cellCheckValue;
					logger.debug("cellCheckValue["+cellCheckValue+"]");
					
					if(cellCheckValue == null ){
						break;
					}
					
					//initial
					mStore = null;
					index = 1;
					salesDate = "";
					storeNo = "";
					storeName = "";
					qty = "";
					item = "";
					netSalesIncVat = 0;
					retailPrice = 0;
					
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						
						//sold_date	,store_name, supplier,	supplier_name,	item,	item_desc,	brand_name  //1-7
						//28-Aug-14	LST SALAYA_139	9810351	เพนส์มาร์เก็ตติ้งแอนด์ดิสทริบิวชั่น บจ.	40255095	บีมีเสื้อชั้นในรุ่น ME1039(C2	B ME/ บีมี
						if(colNo==0){
							//SALES_DATE =28-Aug-14	
							cellValue  = cell.getDateCellValue();
							java.util.Date asOfDate =  (java.util.Date) cellValue;
							//logger.debug("Date:"+asOfDate);
							salesDate = Utils.stringValue(asOfDate, Utils.DD_MM_YYYY_WITH_SLASH);
							logger.debug("salesDate:"+salesDate);
							
							logger.debug("1index:"+index);
							ps.setTimestamp(index++, new java.sql.Timestamp(asOfDate.getTime()));  
							
						}else if(colNo==1){
						  //store_no,store_name
						   storeName = Utils.isNull(cellValue);
						   storeNo = Utils.isNull(cellValue);
						   mStore = importDAO.getStoreTops(conn, Utils.isNull(cellValue));
						   if(mStore != null){
						      storeNo = mStore.getPensValue();
						   } 
						   ps.setString(index++, storeNo);//storeNo
						   ps.setString(index++, Utils.isNull(cellValue));//store_name
						   
						}else if(colNo==2){
						   //supplier
						   ps.setString(index++, Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue)));
						   
						}else if(colNo==3){
						   //supplier_name
						   ps.setString(index++, Utils.isNull(cellValue));
						   
						}else if(colNo==4){
						  //item 
						   item = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(index++, item);
						   
						}else if(colNo==5){
						  //item_desc
						  ps.setString(index++, Utils.isNull(cellValue));
						  
						}else if(colNo==6){
						  //brand_name
						   ps.setString(index++, Utils.isNull(cellValue));
						   
					 /** group_no	,group_name	,dept	,dept_name //8-11  **/
						}else if(colNo==7){
						   //group_no
						  ps.setString(index++, Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue)));
						}else if(colNo==8){
						  //group_name
						  ps.setString(index++, Utils.isNull(cellValue));
						  
						}else if(colNo==9){
						  //dept
						  ps.setString(index++, Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue)));
						  
						}else if(colNo==10){
						  //dept_name
						  ps.setString(index++,Utils.isNull(cellValue));
						 
					/**unit_cost	,retail_price	.GP %	.sold_qty	//12-15  */
						
						}else if(colNo==11){
						  //unit_cost
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==12){
						  //retail_price
						  retailPrice = Utils.isDoubleNull(cellValue);
						  ps.setDouble(index++, retailPrice);
						  
						}else if(colNo==13){
						  //GP %
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==14){
						  //sold_qty
						  qty = ""+Utils.isDoubleNull(cellValue);
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						  
						/** net_sales_incl_vat,net_sales_excl_vat	,gp_amount	,gross_sales //16-19 **/
							
						}else if(colNo==15){
						  //net_sales_incl_vat
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==16){
						  //NET_SALES_EXC_VAT
					      netSalesIncVat = Utils.isDoubleNull(cellValue);
						  ps.setDouble(index++,Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==17){
						  //GP_AMOUNT
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==18){
						  //gross_sales
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						  
					  /** discount	,cus_return	,discount_cus_return,net_cus_return	,cogs //20-24   **/
						}else if(colNo==19){
						  //discount
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==20){
						  //cus_return
						  ps.setDouble(index++,Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==21){
						  //discount_cus_return
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==22){
						  //net_cus_return
						  ps.setDouble(index++,Utils.isDoubleNull(cellValue));
						  
					   }else if(colNo==23){
						  //cogs
						  ps.setDouble(index++,Utils.isDoubleNull(cellValue));
						}
						 
					}//for column
					
				    logger.debug("index:"+index);
					   
					 //CREATE_DATE
					 ps.setTimestamp(index++, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(index++, user.getUserName());
			         
			         //Find pens_item,groupType
			         Master mGroup = importDAO.getGroupTops(conn, item);
			         
			         String pensItem = "";
			         /** case Start with  'W' no check "WB7805D4BL"**/
			         if(item.startsWith("W")){
			        	  //Add Success Msg No Check PensItem
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName(storeName);
				         s.setDescription(storeNo);
				         s.setQty(qty);
				         s.setMessage("Success :No Validate Pens Item");
				         successMap.put(i+"", s); 
				          
				         if(mStore !=null){
					         // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(index++, mStore.getPensValue());
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(index++, mStore.getPensDesc());
				         }else if(mStore ==null){
				        	 // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(index++, storeNo);
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(index++,storeNo); 
				         }
				         if(mGroup !=null){
					          // PENS_GROUP VARCHAR2(30),
					          ps.setString(index++, mGroup.getPensDesc3()); //BRA ,UNDERWARE
					          // PENS_GROUP_TYPE VARCHAR2(30),
					          ps.setString(index++, mGroup.getPensDesc2());//ME1000
					     }else if(mGroup ==null){
						     ps.setString(index++, item);//m.getPensDesc());
					         ps.setString(index++, item);//m.getPensDesc());
					     } 
				         
			         }else{
			        	 if(mGroup != null){
				            pensItem = mGroup.getPensValue();
			        	 }else{
			        		 pensItem = ""; 
			        	 }
			        	 
				         if(mStore != null && mGroup != null && !Utils.isNull(pensItem).equals("")){
				        	   //Add Success Msg
					         ImportSummary s = new ImportSummary();
					         s.setRow(i+1);
					         s.setSalesDate(salesDate);
					         s.setStoreNo(storeNo);
					         s.setStoreName(storeName);
					         s.setDescription(storeNo);
					         s.setQty(qty);
					         s.setMessage("Success");
					         successMap.put(i+"", s);
				         }
				         
				         if(mStore !=null){
					         // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(index++, mStore.getPensValue());
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(index++, mStore.getPensDesc());
				         }else if(mStore ==null){
				        	 // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(index++, storeNo);
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(index++,storeNo); 
				         }
				         if(mGroup !=null){
					          // PENS_GROUP VARCHAR2(30),
					          ps.setString(index++, mGroup.getPensDesc3());//BRA ,UNDERWARE
					          // PENS_GROUP_TYPE VARCHAR2(30),
					          ps.setString(index++, mGroup.getPensDesc2());//ME1000
					     }else if(mGroup ==null){
						     ps.setString(index++, item);//fine form item(E)
					         ps.setString(index++, item);//fine form item(E)
					     } 
				         
				         if(mStore ==null || mGroup==null || Utils.isNull(pensItem).equals("")){
				        	 //Add Error Msg
					         importError = true;
					         ImportSummary s = new ImportSummary();
					         s.setRow(i+1);
					         s.setSalesDate(salesDate);
					         s.setStoreNo(storeNo);
					         s.setStoreName(storeName);
					         s.setDescription(storeNo);
					         s.setQty(qty);
					         
					         String ms = "";
					         if(mStore ==null){
					        	 ms +="ไม่พบข้อมูล STORE NAME";
					         }
					         if(mGroup==null){
					        	 ms +="ไม่พบข้อมูล Group";
					         }
					         if(Utils.isNull(pensItem).equals("")){
					        	 ms +="ไม่พบข้อมูล Pens Item"; 
					         }
					         s.setMessage(ms);
					         errorMap.put(i+"", s);
				         }  
			         }
			         
			         String year = "";
			         String month ="";
			         
			         // dd/mm/yyyy
			         year = salesDate.substring(6,10);
			         month = salesDate.substring(3,5);
			         
			         // SALES_YEAR VARCHAR2(10),
			         ps.setString(index++, year);
			         //SALES_MONTH VARCHAR2(10),
			         ps.setString(index++, month);
			         // File_name VARCHAR2(100),
			         ps.setString(index++, fileName);
			         //PENS_ITEM
			         ps.setString(index++, pensItem);
			         
			         //Case LOTUS
			         //RETAIL_PRICE_BF 	= NET SALES INC. VAT (P) ขายปลีกสุทธิ รวม vat
			         //TOTAL_WHOLE_PRICE_BF 	= A/P AMOUNT (W) ขายส่ง
			      
			         ps.setDouble(index++, retailPrice);//RETAIL_PRICE_BF
			         ps.setDouble(index++, netSalesIncVat);//TOTAL_WHOLE_PRICE_BF
			         
			         logger.debug("index:"+index);
			         
					 ps.executeUpdate();
				}//for Row
			}
			
			if(importError){
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
			   
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
					  
				  
				  importForm.setSummaryErrorList(errorList);
				  importForm.setSummarySuccessList(successList);
				  
				  importForm.setTotalSize(errorList.size()+successList.size());
				  importForm.setSummaryLotusErrorSize(errorList!=null?errorList.size():0);
				  importForm.setSummaryLotusSuccessSize(successList!=null?successList.size():0);
				  
			      conn.rollback();
			}else{
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
				
			   importForm.setSummaryErrorList(errorList);
			   importForm.setSummarySuccessList(successList);
			   
			   importForm.setTotalSize(errorList.size()+successList.size());
			   importForm.setSummaryLotusErrorSize(errorList!=null?errorList.size():0);
			   importForm.setSummaryLotusSuccessSize(successList!=null?successList.size():0);
				  
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
			   conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mapping.findForward("success");
	}
	
	public ActionForward importFromKing(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		StringBuffer whereCause = null;
		String errorDesc = null;
		int id = 0;
		int allCount = 0;
		int successCount = 0;
	    int errorCount = 0;
		String forward = "view";
		ImportForm importForm = (ImportForm) form;
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    User user = (User) request.getSession().getAttribute("user");
	    String fileName = "";
	    String fileType = "";
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	 
	    Map<String, ImportSummary> successMap = new HashMap<String, ImportSummary>();
	    Map<String, ImportSummary> errorMap = new HashMap<String, ImportSummary>();
	    boolean importError = false;
		try {

			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				conn = DBConnection.getInstance().getConnection();
				
				/** cehck FileName duplicate **/
				boolean dup = importDAO.importKingFileNameIsDuplicate(conn, fileName);
				if(dup){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+"ได้เนื่องจากมีการ  Upload ไปแล้ว");
					return mapping.findForward("success");
				}
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());

				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_SALES_FROM_KING( \n");
				
				sql.append(" Code, Description, Reference, Unit_Price,  \n");//1-4
				sql.append(" Unit_cost, Qty, Amount, Cost_amt, \n");//5-8
				sql.append(" SALE_DATE, CUST_GROUP, CUST_NO, File_name,  \n");//
				sql.append(" GROUP_CODE,PENS_ITEM,CREATE_DATE,CREATE_USER  ) \n");//
				
				sql.append(" VALUES(?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?)");

				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 10; // row of begin data
				int maxColumnNo = 8; // max column of data per row
				
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
				String salesDate = importForm.getImportDate();
				String storeGroup = importForm.getStoreCode().substring(0,importForm.getStoreCode().indexOf("-"));//"020056";
				String storeNo = importForm.getStoreCode();//"020056-1";
				//String storeName = "020056-1";
				String qty = "";
				String groupCode ="";
				String pensItem = "";
				String desc = "";
	
				int index = 0;
	            int no = 0;
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
				for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
					row = sheet.getRow(i);
					
					/** Check Row is null **/
					Cell cellCheck = row.getCell((short) 0);
					Object cellCheckValue = xslUtils.getCellValue(0, cellCheck);
					logger.debug("cellCheckValue["+cellCheckValue+"]");
					
					String rowCheck =  cellCheckValue.toString();
					logger.debug("rowCheck["+rowCheck+"]");
					
					
					if(cellCheckValue == null  || Utils.isNumeric(rowCheck)==false ){
						break;
					}
					
					//initial
					index = 1;
					qty = "";
					groupCode = "";
					pensItem = "";
					desc = "";
					
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						///Reference, Unit_Price
						//code
						if(colNo==0){
							ps.setString(index++, Utils.isNull(cellValue));
						}else if(colNo==1){
						   //Desc
							desc = Utils.isNull(cellValue);
						    ps.setString(index++, desc);
						   
						}else if(colNo==2){
						   //Reference
						   groupCode = Utils.isNull(cellValue);
						   ps.setString(index++, groupCode);
						   
						}else if(colNo==3){
						  //Unit_Price
						  ps.setDouble(index++,Utils.isDoubleNull(cellValue));
							    
						  //Unit_cost, Qty, Amount, Cost_amt, \
						}else if(colNo==4){
						  //Unit_cost 
						   ps.setDouble(index++,Utils.isDoubleNull(cellValue));
						   
						}else if(colNo==5){
						  //qty
						   qty = Utils.isDoubleNull(cellValue)+"";
						   ps.setDouble(index++,Utils.isDoubleNull(cellValue));
						  
						}else if(colNo==6){
						  //amount
						   ps.setDouble(index++,Utils.isDoubleNull(cellValue));
						   
						}else if(colNo==7){
						   //cost_amt
						  ps.setDouble(index++, Utils.isDoubleNull(cellValue));
						}
						 
					}//for column
					
				    logger.debug("index:"+index);

			         //Find pens_item,groupType
				     pensItem = GeneralDAO.searchPensItemByGroupCode(conn,groupCode);
			         
			         /** case Start with  'W' no check "WB7805D4BL"**/
			         if(pensItem.startsWith("W")){
			        	  //Add Success Msg No Check PensItem
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setPensItem(pensItem);
				         s.setDescription(desc);
				         s.setGroupCode(groupCode);
				         s.setQty(qty);
				         s.setMessage("Success :No Validate Pens Item");
				         successMap.put(i+"", s); 
				          
			         }else{
				         if( !Utils.isNull(pensItem).equals("")){
				        	   //Add Success Msg
					         ImportSummary s = new ImportSummary();
					         s.setRow(i+1);
					         s.setSalesDate(salesDate);
					         s.setStoreNo(storeNo);
					         s.setPensItem(pensItem);
					         s.setDescription(desc);
					         s.setGroupCode(groupCode);
					         s.setQty(qty);
					         s.setMessage("Success");
					         successMap.put(i+"", s);
				         }
				         
				         if(Utils.isNull(pensItem).equals("")){
				        	 //Add Error Msg
					         importError = true;
					         ImportSummary s = new ImportSummary();
					         s.setRow(i+1);
					         s.setSalesDate(salesDate);
					         s.setPensItem(pensItem);
					         s.setDescription(desc);
					         s.setGroupCode(groupCode);
					         s.setQty(qty);
					         
					         String ms = "";
					         if(Utils.isNull(pensItem).equals("")){
					        	 ms +="ไม่พบข้อมูล Pens Item"; 
					         }
					         s.setMessage(ms);
					         errorMap.put(i+"", s);
				         }  
			         }

			         //SALES_DATE
			         ps.setDate(index++, new java.sql.Date(Utils.parse(salesDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			         //CUST_GROUP
			         ps.setString(index++, storeGroup);
			         //CUST_NO
			         ps.setString(index++, storeNo);
			         
			         // File_name VARCHAR2(100),
			         ps.setString(index++, fileName);
			         
			         ps.setString(index++, groupCode);
			         //PENS_ITEM
			         ps.setString(index++, pensItem);
			         
			        //CREATE_DATE
					 ps.setTimestamp(index++, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(index++, user.getUserName());
			         
			         logger.debug("index:"+index);
			         
					 ps.executeUpdate();
				}//for Row
			}
			
			if(importError){
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
			   
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
					  
				  
				  importForm.setSummaryErrorList(errorList);
				  importForm.setSummarySuccessList(successList);
				  
				  importForm.setTotalSize(errorList.size()+successList.size());
				  importForm.setSummaryKingErrorSize(errorList!=null?errorList.size():0);
				  importForm.setSummaryKingSuccessSize(successList!=null?successList.size():0);
				  
			      conn.rollback();
			}else{
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
				
			   importForm.setSummaryErrorList(errorList);
			   importForm.setSummarySuccessList(successList);
			   
			   importForm.setTotalSize(errorList.size()+successList.size());
			   importForm.setSummaryKingErrorSize(errorList!=null?errorList.size():0);
			   importForm.setSummaryKingSuccessSize(successList!=null?successList.size():0);
				  
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
			   conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mapping.findForward("success");
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward importFromBigc(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		StringBuffer whereCause = null;
		String errorDesc = null;
		int id = 0;
		int allCount = 0;
		int successCount = 0;
	    int errorCount = 0;
		String forward = "view";
		ImportForm importForm = (ImportForm) form;
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    User user = (User) request.getSession().getAttribute("user");
	    String fileName = "";
	    String fileType = "";
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	 
	    Map<String, ImportSummary> successMap = new HashMap<String, ImportSummary>();
	    Map<String, ImportSummary> errorMap = new HashMap<String, ImportSummary>();
	    boolean importError = false;
		try {
			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				conn = DBConnection.getInstance().getConnection();
				
				/** cehck FileName duplicate **/
				boolean dup = importDAO.importLotusFileNameIsDuplicate(conn, fileName);
				if(dup){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+"ได้เนื่องจากมีการ  Upload ไปแล้ว");
					return mapping.findForward("success");
				}
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());

				//รหัสร้านค้า	ชื่อร้านค้า	บาร์สินค้า	รหัสสินค้า	รายละเอียดสินค้า	รหัสสาขา	ชื่อสาขา	วันที่ขายสินค้า	 GP %	จำนวนชิ้น	ราคาทุน	ปลีก (invat) 
				///7100540	เพนส์ มาร์เก็ตติ้ง แอนด์ ดิสทริบิวช	2000003741625    	102126441	ชุดชั้นในเต็มตัว	107	NP	8/4/2013	22	1	284.3	390
        
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_SALES_FROM_BIGC( \n");
				sql.append(" VENDOR, NAME, BARCODE,  \n");
				sql.append(" STYLE_NO, DESCRIPTION, STORE_NO,  \n");
				sql.append(" STORE_NAME, SALES_DATE, GP_PERCENT,   \n");
				sql.append(" QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,CREATE_DATE, CREATE_USER,  \n");
				sql.append(" PENS_CUST_CODE, PENS_CUST_DESC,PENS_ITEM,    \n");
				sql.append(" SALES_YEAR ,SALES_MONTH,PENS_GROUP,PENS_GROUP_TYPE ,  \n");
				sql.append(" FILE_NAME,TOTAL_WHOLE_PRICE_BF)  \n");
				
				sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?) \n");

				conn.setAutoCommit(false);
				
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 1; // row of begin data
				int maxColumnNo = 12; // max column of data per row
				
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
				String itemBigC = "";
				String itemDescBigC = "";
				String salesDate = "";
				String storeNo = "";
				String storeName = "";
				String description ="";
				BigDecimal qty = new BigDecimal("0");;
				String barcode = "";
				BigDecimal wholePriceBF = new BigDecimal("0");
				String pensGroup = "";
				String pensGroupType = "";
				
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
					
					//รหัสร้านค้า	ชื่อร้านค้า	บาร์สินค้า	รหัสสินค้า	รายละเอียดสินค้า	รหัสสาขา	ชื่อสาขา	วันที่ขายสินค้า	 GP %	จำนวนชิ้น	ราคาทุน	ปลีก (invat) 
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						//logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						
						if(colNo==0){
						   //VENDOR
						   String vendor = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(1,  vendor);
						   
						}else if(colNo==1){
						   //NAME
						   ps.setString(2, Utils.isNull(cellValue));
						}else if(colNo==2){
						   //BARCODE
						   barcode = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(3,barcode);
						}else if(colNo==3){
						   //ITEM
						   itemBigC = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(4, itemBigC);
						}else if(colNo==4){
						  //ITEM_DESC
						   itemDescBigC = Utils.isNull(cellValue);
						   ps.setString(5, itemDescBigC);
						}else if(colNo==5){
						  //StoreNo
						  storeNo = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						  ps.setString(6, storeNo);
						}else if(colNo==6){
						  //StoreName
						   //ps.setString(7, Utils.isNull(cellValue));
						}else if(colNo==7){
						  //SALES_DATE
						  java.util.Date asOfDate =  (java.util.Date) cellValue;
						  logger.debug("Date:"+asOfDate);
						  salesDate = Utils.stringValue(asOfDate, Utils.DD_MM_YYYY_WITH_SLASH);
						  logger.debug("salesDate:"+salesDate);
						  ps.setDate(8, new java.sql.Date(asOfDate.getTime()));  
						}else if(colNo==8){
						  //GP %
						  ps.setDouble(9, Utils.isDoubleNull(cellValue));
						}else if(colNo==9){
						  //QTY
						  qty = new BigDecimal(Utils.isDoubleNull(cellValue));
						  ps.setBigDecimal(10, qty);
						}else if(colNo==10){
						  //WHOLE_PRICE_AMT
						  wholePriceBF = new BigDecimal(Utils.isDoubleNull(cellValue));
						  ps.setBigDecimal(11, wholePriceBF);
						}else if(colNo==11){
						  //Retail_Price_BF
						  ps.setDouble(12, Utils.isDoubleNull(cellValue));
						}
						 
					}//for column
					
					 //CREATE_DATE
					 ps.setTimestamp(13, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(14, user.getUserName());
			         
			         Master mStore = importDAO.getMasterMapping(conn, "Store", storeNo);
			         String pensItem = importDAO.getItemByBarcodeTypeBigC(conn,"BigCitem", barcode);
			        
			         if(mStore != null && !Utils.isNull(pensItem).equals("")){
			        	 //Set StoreName
			        	 ps.setString(7, Utils.isNull(mStore.getInterfaceDesc()));
			        	 
			        	   //Add Success Msg
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName(mStore.getInterfaceDesc());
				         s.setDescription("bar["+barcode+"]-item["+itemBigC+"]-"+itemDescBigC);
				         s.setQty(qty.toString());
				         s.setMessage("Success");
				         successMap.put(i+"", s);
			         }
			         
			         if(mStore !=null){
				         // PENS_CUST_CODE VARCHAR2(30),
				         ps.setString(15, mStore.getPensValue());
				         // PENS_CUST_DESC VARCHAR2(100),O
				         ps.setString(16, mStore.getPensDesc());
			         }else if(mStore ==null){
			        	 // PENS_CUST_CODE VARCHAR2(30),
				         ps.setString(15, storeNo);
				         // PENS_CUST_DESC VARCHAR2(100),O
				         ps.setString(16,storeNo); 
			         }
			         
			         //Pens_item
			         ps.setString(17, Utils.isNull(pensItem));
  
			         if(mStore ==null  && Utils.isNull(pensItem).equals("")){
				         //Add Error Msg
				         importError = true;
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName(storeNo);
				         s.setDescription("bar["+barcode+"]-item["+itemBigC+"]-"+itemDescBigC);
				         s.setQty(qty.toString());
				         s.setMessage("ไม่พบข้อมูล Store Name ,ไม่พบข้อมูลรหัสสินค้า");
				         errorMap.put(i+"", s);
			         }
			      
			         if(mStore ==null  && !Utils.isNull(pensItem).equals("")){
				         //Add Error Msg
				         importError = true;
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName(storeNo);
				         s.setDescription("bar["+barcode+"]-item["+itemBigC+"]-"+itemDescBigC);
				         s.setQty(qty.toString());
				         s.setMessage("ไม่พบข้อมูล Store Name ");
				         errorMap.put(i+"", s);
			         }
			         if(mStore !=null  && Utils.isNull(pensItem).equals("")){
				         //Add Error Msg
				         importError = true;
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName(mStore.getPensDesc());
				         s.setDescription("bar["+barcode+"]-item["+itemBigC+"]-"+itemDescBigC);
				         s.setQty(qty.toString());
				         s.setMessage("ไม่พบข้อมูล รหัสสินค้า ");
				         errorMap.put(i+"", s);
			         }
			         
			         String year = "";
			         String month ="";
			         
			         // dd/mm/yyyy
			         year = salesDate.substring(6,10);
			         month = salesDate.substring(3,5);
			         
			         // SALES_YEAR VARCHAR2(10),
			         ps.setString(18, year);
			         //SALES_MONTH VARCHAR2(10),
			         ps.setString(19, month);
			         
			         //PENS_GROUP
			         Master mGroupType = importDAO.getMasterByBarcodeTypeBigC(conn, barcode);
			         logger.debug("mGroupType:"+mGroupType);
			         Master mGroup = mGroupType!=null? importDAO.getMasterMapping(conn, "Group", Utils.isNull(mGroupType.getPensDesc2())) :null;
			 
			         ps.setString(20, mGroup!=null?Utils.isNull(mGroup.getPensValue()):"");
			         //PENS_GROUP_TYPE
			         ps.setString(21, mGroupType!=null?Utils.isNull(mGroupType.getPensDesc2()):"");
			         
			        // File_name VARCHAR2(100),
			         ps.setString(22, fileName);
			         
			         //TOTAL_WHOLE_PRICE_BF =QTY * WHOLE_PRICE_BF
			         ps.setBigDecimal(23, qty.multiply(wholePriceBF));
			         
					 ps.executeUpdate();
					 
				}//for Row
			}
			
			if(importError){
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
			   
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
				  
				  /** Error List **/
				  it = errorMap.keySet().iterator();
					 while(it.hasNext()){
						  String key = (String)it.next();
						  //logger.debug("key:"+key);
						  ImportSummary mm = (ImportSummary)errorMap.get(key);
						  errorList.add(mm);
					  }
					 
					 //Sort
					  Collections.sort(errorList, new Comparator<ImportSummary>() {
						  public int compare(ImportSummary a, ImportSummary b) {
						        return a.getRow()- b.getRow();
						    }
						});
					  
				  
				  importForm.setSummaryErrorList(errorList);
				  importForm.setSummarySuccessList(successList);
				  
				  importForm.setTotalSize(errorList.size()+successList.size());
				  importForm.setSummaryBigCErrorSize(errorList!=null?errorList.size():0);
				  importForm.setSummaryBigCSuccessSize(successList!=null?successList.size():0);
				  
			      conn.rollback();
			}else{
				 /** Success List **/
				 Iterator it = successMap.keySet().iterator();
				 while(it.hasNext()){
					  String key = (String)it.next();
					  logger.debug("key:"+key);
					  ImportSummary mm = (ImportSummary)successMap.get(key);
					  successList.add(mm);
				  }
				 
				 //Sort
				  Collections.sort(successList, new Comparator<ImportSummary>() {
					  public int compare(ImportSummary a, ImportSummary b) {
					        return a.getRow()- b.getRow();
					    }
					});
				
			   importForm.setSummaryErrorList(errorList);
			   importForm.setSummarySuccessList(successList);
			   
			   importForm.setTotalSize(errorList.size()+successList.size());
			   importForm.setSummaryBigCErrorSize(errorList!=null?errorList.size():0);
			   importForm.setSummaryBigCSuccessSize(successList!=null?successList.size():0);
				  
			   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
			   conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mapping.findForward("success");
	}
	
	public ActionForward importFromWacoal(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Import :Excel");
		ImportForm importForm = (ImportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
		try {
			
			String noCheckError = Utils.isNull(request.getParameter("NO_CHECK_ERROR"));
			 
			conn = DBConnection.getInstance().getConnection();
			//conn = new DBCPConnectionProvider().getConnection(conn);
			
            conn.setAutoCommit(false);
			FormFile dataFile = importForm.getDataFile();
			
			/** Step 1 Validate Name Date of File ,Must more than old import  **/
			String fileName = dataFile.getFileName();
			java.util.Date fileNameAsOfDate = Utils.parse(fileName.substring(9,17),Utils.YYYY_MM_DD_WITHOUT_SLASH);
			// Get LastFileNameImport 
			String lastFileNameImport = importDAO.getLastFileNameImport(conn);
			if( !Utils.isNull(lastFileNameImport).equals("")){
				java.util.Date lastFileNameAsOfDate = Utils.parse(lastFileNameImport.substring(9,17),Utils.YYYY_MM_DD_WITHOUT_SLASH);
				
				if(fileNameAsOfDate.before(lastFileNameAsOfDate)){ //dateImport < lastDateImport
					request.setAttribute("Message","ชื่อไฟล์ที่  Upload ["+fileName+"] วันที่น้อยกว่า  ชื่อไฟล์วันที่ล่าสุดที่  Upload ["+lastFileNameImport+"] ");
					return mapping.findForward("success");
				}
			}
			
			if (dataFile != null) {
			  
			  logger.debug("contentType: " + dataFile.getContentType());
			  logger.debug("fileName: " + dataFile.getFileName());

			  
			  /** Delete All before Import **/
			  psDelete = conn.prepareStatement("delete from PENSBME_ONHAND_BME");
			  psDelete.executeUpdate();
	
			  StringBuffer sql = new StringBuffer("");
			  sql.append("INSERT INTO PENSBME_ONHAND_BME( \n");
			  sql.append(" AS_OF_DATE, MATERIAL_MASTER, BARCODE, \n");
			  sql.append(" ONHAND_QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,  \n");
			  sql.append(" ITEM, ITEM_DESC, CREATE_DATE, CREATE_USER,FILE_NAME,GROUP_ITEM,STATUS,MESSAGE,PENS_ITEM) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?,?,?,?)");
			  
			  ps = conn.prepareStatement(sql.toString());
			  
			  String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "tis-620");
			  //replace '?' in index =0
			 // dataFileStr = dataFileStr.substring(1,dataFileStr.length());
			  logger.debug("dataFileStr:"+dataFileStr);
			  
	    	  String[] dataStrArray = dataFileStr.split("\n");

		      int start = 0;
		      int end = 0;
		      String lines = "";
		      String materialMaster = "";
		      String barcode = "";
		      String onhandQty = "";
		      String onhandQty2Digit = "";
		      String wholePriceBF = "";
		      String wholePriceBF2Digit = "";
		      String retailPriceBF = "";
		      String retailPriceBF2Digit = "";
		      String item  ="";
		      String itemDesc= "";
		      String groupItem = "";
		      String status = "";
		      String message ="";
		      String pensItem = "";
		      
		     for(int i=0;i<dataStrArray.length;i++){
		    	 lineError = false;
		    	 errorMsgList = new ArrayList<Message>();
		    	 lines = dataStrArray[i];
		    	 logger.debug("lines:"+lines);
		         if(!Utils.isNull(lines).equals("")){
		        	try{
				         // 1
			        	 start = 0;
				         end = 18;
				         materialMaster = lines.substring(start,end);
				         //logger.debug("materialMaster["+materialMaster+"]length["+materialMaster.length()+"]");
				         
				         //2
				         start = end;
				         end = start+13;
				         barcode = lines.substring(start,end);
				        // logger.debug("barcode["+barcode+"]length["+barcode.length()+"]");

				         //3
				         start = end;
				         end = start+11;
				         onhandQty = lines.substring(start,end-2);
				         onhandQty2Digit = lines.substring(end-2,end);
				         //logger.debug("onhandQty["+onhandQty+"]onhandQty2Digit["+onhandQty2Digit+"]length["+onhandQty.length()+"]");
				         
				         //4
				         start = end;
				         end = start+11;
				         wholePriceBF = lines.substring(start,end-2);
				         wholePriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("wholePriceBF["+wholePriceBF+"]wholePriceBF2Digit["+wholePriceBF2Digit+"]length["+wholePriceBF.length()+"]");
				         
				         //5
				         start = end;
				         end = start+11;
				         retailPriceBF = lines.substring(start,end-2);
				         retailPriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("retailPriceBF["+retailPriceBF+"]retailPriceBF2Digit["+retailPriceBF2Digit+"]length["+retailPriceBF.length()+"]");
				         
				         //6
				         start = end;
				         end = start+35;
				         item = lines.substring(start,end);
				        // logger.debug("item["+item+"]length["+item.length()+"]");
				         
				         //7
				         start = end;
				         end = start+40;
				         itemDesc = lines.substring(start,end);
				         //logger.debug("itemDesc["+itemDesc+"]length["+itemDesc.length()+"]");
				         
				         /** Prepare Message To Display **/
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         OnhandSummary oh = new OnhandSummary();
				         oh.setAsOfDate(Utils.stringValue(fileNameAsOfDate, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				         oh.setItem(Utils.isNull(item));
				         oh.setItemDesc(Utils.isNull(itemDesc));
				         oh.setOnhandQty(Utils.isNull(onhandQty+onhandQty2Digit));
				         oh.setWholePriceBF(Utils.isNull(wholePriceBF+wholePriceBF2Digit));
				         oh.setRetailPriceBF(Utils.isNull(retailPriceBF+retailPriceBF2Digit));
				         oh.setBarcode(Utils.isNull(barcode));
				         oh.setMaterialMaster(Utils.isNull(materialMaster));
				         //Find pens_item **/
				         pensItem = importDAO.getItemByBarcode(conn,Constants.STORE_TYPE_LOTUS_ITEM, oh.getBarcode());
				         oh.setPensItem(Utils.isNull(pensItem));
				         
				         s.setOnhandSummary(oh);
				         
				         /** Case Onhand Qty == 0 No validate **/
				         BigDecimal onhandQtyValid = new BigDecimal(onhandQty+"."+onhandQty2Digit);
				         if(onhandQtyValid.compareTo(bigZero) != 0 ){
				         
					         /** Validate Barcode **/
					         MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_LOTUS_ITEM, barcode);
					         String itemCodeValid = mb!=null?mb.getItem():"";
					         groupItem = mb!=null?mb.getGroup():"";
					         
					        // logger.debug("itemCodeValid["+itemCodeValid+"]");
					         if(Utils.isNull(itemCodeValid).equals("")){
					        	 Message m = new Message();
					        	 m.setMessage("ไม่พบข้อมูล Barcode");
					        	 errorMsgList.add(m);
					        	 lineError = true;
					         }
					         
					         /** Validate ItemCodeOracle VS ItemCodeWacoal IS Equals**/
					         /*if( !Utils.isNull(itemCodeValid).equals("")){
						         if( !Utils.isNull(itemCodeValid).equals(Utils.isNull(item))){
						        	 Message m = new Message();
						        	 m.setMessage("ข้อมูล Item Wacoal["+Utils.isNull(item)+"] กับ Item Oracle["+Utils.isNull(itemCodeValid)+"] ไม่ตรงกัน");
						        	 errorMsgList.add(m);
						        	 lineError = true;
						         }
					         }*/
					         
					         /** Validate WholePriceBF **/
					         BigDecimal wholePriceBFOracle = importDAO.getWholePriceBFFromOracle(conn, itemCodeValid);
					    
					         if(wholePriceBFOracle.compareTo(bigZero) ==0){//== 0
					        	 //Not found Add Fail Msg
					        	 Message m = new Message();
						         m.setMessage("ไม่พบข้อมูลราคาขายส่งนี้ในระบบ Oracle");
						         errorMsgList.add(m);
						         lineError = true;
					         }else if( wholePriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 BigDecimal wholePriceBFWacoal =  new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit);
					        	
					        	 if( wholePriceBFWacoal.compareTo(bigZero) != 0 &&  wholePriceBFOracle.compareTo(wholePriceBFWacoal) != 0){
					        		 Message m = new Message();
							         m.setMessage("ราคาขายส่ง  Wacoal["+wholePriceBFWacoal+"] กับ  Oracle["+wholePriceBFOracle+"] ไม่ตรงกัน");
							         errorMsgList.add(m);
							         lineError = true;
					        	 }
					         }
					         
					         /** Validate RetailPriceBF **/
					         //BigDecimal retailPriceBFOracle = importDAO.getRetailPriceBFFromOracle(conn, itemCodeValid);
					    
					         //if(retailPriceBFOracle.compareTo(bigZero) ==0){ // =0
					        	 //Not found Add Fail Msg
					        	 //Message m = new Message();
						         //m.setMessage("ไม่พบข้อมูลราคาขายปลีกนี้ในระบบ Oracle");
						         //errorMsgList.add(m);
						         //lineError = true;
					        // }else if( retailPriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 //BigDecimal reatilPriceBFWacoal =  new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit);
					        	 //if(reatilPriceBFWacoal.compareTo(bigZero) != 0 && retailPriceBFOracle.compareTo(reatilPriceBFWacoal) != 0){
					        		 //Message m = new Message();
							         //m.setMessage("ราคาขายปลีก  Wacoal["+reatilPriceBFWacoal+"] กับ Oracle["+retailPriceBFOracle+"] ไม่ตรงกัน");
							         //errorMsgList.add(m);
							         //lineError = true;
					        	 //}
					         //}
					         
				         }else{//if onhand qty != 0
				        	 //Case QTY =0  no validate item
				        	 MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_LOTUS_ITEM, barcode);
					         groupItem = mb!=null?mb.getGroup():"";
					         
				         }
				         
				        
				         message = "";
				         
				         if(lineError){
				        	 importError = true;
				        	 s.setErrorMsgList(errorMsgList);
				        	 errorList.add(s);
				        	 
				        	 
				        	 /** set Message TO Save **/
				        	 for(int e=0;e<errorMsgList.size();e++){
				        		 Message me = (Message)errorMsgList.get(e);
				        		 message += me.getMessage()+",";
				        	 }
				         }else{
				        	 Message m = new Message();
				        	 m.setMessage("Success");
				        	 errorMsgList.add(m);
				        	 successList.add(s);
				         }
				         
				         //Check Status and Message
				         status = lineError?"ERROR":"SUCCESS";
				         
				         
				         //Line No Error
				         if(lineError==false || "NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
					         ps.setDate(1, new java.sql.Date(fileNameAsOfDate.getTime()));
					         ps.setString(2, Utils.isNull(materialMaster));
					         ps.setString(3, Utils.isNull(barcode));
					         ps.setBigDecimal(4, new BigDecimal(onhandQty+"."+onhandQty2Digit));
					         ps.setBigDecimal(5, new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit));
					         ps.setBigDecimal(6, new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit));
					         ps.setString(7, Utils.isNull(item));
					         ps.setString(8, Utils.isNull(itemDesc));
					         ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
					         ps.setString(10, user.getUserName());
					         ps.setString(11, fileName);
					         ps.setString(12, groupItem);
					         ps.setString(13, status);
					         ps.setString(14, message);
					         ps.setString(15, pensItem);
					         
					         allCount++;
					         
					         ps.executeUpdate();
				         }
				         
					  }catch(Exception e){
					     failCount++;
					     e.printStackTrace();
					     importError=true;
					  }
		         }//if
		      }//while

		      
		     importForm.setSummaryErrorList(errorList);
			 importForm.setSummarySuccessList(successList);
			  
			 importForm.setTotalSize(errorList.size()+successList.size());
			 importForm.setSummaryWacoalListErrorSize(errorList!=null?errorList.size():0);
			 importForm.setSummaryWacoalListSuccessSize(successList!=null?successList.size():0);
		     
			 if("NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
				 request.setAttribute("Message","Upload ไฟล์ "+fileName+"สำเร็จ โดย นำข้อมูลที่ Error เข้าทั้งหมด"); 
				 conn.commit();
			 }else{
				 if(importError){
					  request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
					  logger.debug("Transaction Rollback");
					  
					  if( "".equalsIgnoreCase(noCheckError))
				         conn.rollback();
					}else{
					   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
					   logger.debug("Transaction Commit");
					   conn.commit();
					}
				 }
			}//if
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
		}finally{
		    	// dispose all the resources after using them.
			      if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
		    }
		return mapping.findForward("success");
	}
	
	public ActionForward importFridayFromWacoal(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("importFridayFromWacoal :Text");
		ImportForm importForm = (ImportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
		try {
			
			String noCheckError = Utils.isNull(request.getParameter("NO_CHECK_ERROR"));
			 
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
			FormFile dataFile = importForm.getDataFile();
			
			/** Step 1 Validate Name Date of File ,Must more than old import  **/
			//FRIDAY_OH_20130923.txt
			//LOTUS
			String fileName = dataFile.getFileName();
			logger.debug("dateSubStr:"+fileName.substring(10,18));
			
			java.util.Date fileNameAsOfDate = Utils.parse(fileName.substring(10,18),Utils.YYYY_MM_DD_WITHOUT_SLASH);
			// Get LastFileNameImport 
			String lastFileNameImport = importDAO.getLastFileNameImportFriday(conn);
			if( !Utils.isNull(lastFileNameImport).equals("")){
				java.util.Date lastFileNameAsOfDate = Utils.parse(lastFileNameImport.substring(10,18),Utils.YYYY_MM_DD_WITHOUT_SLASH);
				
				if(fileNameAsOfDate.before(lastFileNameAsOfDate)){ //dateImport < lastDateImport
					request.setAttribute("Message","ชื่อไฟล์ที่  Upload ["+fileName+"] วันที่น้อยกว่า  ชื่อไฟล์วันที่ล่าสุดที่  Upload ["+lastFileNameImport+"] ");
					return mapping.findForward("success");
				}
			}
			
			if (dataFile != null) {
			  
			  logger.debug("contentType: " + dataFile.getContentType());
			  logger.debug("fileName: " + dataFile.getFileName());

			  
			  /** Delete All before Import **/
			  psDelete = conn.prepareStatement("delete from PENSBME_ONHAND_BME_FRIDAY");
			  psDelete.executeUpdate();
	
			  StringBuffer sql = new StringBuffer("");
			  sql.append("INSERT INTO PENSBME_ONHAND_BME_FRIDAY( \n");
			  sql.append(" AS_OF_DATE, MATERIAL_MASTER, BARCODE, \n");
			  sql.append(" ONHAND_QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,  \n");
			  sql.append(" ITEM, ITEM_DESC, CREATE_DATE, CREATE_USER,FILE_NAME,GROUP_ITEM,STATUS,MESSAGE,PENS_ITEM) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?,?,?,?)");
			  
			  ps = conn.prepareStatement(sql.toString());
			  
			  String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "tis-620");
			  //replace '?' in index =0
			 // dataFileStr = dataFileStr.substring(1,dataFileStr.length());
			  logger.debug("dataFileStr:"+dataFileStr);
			  
	    	  String[] dataStrArray = dataFileStr.split("\n");

		      int start = 0;
		      int end = 0;
		      String lines = "";
		      String materialMaster = "";
		      String barcode = "";
		      String onhandQty = "";
		      String onhandQty2Digit = "";
		      String wholePriceBF = "";
		      String wholePriceBF2Digit = "";
		      String retailPriceBF = "";
		      String retailPriceBF2Digit = "";
		      String item  ="";
		      String itemDesc= "";
		      String groupItem = "";
		      String status = "";
		      String message ="";
		      String pensItem = "";
		      
		     for(int i=0;i<dataStrArray.length;i++){
		    	 lineError = false;
		    	 errorMsgList = new ArrayList<Message>();
		    	 lines = dataStrArray[i];
		    	 logger.debug("lines:"+lines);
		         if(!Utils.isNull(lines).equals("")){
		        	try{
				         // 1
			        	 start = 0;
				         end = 18;
				         materialMaster = lines.substring(start,end);
				         //logger.debug("materialMaster["+materialMaster+"]length["+materialMaster.length()+"]");
				         
				         //2
				         start = end;
				         end = start+13;
				         barcode = lines.substring(start,end);
				        // logger.debug("barcode["+barcode+"]length["+barcode.length()+"]");

				         //3
				         start = end;
				         end = start+11;
				         onhandQty = lines.substring(start,end-2);
				         onhandQty2Digit = lines.substring(end-2,end);
				         //logger.debug("onhandQty["+onhandQty+"]onhandQty2Digit["+onhandQty2Digit+"]length["+onhandQty.length()+"]");
				         
				         //4
				         start = end;
				         end = start+11;
				         wholePriceBF = lines.substring(start,end-2);
				         wholePriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("wholePriceBF["+wholePriceBF+"]wholePriceBF2Digit["+wholePriceBF2Digit+"]length["+wholePriceBF.length()+"]");
				         
				         //5
				         start = end;
				         end = start+11;
				         retailPriceBF = lines.substring(start,end-2);
				         retailPriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("retailPriceBF["+retailPriceBF+"]retailPriceBF2Digit["+retailPriceBF2Digit+"]length["+retailPriceBF.length()+"]");
				         
				         //6
				         start = end;
				         end = start+35;
				         item = lines.substring(start,end);
				         logger.debug("item["+item+"]length["+item.length()+"]");
				         
				         //7
				         start = end;
				         end = start+40;
				         itemDesc = lines.substring(start,end);
				         //logger.debug("itemDesc["+itemDesc+"]length["+itemDesc.length()+"]");
				         
				         /** Prepare Message To Display **/
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         OnhandSummary oh = new OnhandSummary();
				         oh.setAsOfDate(Utils.stringValue(fileNameAsOfDate, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				         oh.setItem(Utils.isNull(item));
				         oh.setItemDesc(Utils.isNull(itemDesc));
				         oh.setOnhandQty(Utils.isNull(onhandQty+onhandQty2Digit));
				         oh.setWholePriceBF(Utils.isNull(wholePriceBF+wholePriceBF2Digit));
				         oh.setRetailPriceBF(Utils.isNull(retailPriceBF+retailPriceBF2Digit));
				         oh.setBarcode(Utils.isNull(barcode));
				         oh.setMaterialMaster(Utils.isNull(materialMaster));
				         //Find pens_item **/
				         pensItem = importDAO.getItemByBarcode(conn,Constants.STORE_TYPE_FRIDAY_ITEM, oh.getBarcode());//
				         oh.setPensItem(Utils.isNull(pensItem));
				         
				         s.setOnhandSummary(oh);
				         
				         /** Case Onhand Qty == 0 No validate **/
				         BigDecimal onhandQtyValid = new BigDecimal(onhandQty+"."+onhandQty2Digit);
				         if(onhandQtyValid.compareTo(bigZero) != 0 ){
				         
					         /** Validate Barcode **/
					         MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_FRIDAY_ITEM, barcode);
					         String itemCodeValid = mb!=null?mb.getItem():"";
					         groupItem = mb!=null?mb.getGroup():"";
					         
					        // logger.debug("itemCodeValid["+itemCodeValid+"]");
					         if(Utils.isNull(itemCodeValid).equals("")){
					        	 Message m = new Message();
					        	 m.setMessage("ไม่พบข้อมูล Barcode");
					        	 errorMsgList.add(m);
					        	 lineError = true;
					         }
					         
					         /** Validate ItemCodeOracle VS ItemCodeWacoal IS Equals**/
					         /*if( !Utils.isNull(itemCodeValid).equals("")){
						         if( !Utils.isNull(itemCodeValid).equals(Utils.isNull(item))){
						        	 Message m = new Message();
						        	 m.setMessage("ข้อมูล Item Wacoal["+Utils.isNull(item)+"] กับ Item Oracle["+Utils.isNull(itemCodeValid)+"] ไม่ตรงกัน");
						        	 errorMsgList.add(m);
						        	 lineError = true;
						         }
					         }*/
					         
					         /** Validate WholePriceBF **/
					        /* BigDecimal wholePriceBFOracle = importDAO.getWholePriceBFFromOracle(conn, itemCodeValid);
					    
					         if(wholePriceBFOracle.compareTo(bigZero) ==0){//== 0
					        	 //Not found Add Fail Msg
					        	 Message m = new Message();
						         m.setMessage("ไม่พบข้อมูลราคาขายส่งนี้ในระบบ Oracle");
						         errorMsgList.add(m);
						         lineError = true;
					         }else if( wholePriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 BigDecimal wholePriceBFWacoal =  new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit);
					        	
					        	 if( wholePriceBFWacoal.compareTo(bigZero) != 0 &&  wholePriceBFOracle.compareTo(wholePriceBFWacoal) != 0){
					        		 Message m = new Message();
							         m.setMessage("ราคาขายส่ง  Wacoal["+wholePriceBFWacoal+"] กับ  Oracle["+wholePriceBFOracle+"] ไม่ตรงกัน");
							         errorMsgList.add(m);
							         lineError = true;
					        	 }
					         }*/
					         
					         /** Validate RetailPriceBF **/
					         //BigDecimal retailPriceBFOracle = importDAO.getRetailPriceBFFromOracle(conn, itemCodeValid);
					    
					         //if(retailPriceBFOracle.compareTo(bigZero) ==0){ // =0
					        	 //Not found Add Fail Msg
					        	 //Message m = new Message();
						         //m.setMessage("ไม่พบข้อมูลราคาขายปลีกนี้ในระบบ Oracle");
						         //errorMsgList.add(m);
						         //lineError = true;
					        // }else if( retailPriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 //BigDecimal reatilPriceBFWacoal =  new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit);
					        	 //if(reatilPriceBFWacoal.compareTo(bigZero) != 0 && retailPriceBFOracle.compareTo(reatilPriceBFWacoal) != 0){
					        		 //Message m = new Message();
							         //m.setMessage("ราคาขายปลีก  Wacoal["+reatilPriceBFWacoal+"] กับ Oracle["+retailPriceBFOracle+"] ไม่ตรงกัน");
							         //errorMsgList.add(m);
							         //lineError = true;
					        	 //}
					         //}
					         
				         }else{//if onhand qty != 0
				        	 //Case QTY =0  no validate item
				        	 MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_FRIDAY_ITEM, barcode);
					         groupItem = mb!=null?mb.getGroup():"";
					         
				         }

				         message = "";
				         
				         if(lineError){
				        	 importError = true;
				        	 s.setErrorMsgList(errorMsgList);
				        	 errorList.add(s);

				        	 /** set Message TO Save **/
				        	 for(int e=0;e<errorMsgList.size();e++){
				        		 Message me = (Message)errorMsgList.get(e);
				        		 message += me.getMessage()+",";
				        	 }
				         }else{
				        	 Message m = new Message();
				        	 m.setMessage("Success");
				        	 errorMsgList.add(m);
				        	 successList.add(s);
				         }
				         
				         //Check Status and Message
				         status = lineError?"ERROR":"SUCCESS";
				         
				         
				         //Line No Error
				         if(lineError==false || "NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
					         ps.setDate(1, new java.sql.Date(fileNameAsOfDate.getTime()));
					         ps.setString(2, Utils.isNull(materialMaster));
					         ps.setString(3, Utils.isNull(barcode));
					         ps.setBigDecimal(4, new BigDecimal(onhandQty+"."+onhandQty2Digit));
					         ps.setBigDecimal(5, new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit));
					         ps.setBigDecimal(6, new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit));
					         ps.setString(7, Utils.isNull(item));
					         ps.setString(8, Utils.isNull(itemDesc));
					         ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
					         ps.setString(10, user.getUserName());
					         ps.setString(11, fileName);
					         ps.setString(12, groupItem);
					         ps.setString(13, status);
					         ps.setString(14, message);
					         ps.setString(15, pensItem);
					         
					         allCount++;
					         
					         ps.executeUpdate();
				         }
				         
					  }catch(Exception e){
					     failCount++;
					     e.printStackTrace();
					     importError=true;
					  }
		         }//if
		      }//while

		      
		     importForm.setSummaryErrorList(errorList);
			 importForm.setSummarySuccessList(successList);
			  
			 importForm.setTotalSize(errorList.size()+successList.size());
			 importForm.setSummaryWacoalListErrorSize(errorList!=null?errorList.size():0);
			 importForm.setSummaryWacoalListSuccessSize(successList!=null?successList.size():0);
		     
			 if("NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
				 request.setAttribute("Message","Upload ไฟล์ "+fileName+"สำเร็จ โดย นำข้อมูลที่ Error เข้าทั้งหมด"); 
				 conn.commit();
			 }else{
				 if(importError){
					  request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
					  logger.debug("Transaction Rollback");
					  
					  if( "".equalsIgnoreCase(noCheckError))
				         conn.rollback();
					}else{
					   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
					   logger.debug("Transaction Commit");
					   conn.commit();
					}
				 }
			}//if
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
		}finally{
		    	// dispose all the resources after using them.
			      if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
		    }
		return mapping.findForward("success");
	}
	

	public ActionForward importFridayFromTVDirect(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("importFridayFromTVDirect :Text");
		ImportForm importForm = (ImportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
		try {
			
			String noCheckError = Utils.isNull(request.getParameter("NO_CHECK_ERROR"));
			 
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
			FormFile dataFile = importForm.getDataFile();
			
			/** Step 1 Validate Name Date of File ,Must more than old import  **/
			//FRIDAY_OH_20130923.txt
			//TVDIRECT
			//LOTUS
			String fileName = dataFile.getFileName();
			logger.debug("dateSubStr:"+fileName.substring(12,20));
			
			java.util.Date fileNameAsOfDate = Utils.parse(fileName.substring(12,20),Utils.YYYY_MM_DD_WITHOUT_SLASH);
			// Get LastFileNameImport 
			String lastFileNameImport = importDAO.getLastFileNameImportTVDirect(conn);
			if( !Utils.isNull(lastFileNameImport).equals("")){
				java.util.Date lastFileNameAsOfDate = Utils.parse(lastFileNameImport.substring(12,20),Utils.YYYY_MM_DD_WITHOUT_SLASH);
				
				if(fileNameAsOfDate.before(lastFileNameAsOfDate)){ //dateImport < lastDateImport
					request.setAttribute("Message","ชื่อไฟล์ที่  Upload ["+fileName+"] วันที่น้อยกว่า  ชื่อไฟล์วันที่ล่าสุดที่  Upload ["+lastFileNameImport+"] ");
					return mapping.findForward("success");
				}
			}
			
			if (dataFile != null) {
			  
			  logger.debug("contentType: " + dataFile.getContentType());
			  logger.debug("fileName: " + dataFile.getFileName());

			  
			  /** Delete All before Import **/
			  psDelete = conn.prepareStatement("delete from PENSBME_ONHAND_BME_TVDIRECT");
			  psDelete.executeUpdate();
	
			  StringBuffer sql = new StringBuffer("");
			  sql.append("INSERT INTO PENSBME_ONHAND_BME_TVDIRECT( \n");
			  sql.append(" AS_OF_DATE, MATERIAL_MASTER, BARCODE, \n");
			  sql.append(" ONHAND_QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,  \n");
			  sql.append(" ITEM, ITEM_DESC, CREATE_DATE, CREATE_USER,FILE_NAME,GROUP_ITEM,STATUS,MESSAGE,PENS_ITEM) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?,?,?,?)");
			  
			  ps = conn.prepareStatement(sql.toString());
			  
			  String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "tis-620");
			  //replace '?' in index =0
			 // dataFileStr = dataFileStr.substring(1,dataFileStr.length());
			  logger.debug("dataFileStr:"+dataFileStr);
			  
	    	  String[] dataStrArray = dataFileStr.split("\n");

		      int start = 0;
		      int end = 0;
		      String lines = "";
		      String materialMaster = "";
		      String barcode = "";
		      String onhandQty = "";
		      String onhandQty2Digit = "";
		      String wholePriceBF = "";
		      String wholePriceBF2Digit = "";
		      String retailPriceBF = "";
		      String retailPriceBF2Digit = "";
		      String item  ="";
		      String itemDesc= "";
		      String groupItem = "";
		      String status = "";
		      String message ="";
		      String pensItem = "";
		      
		     for(int i=0;i<dataStrArray.length;i++){
		    	 lineError = false;
		    	 errorMsgList = new ArrayList<Message>();
		    	 lines = dataStrArray[i];
		    	 logger.debug("lines:"+lines);
		         if(!Utils.isNull(lines).equals("")){
		        	try{
				         // 1
			        	 start = 0;
				         end = 18;
				         materialMaster = lines.substring(start,end);
				         //logger.debug("materialMaster["+materialMaster+"]length["+materialMaster.length()+"]");
				         
				         //2
				         start = end;
				         end = start+13;
				         barcode = lines.substring(start,end);
				        // logger.debug("barcode["+barcode+"]length["+barcode.length()+"]");

				         //3
				         start = end;
				         end = start+11;
				         onhandQty = lines.substring(start,end-2);
				         onhandQty2Digit = lines.substring(end-2,end);
				         //logger.debug("onhandQty["+onhandQty+"]onhandQty2Digit["+onhandQty2Digit+"]length["+onhandQty.length()+"]");
				         
				         //4
				         start = end;
				         end = start+11;
				         wholePriceBF = lines.substring(start,end-2);
				         wholePriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("wholePriceBF["+wholePriceBF+"]wholePriceBF2Digit["+wholePriceBF2Digit+"]length["+wholePriceBF.length()+"]");
				         
				         //5
				         start = end;
				         end = start+11;
				         retailPriceBF = lines.substring(start,end-2);
				         retailPriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("retailPriceBF["+retailPriceBF+"]retailPriceBF2Digit["+retailPriceBF2Digit+"]length["+retailPriceBF.length()+"]");
				         
				         //6
				         start = end;
				         end = start+35;
				         item = lines.substring(start,end);
				         logger.debug("item["+item+"]length["+item.length()+"]");
				         
				         //7
				         start = end;
				         end = start+40;
				         itemDesc = lines.substring(start,end);
				         //logger.debug("itemDesc["+itemDesc+"]length["+itemDesc.length()+"]");
				         
				         /** Prepare Message To Display **/
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         OnhandSummary oh = new OnhandSummary();
				         oh.setAsOfDate(Utils.stringValue(fileNameAsOfDate, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				         oh.setItem(Utils.isNull(item));
				         oh.setItemDesc(Utils.isNull(itemDesc));
				         oh.setOnhandQty(Utils.isNull(onhandQty+onhandQty2Digit));
				         oh.setWholePriceBF(Utils.isNull(wholePriceBF+wholePriceBF2Digit));
				         oh.setRetailPriceBF(Utils.isNull(retailPriceBF+retailPriceBF2Digit));
				         oh.setBarcode(Utils.isNull(barcode));
				         oh.setMaterialMaster(Utils.isNull(materialMaster));
				         //Find pens_item **/
				         pensItem = importDAO.getItemByBarcode(conn,Constants.STORE_TYPE_TVD_ITEM, oh.getBarcode());//
				         oh.setPensItem(Utils.isNull(pensItem));
				         
				         s.setOnhandSummary(oh);
				         
				         /** Case Onhand Qty == 0 No validate **/
				         BigDecimal onhandQtyValid = new BigDecimal(onhandQty+"."+onhandQty2Digit);
				         if(onhandQtyValid.compareTo(bigZero) != 0 ){
				         
					         /** Validate Barcode **/
					         MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_TVD_ITEM, barcode);
					         String itemCodeValid = mb!=null?mb.getItem():"";
					         groupItem = mb!=null?mb.getGroup():"";
					         
					        // logger.debug("itemCodeValid["+itemCodeValid+"]");
					         if(Utils.isNull(itemCodeValid).equals("")){
					        	 Message m = new Message();
					        	 m.setMessage("ไม่พบข้อมูล Barcode");
					        	 errorMsgList.add(m);
					        	 lineError = true;
					         }
					         
					         /** Validate ItemCodeOracle VS ItemCodeWacoal IS Equals**/
					         /*if( !Utils.isNull(itemCodeValid).equals("")){
						         if( !Utils.isNull(itemCodeValid).equals(Utils.isNull(item))){
						        	 Message m = new Message();
						        	 m.setMessage("ข้อมูล Item Wacoal["+Utils.isNull(item)+"] กับ Item Oracle["+Utils.isNull(itemCodeValid)+"] ไม่ตรงกัน");
						        	 errorMsgList.add(m);
						        	 lineError = true;
						         }
					         }*/
					         
					         /** Validate WholePriceBF **/
					        /* BigDecimal wholePriceBFOracle = importDAO.getWholePriceBFFromOracle(conn, itemCodeValid);
					    
					         if(wholePriceBFOracle.compareTo(bigZero) ==0){//== 0
					        	 //Not found Add Fail Msg
					        	 Message m = new Message();
						         m.setMessage("ไม่พบข้อมูลราคาขายส่งนี้ในระบบ Oracle");
						         errorMsgList.add(m);
						         lineError = true;
					         }else if( wholePriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 BigDecimal wholePriceBFWacoal =  new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit);
					        	
					        	 if( wholePriceBFWacoal.compareTo(bigZero) != 0 &&  wholePriceBFOracle.compareTo(wholePriceBFWacoal) != 0){
					        		 Message m = new Message();
							         m.setMessage("ราคาขายส่ง  Wacoal["+wholePriceBFWacoal+"] กับ  Oracle["+wholePriceBFOracle+"] ไม่ตรงกัน");
							         errorMsgList.add(m);
							         lineError = true;
					        	 }
					         }*/
					         
					         /** Validate RetailPriceBF **/
					         //BigDecimal retailPriceBFOracle = importDAO.getRetailPriceBFFromOracle(conn, itemCodeValid);
					    
					         //if(retailPriceBFOracle.compareTo(bigZero) ==0){ // =0
					        	 //Not found Add Fail Msg
					        	 //Message m = new Message();
						         //m.setMessage("ไม่พบข้อมูลราคาขายปลีกนี้ในระบบ Oracle");
						         //errorMsgList.add(m);
						         //lineError = true;
					        // }else if( retailPriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 //BigDecimal reatilPriceBFWacoal =  new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit);
					        	 //if(reatilPriceBFWacoal.compareTo(bigZero) != 0 && retailPriceBFOracle.compareTo(reatilPriceBFWacoal) != 0){
					        		 //Message m = new Message();
							         //m.setMessage("ราคาขายปลีก  Wacoal["+reatilPriceBFWacoal+"] กับ Oracle["+retailPriceBFOracle+"] ไม่ตรงกัน");
							         //errorMsgList.add(m);
							         //lineError = true;
					        	 //}
					         //}
					         
				         }else{//if onhand qty != 0
				        	 //Case QTY =0  no validate item
				        	 MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_TVD_ITEM, barcode);
					         groupItem = mb!=null?mb.getGroup():"";
					         
				         }

				         message = "";
				         
				         if(lineError){
				        	 importError = true;
				        	 s.setErrorMsgList(errorMsgList);
				        	 errorList.add(s);

				        	 /** set Message TO Save **/
				        	 for(int e=0;e<errorMsgList.size();e++){
				        		 Message me = (Message)errorMsgList.get(e);
				        		 message += me.getMessage()+",";
				        	 }
				         }else{
				        	 Message m = new Message();
				        	 m.setMessage("Success");
				        	 errorMsgList.add(m);
				        	 successList.add(s);
				         }
				         
				         //Check Status and Message
				         status = lineError?"ERROR":"SUCCESS";
				         
				         
				         //Line No Error
				         if(lineError==false || "NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
					         ps.setDate(1, new java.sql.Date(fileNameAsOfDate.getTime()));
					         ps.setString(2, Utils.isNull(materialMaster));
					         ps.setString(3, Utils.isNull(barcode));
					         ps.setBigDecimal(4, new BigDecimal(onhandQty+"."+onhandQty2Digit));
					         ps.setBigDecimal(5, new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit));
					         ps.setBigDecimal(6, new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit));
					         ps.setString(7, Utils.isNull(item));
					         ps.setString(8, Utils.isNull(itemDesc));
					         ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
					         ps.setString(10, user.getUserName());
					         ps.setString(11, fileName);
					         ps.setString(12, groupItem);
					         ps.setString(13, status);
					         ps.setString(14, message);
					         ps.setString(15, pensItem);
					         
					         allCount++;
					         
					         ps.executeUpdate();
				         }
				         
					  }catch(Exception e){
					     failCount++;
					     e.printStackTrace();
					     importError=true;
					  }
		         }//if
		      }//while

		      
		     importForm.setSummaryErrorList(errorList);
			 importForm.setSummarySuccessList(successList);
			  
			 importForm.setTotalSize(errorList.size()+successList.size());
			 importForm.setLoadOnhandTVDirectListErrorSize(errorList!=null?errorList.size():0);
			 importForm.setLoadOnhandTVDirectListSuccessSize(successList!=null?successList.size():0);
		     
			 if("NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
				 request.setAttribute("Message","Upload ไฟล์ "+fileName+"สำเร็จ โดย นำข้อมูลที่ Error เข้าทั้งหมด"); 
				 conn.commit();
			 }else{
				 if(importError){
					  request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
					  logger.debug("Transaction Rollback");
					  
					  if( "".equalsIgnoreCase(noCheckError))
				         conn.rollback();
					}else{
					   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
					   logger.debug("Transaction Commit");
					   conn.commit();
					}
				 }
			}//if
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
		}finally{
		    	// dispose all the resources after using them.
			      if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
		    }
		return mapping.findForward("success");
	}
	
	public ActionForward import7CatalogFromWacoal(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("import7CatalogFromWacoal :Text");
		ImportForm importForm = (ImportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
		try {
			
			String noCheckError = Utils.isNull(request.getParameter("NO_CHECK_ERROR"));
			 
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
			FormFile dataFile = importForm.getDataFile();
			
			/** Step 1 Validate Name Date of File ,Must more than old import  **/
			//7CATALOG_OH_20130923.txt
			//SEVENCAT
			//LOTUS
			String fileName = dataFile.getFileName();
			logger.debug("dateSubStr:"+fileName.substring(12,20));
			
			java.util.Date fileNameAsOfDate = Utils.parse(fileName.substring(12,20),Utils.YYYY_MM_DD_WITHOUT_SLASH);
			// Get LastFileNameImport 
			String lastFileNameImport = importDAO.getLastFileNameImport7Catalog(conn);
			if( !Utils.isNull(lastFileNameImport).equals("")){
				java.util.Date lastFileNameAsOfDate = Utils.parse(lastFileNameImport.substring(12,20),Utils.YYYY_MM_DD_WITHOUT_SLASH);
				
				if(fileNameAsOfDate.before(lastFileNameAsOfDate)){ //dateImport < lastDateImport
					request.setAttribute("Message","ชื่อไฟล์ที่  Upload ["+fileName+"] วันที่น้อยกว่า  ชื่อไฟล์วันที่ล่าสุดที่  Upload ["+lastFileNameImport+"] ");
					return mapping.findForward("success");
				}
			}
			
			if (dataFile != null) {
			  
			  logger.debug("contentType: " + dataFile.getContentType());
			  logger.debug("fileName: " + dataFile.getFileName());

			  
			  /** Delete All before Import **/
			  psDelete = conn.prepareStatement("delete from PENSBME_ONHAND_BME_7CATALOG");
			  psDelete.executeUpdate();
	
			  StringBuffer sql = new StringBuffer("");
			  sql.append("INSERT INTO PENSBME_ONHAND_BME_7CATALOG( \n");
			  sql.append(" AS_OF_DATE, MATERIAL_MASTER, BARCODE, \n");
			  sql.append(" ONHAND_QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,  \n");
			  sql.append(" ITEM, ITEM_DESC, CREATE_DATE, CREATE_USER,FILE_NAME,GROUP_ITEM,STATUS,MESSAGE,PENS_ITEM) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?,?,?,?)");
			  
			  ps = conn.prepareStatement(sql.toString());
			  
			  String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "tis-620");
			  //replace '?' in index =0
			 // dataFileStr = dataFileStr.substring(1,dataFileStr.length());
			  logger.debug("dataFileStr:"+dataFileStr);
			  
	    	  String[] dataStrArray = dataFileStr.split("\n");

		      int start = 0;
		      int end = 0;
		      String lines = "";
		      String materialMaster = "";
		      String barcode = "";
		      String onhandQty = "";
		      String onhandQty2Digit = "";
		      String wholePriceBF = "";
		      String wholePriceBF2Digit = "";
		      String retailPriceBF = "";
		      String retailPriceBF2Digit = "";
		      String item  ="";
		      String itemDesc= "";
		      String groupItem = "";
		      String status = "";
		      String message ="";
		      String pensItem = "";
		      
		     for(int i=0;i<dataStrArray.length;i++){
		    	 lineError = false;
		    	 errorMsgList = new ArrayList<Message>();
		    	 lines = dataStrArray[i];
		    	 logger.debug("lines:"+lines);
		         if(!Utils.isNull(lines).equals("")){
		        	try{
				         // 1
			        	 start = 0;
				         end = 18;
				         materialMaster = lines.substring(start,end);
				         //logger.debug("materialMaster["+materialMaster+"]length["+materialMaster.length()+"]");
				         
				         //2
				         start = end;
				         end = start+13;
				         barcode = lines.substring(start,end);
				        // logger.debug("barcode["+barcode+"]length["+barcode.length()+"]");

				         //3
				         start = end;
				         end = start+11;
				         onhandQty = lines.substring(start,end-2);
				         onhandQty2Digit = lines.substring(end-2,end);
				         //logger.debug("onhandQty["+onhandQty+"]onhandQty2Digit["+onhandQty2Digit+"]length["+onhandQty.length()+"]");
				         
				         //4
				         start = end;
				         end = start+11;
				         wholePriceBF = lines.substring(start,end-2);
				         wholePriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("wholePriceBF["+wholePriceBF+"]wholePriceBF2Digit["+wholePriceBF2Digit+"]length["+wholePriceBF.length()+"]");
				         
				         //5
				         start = end;
				         end = start+11;
				         retailPriceBF = lines.substring(start,end-2);
				         retailPriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("retailPriceBF["+retailPriceBF+"]retailPriceBF2Digit["+retailPriceBF2Digit+"]length["+retailPriceBF.length()+"]");
				         
				         //6
				         start = end;
				         end = start+35;
				         item = lines.substring(start,end);
				        // logger.debug("item["+item+"]length["+item.length()+"]");
				         
				         //7
				         start = end;
				         end = start+40;
				         itemDesc = lines.substring(start,end);
				         //logger.debug("itemDesc["+itemDesc+"]length["+itemDesc.length()+"]");
				         
				         /** Prepare Message To Display **/
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         OnhandSummary oh = new OnhandSummary();
				         oh.setAsOfDate(Utils.stringValue(fileNameAsOfDate, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				         oh.setItem(Utils.isNull(item));
				         oh.setItemDesc(Utils.isNull(itemDesc));
				         oh.setOnhandQty(Utils.isNull(onhandQty+onhandQty2Digit));
				         oh.setWholePriceBF(Utils.isNull(wholePriceBF+wholePriceBF2Digit));
				         oh.setRetailPriceBF(Utils.isNull(retailPriceBF+retailPriceBF2Digit));
				         oh.setBarcode(Utils.isNull(barcode));
				         oh.setMaterialMaster(Utils.isNull(materialMaster));
				         //Find pens_item **/
				         pensItem = importDAO.getItemByBarcode(conn,Constants.STORE_TYPE_7CATALOG_ITEM, oh.getBarcode());//
				         oh.setPensItem(Utils.isNull(pensItem));
				         
				         s.setOnhandSummary(oh);
				         
				         /** Case Onhand Qty == 0 No validate **/
				         BigDecimal onhandQtyValid = new BigDecimal(onhandQty+"."+onhandQty2Digit);
				         if(onhandQtyValid.compareTo(bigZero) != 0 ){
				         
					         /** Validate Barcode **/
					         MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_7CATALOG_ITEM, barcode);
					         String itemCodeValid = mb!=null?mb.getItem():"";
					         groupItem = mb!=null?mb.getGroup():"";
					         
					        // logger.debug("itemCodeValid["+itemCodeValid+"]");
					         if(Utils.isNull(itemCodeValid).equals("")){
					        	 Message m = new Message();
					        	 m.setMessage("ไม่พบข้อมูล Barcode");
					        	 errorMsgList.add(m);
					        	 lineError = true;
					         }
					         
					         /** Validate ItemCodeOracle VS ItemCodeWacoal IS Equals**/
					         /*if( !Utils.isNull(itemCodeValid).equals("")){
						         if( !Utils.isNull(itemCodeValid).equals(Utils.isNull(item))){
						        	 Message m = new Message();
						        	 m.setMessage("ข้อมูล Item Wacoal["+Utils.isNull(item)+"] กับ Item Oracle["+Utils.isNull(itemCodeValid)+"] ไม่ตรงกัน");
						        	 errorMsgList.add(m);
						        	 lineError = true;
						         }
					         }*/
					         
					         /** Validate WholePriceBF **/
					        /* BigDecimal wholePriceBFOracle = importDAO.getWholePriceBFFromOracle(conn, itemCodeValid);
					    
					         if(wholePriceBFOracle.compareTo(bigZero) ==0){//== 0
					        	 //Not found Add Fail Msg
					        	 Message m = new Message();
						         m.setMessage("ไม่พบข้อมูลราคาขายส่งนี้ในระบบ Oracle");
						         errorMsgList.add(m);
						         lineError = true;
					         }else if( wholePriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 BigDecimal wholePriceBFWacoal =  new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit);
					        	
					        	 if( wholePriceBFWacoal.compareTo(bigZero) != 0 &&  wholePriceBFOracle.compareTo(wholePriceBFWacoal) != 0){
					        		 Message m = new Message();
							         m.setMessage("ราคาขายส่ง  Wacoal["+wholePriceBFWacoal+"] กับ  Oracle["+wholePriceBFOracle+"] ไม่ตรงกัน");
							         errorMsgList.add(m);
							         lineError = true;
					        	 }
					         }*/
					         
					         /** Validate RetailPriceBF **/
					         //BigDecimal retailPriceBFOracle = importDAO.getRetailPriceBFFromOracle(conn, itemCodeValid);
					    
					         //if(retailPriceBFOracle.compareTo(bigZero) ==0){ // =0
					        	 //Not found Add Fail Msg
					        	 //Message m = new Message();
						         //m.setMessage("ไม่พบข้อมูลราคาขายปลีกนี้ในระบบ Oracle");
						         //errorMsgList.add(m);
						         //lineError = true;
					        // }else if( retailPriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 //BigDecimal reatilPriceBFWacoal =  new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit);
					        	 //if(reatilPriceBFWacoal.compareTo(bigZero) != 0 && retailPriceBFOracle.compareTo(reatilPriceBFWacoal) != 0){
					        		 //Message m = new Message();
							         //m.setMessage("ราคาขายปลีก  Wacoal["+reatilPriceBFWacoal+"] กับ Oracle["+retailPriceBFOracle+"] ไม่ตรงกัน");
							         //errorMsgList.add(m);
							         //lineError = true;
					        	 //}
					         //}
					         
				         }else{//if onhand qty != 0
				        	 //Case QTY =0  no validate item
				        	 MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_7CATALOG_ITEM, barcode);
					         groupItem = mb!=null?mb.getGroup():"";
					         
				         }

				         message = "";
				         
				         if(lineError){
				        	 importError = true;
				        	 s.setErrorMsgList(errorMsgList);
				        	 errorList.add(s);

				        	 /** set Message TO Save **/
				        	 for(int e=0;e<errorMsgList.size();e++){
				        		 Message me = (Message)errorMsgList.get(e);
				        		 message += me.getMessage()+",";
				        	 }
				         }else{
				        	 Message m = new Message();
				        	 m.setMessage("Success");
				        	 errorMsgList.add(m);
				        	 successList.add(s);
				         }
				         
				         //Check Status and Message
				         status = lineError?"ERROR":"SUCCESS";
				         
				         
				         //Line No Error
				         if(lineError==false || "NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
					         ps.setDate(1, new java.sql.Date(fileNameAsOfDate.getTime()));
					         ps.setString(2, Utils.isNull(materialMaster));
					         ps.setString(3, Utils.isNull(barcode));
					         ps.setBigDecimal(4, new BigDecimal(onhandQty+"."+onhandQty2Digit));
					         ps.setBigDecimal(5, new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit));
					         ps.setBigDecimal(6, new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit));
					         ps.setString(7, Utils.isNull(item));
					         ps.setString(8, Utils.isNull(itemDesc));
					         ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
					         ps.setString(10, user.getUserName());
					         ps.setString(11, fileName);
					         ps.setString(12, groupItem);
					         ps.setString(13, status);
					         ps.setString(14, message);
					         ps.setString(15, pensItem);
					         
					         allCount++;
					         
					         ps.executeUpdate();
				         }
				         
					  }catch(Exception e){
					     failCount++;
					     e.printStackTrace();
					     importError=true;
					  }
		         }//if
		      }//while

		      
		     importForm.setSummaryErrorList(errorList);
			 importForm.setSummarySuccessList(successList);
			  
			 importForm.setTotalSize(errorList.size()+successList.size());
			 importForm.setSummaryWacoalListErrorSize(errorList!=null?errorList.size():0);
			 importForm.setSummaryWacoalListSuccessSize(successList!=null?successList.size():0);
		     
			 if("NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
				 request.setAttribute("Message","Upload ไฟล์ "+fileName+"สำเร็จ โดย นำข้อมูลที่ Error เข้าทั้งหมด"); 
				 conn.commit();
			 }else{
				 if(importError){
					  request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
					  logger.debug("Transaction Rollback");
					  
					  if( "".equalsIgnoreCase(noCheckError))
				         conn.rollback();
					}else{
					   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
					   logger.debug("Transaction Commit");
					   conn.commit();
					}
				 }
			}//if
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
		}finally{
		    	// dispose all the resources after using them.
			      if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
		    }
		return mapping.findForward("success");
	}
	
	public ActionForward importOShoppingFromWacoal(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("importOShoppingFromWacoal :Text");
		ImportForm importForm = (ImportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
		try {
			
			String noCheckError = Utils.isNull(request.getParameter("NO_CHECK_ERROR"));
			 
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
			FormFile dataFile = importForm.getDataFile();
			
			/** Step 1 Validate Name Date of File ,Must more than old import  **/
			//O-SHOPPING_OH_20130923.txt
			
			String fileName = dataFile.getFileName();
			logger.debug("dateSubStr:"+fileName.substring(14,22));
			
			java.util.Date fileNameAsOfDate = Utils.parse(fileName.substring(14,22),Utils.YYYY_MM_DD_WITHOUT_SLASH);
			// Get LastFileNameImport 
			String lastFileNameImport = importDAO.getLastFileNameImportOShopping(conn);
			
			if( !Utils.isNull(lastFileNameImport).equals("")){
				java.util.Date lastFileNameAsOfDate = Utils.parse(lastFileNameImport.substring(13,21),Utils.YYYY_MM_DD_WITHOUT_SLASH);
				logger.debug("lastFileNameAsOfDate:"+lastFileNameAsOfDate);
				
				if(fileNameAsOfDate.before(lastFileNameAsOfDate)){ //dateImport < lastDateImport
					request.setAttribute("Message","ชื่อไฟล์ที่  Upload ["+fileName+"] วันที่น้อยกว่า  ชื่อไฟล์วันที่ล่าสุดที่  Upload ["+lastFileNameImport+"] ");
					return mapping.findForward("success");
				}
			}
			
			if (dataFile != null ) {
			  
			  logger.debug("contentType: " + dataFile.getContentType());
			  logger.debug("fileName: " + dataFile.getFileName());

			  
			  /** Delete All before Import **/
			  psDelete = conn.prepareStatement("delete from PENSBME_ONHAND_BME_OSHOPPING");
			  psDelete.executeUpdate();
	
			  StringBuffer sql = new StringBuffer("");
			  sql.append("INSERT INTO PENSBME_ONHAND_BME_OSHOPPING( \n");
			  sql.append(" AS_OF_DATE, MATERIAL_MASTER, BARCODE, \n");
			  sql.append(" ONHAND_QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,  \n");
			  sql.append(" ITEM, ITEM_DESC, CREATE_DATE, CREATE_USER,FILE_NAME,GROUP_ITEM,STATUS,MESSAGE,PENS_ITEM) \n");
			  sql.append(" VALUES( ?,?,?, ?,?,? ,?,?,?,? ,?,?,?,?,?)");
			  
			  ps = conn.prepareStatement(sql.toString());
			  
			  String dataFileStr = FileUtil.readFile2(dataFile.getInputStream(), "tis-620");
			  //replace '?' in index =0
			 // dataFileStr = dataFileStr.substring(1,dataFileStr.length());
			  logger.debug("dataFileStr:"+dataFileStr);
			  
	    	  String[] dataStrArray = dataFileStr.split("\n");

		      int start = 0;
		      int end = 0;
		      String lines = "";
		      String materialMaster = "";
		      String barcode = "";
		      String onhandQty = "";
		      String onhandQty2Digit = "";
		      String wholePriceBF = "";
		      String wholePriceBF2Digit = "";
		      String retailPriceBF = "";
		      String retailPriceBF2Digit = "";
		      String item  ="";
		      String itemDesc= "";
		      String groupItem = "";
		      String status = "";
		      String message ="";
		      String pensItem = "";
		      
		     for(int i=0;i<dataStrArray.length;i++){
		    	 lineError = false;
		    	 errorMsgList = new ArrayList<Message>();
		    	 lines = dataStrArray[i];
		    	 logger.debug("lines:"+lines);
		         if(!Utils.isNull(lines).equals("")){
		        	try{
				         // 1
			        	 start = 0;
				         end = 18;
				         materialMaster = lines.substring(start,end);
				         //logger.debug("materialMaster["+materialMaster+"]length["+materialMaster.length()+"]");
				         
				         //2
				         start = end;
				         end = start+13;
				         barcode = lines.substring(start,end);
				        // logger.debug("barcode["+barcode+"]length["+barcode.length()+"]");

				         //3
				         start = end;
				         end = start+11;
				         onhandQty = lines.substring(start,end-2);
				         onhandQty2Digit = lines.substring(end-2,end);
				         //logger.debug("onhandQty["+onhandQty+"]onhandQty2Digit["+onhandQty2Digit+"]length["+onhandQty.length()+"]");
				         
				         //4
				         start = end;
				         end = start+11;
				         wholePriceBF = lines.substring(start,end-2);
				         wholePriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("wholePriceBF["+wholePriceBF+"]wholePriceBF2Digit["+wholePriceBF2Digit+"]length["+wholePriceBF.length()+"]");
				         
				         //5
				         start = end;
				         end = start+11;
				         retailPriceBF = lines.substring(start,end-2);
				         retailPriceBF2Digit = lines.substring(end-2,end);
				         //logger.debug("retailPriceBF["+retailPriceBF+"]retailPriceBF2Digit["+retailPriceBF2Digit+"]length["+retailPriceBF.length()+"]");
				         
				         //6
				         start = end;
				         end = start+35;
				         item = lines.substring(start,end);
				        // logger.debug("item["+item+"]length["+item.length()+"]");
				         
				         //7
				         start = end;
				         end = start+40;
				         itemDesc = lines.substring(start,end);
				         //logger.debug("itemDesc["+itemDesc+"]length["+itemDesc.length()+"]");
				         
				         /** Prepare Message To Display **/
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         OnhandSummary oh = new OnhandSummary();
				         oh.setAsOfDate(Utils.stringValue(fileNameAsOfDate, Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				         oh.setItem(Utils.isNull(item));
				         oh.setItemDesc(Utils.isNull(itemDesc));
				         oh.setOnhandQty(Utils.isNull(onhandQty+onhandQty2Digit));
				         oh.setWholePriceBF(Utils.isNull(wholePriceBF+wholePriceBF2Digit));
				         oh.setRetailPriceBF(Utils.isNull(retailPriceBF+retailPriceBF2Digit));
				         oh.setBarcode(Utils.isNull(barcode));
				         oh.setMaterialMaster(Utils.isNull(materialMaster));
				         //Find pens_item **/
				         pensItem = importDAO.getItemByBarcode(conn,Constants.STORE_TYPE_OSHOPPING_ITEM, oh.getBarcode());//
				         oh.setPensItem(Utils.isNull(pensItem));
				         
				         s.setOnhandSummary(oh);
				         
				         /** Case Onhand Qty == 0 No validate **/
				         BigDecimal onhandQtyValid = new BigDecimal(onhandQty+"."+onhandQty2Digit);
				         if(onhandQtyValid.compareTo(bigZero) != 0 ){
				         
					         /** Validate Barcode **/
					         MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_OSHOPPING_ITEM, barcode);
					         String itemCodeValid = mb!=null?mb.getItem():"";
					         groupItem = mb!=null?mb.getGroup():"";
					         
					        // logger.debug("itemCodeValid["+itemCodeValid+"]");
					         if(Utils.isNull(itemCodeValid).equals("")){
					        	 Message m = new Message();
					        	 m.setMessage("ไม่พบข้อมูล Barcode");
					        	 errorMsgList.add(m);
					        	 lineError = true;
					         }
					         
					         /** Validate ItemCodeOracle VS ItemCodeWacoal IS Equals**/
					         /*if( !Utils.isNull(itemCodeValid).equals("")){
						         if( !Utils.isNull(itemCodeValid).equals(Utils.isNull(item))){
						        	 Message m = new Message();
						        	 m.setMessage("ข้อมูล Item Wacoal["+Utils.isNull(item)+"] กับ Item Oracle["+Utils.isNull(itemCodeValid)+"] ไม่ตรงกัน");
						        	 errorMsgList.add(m);
						        	 lineError = true;
						         }
					         }*/
					         
					         /** Validate WholePriceBF **/
					        /* BigDecimal wholePriceBFOracle = importDAO.getWholePriceBFFromOracle(conn, itemCodeValid);
					    
					         if(wholePriceBFOracle.compareTo(bigZero) ==0){//== 0
					        	 //Not found Add Fail Msg
					        	 Message m = new Message();
						         m.setMessage("ไม่พบข้อมูลราคาขายส่งนี้ในระบบ Oracle");
						         errorMsgList.add(m);
						         lineError = true;
					         }else if( wholePriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 BigDecimal wholePriceBFWacoal =  new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit);
					        	
					        	 if( wholePriceBFWacoal.compareTo(bigZero) != 0 &&  wholePriceBFOracle.compareTo(wholePriceBFWacoal) != 0){
					        		 Message m = new Message();
							         m.setMessage("ราคาขายส่ง  Wacoal["+wholePriceBFWacoal+"] กับ  Oracle["+wholePriceBFOracle+"] ไม่ตรงกัน");
							         errorMsgList.add(m);
							         lineError = true;
					        	 }
					         }*/
					         
					         /** Validate RetailPriceBF **/
					         //BigDecimal retailPriceBFOracle = importDAO.getRetailPriceBFFromOracle(conn, itemCodeValid);
					    
					         //if(retailPriceBFOracle.compareTo(bigZero) ==0){ // =0
					        	 //Not found Add Fail Msg
					        	 //Message m = new Message();
						         //m.setMessage("ไม่พบข้อมูลราคาขายปลีกนี้ในระบบ Oracle");
						         //errorMsgList.add(m);
						         //lineError = true;
					        // }else if( retailPriceBFOracle.compareTo(bigZero) != 0){
					        	 //Found but no equals
					        	 //BigDecimal reatilPriceBFWacoal =  new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit);
					        	 //if(reatilPriceBFWacoal.compareTo(bigZero) != 0 && retailPriceBFOracle.compareTo(reatilPriceBFWacoal) != 0){
					        		 //Message m = new Message();
							         //m.setMessage("ราคาขายปลีก  Wacoal["+reatilPriceBFWacoal+"] กับ Oracle["+retailPriceBFOracle+"] ไม่ตรงกัน");
							         //errorMsgList.add(m);
							         //lineError = true;
					        	 //}
					         //}
					         
				         }else{//if onhand qty != 0
				        	 //Case QTY =0  no validate item
				        	 MasterBean mb = importDAO.getMasterBeanByBarcode(conn,Constants.STORE_TYPE_OSHOPPING_ITEM, barcode);
					         groupItem = mb!=null?mb.getGroup():"";
					         
				         }

				         message = "";
				         
				         if(lineError){
				        	 importError = true;
				        	 s.setErrorMsgList(errorMsgList);
				        	 errorList.add(s);

				        	 /** set Message TO Save **/
				        	 for(int e=0;e<errorMsgList.size();e++){
				        		 Message me = (Message)errorMsgList.get(e);
				        		 message += me.getMessage()+",";
				        	 }
				         }else{
				        	 Message m = new Message();
				        	 m.setMessage("Success");
				        	 errorMsgList.add(m);
				        	 successList.add(s);
				         }
				         
				         //Check Status and Message
				         status = lineError?"ERROR":"SUCCESS";
				         
				         
				         //Line No Error
				         if(lineError==false || "NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
					         ps.setDate(1, new java.sql.Date(fileNameAsOfDate.getTime()));
					         ps.setString(2, Utils.isNull(materialMaster));
					         ps.setString(3, Utils.isNull(barcode));
					         ps.setBigDecimal(4, new BigDecimal(onhandQty+"."+onhandQty2Digit));
					         ps.setBigDecimal(5, new BigDecimal(wholePriceBF+"."+wholePriceBF2Digit));
					         ps.setBigDecimal(6, new BigDecimal(retailPriceBF+"."+retailPriceBF2Digit));
					         ps.setString(7, Utils.isNull(item));
					         ps.setString(8, Utils.isNull(itemDesc));
					         ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
					         ps.setString(10, user.getUserName());
					         ps.setString(11, fileName);
					         ps.setString(12, groupItem);
					         ps.setString(13, status);
					         ps.setString(14, message);
					         ps.setString(15, pensItem);
					         
					         allCount++;
					         
					         ps.executeUpdate();
				         }
				         
					  }catch(Exception e){
					     failCount++;
					     e.printStackTrace();
					     importError=true;
					  }
		         }//if
		      }//while

		      
		     importForm.setSummaryErrorList(errorList);
			 importForm.setSummarySuccessList(successList);
			  
			 importForm.setTotalSize(errorList.size()+successList.size());
			 importForm.setShoppingListErrorSize(errorList!=null?errorList.size():0);
			 importForm.setShoppingListSuccessSize(successList!=null?successList.size():0);
		     
			 if("NO_CHECK_ERROR".equalsIgnoreCase(noCheckError)){
				 request.setAttribute("Message","Upload ไฟล์ "+fileName+"สำเร็จ โดย นำข้อมูลที่ Error เข้าทั้งหมด"); 
				 conn.commit();
			 }else{
				 if(importError){
					  request.setAttribute("Message","Upload ไฟล์ "+fileName+" ไม่สำเร็จ กรุณาแก้ไขข้อมูลแล้วทำการ Import ใหม่");
					  logger.debug("Transaction Rollback");
					  
					  if( "".equalsIgnoreCase(noCheckError))
				         conn.rollback();
					}else{
					   request.setAttribute("Message","Upload ไฟล์ "+fileName+" สำเร็จ");
					   logger.debug("Transaction Commit");
					   conn.commit();
					}
				 }
			}//if
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message","ข้อมูลไฟล์ไม่ถูกต้อง:"+e.toString());
		}finally{
		    	// dispose all the resources after using them.
			      if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
		    }
		return mapping.findForward("success");
	}
	
	public boolean importFileScanBarcode(User user,List<FTPFileBean> ftpFileBeanList)  throws Exception {
		logger.debug("importFileScanBarcode :Text");
		int i=0;
		int l=0;
		int lineId = 0;
		int allCount = 0;
		int successCount = 0;
		int failCount = 0;
	    Connection conn = null;
	    PreparedStatement psH = null;
	    PreparedStatement psL = null;
	
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	    List<Message> errorMsgList = new ArrayList<Message>();
	    boolean importError = false;
	    boolean lineError = false;
	    BigDecimal bigZero = new BigDecimal("0");
		try {
			logger.debug("Start insert scan barcode DB");
			conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

			 StringBuffer sql = new StringBuffer("");
			 sql.append("INSERT INTO pensbme_barcode_scan(doc_no, Doc_date, Cust_Group, Cust_no, Remark, Status, FILE_NAME,Create_date, Create_user)");
			 sql.append("VALUES(?,?,?, ?,?,?, ?,?,?)");
			  
			 psH = conn.prepareStatement(sql.toString());
			 
			 StringBuffer sqlLine = new StringBuffer("");
			 sqlLine.append("INSERT INTO pensbme_barcode_scan_item(doc_no, LINE_ID, Barcode, Material_master, Group_code, Pens_item, Create_date, Create_user)");
			 sqlLine.append("VALUES( ?,?,?, ?,?,? ,?,?)");
			  
			 psL = conn.prepareStatement(sqlLine.toString());
			 
			 if(ftpFileBeanList != null && ftpFileBeanList.size() >0){
				 for(i=0;i<ftpFileBeanList.size();i++){
					FTPFileBean fileBean = ftpFileBeanList.get(i);
					String[] dataLineTextArr = fileBean.getDataLineText();
					for(l=0;l<dataLineTextArr.length;l++){
						logger.debug("dataLineTextArr[l]:"+dataLineTextArr[l]);
						
						String[] lineStrArrPipe =  dataLineTextArr[l].split("\\|");
						
						if(lineStrArrPipe[0].startsWith("H")){
							lineId = 0;//reset lineId by Head
							//H020047-99-201504-001|02042015|020047|020047-99||C|20150403095903-admin-barcode.txt
							logger.debug("0:"+lineStrArrPipe[0].substring(1,lineStrArrPipe[0].length()));
							logger.debug("1:"+lineStrArrPipe[1]);
							logger.debug("2:"+lineStrArrPipe[2]);
							logger.debug("3:"+lineStrArrPipe[3]);
							logger.debug("4:"+lineStrArrPipe[4]);
							logger.debug("5:"+lineStrArrPipe[5]);
							logger.debug("6:"+lineStrArrPipe[6]);
							
							psH.setString(1,lineStrArrPipe[0].substring(1,lineStrArrPipe[0].length()));//docNo
							
							Date docDate = Utils.parse(lineStrArrPipe[1], Utils.DD_MM_YYYY_WITHOUT_SLASH);
							psH.setDate(2, new java.sql.Date(docDate.getTime()));
							psH.setString(3,lineStrArrPipe[2]);//custGroup
							psH.setString(4,lineStrArrPipe[3]);//custNo
							psH.setString(5,lineStrArrPipe[4]);//remark
							psH.setString(6,lineStrArrPipe[5]);//status
							psH.setString(7,lineStrArrPipe[6]);//fileName
							psH.setDate(8, new java.sql.Date(new Date().getTime()));//createDate
							psH.setString(9, user.getUserName());//createUser
							
							psH.execute();
						}else{
							lineId++;
							//D020047-99-201504-001|8850009385309|ME1106A4VI|ME1106|835057
                            psL.setString(1,lineStrArrPipe[0].substring(1,lineStrArrPipe[0].length()));//DocNo
                            psL.setInt(2,lineId);//Barcode
                            psL.setString(3,lineStrArrPipe[1]);//Barcode
                            psL.setString(4,lineStrArrPipe[2]);//Material
                            psL.setString(5,lineStrArrPipe[3]);//GroupCode
                            psL.setString(6,lineStrArrPipe[4]);//PENSITEM
                            psL.setDate(7, new java.sql.Date(new Date().getTime()));
                            psL.setString(8, user.getUserName());//createUser
							
                            psL.execute();
						}
					}
				 }
			 }
			 
		    conn.commit();
		    
		    logger.debug("End insert Scan barcode DB");
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			
		}finally{
		    	// dispose all the resources after using them.
			      if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(psH != null){
			    	  psH.close();psH=null;
			      }
			      if(psL != null){
			    	  psL.close();psL=null;
				  }
		    }
		return true;
	}
	
	/** Export ReturnWacoal **/
	public ActionForward exportReturnWacoal(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		try{
			User user = (User)request.getSession().getAttribute("User");
			/*String data = ExportReturnWacoal.exportExcel().toString();
			logger.debug("data:"+data);
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=data.xls");
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(data);
		    w.flush();
		    w.close();
	
		    out.flush();
		    out.close();*/
			
           XSSFWorkbook xssfWorkbookDta = ExportReturnWacoal.genExportToExcel(user);
			
			response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");
			response.setContentType("application/vnd.ms-excel; charset=windows-874");
			java.io.OutputStream out = response.getOutputStream();

			xssfWorkbookDta.write(out);

		    out.flush();
		    out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapping.findForward("success");
	}
	
}
