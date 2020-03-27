package com.isecinc.pens.web.importall.page;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.importall.ImportAllBean;
import com.isecinc.pens.web.importall.ImportAllForm;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.SQLHelper;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;
import com.pens.util.excel.ExcelHelper;
import com.pens.util.importexcel.ImportAllExcel;
import com.pens.util.importexcel.ImportManualExcel;
import com.pens.util.seq.SequenceProcessAll;

public class ImportMasterOrderREPAction {
	
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "importAll";
		ImportAllForm importAllForm = (ImportAllForm) form;
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().removeAttribute("ERROR_LIST");
				 request.getSession().removeAttribute("SUCCESS_LIST");
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 request.getSession().removeAttribute("TOPIC_NAME");
				 request.getSession().removeAttribute("VIEW_DATA");    
				 importAllForm.setResults(null);
				 importAllForm.setBean(new ImportAllBean());
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}
	
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm importAllForm = (ImportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		boolean excel = false;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			String reportType = Utils.isNull(request.getParameter("reportType"));
			if("rep_config".equalsIgnoreCase(reportType)){
				request.getSession().setAttribute("TOPIC_NAME", "ข้อมูล Replenishment Config");
			}else if("rep_minmax".equalsIgnoreCase(reportType)){
				request.getSession().setAttribute("TOPIC_NAME", "ข้อมูล Replenishment Min-Max");
			}else if("rep_priority".equalsIgnoreCase(reportType)){
				request.getSession().setAttribute("TOPIC_NAME", "ข้อมูล Replenishment Priority");
			}
			request.getSession().setAttribute("VIEW_DATA",searchRepConfig(conn,excel,reportType,importAllForm.getBean()));
			
	   }catch(Exception e){
		   request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		   logger.error(e.getMessage(),e);
	   }finally{
		   if(conn != null){
			   conn.close();conn=null;
		   }
	   }
	   return mapping.findForward("importAll");
	}
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm ImportAllForm = (ImportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean excel = false;
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			String reportType = Utils.isNull(request.getParameter("reportType"));
			
			StringBuffer htmlTable = searchRepConfig(conn,excel,reportType,ImportAllForm.getBean());
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=data.xls");
			response.setContentType("application/vnd.ms-excel");
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();
		
		    out.flush();
		    out.close();
			
	   }catch(Exception e){
		   request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		   logger.error(e.getMessage(),e);
	   }finally{
		   if(conn != null){
			   conn.close();conn=null;
		   }
	   }
	   return mapping.findForward("importAll");
	}
	
	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ImportAllForm ImportAllForm = (ImportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try{
			request.getSession().removeAttribute("VIEW_DATA");
			String refCode = Utils.isNull(request.getParameter("refCode"));
			logger.debug("importExcel RefCode:"+refCode);
			
			//1 MST_REFERENCE
			if(refCode.equalsIgnoreCase("replenishment_minmax")){
			  if(ImportAllForm.getDataFile() != null && !Utils.isNull(ImportAllForm.getDataFile().getFileName()).equals("") ){
			     ImportAllForm = importExcelRepMinMax(ImportAllForm, user, request);
			  }
			}
			
			//2 MST_REFERENCE
			if(refCode.equalsIgnoreCase("replenishment_priority")){
			  if(ImportAllForm.getDataFile2() != null && !Utils.isNull(ImportAllForm.getDataFile2().getFileName()).equals("") ){
			     ImportAllForm = importExcelRepPriority(ImportAllForm, user, request);
			  }
			}
			//3 PENSBI.BME_CONFIG_REP  By CustGroup
			if(refCode.equalsIgnoreCase("ref_config")){
			   if(ImportAllForm.getDataFile3() != null && !Utils.isNull(ImportAllForm.getDataFile3().getFileName()).equals("") ){
				  ImportAllForm = importExcelRepConfig(ImportAllForm, user, request);
			   }
			}
			//request.setAttribute("Message", "Import Success");
	   }catch(Exception e){
		   request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		   logger.error(e.getMessage(),e);
	   }
	   return mapping.findForward("importAll");
	}

	public ImportAllForm importExcelRepConfig(ImportAllForm ImportAllForm,User user,HttpServletRequest request)  throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql  =new StringBuffer();
		int status = Constants.STATUS_FAIL;
		int dataCount=0,successCount =0,failCount =0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		boolean passValid = false;String lineMsg = "";
		List<String> failList = new ArrayList<String>();
		List<String> successList = new ArrayList<String>();
		int colNo = 0 ;int r = 0;
		String errorMsg ="";
		Cell cellCheck = null;
		boolean hardBreak = false;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    String columnCheck1 ="",columnCheck2 ="";
	    String storeCode  = "",materialMaster = "";
		try{
			//clear ERROR_LIST Session
			request.getSession().removeAttribute("ERROR_LIST");
			request.getSession().removeAttribute("SUCCESS_LIST");
			
			conn = DBConnection.getInstance().getConnectionApps();
	    	conn.setAutoCommit(false);
	    	 
			//Get Parameter Value
			FormFile dataFile = ImportAllForm.getDataFile3();
			String custGroup = ImportAllForm.getBean().getCustGroup();
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			logger.debug("fileType:"+fileType +",custGroup:"+custGroup);
			
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
			
			//Check Column to Check
			if( sheet.getRow(0) !=null){
			   cellCheck =  sheet.getRow(0).getCell((short) 0);
			   columnCheck1 = Utils.isNull(xslUtils.getCellValue(0, cellCheck));
			   
			   cellCheck =  sheet.getRow(0).getCell((short) 1);
			   columnCheck2 = Utils.isNull(xslUtils.getCellValue(1, cellCheck));
			}
			logger.debug("columnCheck1:"+columnCheck1+",columnCheck2:"+columnCheck2);
			if(!columnCheck1.equalsIgnoreCase("รหัสร้านค้า") || !columnCheck2.equalsIgnoreCase("MaterialMaster")){
				logger.debug("Error");
	    		 //error
	    		 request.setAttribute("Message", "ข้อมูลไฟล์ ไม่ตรง Format ที่กำหนดไว้");
	    		 return ImportAllForm;
	    	 }
			/*if(true){
				throw new Exception("Test");
			}*/
            //delete prev all data
			SQLHelper.excUpdate(conn, "delete from PENSBI.BME_CONFIG_REP where store_code like '"+custGroup+"%'");
			
			//prepare insert 
			sql = new StringBuffer();
			sql.append("INSERT INTO PENSBI.BME_CONFIG_REP \n");
			sql.append("(STORE_CODE, MATERIAL_MASTER,CREATE_DATE, CREATE_USER) VALUES(?,?,?,? ) \n");
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
				storeCode = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				materialMaster =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				
				//add Head Table Display
				if(dataCount==1){
				   lineMsg = "Line No Excel|รหัสร้านค้า|Material Master|Status|Message";
				   failList.add(lineMsg);
				   successList.add(lineMsg);
				   lineMsg = "";
				}
				
				//Add Detail LineMsg
				lineMsg = (r+1)+"|"+storeCode+"|"+materialMaster;
				
				//step validate 
				//0:custGroup in screen
				if(storeCode.indexOf(custGroup) ==-1){
					errorMsg = "ข้อมูลกลุ่มร้านค้าที่เลือก ไม่ตรงกับรหัสร้านค้าใน Excel ,";
					passValid = false;
					hardBreak = true;
				}
				
				//1:validate StoreCode
				if( !isStoreCodeValid(conn, storeCode)){
					errorMsg = "ไม่พบข้อมูล รหัสสาขานี้ในระบบ ,";
					passValid = false;
				}
				//2:validate MasterilMaster
				if( !isMaterialMasterValid(conn, materialMaster)){
					errorMsg += "ไม่พบข้อมูล MaterialMaster นี้ในระบบ ";
					passValid = false;
				}
				
				//logger.debug("lineMsg:"+lineMsg);
				
				//is valid
				int index =1;
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					successList.add(lineMsg);
				}else{
					failCount++;
					lineMsg += "|FAIL|"+errorMsg;
					failList.add(lineMsg);
				}

				//add statement
				ps.setString(index++, Utils.isNull(storeCode));
				ps.setString(index++, Utils.isNull(materialMaster));
			    ps.setTimestamp(index++, new java.sql.Timestamp(new Date().getTime()));
			    ps.setString(index++, user.getUserName());
			    ps.addBatch();
			    
			    //Case Hard Break 
				if(hardBreak){
					break;
				}
				
	    	}//for row
		
			logger.debug("result failCount:"+failCount);
			if(failCount ==0){
				status = Constants.STATUS_SUCCESS;
				//no error
				int e[] = ps.executeBatch();
				logger.debug("excute count:"+e.length);
				
				//Execute Insert invoice
				ps.executeBatch();
				request.setAttribute("Message","Import ข้อมูลเรียบร้อยแล้ว");
				request.getSession().setAttribute("SUCCESS_LIST", successList);
				conn.commit();
			}else{
				conn.rollback();
				request.getSession().setAttribute("ERROR_LIST", failList);
				request.setAttribute("Message", "ไม่สามารถ Import ข้อมูลได้  มีข้อมูลไม่ถูกต้อง โปรดตรวจสอบ");
			}
			
			logger.debug("result status:"+status);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			conn.rollback();
		}finally{
			try{
				if(ps != null){
					ps.close();ps = null;
				}
				if(conn != null){
					conn.close();conn = null;
				}
			}catch(Exception ee){}
		}
		return ImportAllForm;	
	}
	
	public ImportAllForm importExcelRepMinMax(ImportAllForm ImportAllForm,User user,HttpServletRequest request)  throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql  =new StringBuffer();
		int status = Constants.STATUS_FAIL;
		int dataCount=0,successCount =0,failCount =0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		boolean passValid = false;String lineMsg = "";
		List<String> failList = new ArrayList<String>();
		List<String> successList = new ArrayList<String>();
		int colNo = 0 ;int r = 0;
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    String columnCheck1 ="",columnCheck2 ="";
	    String groupCode = "",min = "",max="",priority="";
		try{
			//clear ERROR_LIST Session
			request.getSession().removeAttribute("ERROR_LIST");
			request.getSession().removeAttribute("SUCCESS_LIST");
			
			conn = DBConnection.getInstance().getConnectionApps();
	    	conn.setAutoCommit(false);
	    	 
			//Get Parameter Value
			FormFile dataFile = ImportAllForm.getDataFile();
			
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
			
			//Check Column to Check
			if( sheet.getRow(0) !=null){
			   cellCheck =  sheet.getRow(0).getCell((short) 0);
			   columnCheck1 = Utils.isNull(xslUtils.getCellValue(0, cellCheck));
			   
			   cellCheck =  sheet.getRow(0).getCell((short) 1);
			   columnCheck2 = Utils.isNull(xslUtils.getCellValue(1, cellCheck));
			}
			logger.debug("columnCheck1:"+columnCheck1+",columnCheck2:"+columnCheck2);
			if(!columnCheck1.equalsIgnoreCase("GroupCode") || !columnCheck2.equalsIgnoreCase("Min")){
				logger.debug("Error");
	    		 //error
	    		 request.setAttribute("Message", "ข้อมูลไฟล์ ไม่ตรง Format ที่กำหนดไว้");
	    		 return ImportAllForm;
	    	 }
			/*if(true){
				throw new Exception("Test");
			}*/
            //delete prev all data
			 SQLHelper.excUpdate(conn,"delete from pensbi.pensbme_mst_reference where reference_code ='replenishment_minmax'");
			
			//prepare insert 
			sql = new StringBuffer();
			sql.append("INSERT INTO pensbi.pensbme_mst_reference \n");
			sql.append("(ID, REFERENCE_CODE,INTERFACE_VALUE,INTERFACE_DESC,PENS_VALUE,PENS_DESC,PENS_DESC2) ");
			sql.append(" VALUES(?,?,?,?,?,?,? ) \n");
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
				groupCode = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				min =ExcelHelper.getCellValue(cellObjValue,"NUMBER","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 2));
				max =ExcelHelper.getCellValue(cellObjValue,"NUMBER","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 3));
				priority =ExcelHelper.getCellValue(cellObjValue,"NUMBER","");
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				
				//add Head Table Display
				if(dataCount==1){
				   lineMsg = "Line No Excel|Group Code|Min|Max|Priority|Status|Message";
				   failList.add(lineMsg);
				   successList.add(lineMsg);
				   lineMsg = "";
				}
				
				//Add Detail LineMsg
				lineMsg = (r+1)+"|"+groupCode+"|"+min+"|"+max+"|"+priority;
				
				//step validate 
				
				//logger.debug("lineMsg:"+lineMsg);
				
				//is valid
				int index =1;
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					successList.add(lineMsg);
				}else{
					failCount++;
					lineMsg += "|FAIL|"+errorMsg;
					failList.add(lineMsg);
				}
				//Get Next ID PENSBME_MST_REFERENCE
				BigDecimal id = SequenceProcessAll.getIns().getNextValue(conn, "PENSBME_MST_REFERENCE");
				
				//add statement
				ps.setBigDecimal(index++, id);
				ps.setString(index++, "replenishment_minmax");
				ps.setString(index++, Utils.isNull(priority));
				ps.setString(index++, "ค่า Min-Max ที่จะเติมเติม ที่ใช้คำนวนเรื่อง Auto-Replenishment");
				ps.setString(index++, Utils.isNull(groupCode));
				ps.setString(index++, Utils.isNull(min));
				ps.setString(index++, Utils.isNull(max));
				
			    ps.addBatch();
			    
	    	}//for row
		
			logger.debug("result failCount:"+failCount);
			if(failCount ==0){
				status = Constants.STATUS_SUCCESS;
				//no error
				int e[] = ps.executeBatch();
				logger.debug("excute count:"+e.length);
				
				//Execute Insert invoice
				ps.executeBatch();
				request.setAttribute("Message","Import ข้อมูลเรียบร้อยแล้ว");
				request.getSession().setAttribute("SUCCESS_LIST", successList);
				conn.commit();
			}else{
				conn.rollback();
				request.getSession().setAttribute("ERROR_LIST", failList);
				request.setAttribute("Message", "ไม่สามารถ Import ข้อมูลได้  มีข้อมูลไม่ถูกต้อง โปรดตรวจสอบ");
			}
			
			logger.debug("result status:"+status);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			conn.rollback();
		}finally{
			try{
				if(ps != null){
					ps.close();ps = null;
				}
				if(conn != null){
					conn.close();conn = null;
				}
			}catch(Exception ee){}
		}
		return ImportAllForm;	
	}
	public ImportAllForm importExcelRepPriority(ImportAllForm ImportAllForm,User user,HttpServletRequest request)  throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql  =new StringBuffer();
		int status = Constants.STATUS_FAIL;
		int dataCount=0,successCount =0,failCount =0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		boolean passValid = false;String lineMsg = "";
		List<String> failList = new ArrayList<String>();
		List<String> successList = new ArrayList<String>();
		int colNo = 0 ;int r = 0;
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    String columnCheck1 ="",columnCheck2 ="",columnCheck3="";
	    String storeCode = "",storeName = "",priority="";
		try{
			//clear ERROR_LIST Session
			request.getSession().removeAttribute("ERROR_LIST");
			request.getSession().removeAttribute("SUCCESS_LIST");
			
			conn = DBConnection.getInstance().getConnectionApps();
	    	conn.setAutoCommit(false);
	    	 
			//Get Parameter Value
			FormFile dataFile = ImportAllForm.getDataFile2();
			
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
			
			//Check Column to Check
			if( sheet.getRow(0) !=null){
			   cellCheck =  sheet.getRow(0).getCell((short) 0);
			   columnCheck1 = Utils.isNull(xslUtils.getCellValue(0, cellCheck));
			   
			   cellCheck =  sheet.getRow(0).getCell((short) 1);
			   columnCheck2 = Utils.isNull(xslUtils.getCellValue(1, cellCheck));
			   
			   cellCheck =  sheet.getRow(0).getCell((short) 2);
			   columnCheck3 = Utils.isNull(xslUtils.getCellValue(2, cellCheck));
			}
			logger.debug("columnCheck1:"+columnCheck1+",columnCheck2:"+columnCheck2);
			if(!columnCheck1.equalsIgnoreCase("รหัสร้านค้า") || !columnCheck2.equalsIgnoreCase("ชื่อร้านค้า")
				|| !columnCheck3.equalsIgnoreCase("Priority")){
				logger.debug("Error");
	    		 //error
	    		 request.setAttribute("Message", "ข้อมูลไฟล์ ไม่ตรง Format ที่กำหนดไว้");
	    		 return ImportAllForm;
	    	}
			/*if(true){
				throw new Exception("Test");
			}*/
            //delete prev all data
			 SQLHelper.excUpdate(conn,"delete from pensbi.pensbme_mst_reference where reference_code ='replenishment_priority'");
			
			//prepare insert 
			sql = new StringBuffer();
			sql.append("INSERT INTO pensbi.pensbme_mst_reference \n");
			sql.append("(ID, REFERENCE_CODE,INTERFACE_VALUE,INTERFACE_DESC,PENS_VALUE,PENS_DESC) ");
			sql.append(" VALUES(?,?,?,?,?,?) \n");
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
				storeCode = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				storeName =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 2));
				priority =ExcelHelper.getCellValue(cellObjValue,"NUMBER","");
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				
				//add Head Table Display
				if(dataCount==1){
				   lineMsg = "Line No Excel|รหัสร้านค้า|ชื่อร้านค้า|Priority|Status|Message";
				   failList.add(lineMsg);
				   successList.add(lineMsg);
				   lineMsg = "";
				}
				
				//Add Detail LineMsg
				lineMsg = (r+1)+"|"+storeCode+"|"+storeName+"|"+priority;
				
				//step validate 
				
				//logger.debug("lineMsg:"+lineMsg);
				
				//is valid
				int index =1;
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					successList.add(lineMsg);
				}else{
					failCount++;
					lineMsg += "|FAIL|"+errorMsg;
					failList.add(lineMsg);
				}
				//Get Next ID PENSBME_MST_REFERENCE
				BigDecimal id = SequenceProcessAll.getIns().getNextValue(conn, "PENSBME_MST_REFERENCE");
				
				//add statement
				ps.setBigDecimal(index++, id);
				ps.setString(index++, "replenishment_priority");
				ps.setString(index++, Utils.isNull(priority));
				ps.setString(index++, "ค่ากำหนด Priority ของร้านค้า ที่ใช้คำนวนเรื่อง Auto-Replenishment");
				ps.setString(index++, Utils.isNull(storeCode));
				ps.setString(index++, Utils.isNull(storeName));
				
			    ps.addBatch();
			    
	    	}//for row
		
			logger.debug("result failCount:"+failCount);
			if(failCount ==0){
				status = Constants.STATUS_SUCCESS;
				//no error
				int e[] = ps.executeBatch();
				logger.debug("excute count:"+e.length);
				
				//Execute Insert invoice
				ps.executeBatch();
				request.setAttribute("Message","Import ข้อมูลเรียบร้อยแล้ว");
				request.getSession().setAttribute("SUCCESS_LIST", successList);
				conn.commit();
			}else{
				conn.rollback();
				request.getSession().setAttribute("ERROR_LIST", failList);
				request.setAttribute("Message", "ไม่สามารถ Import ข้อมูลได้  มีข้อมูลไม่ถูกต้อง โปรดตรวจสอบ");
			}
			
			logger.debug("result status:"+status);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			conn.rollback();
		}finally{
			try{
				if(ps != null){
					ps.close();ps = null;
				}
				if(conn != null){
					conn.close();conn = null;
				}
			}catch(Exception ee){}
		}
		return ImportAllForm;	
	}
	 public static boolean isStoreCodeValid(Connection conn,String storeCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean valid = false;
		try {
			sql.append("\n  SELECT count(*) as c from PENSBI.PENSBME_MST_REFERENCE M");
			sql.append("\n  where M.reference_code ='Store' ");
			sql.append("\n  and M.pens_desc4 ='N' ");
			sql.append("\n  and M.pens_value ='"+storeCode+"'");
			
			//logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				if(rst.getInt("c") >0){
					valid = true;
				}
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				
			} catch (Exception e) {}
		}
		return valid;
	}
	 public static boolean isMaterialMasterValid(Connection conn,String materialMaster) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean valid = false;
			try {
				sql.append("\n  SELECT count(*) as c from PENSBI.PENSBME_MST_REFERENCE M");
				sql.append("\n  where M.reference_code ='LotusItem' ");
				sql.append("\n  and M.interface_value ='"+materialMaster+"'");
				
				//logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					if(rst.getInt("c") >0){
						valid = true;
					}
				}//if
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					
				} catch (Exception e) {}
			}
			return valid;
		}
	 
	 public static StringBuffer searchRepConfig(Connection conn,boolean excel,String reportType,ImportAllBean bean) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			StringBuffer h = new StringBuffer();
	        int i = 0;
	        String classText="td_text",classTextCenter ="td_text_center",classNumber="td_number";
			try {
				if(excel){
					classText="text";
					classTextCenter ="text";
					classNumber="number";
				}
				if("rep_config".equalsIgnoreCase(reportType)){
				   sql.append("\n select * FROM PENSBI.BME_CONFIG_REP WHERE 1=1");
				   if( !Utils.isNull(bean.getCustGroup()).equals("")){
				      sql.append("\n AND STORE_CODE LIKE '"+bean.getCustGroup()+"%'");
				   }
				   if( !Utils.isNull(bean.getStoreCode()).equals("")){
				      sql.append("\n AND STORE_CODE LIKE '"+bean.getStoreCode()+"'");
				   }
				   if( !Utils.isNull(bean.getGroup()).equals("")){
					   sql.append("\n AND MATERIAL_MASTER LIKE '"+bean.getGroup()+"%'");
				   }
				   sql.append("\n ORDER BY STORE_CODE,material_master ");
				}else if("rep_minmax".equalsIgnoreCase(reportType)){
				   sql.append("\n SELECT pens_value as group_code,pens_desc as min,pens_desc2 as max,interface_value as priority  ");
				   sql.append("\n FROM  PENSBI.PENSBME_MST_REFERENCE where reference_code='replenishment_minmax'");
				   sql.append("\n ORDER BY interface_value,pens_value ");
				}else if("rep_priority".equalsIgnoreCase(reportType)){
				   sql.append("\n SELECT pens_value as store_code,pens_desc as store_name,interface_value as priority  ");
				   sql.append("\n FROM  PENSBI.PENSBME_MST_REFERENCE where reference_code='replenishment_priority'");
				   sql.append("\n ORDER BY interface_value");
				}
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					i++;
					if(i==1){
						//add header table
						h.append(ExcelHeader.EXCEL_HEADER);
						//Header
						h.append("<table width='60%' align='center' border='1' cellpadding='3' cellspacing='0' class='tableSearchNoWidth'> \n");
						h.append("<tr>");
						if("rep_config".equalsIgnoreCase(reportType)){
						   h.append(" <th align='center'>รหัสร้านค้า </th> \n");
						   h.append(" <th align='center'>MaterialMaster </th> \n");
						}else if("rep_minmax".equalsIgnoreCase(reportType)){
						   h.append(" <th align='center'> GroupCode</th> \n");
						   h.append(" <th align='center'>Min </th> \n");
						   h.append(" <th align='center'>Max </th> \n");
						   h.append(" <th align='center'>Priority </th> \n");
						}else if("rep_priority".equalsIgnoreCase(reportType)){
						   h.append(" <th align='center'>รหัสร้านค้า</th> \n");
						   h.append(" <th align='center'>ชื่อร้านค้า</th> \n");
						   h.append(" <th align='center'>Priority </th> \n");
						}
						h.append("</tr>");
					}
					//Row Detail
					h.append("<tr>");
					if("rep_config".equalsIgnoreCase(reportType)){
					   h.append("  <td width='10%' class='"+classTextCenter+"'>"+rst.getString("store_code")+"</td> \n");
					   h.append("  <td width='10%' class='"+classTextCenter+"' >"+rst.getString("material_master")+"</td>\n");
					}else if("rep_minmax".equalsIgnoreCase(reportType)){
					   h.append("  <td width='5%' class='"+classTextCenter+"'>"+rst.getString("group_code")+"</td> \n");
					   h.append("  <td width='5%' class='"+classTextCenter+"' >"+rst.getString("min")+"</td>\n");
					   h.append("  <td width='5%' class='"+classTextCenter+"' >"+rst.getString("max")+"</td>\n");
					   h.append("  <td width='5%' class='"+classTextCenter+"' >"+rst.getString("priority")+"</td>\n");
					}else if("rep_priority".equalsIgnoreCase(reportType)){
					   h.append("  <td width='5%' class='"+classTextCenter+"'>"+rst.getString("store_code")+"</td> \n");
					   h.append("  <td width='5%' class='"+classTextCenter+"' >"+rst.getString("store_name")+"</td>\n");
					   h.append("  <td width='5%' class='"+classTextCenter+"' >"+rst.getString("priority")+"</td>\n");
					}
					h.append("</tr>");
					
				}//if
				if(i>0){
					h.append("</table> \n");
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return h;
		}
}
