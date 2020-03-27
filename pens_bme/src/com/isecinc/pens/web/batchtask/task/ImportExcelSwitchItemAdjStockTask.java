package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import sun.security.krb5.internal.SeqNumber;

import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.dao.AdjustStockDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHelper;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;
import com.pens.util.seq.SequenceProcessAll;

public class ImportExcelSwitchItemAdjStockTask extends BatchTask implements BatchTaskInterface{
	
	
	/**
	 * Return :P Name|P label|P Type|default value|valid,P2...$processName,Button Name|....
	*/
	public String[] getParam(){
		String[] param = new String[1];
		param[0] = "dataFormFile|เลือกไฟล์|FROMFILE||VALID";
		return param;
	}
	public List<BatchTaskListBean> getParamListBox(){
		List<BatchTaskListBean> listAll = new ArrayList<BatchTaskListBean>();
		return listAll;
	}
	public String getButtonName(){
		return "Import ข้อมูล";
	}
	public String getDescription(){
		return "Load file Excel สลับ Item For ADJUST Stock INVENTORY ";
	}
	public String getDevInfo(){
		return "PENSBI.PENSBME_ADJUST_INVENTORY";
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
			monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
			monitorModel.setFileName(monitorModel.getDataFile().getFileName());
            monitorModel.setThName("Load file สลับ Item");
			
			/** Step validate **/
			
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
			monitorModel.setType("IMPORT");
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
		int rowNo = 3; // row of begin data
		boolean passValid = false;String lineMsg = "";
		int colNo = 0 ;int r = 0;
		boolean foundError = false;
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    String docNo= "",storeCode ="",storeName="",transactionDateInput ="",transactionDate ="";
	    int seq = 0;
	    String itemIssue = "",itemIssueDesc="",itemIssueQty = "",itemIssueUom="",itemIssuePrice="";
	    String itemReceipt = "",itemReceiptDesc="",itemReceiptQty="",itemReceiptUom="",itemReceiptPrice="";
	    String bankNo ="",org="",subInv="",ref="",lineStatus="";
	    double diffCost=0;
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
     
			//prepare insert temp import
			sql = new StringBuffer();
			sql.append(" INSERT INTO PENSBI.PENSBME_ADJUST_INVENTORY \n");
			sql.append(" (DOCUMENT_NO, SEQ_NO, TRANSACTION_DATE, STORE_CODE,  \n");
			sql.append(" STORE_NAME, BANK_NO, ORG, SUB_INV,  \n");
			sql.append(" REFERENCE, STATUS, ITEM_ISSUE, ITEM_ISSUE_DESC,  \n");
			sql.append(" ITEM_ISSUE_UOM, ITEM_ISSUE_QTY, ITEM_ISSUE_RETAIL_NON_VAT, ITEM_RECEIPT,  \n");
			sql.append(" ITEM_RECEIPT_DESC, ITEM_RECEIPT_UOM, ITEM_RECEIPT_QTY, ITEM_RECEIPT_RETAIL_NON_VAT,  \n");
			sql.append(" DIFF_COST, CREATE_DATE, CREATE_USER,FILE_NAME) \n");
		    sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
		    
			ps = conn.prepareStatement(sql.toString());
			
			/** Get Header Excel **/
			//row[0][1] StoreCode ,row[1][1] transactionDate
			row = sheet.getRow(0);
		    cellCheck = row.getCell((short) 1);
		    storeCode = ExcelHelper.getCellValue(xslUtils.getCellValue(1, cellCheck),"STRING","");  
		    
		    row = sheet.getRow(1);
		    cellCheck = row.getCell((short) 1);
		    transactionDateInput = ExcelHelper.getCellValue(xslUtils.getCellValue(1, cellCheck),"DATE","");
		    
			//validate format date
		    try{
				logger.debug("transactionDateInput:"+transactionDateInput);
				Date transactionDateObj = DateUtil.parse(transactionDateInput, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				logger.debug("transactionDateObj:"+transactionDateObj);
				//convert to ChristDate
				transactionDate = DateUtil.stringValue(transactionDateObj, DateUtil.DD_MM_YYYY_WITH_SLASH);
		    }catch(Exception e){
		    	monitorItemBean.setStatus(Constants.STATUS_FAIL);
		    	monitorItemBean.setErrorMsg("ข้อมูลวันที่: "+transactionDateInput+"ไม่ถูกต้อง กรุณาตรวจสอบ");
		    	monitorItemBean.setErrorCode("ImportException");
		    	return monitorItemBean;
		    }
		    
		    //validate head storeCode,transactionDate is Exist
		    if(isDataExist(conn, storeCode,transactionDate)){
		    	monitorItemBean.setStatus(Constants.STATUS_FAIL);
		    	monitorItemBean.setErrorMsg("ร้านค้า : "+storeCode+",วันที่: "+transactionDate+" ได้เคย Importไปแล้ว");
		    	monitorItemBean.setErrorCode("ImportException");
		    	return monitorItemBean;
		    }
		    //Get StoreCode Detail for validate
		    AdjustStock storeInfo = getStoreInfo(conn, storeCode);
			if(storeInfo ==null){
				monitorItemBean.setStatus(Constants.STATUS_FAIL);
		    	monitorItemBean.setErrorMsg("ร้านค้า : "+storeCode+" ไม่พบข้อมูลใน Master");
		    	monitorItemBean.setErrorCode("ImportException");
		    	return monitorItemBean;
			}else{
				if(Utils.isNull(storeInfo.getSubInv()).equals("") || Utils.isNull(storeInfo.getOrg()).equals("") ){
					monitorItemBean.setStatus(Constants.STATUS_FAIL);
			    	monitorItemBean.setErrorMsg("ร้านค้า : "+storeCode+" ไม่สามารถทำรายการได้  ไม่พบข้อมูล Suv inv/Org");
			    	monitorItemBean.setErrorCode("ImportException");
			    	return monitorItemBean;
				}
			}
		
			logger.debug("StoreCode:"+storeCode+"["+storeInfo.getStoreName()+"],transactionDate:"+transactionDate);
		    
		    //Gen DocNo
		    docNo = genDocumentNo(conn,transactionDate,storeCode);
		     
		    /** Loop Row **/
			for (r = rowNo; r <=sheet.getLastRowNum() ; r++) {
				//reset value 
				itemIssue = "";
				itemIssueQty = "";
				itemIssueDesc = "";
				itemIssuePrice = "";
				itemIssueUom ="";
				
				itemReceipt  ="";
				itemReceiptQty = "";
				itemReceiptDesc = "";
				itemReceiptPrice = "";
				itemReceiptUom ="";
				
				//get Row Excel
				row = sheet.getRow(r);
				//logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
			       cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			       if(cellCheckValue == null){
			    	   //case column 0 is not check col 2 
			    	   cellCheck = row.getCell((short) 2);
				       cellCheckValue = xslUtils.getCellValue(2, cellCheck);
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
				itemIssueDesc = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				itemIssueQty =ExcelHelper.getCellValue(cellObjValue,"NUMBER","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 2));
				itemReceiptDesc =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 3));
				itemReceiptQty = ExcelHelper.getCellValue(cellObjValue,"NUMBER","");
				
				//get detail from head
				storeName = storeInfo.getStoreName();
				bankNo = storeInfo.getBankNo();
				org = storeInfo.getOrg();
				subInv = storeInfo.getSubInv();
				ref= "";//input by user 
				lineStatus = "O";//open
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				seq++;
				
				PopupForm popupForm = new PopupForm();
				List<PopupForm> results = null;
				/** step validate item  **/
				// 1.) Validate ItemIssue (can be null)
				// 1.1 check ItemIssue detail
				if( !Utils.isNull(itemIssueDesc).equals("")){	
					popupForm.setCodeSearch(itemIssueDesc);
					results = SummaryDAO.searchProductFromMstByGroupCode(popupForm);
					if(results != null && results.size()>0){
						PopupForm p = (PopupForm)results.get(0);
						itemIssue = p.getCode();
						itemIssuePrice = p.getPrice();
						itemIssueUom ="EA";
					}else{
						passValid = false;
						foundError = true;
						errorMsg = " ไม่ข้อมูล Group Issue :"+itemIssue +" ในระบบ ";
					}
					// 1.2 check itemIssueQty != 0
					if(Utils.convertToDouble(itemIssueQty) ==0){
						passValid = false;
						foundError = true;
						errorMsg = ",Issue Qty:"+itemIssueQty +" ต้องไม่เท่ากับ 0";
					}//if
				}
				
				// 2.) Validate ItemReceipt (can be null)
				// 1.1 check ItemReceipt detail
				if( !Utils.isNull(itemReceiptDesc).equals("")){	
					popupForm = new PopupForm();
					popupForm.setCodeSearch(itemReceiptDesc);
					results = SummaryDAO.searchProductFromMstByGroupCode(popupForm);
					if(results != null && results.size()>0){
						PopupForm p = (PopupForm)results.get(0);
						itemReceipt = p.getCode();
						itemReceiptPrice = p.getPrice();
						itemReceiptUom ="EA";
					}else{
						passValid = false;
						foundError = true;
						errorMsg = " ไม่ข้อมูล Group Receipt :"+itemIssue +" ในระบบ ";
					}
					// 1.2 check itemReceiptQty != 0
					if(Utils.convertToDouble(itemReceiptQty) ==0){
						passValid = false;
						foundError = true;
						errorMsg = ",Receipt Qty:"+itemReceiptQty +" ต้องไม่เท่ากับ 0";
					}//if
				}//if
				
				//insert log to display
				lineMsg  = (r+1)+"|"+itemIssue+"|"+itemIssueQty+"|"+itemReceipt+"|"+itemReceiptQty;
				
				//logger.debug("lineMsg:"+lineMsg);
				//is valid
				int index =1;
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
					
				}else{
					failCount++;
					//foundError = true; no roll back
					lineMsg += "|FAIL|"+errorMsg;
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				}
				
				//add statement
				ps.setString(index++, docNo);
				ps.setInt(index++, seq);
				ps.setDate(index++, new java.sql.Date(DateUtil.parse(transactionDate,DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
				ps.setString(index++, storeCode);
				ps.setString(index++, storeName);
				ps.setString(index++, bankNo);
				ps.setString(index++, org);
				ps.setString(index++, subInv);
				ps.setString(index++, ref);
				ps.setString(index++, lineStatus);
				
				ps.setString(index++, itemIssue);
				ps.setString(index++, itemIssueDesc);
				ps.setString(index++, itemIssueUom);
				ps.setDouble(index++, Utils.convertStrToDouble(itemIssueQty));
				ps.setDouble(index++, Utils.convertStrToDouble(itemIssuePrice));
				
				ps.setString(index++, itemReceipt);
				ps.setString(index++, itemReceiptDesc);
				ps.setString(index++, itemReceiptUom);
				ps.setDouble(index++, Utils.convertStrToDouble(itemReceiptQty));
				ps.setDouble(index++, Utils.convertStrToDouble(itemReceiptPrice));
				
				//issue-receipt
				diffCost  = Utils.convertStrToDouble(itemIssueQty)* Utils.convertStrToDouble(itemIssuePrice);
				diffCost -= Utils.convertStrToDouble(itemReceiptQty)* Utils.convertStrToDouble(itemReceiptPrice);
				
				ps.setDouble(index++, diffCost);
				ps.setDate(index++, new java.sql.Date(new Date().getTime()));
				ps.setString(index++, monitorModel.getCreateUser());
				ps.setString(index++, fileName);
				
			    ps.addBatch();
	    	}//for row
		
			//Insert Column For Display Result (1:no, last column ->status,Message )default column
			lineMsg ="";
			lineMsg  = "No|Line Excel|Group Issue|Issue Qty|Group Receipt|Receipt Qty|";
			lineMsg += "Status|Message";
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
	
	public static boolean isDataExist(Connection conn,String storeCode,String transactionDate) {
		ResultSet rs = null;
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		boolean exist = false;
		try{
			sql.append("\n SELECT count(*) as c");
			sql.append("\n FROM PENSBI.PENSBME_ADJUST_INVENTORY ");
			sql.append("\n WHERE store_code ='"+storeCode+"'");
			sql.append("\n and transaction_date =to_date('"+transactionDate+"','dd/mm/yyyy')");
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
			   if(rs.getInt("c") >0){
				   exist = true;
			   }
			}//while	
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return exist;	
	}
	// ( Running :  yyyymm+running  เช่น 201403001 )		
	//input tDate dd/mm/yyyy
	 private static String genDocumentNo(Connection conn,String tDate,String storeCode) throws Exception{
		   String orderNo = "";
		   try{
			   String[] d1 = tDate.split("\\/");
			   int curYear = Integer.parseInt(d1[2]);
			   int curMonth = Integer.parseInt(d1[1]);
			 
			   Date tDateObj = DateUtil.parse(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			   //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"ADJUST", "DOCUMENT_NO",tDateObj);
			   
			   orderNo = new DecimalFormat("0000").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }
		  return orderNo;
	}
	
	 public static AdjustStock getStoreInfo(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String BANK_NO = "01-0000-911530-000-821-000-001";//default hard code
			AdjustStock o = null;
			try {
				sql.append("\n SELECT interface_value, interface_desc FROM  ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE where reference_code = 'SubInv' ");
				sql.append("\n and pens_value ='"+storeCode+"'");
				sql.append("\n ORDER BY interface_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					o = new AdjustStock();
					o.setStoreCode(storeCode);
					o.setOrg(rst.getString("interface_value"));
					o.setSubInv(rst.getString("interface_desc"));
					o.setBankNo(BANK_NO);
					o.setStoreName(GeneralDAO.getStoreNameModel(conn,o.getStoreCode()));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();	
				} catch (Exception e) {}
			}
			return o;
		}
}
