package com.isecinc.pens.imports.process;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import util.DBCPConnectionProvider;
import util.UploadXLSUtil;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.imports.ImportForm;
import com.isecinc.pens.web.popup.PopupForm;

public class ImportLoadStockInitProcess {
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static ActionForward importProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response,String importType) {
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
	    String tableInitName ="";
	    String tableInitOnhandName = "";
	    String storeCodeValidate = "";
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 0; // row of begin data
		int maxColumnNo = 3; // max column of data per row
		
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		
		Row row = null;
		Cell cell = null;
		String salesDate = "";
		String storeNo = "";
		String storeName = "";
		String qty = "";
		String groupCode ="";
		String pensItem = "";
		String barcode = "";
		String mat = "";
		
		int index = 0;
        int no = 0;
        double totalQty = 0;
		try {
            if("Lotus".equalsIgnoreCase(importType)){
            	tableInitName = "PENSBME_LOTUS_INIT_STK";
            	tableInitOnhandName = "PENSBME_LOTUS_ONHAND_INIT_STK";
            	storeCodeValidate = "020047";
            }else  if("BigC".equalsIgnoreCase(importType)){
            	tableInitName = "PENSBME_BIGC_INIT_STK";
            	tableInitOnhandName = "PENSBME_BIGC_ONHAND_INIT_STK";
            	storeCodeValidate = "020049";
            }else  if("MTT".equalsIgnoreCase(importType)){
            	tableInitName = "PENSBME_MTT_INIT_STK";
            	tableInitOnhandName = "PENSBME_MTT_ONHAND_INIT_STK";
            	storeCodeValidate = "100001";
            }
           
			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());
				
				conn = DBConnection.getInstance().getConnection();
				conn.setAutoCommit(false);
				
				//Insert Head INIT
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO "+tableInitName+" \n");
				sql.append(" (CUST_NO, COUNT_STK_DATE, CREATE_DATE, CREATE_USER)  \n");//1-4
				sql.append(" VALUES(?,?,?,?)");
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
				
				rowNo = 0;
				row = sheet.getRow(rowNo);
				cell = row.getCell((short) 1);
				salesDate = Utils.isNull(xslUtils.getCellValue(1, cell));
				java.util.Date salesDateObj = Utils.parse(salesDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				rowNo = 1;
				row = sheet.getRow(rowNo);
				cell = row.getCell((short) 1);
				storeNo = Utils.isNull(xslUtils.getCellValue(1, cell));
				cell = row.getCell((short) 2);
				storeName = Utils.isNull(xslUtils.getCellValue(2, cell));
				
				logger.debug("StoreNo:"+storeNo+",countDate:"+salesDate);
				
				/** cehck FileName duplicate **/
				/*boolean dup = importDAO.importStockInitFileNameIsDuplicate(conn, tableInitName,salesDate);
				if(dup){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+"ได้เนื่องจากมีการ  Upload ไปแล้ว");
					return mapping.findForward("success");
				}*/
				 
	            /** Validate Init by custNo,Count_str_date **/
				if( isInitExist(tableInitName,storeNo,salesDateObj) ){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+"ได้เนื่องจากมีการ  ข้อมูลวันที่ :"+salesDate+" ร้านค้า:"+storeNo+"-"+storeName+" ถุกสร้างไปแล้ว");
					return mapping.findForward("success");
				}
				
				/** validate StoreNo **/
				if( !storeNo.startsWith(storeCodeValidate)){
					request.setAttribute("Message","ไม่สามารถ Upload ไฟล์ "+fileName+"ได้เนื่องจากมีการ  ข้อมูลร้านค้าไม่ถูกต้อง");
					return mapping.findForward("success");
				}
				
				ps.setString(1, storeNo);
				ps.setDate(2, new java.sql.Date(salesDateObj.getTime()));
				ps.setDate(3,new java.sql.Date(new java.util.Date().getTime()));
				ps.setString(4,user.getUserName());
				ps.execute();
				
				rowNo = rowNo+2;
				
                //Insert Detaik INIT
				sql = new StringBuffer("");
				sql.append(" INSERT INTO "+tableInitOnhandName+" \n");
				sql.append("(CUST_NO, COUNT_STK_DATE, GROUP_CODE, MATERIAL_MASTER, QTY,PENS_ITEM,BARCODE, CREATE_DATE, CREATE_USER)\n");
				sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) \n"); //1-9
				ps = conn.prepareStatement(sql.toString());
				  
			
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
				for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
					row = sheet.getRow(i);
					
					/** Check Row is null **/
					Cell cellCheck = row.getCell((short) 1);
					Object cellCheckValue = xslUtils.getCellValue(1, cellCheck);
					logger.debug("cellCheckValue["+cellCheckValue+"]");
					
					if(cellCheckValue == null ){
						break;
					}
					
					//initial
					index = 1;
					groupCode = "";
					pensItem = "";
					barcode = "";
					mat = "";
					qty = "";
					
					ps.setString(index++, storeNo);//CUST_NO 1
					ps.setDate(index++, new java.sql.Date(salesDateObj.getTime())); //COUNT_STK_DATE 2
					
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
				
						//GROUP_CODE
						if(colNo==0){
							
						}else if(colNo==1){
						   //Mat
							mat = Utils.isNull(cellValue);
                            groupCode  = mat.substring(0,6);
							
							ps.setString(index++, groupCode);//GROUP_CODE 3
						    ps.setString(index++, mat);//MAT 4
						}else if(colNo==2){
						  //qty
						   qty = Utils.decimalFormat(Utils.isDoubleNull(cellValue),Utils.format_current_no_disgit);
						   ps.setDouble(index++,Utils.isDoubleNull(cellValue));//QTY 5
						   
						   totalQty += Utils.isDoubleNull(cellValue);
						}
					}//for column
					
					// Find PensItem by MaterialMaster
					Barcode master = GeneralDAO.searchProductByMat(mat);
					if(master != null){
						 pensItem = master.getPensItem();
						 barcode = master.getBarcode();
						 
						 ps.setString(index++, pensItem);//PENS_ITEM 6
						 ps.setString(index++, barcode);//BARCODE 7
					}else{
						pensItem = "";
						barcode = "";
						
						ps.setString(index++, "null");
						ps.setString(index++, "null");
					}
					
					//CREATE_DATE
					 ps.setTimestamp(index++, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(index++, user.getUserName());
			         
			         logger.debug("index:"+index);
 
			         if( !Utils.isNull(pensItem).equals("")){
			        	   //Add Success Msg
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setPensItem(pensItem);
				         s.setMaterialMaster(mat);
				         s.setBarcode(barcode);
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
				         s.setPensItem(pensItem);
				         s.setMaterialMaster(mat);
				         s.setBarcode(barcode);
				         s.setGroupCode(groupCode);
				         s.setQty(qty);
				         
				         String ms = "";
				         if(Utils.isNull(pensItem).equals("")){
				        	 ms +="ไม่พบข้อมูล Pens Item"; 
				         }
				         s.setMessage(ms);
				         errorMap.put(i+"", s);
			         }  
			         
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
				  importForm.setLoadStockInitListErrorSize(errorList!=null?errorList.size():0);
				  importForm.setLoadStockInitListSuccessSize(successList!=null?successList.size():0);
				  
				  importForm.setTotalQty( Utils.decimalFormat(totalQty,Utils.format_current_no_disgit));
				  importForm.setCountDate(salesDate);
				  importForm.setStoreCode(storeNo);
				  importForm.setStoreName(storeName);
			         
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
			   importForm.setLoadStockInitListErrorSize(errorList!=null?errorList.size():0);
			   importForm.setLoadStockInitListSuccessSize(successList!=null?successList.size():0);
			   importForm.setTotalQty( Utils.decimalFormat(totalQty,Utils.format_current_no_disgit));
			   importForm.setCountDate(salesDate);
			   importForm.setStoreCode(storeNo);
			   importForm.setStoreName(storeName);
				  
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
	
	  public static boolean isInitExist(String tableName,String custNo,java.util.Date countStkDate) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			boolean exist = false;
			try {
				sql.append(" select cust_no,count_stk_date FROM "+tableName +" WHERE 1=1 \n");
				sql.append(" and cust_no = '"+custNo+"' \n");
				sql.append(" and count_stk_date =? \n");
				
				logger.debug("sql:"+sql);
	
				conn = new DBCPConnectionProvider().getConnection(conn);
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setDate(1, new java.sql.Date(countStkDate.getTime())); //COUNT_STK_DATE 2
				rst = ps.executeQuery();
				
				if (rst.next()) {
					exist = true;
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					conn.close();
				} catch (Exception e) {}
			}
			return exist;
		}
}
